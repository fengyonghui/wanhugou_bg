/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.*;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.custom.BizCustomCenterConsultantDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;

import javax.annotation.Resource;

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
    @Autowired
    private OfficeDao officeDao;
    @Autowired
    private BizCustCreditService bizCustCreditService;
    @Autowired
    private BizCustomCenterConsultantDao bizCustomCenterConsultantDao;
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

    public String PHOTO_SPLIT_CHAR = "\\|";


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
    @Transactional(readOnly = false)
    public void save(Office office) {
        super.save(office);
        //保存供应商经营品类
        if (office.getBizVendInfo() != null && office.getBizVendInfo().getBizCategoryInfo().getId() != null) {
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
            bizVendInfo.setCode(office.getCode());
            bizVendInfo.setAuditStatus(BizVendInfo.AuditStatus.UNAUDITED.getStatus());
            bizVendInfoService.save(bizVendInfo);
        }

        if (office.getBizVendInfo() != null && StringUtils.isNotBlank(office.getBizVendInfo().getCompactPhotos())) {
            saveImg(office.getBizVendInfo().getCompactPhotos(), office.getId(), ImgEnum.VEND_COMPACT);
        }

        if (office.getBizVendInfo() != null && StringUtils.isNotBlank(office.getBizVendInfo().getIdCardPhotos())) {
            saveImg(office.getBizVendInfo().getIdCardPhotos(), office.getId(), ImgEnum.VEND_IDENTITY_CARD);
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
//		if(bizCustCredit.getId()== null){
//			if(bizCustCredit != null ){
////				bizCustCredit.setId(office.getId());
//				bizCustCredit.setCustomer(office);
//				bizCustCredit.setPayPwd(SystemService.entryptPassword(DictUtils.getDictValue("密码", "payment_password", "")));
//				bizCustCredit.setuVersion(1);
//				bizCustCredit.setCustFalg("officeCust");
//				bizCustCreditService.save(bizCustCredit);
//			}
//		}else{
//			bizCustCreditService.save(bizCustCredit);
//		}
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
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
                || user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) || user.getCompany().getType().equals(OfficeTypeEnum.NETWORKSUPPLY.getType())) {
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
                    !String.valueOf(10).equals(office1.getType()) && !String.valueOf(11).equals(office1.getType())) {
                iterator.remove();   //注意这个地方
            }
        }

        return offices;

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
}
