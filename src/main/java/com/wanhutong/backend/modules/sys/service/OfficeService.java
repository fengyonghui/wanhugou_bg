/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomerInfo;
import com.wanhutong.backend.modules.biz.entity.dto.OfficeLevelApplyDto;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageUser;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomerInfoService;
import com.wanhutong.backend.modules.biz.service.message.BizMessageUserService;
import com.wanhutong.backend.modules.biz.service.shop.BizShopCartService;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.SystemConfig;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wanhutong.backend.common.persistence.BaseEntity.DEL_FLAG_NORMAL;

/**
 * 机构Service
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {

    public static final Integer VENDORROLEID = 29;
    public static final Integer PURCHASERSPEOPLE = 9;
    public static final Integer SHOPKEEPER = 63;
    public static final Integer COMMISSION_MERCHANT = 64;
    @Autowired
    private OfficeDao officeDao;
    @Autowired
    private BizCustCreditService bizCustCreditService;
    @Autowired
    private CommonLocationService commonLocationService;
    @Autowired
    private SysOfficeAddressService sysOfficeAddressService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizVarietyInfoService bizVarietyInfoService;
    @Autowired
    private BizVendInfoService bizVendInfoService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private BizCustomerInfoService bizCustomerInfoService;
    @Autowired
    private BizMessageUserService bizMessageUserService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private BizShopCartService bizShopCartService;

    public static final String PHOTO_SPLIT_CHAR = "\\|";
    public static final String CUSTOMER_APPLY_LEVEL_OBJECT_NAME = "CUSTOMER_APPLY_LEVEL_OBJECT_NAME";


    public List<Office> findAll() {
        return UserUtils.getOfficeList();
    }

    public List<Office> findList(Boolean isAll) {
        if (isAll != null && isAll) {
            return UserUtils.getOfficeAllList();
        } else {
            return UserUtils.getOfficeList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Office> findList(Office office) {
        if (office != null) {
            office.setParentIds(office.getParentIds() + "%");
            User user = UserUtils.getUser();
            if (!user.isAdmin() && !OfficeTypeEnum.VENDOR.getType().equals(office.getType()) && !OfficeTypeEnum.CUSTOMER.getType().equals(office.getType())) {
                office.setDataStatus("filter");
                office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
            } else if (!user.isAdmin() && OfficeTypeEnum.CUSTOMER.getType().equals(office.getType())) {
                boolean flag = false;
                boolean flagb = false;
                if (user.getRoleList() != null) {
                    for (Role role : user.getRoleList()) {
                        if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                            flag = true;
                            break;
                        } else if (RoleEnNameEnum.BUYER.getState().equals(role.getName())) {
                            flagb = true;
                            break;
                        }
                    }
                }
                BizCustomCenterConsultant customCenterConsultant = new BizCustomCenterConsultant();
                if (flag) {
                    customCenterConsultant.setCenters(user.getCompany());
                } else if (flagb) {
                    customCenterConsultant.setConsultants(user);
                }
                customCenterConsultant.setParentIds(office.getParentIds());

                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            }
            return dao.findByParentIdsLike(office);
        }
        return new ArrayList<Office>();
    }

    public List<Office> filerOffice(List<Office> offices, String source, OfficeTypeEnum officeType) {
        Office office = new Office();
        User user = UserUtils.getUser();
        if (!user.isAdmin() && !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) && !OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())) {
            office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
        } else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))) {

        } else if (!user.isAdmin() && OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())) {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;

                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;

                    }
                }
            }
            BizCustomCenterConsultant customCenterConsultant = new BizCustomCenterConsultant();
            if (flag && (source == null || source.equals("") || source.equals("purchaser"))) {
                customCenterConsultant.setCenters(user.getCompany());

                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                customCenterConsultant.setCenters(user.getCompany());
                if (StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                    customCenterConsultant.setConsultants(user);
                }
                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb || (flag && StringUtils.isNotBlank(source) && source.equals("con"))) {
                office.setType(String.valueOf(officeType.ordinal()));

                office.setDelFlag(DEL_FLAG_NORMAL);
                List<Office> officeList = officeDao.findOfficeCustByIdToParent(office);
                return officeList;
            }
        }

        office.setType(String.valueOf(officeType.ordinal()));

        office.setDelFlag(DEL_FLAG_NORMAL);

        List<Office> list = queryList(office);
        //get all parents
        Set<Integer> parentSet = new HashSet<>();
        for (Office office1 : list) {
            String[] parentIds = office1.getParentIds().split(",");
            for (String id : parentIds) {
                parentSet.add(Integer.valueOf(id));
            }
        }

        if (offices == null || offices.size() == 0) {
            office.setType(null);
            //	office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "so", ""));
            offices = queryList(office);
        }

        Iterator<Office> iterator = offices.iterator();
        while (iterator.hasNext()) {
            Office office1 = iterator.next();
            Integer id = office1.getId();
            if (!parentSet.contains(id) && !String.valueOf(officeType.ordinal()).equals(office1.getType())) {
                iterator.remove();   //注意这个地方
            }
        }

        return offices;

    }

    public List<Office> filerOffice4mobile(List<Office> offices, String source, OfficeTypeEnum officeType) {
        Office office = new Office();
        User user = UserUtils.getUser();
        if (!user.isAdmin() && !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) && !OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())) {
            office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
        } else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))) {

        } else if (!user.isAdmin() && OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())) {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;

                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;

                    }
                }
            }
            BizCustomCenterConsultant customCenterConsultant = new BizCustomCenterConsultant();
            if (flag && (source == null || source.equals("") || source.equals("purchaser"))) {
                customCenterConsultant.setCenters(user.getCompany());

                //List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);
                List<Office> officeList = officeDao.findOfficeById4Mobile(customCenterConsultant);

                return officeList;
            } else if (flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                customCenterConsultant.setCenters(user.getCompany());
                if (StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                    customCenterConsultant.setConsultants(user);
                }
                //List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);
                List<Office> officeList = officeDao.findOfficeById4Mobile(customCenterConsultant);

                return officeList;
            } else if (flagb || (flag && StringUtils.isNotBlank(source) && source.equals("con"))) {
                office.setType(String.valueOf(officeType.ordinal()));

                office.setDelFlag(DEL_FLAG_NORMAL);
                List<Office> officeList = officeDao.findOfficeCustByIdToParent(office);
                return officeList;
            }
        }

        office.setType(String.valueOf(officeType.ordinal()));

        office.setDelFlag(DEL_FLAG_NORMAL);

        List<Office> list = queryList(office);

        return list;

    }

    public List<Office> filerOfficeByPhone(List<Office> offices, String source, OfficeTypeEnum officeType, String phone) {
        Office office = new Office();
        office.setPhone(phone);
        User user = UserUtils.getUser();
        if (!user.isAdmin() && !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) && !OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())) {
            office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
        } else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))) {

        } else if (!user.isAdmin() && OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())) {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;

                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;

                    }
                }
            }
            BizCustomCenterConsultant customCenterConsultant = new BizCustomCenterConsultant();
            if (flag && (source == null || source.equals("") || source.equals("purchaser"))) {
                customCenterConsultant.setCenters(user.getCompany());

                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                customCenterConsultant.setCenters(user.getCompany());
                if (StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                    customCenterConsultant.setConsultants(user);
                }
                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb || (flag && StringUtils.isNotBlank(source) && source.equals("con"))) {
                office.setType(String.valueOf(officeType.ordinal()));

                office.setDelFlag(DEL_FLAG_NORMAL);
                List<Office> officeList = officeDao.findOfficeCustByIdToParent(office);
                return officeList;
            }
        }

        office.setType(String.valueOf(officeType.ordinal()));

        office.setDelFlag(DEL_FLAG_NORMAL);


        List<Office> list = queryList(office);
        //get all parents
        Set<Integer> parentSet = new HashSet<>();
        for (Office office1 : list) {
            String[] parentIds = office1.getParentIds().split(",");
            for (String id : parentIds) {
                parentSet.add(Integer.valueOf(id));
            }
        }

        if (offices == null || offices.size() == 0) {
            office.setType(null);
            //	office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "so", ""));
            offices = queryList(office);
        }

        Iterator<Office> iterator = offices.iterator();
        while (iterator.hasNext()) {
            Office office1 = iterator.next();
            Integer id = office1.getId();
            if (!parentSet.contains(id) && !String.valueOf(officeType.ordinal()).equals(office1.getType())) {
                iterator.remove();   //注意这个地方
            }
        }

        return offices;

    }

    public List<Office> queryList(Office office) {
        return officeDao.queryList(office);
    }

    public List<Office> queryCenterList(Office office) {
        User user = UserUtils.getUser();
        office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
        return officeDao.queryList(office);
    }

    public List<CommonImg> getImgList(Integer id, ImgEnum imgEnum) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(id);
        commonImg.setObjectName(imgEnum.getTableName());
        commonImg.setImgType(imgEnum.getCode());
        return commonImgService.findList(commonImg);
    }

    private void saveImg(String photos, Integer id, ImgEnum imgEnum) {
        if (StringUtils.isBlank(photos)) {
            return;
        }
        String[] split = photos.split(PHOTO_SPLIT_CHAR);
        List<String> photoList = Arrays.asList(split);

        List<CommonImg> oldCommonImgs = getImgList(id, imgEnum);

        Set<String> existSet = new HashSet<>();
        for (CommonImg commonImg1 : oldCommonImgs) {
            existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
        }
        Set<String> newSet = new HashSet<>(photoList);

        Set<String> result = new HashSet<String>();
        //差集，结果做删除操作
        result.clear();
        result.addAll(existSet);
        result.removeAll(newSet);
        for (String url : result) {
            for (CommonImg commonImg1 : oldCommonImgs) {
                if (url.equals(commonImg1.getImgServer() + commonImg1.getImgPath())) {
                    commonImgService.delete(commonImg1);
                }
            }
        }

        //差集，结果做插入操作
        result.clear();
        result.addAll(newSet);
        result.removeAll(existSet);

        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(id);
        commonImg.setObjectName(imgEnum.getTableName());
        commonImg.setImgType(imgEnum.getCode());

        int i = 0;
        for (String img : result) {
            if (StringUtils.isBlank(img)) {
                continue;
            }
            commonImg.setImgType(imgEnum.getCode());
            commonImg.setObjectId(id);
            commonImg.setObjectName(imgEnum.getTableName());
            commonImg.setImgSort(i);
            commonImg.setImgServer(StringUtils.isBlank(img) ? StringUtils.EMPTY : DsConfig.getImgServer());
            commonImg.setImgPath(img.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY));
            commonImgService.save(commonImg);
            i ++;
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void save(Office office) {
        super.save(office);
        //保存供应商经营品类
        if (office.getBizVendInfo() != null && office.getBizVendInfo().getBizCategoryInfo()!=null && office.getBizVendInfo().getBizCategoryInfo().getId() != null) {
            BizVarietyInfo bizVarietyInfo = bizVarietyInfoService.get(office.getBizVendInfo().getBizCategoryInfo().getId());
            BizVendInfo bizVendInfo = new BizVendInfo();
            BizVendInfo vendInfo = bizVendInfoService.get(office.getId());
            if (vendInfo != null) {
                bizVendInfo.setId(office.getId());
            }
            bizVendInfo.setOffice(office);
            bizVendInfo.setVendName(office.getName());
            BizCategoryInfo bizCategoryInfo = new BizCategoryInfo();
            bizCategoryInfo.setId(office.getBizVendInfo().getBizCategoryInfo().getId());
            bizVendInfo.setBizCategoryInfo(bizCategoryInfo);
            bizVendInfo.setCateName(bizVarietyInfo.getName());
            bizVendInfo.setCardNumber(office.getBizVendInfo().getCardNumber());
            bizVendInfo.setBankName(office.getBizVendInfo().getBankName());
            bizVendInfo.setPayee(office.getBizVendInfo().getPayee());
            bizVendInfo.setCardNumber(office.getBizVendInfo().getCardNumber());
            bizVendInfo.setCode(office.getCode());
            bizVendInfo.setAuditStatus(BizVendInfo.AuditStatus.UNAUDITED.getStatus());
            bizVendInfo.setRemarks(office.getBizVendInfo().getRemarks());
            bizVendInfo.setRemark(office.getBizVendInfo().getRemark());
            bizVendInfo.setIntroduce(office.getBizVendInfo().getIntroduce());
            bizVendInfo.setProdAdv(office.getBizVendInfo().getProdAdv());
            bizVendInfo.setType(office.getBizVendInfo().getType());
            bizVendInfoService.save(bizVendInfo);
        }

        if (office.getBizVendInfo() != null && StringUtils.isNotBlank(office.getBizVendInfo().getCompactPhotos())) {
            saveImg(office.getBizVendInfo().getCompactPhotos(), office.getId(), ImgEnum.VEND_COMPACT);
        }

        if (office.getBizVendInfo() != null && StringUtils.isNotBlank(office.getBizVendInfo().getIdCardPhotos())) {
            saveImg(office.getBizVendInfo().getIdCardPhotos(), office.getId(), ImgEnum.VEND_IDENTITY_CARD);
        }

        if (office.getVendVideo() != null) {
            saveVideo(office.getId(),office.getVendVideo(),ImgEnum.VENDOR_VIDEO);
        }

        SysOfficeAddress address = new SysOfficeAddress();
        /**
         * 用于保存供应商地址
         * */
        SysOfficeAddress sysOfficeAddress = new SysOfficeAddress();
        sysOfficeAddress.setOffice(office);
        List<SysOfficeAddress> list = sysOfficeAddressService.findList(sysOfficeAddress);
        if (list.size() != 0) {
            for (SysOfficeAddress add : list) {
                if (add.getDeFaultStatus() != null && add.getDeFaultStatus() == 1) {
                    address.setId(add.getId());
                    break;
                }
            }
        }
        if (office.getOfficeAddress() != null) {
            CommonLocation commonLocation = commonLocationService.get(office.getLocationId());
            if (office.getLocationId() != null && office.getOfficeAddress().getBizLocation().getSelectedRegionId() == null &&
                    commonLocation.getAddress().equals(office.getOfficeAddress().getBizLocation().getAddress())) {
                address.setBizLocation(commonLocation);
            } else {
                if (office.getOfficeAddress().getBizLocation().getSelectedRegionId() == null) {
                    if (office.getLocationId() != null && office.getOfficeAddress().getBizLocation().getProvince() == null) {
                        office.getOfficeAddress().getBizLocation().setProvince(commonLocation.getProvince());
                    }
                    if (office.getLocationId() != null && office.getOfficeAddress().getBizLocation().getCity() == null) {
                        office.getOfficeAddress().getBizLocation().setCity(commonLocation.getCity());
                    }
                    if (office.getLocationId() != null && office.getOfficeAddress().getBizLocation().getRegion() == null) {
                        office.getOfficeAddress().getBizLocation().setRegion(commonLocation.getRegion());
                    }
                }
                address.setBizLocation(office.getOfficeAddress().getBizLocation());
            }
            address.setOffice(office);
            if (office.getPrimaryPerson() != null && !office.getPrimaryPerson().getName().equals("")) {
                address.setReceiver(String.valueOf(office.getPrimaryPerson().getName()));
            } else {
                address.setReceiver("无");
            }
            address.setPhone(office.getPhone());
            String offType = DictUtils.getDictValue("公司地址", "office_type", "");
            String sysDefau = DictUtils.getDictValue("默认", "sysadd_deFault", "");
            if (offType != null) {
                address.setType(Integer.parseInt(offType));
            } else {
                address.setType(2);
            }
            if (sysDefau != null) {
                address.setDeFaultStatus(Integer.parseInt(sysDefau));
            } else {
                address.setDeFaultStatus(1);
            }
            sysOfficeAddressService.save(address);
            office.setAddress(String.valueOf(address.getBizLocation().getPcrName()) + address.getBizLocation().getAddress());
        }
        super.save(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
        //保存新建联系人
        String gys = "gys_save";
        if (office.getPrimaryPerson() != null && office.getGysFlag() != null && office.getGysFlag().equals(gys)) {
            if (office.getPrimaryPerson().getId() == null || office.getPrimaryPerson().getId().equals("")) {
                User primaryPerson = office.getPrimaryPerson();
                primaryPerson.setCompany(office);
                primaryPerson.setOffice(office);
                primaryPerson.setPassword((SystemService.entryptPassword(primaryPerson.getNewPassword())));
                primaryPerson.setLoginFlag("1");
                List<Role> roleList = Lists.newArrayList();
                roleList.add(systemService.getRole(VENDORROLEID));
                primaryPerson.setRoleList(roleList);
                systemService.saveUser(primaryPerson);
                UserUtils.clearCache(primaryPerson);
                office.setPrimaryPerson(primaryPerson);
                super.save(office);
            }
        }
    }

    //	创建采购商同时创建钱包
    @Transactional(readOnly = false)
    public void save(Office office, BizCustCredit bizCustCredit) {
        super.save(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);

        //保存钱包
        if (StringUtils.equals(office.getType(),OfficeTypeEnum.CUSTOMER.getType())
            || StringUtils.equals(office.getType(),OfficeTypeEnum.SHOPKEEPER.getType())
            || StringUtils.equals(office.getType(),OfficeTypeEnum.COMMISSION_MERCHANT.getType())
        ) {
            bizCustCredit = new BizCustCredit();
            bizCustCredit.setCustomer(office);
            bizCustCredit.setLevel(StringUtils.isBlank(office.getLevel()) ? "1" : office.getLevel());
            bizCustCreditService.save(bizCustCredit);
        }

        if (office.getBizCustomerInfo() != null) {
            office.getBizCustomerInfo().setOfficeId(office.getId());
            bizCustomerInfoService.save(office.getBizCustomerInfo());
        }

        //经销店保存新建联系人
        if (office.getPrimaryPerson() != null && office.getSource() != null && office.getSource().equals("add_prim")) {
            if(office.getPrimaryPerson().getName()!=null && !office.getPrimaryPerson().getName().equals("")){
                if (office.getPrimaryPerson().getId() == null || office.getPrimaryPerson().getId().equals("")) {
                    User primaryPerson = office.getPrimaryPerson();
                    primaryPerson.setCompany(office);
                    primaryPerson.setOffice(office);
                    primaryPerson.setPassword((SystemService.entryptPassword(primaryPerson.getNewPassword())));
                    primaryPerson.setLoginFlag("1");
                    List<Role> roleList = Lists.newArrayList();
                    switch (OfficeTypeEnum.stateOf(office.getType())) {
                        case CUSTOMER:
                            roleList.add(systemService.getRole(PURCHASERSPEOPLE));
                            break;
                        case SHOPKEEPER:
                            roleList.add(systemService.getRole(SHOPKEEPER));
                            break;
                        case COMMISSION_MERCHANT:
                            roleList.add(systemService.getRole(COMMISSION_MERCHANT));
                            break;
                        default:
                            break;
                    }

                    primaryPerson.setRoleList(roleList);
                    systemService.saveUser(primaryPerson);
                    UserUtils.clearCache(primaryPerson);
                    office.setPrimaryPerson(primaryPerson);
                    super.save(office);
                }
            }
        }
    }

    /**
     * 保存视频
     *
     * @param id
     * @param videos
     * @param imgEnum
     */
    private void saveVideo(Integer id, String videos, ImgEnum imgEnum) {
        if (StringUtils.isBlank(videos)) {
            return;
        }
        String[] videoArr = videos.split(",");

        List<CommonImg> commonImgs = getImgList(imgEnum.getCode(), id, imgEnum.getTableName());

        Set<String> existSet = new LinkedHashSet<>();
        for (CommonImg c : commonImgs) {
            existSet.add(c.getImgServer() + c.getImgPath());
        }
        Set<String> newSet = new LinkedHashSet<>(Arrays.asList(videoArr));

        Set<String> result = new LinkedHashSet<>();
        //差集，结果做删除操作
        result.clear();
        result.addAll(existSet);
        result.removeAll(newSet);
        for (String url : result) {
            for (CommonImg c : commonImgs) {
                if (url.equals(c.getImgServer() + c.getImgPath())) {
                    c.setDelFlag("0");
                    commonImgService.delete(c);
                }
            }
        }

        result.clear();
        result.addAll(newSet);
        result.removeAll(existSet);

        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(id);
        commonImg.setObjectName(imgEnum.getTableName());
        commonImg.setImgType(imgEnum.getCode());

        int index = 0;
        for (String video : result) {
            if (StringUtils.isNotBlank(video)) {
                commonImg.setImgSort(index);
                commonImg.setId(null);
                commonImg.setImgPath(video.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY).replaceAll(DsConfig.getOldImgServer(), StringUtils.EMPTY));
                commonImg.setImgServer(video.contains(DsConfig.getOldImgServer()) ? DsConfig.getOldImgServer() : DsConfig.getImgServer());
                commonImgService.save(commonImg);
                continue;
            }
            index ++;
        }
    }

    private List<CommonImg> getImgList(Integer imgType, Integer officeId, String objectName) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(officeId);
        commonImg.setObjectName(objectName);
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Office office) {
        super.delete(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }

    @Override
    public Page<Office> findPage(Page<Office> page, Office office) {
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            office.setDataStatus("all");
            return super.findPage(page, office);
        } else {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;

                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;

                    }
                }
            }
            if (flag) {
                office.setCenterId(user.getCompany().getId());
            } else if (flagb) {
                office.setConsultantId(user.getId());
            }
            //office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
            return super.findPage(page, office);
        }
    }

    /**
     * 用于客户专员查询采购中心
     */
    public List<Office> CustomerfilerOffice(List<Office> offices, String source, OfficeTypeEnum officeType) {
        Office office = new Office();
        User user = UserUtils.getUser();
        if (!user.isAdmin() && !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) && !OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())
                || user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) || user.getCompany().getType().equals(OfficeTypeEnum.NETWORK.getType())) {
            office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
        } else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))) {

        } else if (!user.isAdmin() && OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())) {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;
                        break;
                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;
                        break;
                    }
                }
            }
            BizCustomCenterConsultant customCenterConsultant = new BizCustomCenterConsultant();
            if (flag && StringUtils.isBlank(source)) {
                customCenterConsultant.setCenters(user.getCompany());

                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                customCenterConsultant.setCenters(user.getCompany());
                if (StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                    customCenterConsultant.setConsultants(user);
                }
                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb || (flag && StringUtils.isNotBlank(source) && source.equals("con"))) {
                office.setType(String.valueOf(officeType.ordinal()));

                office.setDelFlag(DEL_FLAG_NORMAL);
                List<Office> officeList = officeDao.findOfficeCustByIdToParent(office);
                return officeList;
            }
        }

        office.setType(String.valueOf(officeType.ordinal()));
        office.setCustomerTypeTen(OfficeTypeEnum.WITHCAPITAL.getType());
        office.setCustomerTypeEleven(OfficeTypeEnum.NETWORKSUPPLY.getType());
        office.setCustomerTypeThirteen(OfficeTypeEnum.NETWORK.getType());
        office.setDelFlag(DEL_FLAG_NORMAL);

        List<Office> list = queryList(office);
        //get all parents
        Set<Integer> parentSet = new HashSet<>();
        for (Office office1 : list) {
            String[] parentIds = office1.getParentIds().split(",");
            for (String id : parentIds) {
                parentSet.add(Integer.valueOf(id));
            }
        }

        if (offices == null || offices.size() == 0) {
            office.setType(null);
            //	office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "so", ""));
            offices = queryList(office);
        }

        Iterator<Office> iterator = offices.iterator();
        while (iterator.hasNext()) {
            Office office1 = iterator.next();
            Integer id = office1.getId();
            if (!parentSet.contains(id) && !String.valueOf(officeType.ordinal()).equals(office1.getType()) &&
                    !String.valueOf(10).equals(office1.getType()) && !String.valueOf(11).equals(office1.getType()) && !String.valueOf(13).equals(office1.getType())) {
                iterator.remove();   //注意这个地方
            }
        }

        return offices;

    }

    /**
     * 用于客户专员查询采购中心
     */
    public List<Office> customerfilerOffice4mobile(List<Office> offices, String source, OfficeTypeEnum officeType) {
        Office office = new Office();
        User user = UserUtils.getUser();
        if (!user.isAdmin() && !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) && !OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())
                || user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) || user.getCompany().getType().equals(OfficeTypeEnum.NETWORKSUPPLY.getType())
                || user.getCompany().getType().equals(OfficeTypeEnum.NETWORK.getType())) {
            office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
        } else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))) {

        } else if (!user.isAdmin() && OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())) {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;
                        break;
                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;
                        break;
                    }
                }
            }
            BizCustomCenterConsultant customCenterConsultant = new BizCustomCenterConsultant();
            if (flag && StringUtils.isBlank(source)) {
                customCenterConsultant.setCenters(user.getCompany());

                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                customCenterConsultant.setCenters(user.getCompany());
                if (StringUtils.isNotBlank(source) && source.equals("purchaser")) {
                    customCenterConsultant.setConsultants(user);
                }
                List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

                return officeList;
            } else if (flagb || (flag && StringUtils.isNotBlank(source) && source.equals("con"))) {
                office.setType(String.valueOf(officeType.ordinal()));

                office.setDelFlag(DEL_FLAG_NORMAL);
                List<Office> officeList = officeDao.findOfficeCustByIdToParent(office);
                return officeList;
            }
        }

        office.setType(String.valueOf(officeType.ordinal()));
        office.setDelFlag(DEL_FLAG_NORMAL);

        List<Office> list = queryList(office);
        return list;
    }

    /**
     * 取所有的供应商
     *
     * @param id
     * @return
     */
    public List<Office> findVendor(String id) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(",").append(id).append(",");
        String parentIds = stringBuffer.toString();
        return officeDao.findVent(parentIds);
    }

    /**
     * 取多个类型的office
     *
     * @param typeList
     * @return
     */
    public List<Office> findListByTypeList(List<String> typeList) {
        return officeDao.findListByTypeList(typeList);
    }

    /**
     * 用于查询配资下边的采购商
     *
     * @param cust
     * @return
     */
    public List<Office> findCapitalList(Office cust) {
        return officeDao.findList(cust);
    }

    /**
     * 根据type取数据
     *
     * @param type
     * @return
     */
    public List<Office> findListByType(String type) {
        return officeDao.findListByType(type);
    }

    /**
     * 根据officeId 取 cust
     *
     * @param officeId
     * @return
     */
    public List<Office> findCustomByOfficeId(Integer officeId) {
        return officeDao.findCustomByOfficeId(officeId);
    }

    /**
     * 供应商审核状态修改
     * @param id 供应商ID
     * @param status 审核状态
     * @return 操作结果
     */
    @Transactional(readOnly = false)
    public Pair<Boolean, String> auditSupplier(int id, int status) {
        return bizVendInfoService.auditSupplier(id, status);
    }

    /**
     * 导出 查询list
     * */
    public List<Office> findMeetingExprot(Office office) {
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            return dao.findList(office);
        } else {
            boolean flag = false;
            boolean flagb = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;

                    } else if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flagb = true;

                    }
                }
            }
            if (flag) {
                office.setCenterId(user.getCompany().getId());
            } else if (flagb) {
                office.setConsultantId(user.getId());
            }
            return dao.findList(office);
        }
    }


    public List<Office> getImgTreeListByPhone(String type, String source, String phone) {
        List<Office> list = null;
        if (StringUtils.isNotBlank(type)) {
            String defType = type;
            list = this.filerOfficeByPhone(null, source, OfficeTypeEnum.stateOf(defType), phone);
        }
        return list;
    }
    public List<String> getCustParentIdByVendorId(Integer vendorId) {
        return dao.getCustParentIdByVendorId(vendorId);
    }
    public List<String> getCustIdByVendorId(Integer vendorId) {
        return dao.getCustIdByVendorId(vendorId);
    }


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> upgradeAudit(Integer id, Integer applyForLevel, CommonProcessEntity.AuditType auditType, User user, String desc) {
        if (applyForLevel == null) {
            return Pair.of(Boolean.FALSE, "操作失败, 申请等级不能为空!");
        }

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectName(CUSTOMER_APPLY_LEVEL_OBJECT_NAME);
        commonProcessEntity.setObjectId(String.valueOf(id));
        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
        if (CollectionUtils.isEmpty(list) || list.size() != 1) {
            return Pair.of(Boolean.FALSE, "操作失败, 申请等级数据异常,请联系技术人员!");
        }

        if (!applyForLevel.equals(Integer.valueOf(list.get(0).getType()))) {
            return Pair.of(Boolean.FALSE, "操作失败, 申请等级异常,请重试或联系技术人员!");
        }

        commonProcessEntity = list.get(0);
        commonProcessEntity.setBizStatus(auditType.getCode());
        commonProcessEntity.setCurrent(0);
        commonProcessEntity.setProcessor(String.valueOf(user.getId()));
        commonProcessEntity.setDescription(desc);
        commonProcessService.save(commonProcessEntity);
        switch (auditType) {
            case PASS:
                dao.updateOfficeType(id, applyForLevel);
                Office office = officeDao.get(id);
                User primaryPerson = office.getPrimaryPerson();
                UserUtils.clearCache(primaryPerson);
                primaryPerson = systemService.getUser(primaryPerson.getId());
                List<Role> roleList = primaryPerson.getRoleList();
                switch (OfficeTypeEnum.stateOf(applyForLevel.toString())) {
                                case CUSTOMER:
                                    roleList.add(systemService.getRole(PURCHASERSPEOPLE));
                                    roleList.removeIf(role -> role.getId().equals(SHOPKEEPER) || role.getId().equals(COMMISSION_MERCHANT));
                                    if (ConfigGeneral.SYSTEM_CONFIG.get().getCustomerUpgradeMessageId() != null && ConfigGeneral.SYSTEM_CONFIG.get().getCustomerUpgradeMessageId() != 0) {
                                        BizMessageUser bizMessageUser = new BizMessageUser();
                                        bizMessageUser.setUser(primaryPerson);
                                        bizMessageUser.setBizStatus("0");
                                        bizMessageUser.setMessageId(ConfigGeneral.SYSTEM_CONFIG.get().getCustomerUpgradeMessageId().toString());
                                        bizMessageUserService.save(bizMessageUser);
                                    }
                                    break;
                                case COMMISSION_MERCHANT:
                                    roleList.add(systemService.getRole(COMMISSION_MERCHANT));
                                    roleList.removeIf(role -> role.getId().equals(PURCHASERSPEOPLE) || role.getId().equals(SHOPKEEPER));
                                    if (ConfigGeneral.SYSTEM_CONFIG.get().getCommissionMerchantUpgradeMessageId() != null && ConfigGeneral.SYSTEM_CONFIG.get().getCommissionMerchantUpgradeMessageId() != 0) {
                                        BizMessageUser bizMessageUser = new BizMessageUser();
                                        bizMessageUser.setUser(primaryPerson);
                                        bizMessageUser.setBizStatus("0");
                                        bizMessageUser.setMessageId(ConfigGeneral.SYSTEM_CONFIG.get().getCommissionMerchantUpgradeMessageId().toString());
                                        bizMessageUserService.save(bizMessageUser);
                                    }
                                    break;
                                default:
                                    break;
                            }
                systemService.saveUser(primaryPerson);
                break;
            case REJECT:
                break;
            default:
                break;
        }

        //审核成功时，清空申请用户的购物车
        User applyUser = new User();
        Office office = officeDao.get(id);
        applyUser.setOffice(office);
        List<User> userList = userDao.findList(applyUser);
        if (CollectionUtils.isNotEmpty(userList)) {
            Integer userId = userList.get(0).getId();
            User currentUser = UserUtils.getUser();
            //删除购物车数据
            bizShopCartService.updateShopCartByUserId(Integer.valueOf(0), currentUser.getId(), userId);
            //删除购物车中间表数据
            bizShopCartService.updateCartSkuByUserId(Integer.valueOf(0), currentUser.getId(), userId);
        }
        return Pair.of(Boolean.TRUE, "操作成功!");

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> officeTypeApply(Office office, OfficeLevelApplyDto officeLevelApplyDto) {
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectName(CUSTOMER_APPLY_LEVEL_OBJECT_NAME);
        commonProcessEntity.setObjectId(String.valueOf(office.getId()));
        commonProcessEntity.setCurrent(1);

        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
        if (CollectionUtils.isNotEmpty(list)) {
            return Pair.of(Boolean.FALSE, "重复申请!");
        }

        commonProcessEntity.setType(officeLevelApplyDto.getApplyLevel().toString());
        commonProcessService.save(commonProcessEntity);

        BizCustomerInfo bizCustomerInfo = office.getBizCustomerInfo();
        if (bizCustomerInfo == null) {
            bizCustomerInfo = new BizCustomerInfo();
        }

        bizCustomerInfo.setOfficeId(office.getId());
        bizCustomerInfo.setBankName(officeLevelApplyDto.getDepositBank());
        bizCustomerInfo.setPayee(officeLevelApplyDto.getRealName());
        bizCustomerInfo.setCardNumber(officeLevelApplyDto.getBankCardNumber());
        bizCustomerInfo.setIdCardNumber(officeLevelApplyDto.getIdCardNumber());
        bizCustomerInfoService.save(bizCustomerInfo);

        return Pair.of(Boolean.TRUE, "操作成功!");

    }

    public List<Office> queryTreeList(String type, String source) {
        List<Office> list = null;
        if (StringUtils.isNotBlank(type)) {
            String defType = type;
            String[] split = type.split(",");
            if (ArrayUtils.isNotEmpty(split)) {
                defType = split[0];
            }
            if (source != null && source.equals("officeConnIndex")) {
                //属于客户专员查询采购中心方法
                list = this.CustomerfilerOffice(null, source, OfficeTypeEnum.stateOf(defType));
            } else {
                if (ArrayUtils.isNotEmpty(split) && split.length > 1) {
                    list = this.findListByTypeList(Arrays.asList(split));
                }else {
                    list = this.filerOffice(null, source, OfficeTypeEnum.stateOf(defType));
                }
            }
        }

        return list;
    }
}
