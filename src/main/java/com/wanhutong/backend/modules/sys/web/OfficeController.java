/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.supcan.treelist.cols.Col;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.chat.BizChatRecordService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomerInfoService;
import com.wanhutong.backend.modules.biz.service.message.BizMessageInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV2Service;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.BuyerAdviser;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.BuyerAdviserService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wanhutong.backend.modules.sys.service.OfficeService.CUSTOMER_APPLY_LEVEL_OBJECT_NAME;

/**
 * 机构Controller
 *
 * @author ThinkGem
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

    @Autowired
    private OfficeService officeService;
    @Autowired
    private BizCustCreditService bizCustCreditService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private SysOfficeAddressService sysOfficeAddressService;
    @Autowired
    private BizVarietyInfoService bizVarietyInfoService;
    @Autowired
    private BizVendInfoService bizVendInfoService;
    @Autowired
    private BizCustomerInfoService bizCustomerInfoService;
    @Autowired
    private DictService dictService;
    @Autowired
    private BuyerAdviserService buyerAdviserService;
    @Autowired
    private BizChatRecordService bizChatRecordService;
    @Autowired
    private BizProductInfoV2Service bizProductInfoV2Service;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private BizMessageInfoService bizMessageInfoService;
    @Autowired
    private UserDao userDao;


    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) Integer id) {
        if (id != null) {
            Office office = officeService.get(id);
            List<CommonImg> compactImgList = officeService.getImgList(id, ImgEnum.VEND_COMPACT);
            List<CommonImg> idCardImgList = officeService.getImgList(id, ImgEnum.VEND_IDENTITY_CARD);
            StringBuilder compactSb = new StringBuilder();
            StringBuilder idCardSb = new StringBuilder();
            compactImgList.forEach(o -> compactSb.append(o.getImgServer().concat(o.getImgPath())).append("|"));
            idCardImgList.forEach(o -> idCardSb.append(o.getImgServer().concat(o.getImgPath())).append("|"));
            if (office.getBizVendInfo() == null) {
                office.setBizVendInfo(new BizVendInfo());
            }
            office.getBizVendInfo().setCompactPhotos(compactSb.toString());
            office.getBizVendInfo().setIdCardPhotos(idCardSb.toString());
            return office;
        } else {
            return new Office();
        }
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = {""})
    public String index(Office office, Model model) {
//        model.addAttribute("list", officeService.findAll());
        return "modules/sys/officeIndex";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "purchasersIndex")
    public String purchasersIndex(Office office, Model model) {
        return "modules/sys/purchasersIndex";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "purchasersList")
    public String purchasersList(Office office, String conn, Integer centers, Integer consultants, HttpServletRequest request, HttpServletResponse response, Model model) {
        Office customer = new Office();

        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (!user.isAdmin() && roleList.contains(role)) {
            customer.setVendorId(user.getCompany().getId());
            customer.setVendor("vendor");
            model.addAttribute("vendor","vendor");
        }


        String purchasersId = DictUtils.getDictValue("采购商", "sys_office_purchaserId", "");
        if (office.getParent()!=null && office.getParent().getId()!=null && office.getParent().getId()!=0) {
            customer.setParent(office);
        } else {
            customer.setParentIds("%," + purchasersId + ",%");
        }
        if (office.getMoblieMoeny() != null && !office.getMoblieMoeny().getMobile().equals("")) {
            customer.setMoblieMoeny(office.getMoblieMoeny());
        }
        Page<Office> page = officeService.findPage(new Page<Office>(request, response), customer);
        if (page.getList().size() == 0) {
            if (office.getQueryMemberGys() != null && office.getQueryMemberGys().equals("query") && office.getMoblieMoeny() != null && !office.getMoblieMoeny().getMobile().equals("")) {
                //列表页输入2个条件查询时
                Office officeUser = new Office();
                officeUser.setQueryMemberGys(office.getName()+"");
                officeUser.setMoblieMoeny(office.getMoblieMoeny());
                page = officeService.findPage(new Page<Office>(request, response), officeUser);
            } else {
                //当点击子节点显示
                page.getList().add(officeService.get(office.getId()));
            }
        }
        model.addAttribute("page", page);
        return "modules/sys/purchasersList";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "purchasersForm")
    public String purchasersForm(Office office, Model model, String option) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            if (OfficeTypeEnum.CUSTOMER.getType().equals(office.getType())) {
                office.setType(office.getType());
                office.setParent(officeService.get(0));
            } else {
                office.setParent(user.getOffice());
            }

        }
        if (office.getParent() != null) {
            office.setParent(officeService.get(office.getParent().getId()));
        }

        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        // 自动获取排序号
        if (office.getId() == null && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
//        BizCustCredit bizCustCredit = bizCustCreditService.get(office.getId());
//        String b="0";
//        if (bizCustCredit != null && !bizCustCredit.getDelFlag().equals(b)) {
//            office.setLevel(bizCustCredit.getLevel());
//        }

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectName(CUSTOMER_APPLY_LEVEL_OBJECT_NAME);
        commonProcessEntity.setObjectId(String.valueOf(office.getId()));
        List<CommonProcessEntity> processList = commonProcessService.findList(commonProcessEntity);

        model.addAttribute("office", office);
        model.addAttribute("option", option);
        model.addAttribute("processList", processList);
        return "modules/sys/purchasersForm";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "supplierIndex")
    public String supplierIndex(Office office, Model model) {
        return "modules/sys/supplierIndex";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "supplierList")
    public String supplierList(Office office, Model model) {
        if (office.getId() == null || office.getParentIds() == null) {
            String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId", "");
            Office off = officeService.get(Integer.valueOf(supplierId));
            office.setParentIds("%," + supplierId + ",");
            List<Office> findList = officeService.findList(office);
            findList.add(off);
            model.addAttribute("list", findList);
        } else {
            model.addAttribute("list", officeService.findList(office));
        }
        return "modules/sys/supplierList";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "supplierForm")
    public String supplierForm(Office office, Model model, String gysFlag) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(office.getId());
        commonImg.setObjectName(ImgEnum.VENDOR_VIDEO.getTableName());
        if (office.getId() != null) {
            commonImg.setImgType(ImgEnum.VENDOR_VIDEO.getCode());
            List<CommonImg> vendVideoList = commonImgService.findList(commonImg);
            if (CollectionUtils.isNotEmpty(vendVideoList)) {
                model.addAttribute("vendVideoList",vendVideoList);
            }
        }
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            if (OfficeTypeEnum.VENDOR.getType().equals(office.getType())) {
                office.setType(office.getType());
                office.setParent(officeService.get(0));
            } else {
                office.setParent(user.getOffice());
            }
        }
        if(office.getParent()!=null){
            office.setParent(officeService.get(office.getParent().getId()));
        }
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        // 自动获取排序号
        if (office.getId() == null && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        //地址回显
        SysOfficeAddress sysOfficeAddress = new SysOfficeAddress();
        sysOfficeAddress.setOffice(office);
        List<SysOfficeAddress> list = sysOfficeAddressService.findList(sysOfficeAddress);
        if(list.size()!=0 && office.getId()!=null){
            for (SysOfficeAddress add : list) {
                if(add.getDeFaultStatus()!=null && add.getDeFaultStatus()==1){
                    sysOfficeAddress.setBizLocation(add.getBizLocation());
                    model.addAttribute("entity", sysOfficeAddress);
                    break;
                }
            }
        }
        if (office.getId()!=null && office.getType().equals(OfficeTypeEnum.VENDOR.getType())) {
            BizVendInfo bizVendInfo = bizVendInfoService.get(office.getId());
            office.setBizVendInfo(bizVendInfo);
        }
        //查询所有商品品类
        model.addAttribute("varietyList",bizVarietyInfoService.findList(new BizVarietyInfo()));
        model.addAttribute("office", office);
        model.addAttribute("gysFlag", gysFlag);
        return "modules/sys/supplierForm";
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "purchaserTreeData")
    public List<Map<String, Object>> purchaserTreeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {



        List<Map<String, Object>> mapList = Lists.newArrayList();

        List<Office> list = officeService.filerOffice(null, "purchaser", OfficeTypeEnum.CUSTOMER);

        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (roleList.contains(role)) {
            List<String> custParentIdByVendorId = officeService.getCustParentIdByVendorId(user.getCompany().getId());
            List<String> custIdByVendorId = officeService.getCustIdByVendorId(user.getCompany().getId());
            List<Office> temp = Lists.newArrayList();
            for (Office o1 : list) {
                for (String o : custParentIdByVendorId) {
                    if (o.contains(String.valueOf(o1.getId()))) {
                        temp.add(o1);
                    }
                }
                for (String o : custIdByVendorId) {
                    if (o.equals(String.valueOf(o1.getId()))) {
                        temp.add(o1);
                    }
                }
            }
            list = temp;
        }

        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }


        return mapList;
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "supplierTreeData")
    public List<Map<String, Object>> supplierTreeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type,
                                                      @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.filerOffice(null, "supplier", OfficeTypeEnum.VENDOR);

//        String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId", "");
//        List<Office> list = officeService.findVendor(supplierId);
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }

    @RequiresPermissions("sys:office:upgradeAudit")
    @RequestMapping(value = "upgradeAudit")
    public String upgradeAudit(RedirectAttributes redirectAttributes, int id, int applyForLevel, int auditType, String desc) {
        Pair<Boolean, String> result = officeService.upgradeAudit(id, applyForLevel, CommonProcessEntity.AuditType.parse(auditType), UserUtils.getUser(), desc);

        String titleName = "";
        if (Integer.valueOf(OfficeTypeEnum.CUSTOMER.getType()) == applyForLevel) {
            titleName = "采购商";
        } else if (Integer.valueOf(OfficeTypeEnum.COMMISSION_MERCHANT.getType()) == applyForLevel){
            titleName = "代销商";
        }

        if (result.getLeft()) {
            addMessage(redirectAttributes, "审核成功!");

            //自动发送站内信
            String title = titleName + "审核通过";
            String content = "您好，恭喜您，您的" + titleName + "申请审核通过";
            saveMessageInfo(id, title, content);
        }else {
            addMessage(redirectAttributes, "审核失败!");

            String title = titleName + "审核不通过";
            String content = "您好，恭喜您，您的" + titleName + "申请审核不通过，原因是:" + desc;
            saveMessageInfo(id, title, content);
        }
        return "redirect:" + adminPath + "/sys/office/purchasersList";
    }

    //审核代销商，采购商完成后，自动发送站内信
    public void saveMessageInfo(Integer id, String title, String content) {
        User user = new User();
        Office office = officeService.get(id);
        user.setOffice(office);
        List<User> userList = userDao.findUserByOfficeId(user);
        if (CollectionUtils.isNotEmpty(userList)) {
            Integer uderId = userList.get(0).getId();
            bizMessageInfoService.autoSendMessageInfo(title, content, uderId, "upgrade");
        }
    }

    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "purchaserSave")
    public String purchaserSave(Office office, Model model, RedirectAttributes redirectAttributes, String option) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/";
        }
        if (!beanValidator(model, office)) {
            return form(office, model, null, null);
        }

        officeService.save(office, null);
        if (office.getChildDeptList() != null) {
            Office childOffice = null;
            for (String id : office.getChildDeptList()) {
                childOffice = new Office();
                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
                childOffice.setParent(office);
                childOffice.setArea(office.getArea());
                childOffice.setType("2");
                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                childOffice.setUseable(Global.YES);
                officeService.save(childOffice);
            }
        }
        addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");

        if ("upgrade".equals(option)) {
            CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
            commonProcessEntity.setObjectName(CUSTOMER_APPLY_LEVEL_OBJECT_NAME);
            commonProcessEntity.setObjectId(String.valueOf(office.getId()));
            commonProcessEntity.setCurrent(1);
            commonProcessEntity.setType(office.getCommonProcess().getType());
            commonProcessService.save(commonProcessEntity);
            addMessage(redirectAttributes, "申请成功!");
        }

        if(office.getSource()!=null && office.getSource().equals("chatRecordSave")){
            try {
                return "redirect:" + adminPath + "/biz/chat/bizChatRecord/form?office.id="+office.getId()+"&office.name="+
                        URLEncoder.encode(office.getName(),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "redirect:" + adminPath + "/sys/office/purchasersList";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = {"list"})
    public String list(Office office, Model model) {
        model.addAttribute("list", officeService.findList(office));
        return "modules/sys/officeList";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "form")
    public String form(Office office, Model model, String flag, String gysFlag) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(officeService.get(office.getParent().getId()));
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        // 自动获取排序号
        if (office.getId() == null && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        if (office.getPrimaryPerson() != null) {
            Office primaryPerson = officeService.get(office.getPrimaryPerson().getId());
            primaryPerson.setPrimaryPerson(primaryPerson.getPrimaryPerson());
            model.addAttribute("office1", primaryPerson);//页面显示主要负责人
        }
        if (office.getPrimaryPerson() != null) {
            office.setPrimaryPerson(systemService.getUser(office.getPrimaryPerson().getId()));
        }
        if (flag != null && !"".equals(flag)) {
            model.addAttribute("flag", flag);
        }
        if (office.getId()!=null && office.getType().equals(OfficeTypeEnum.VENDOR.getType())) {
            BizVendInfo bizVendInfo = bizVendInfoService.get(office.getId());
            office.setBizVendInfo(bizVendInfo);
        }
        model.addAttribute("gysFlag", gysFlag);
        model.addAttribute("office", office);
        return "modules/sys/officeForm";
    }

    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "save")
    public String save(Office office, Model model, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/";
        }
        if (!beanValidator(model, office)) {
            return form(office, model, null, null);
        }
        officeService.save(office);
        //保存钱包
        String purchasersId = DictUtils.getDictValue("经销店", "sys_office_type", "");
        if (StringUtils.isNotBlank(purchasersId) && purchasersId.equals(office.getType())) {
            BizCustCredit bizCustCredit = new BizCustCredit();
            bizCustCredit = new BizCustCredit();
            bizCustCredit.setCustomer(office);
            bizCustCredit.setLevel(StringUtils.isBlank(office.getLevel()) ? "1" : office.getLevel());
            bizCustCreditService.save(bizCustCredit);
        }


        if (office.getChildDeptList() != null) {
            Office childOffice = null;
            for (String id : office.getChildDeptList()) {
                childOffice = new Office();
                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
                childOffice.setParent(office);
                childOffice.setArea(office.getArea());
                childOffice.setType("2");
                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                childOffice.setUseable(Global.YES);
                officeService.save(childOffice);
            }
        }

        addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
        Integer id = office.getParentId() == 0 ? null : office.getParentId();
        if(office.getSource()!=null && office.getSource().equals("contact_ck")){
            //跳回会员搜索
            return "redirect:" + adminPath + "/sys/user/contact";
        }
        if(office.getGysFlag()!=null && office.getGysFlag().equals("chatRecordSave")){
            //跳回沟通记录
            try {
                return "redirect:" + adminPath + "/biz/chat/bizChatRecord/form?office.id="+office.getId()+"&office.name="+
                        URLEncoder.encode(office.getName(),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (office.getGysFlag() != null && office.getGysFlag().equals("gys_save")) {
            //供应商保存、修改跳转
            return "redirect:" + adminPath + "/sys/office/supplierListGys";
        } else {
            return "redirect:" + adminPath + "/sys/office/list?id=" + id + "&parentIds=" + office.getParentIds();
        }

    }

    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "delete")
    public String delete(Office office, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/list";
        }
//		if (Office.isRoot(id)){
//			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构或编号空");
//		}else{
        if (office.getGysFlag() != null && office.getGysFlag().equals("gys_delete")) {
            BizProductInfo bizProductInfo = new BizProductInfo();
            bizProductInfo.setOffice(office);
            List<BizProductInfo> productInfoList = bizProductInfoV2Service.findList(bizProductInfo);
            if (CollectionUtils.isNotEmpty(productInfoList)) {
                addMessage(redirectAttributes,"该供应商下有产品，请修改产品所属的供应商后再进行删除");
                return "redirect:" + adminPath + "/sys/office/supplierListGys";
            }
        }
        office.setDelFlag(Office.DEL_FLAG_DELETE);
        officeService.delete(office);
        addMessage(redirectAttributes, "删除机构成功");
//		}
        if (office.getSource() != null && office.getSource().equals("purchListDelete")) {
            //会员列表删除跳转
            return "redirect:" + adminPath + "/sys/office/purchasersList";
        }
        if (office.getGysFlag() != null && office.getGysFlag().equals("gys_delete")) {
            //供应商列表删除跳转
            return "redirect:" + adminPath + "/sys/office/supplierListGys";
        }
        return "redirect:" + adminPath + "/sys/office/list?id=" + office.getParentId() + "&parentIds=" + office.getParentIds();
    }
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "recovery")
    public String recovery(Office office, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/list";
        }
//		if (Office.isRoot(id)){
//			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构或编号空");
//		}else{
        office.setDelFlag(Office.DEL_FLAG_NORMAL);
        officeService.delete(office);
        addMessage(redirectAttributes, "恢复机构成功");
//		}
        return "redirect:" + adminPath + "/sys/office/list?id=" + office.getParentId() + "&parentIds=" + office.getParentIds();
    }

    /**
     * 获取机构JSON数据。
     *
     * @param extId    排除的ID
     * @param type     类型（1：公司；2：部门/小组/其它：3：用户）
     * @param grade    显示级别
     * @param response
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type,
                                              @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.findList(isAll);
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
//				if (type != null && "3".equals(type)){
//					map.put("isParent", true);
//				}
                mapList.add(map);
            }
        }
        return mapList;
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "queryTreeList")
    public List<Map<String, Object>> getImgTreeList(@RequestParam(required = false) String type, String source, RedirectAttributes redirectAttributes) {
        List<Office> list = null;
        if (StringUtils.isNotBlank(type)) {
            String defType = type;
            String[] split = type.split(",");
            if (ArrayUtils.isNotEmpty(split)) {
                defType = split[0];
            }
            if (source != null && source.equals("officeConnIndex")) {
                //属于客户专员查询采购中心方法
                list = officeService.CustomerfilerOffice(null, source, OfficeTypeEnum.stateOf(defType));
            } else {
                if (ArrayUtils.isNotEmpty(split) && split.length > 1) {
                    list = officeService.findListByTypeList(Arrays.asList(split));
                }else {
                    list = officeService.filerOffice(null, source, OfficeTypeEnum.stateOf(defType));
                }
            }
        }
        if (list == null || list.size() == 0) {
            addMessage(redirectAttributes, "列表不存在");
        }
        return convertList(list);
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "queryTreeListByPhone")
    public List<Map<String, Object>> queryTreeListByPhone(@RequestParam(required = false) String type, String source, RedirectAttributes redirectAttributes) {
        List<Office> list = null;
        if (StringUtils.isNotBlank(type)) {
            String defType = type;
            String[] split = type.split(",");
            if (ArrayUtils.isNotEmpty(split)) {
                defType = split[0];
            }
            if (source != null && source.equals("officeConnIndex")) {
                //属于客户专员查询采购中心方法
                list = officeService.customerfilerOffice4mobile(null, source, OfficeTypeEnum.stateOf(defType));
            } else {
                if (ArrayUtils.isNotEmpty(split) && split.length > 1) {
                    list = officeService.findListByTypeList(Arrays.asList(split));
                }else {
                    list = officeService.filerOffice4mobile(null, source, OfficeTypeEnum.stateOf(defType));
                }
            }
        }
        if (list == null || list.size() == 0) {
            addMessage(redirectAttributes, "列表不存在");
        }
        return convertList(list);
    }

    private List<Map<String, Object>> convertList(List<Office> list) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("type", e.getType());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 供应商列表分页查询
     */
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "supplierListGys")
    public String supplierListGys(Office office, HttpServletRequest request, HttpServletResponse response, Model model) {
        String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId", "");
        Office vendor = new Office();
        if (office.getParent()!=null && office.getParent().getId()!=null && office.getParent().getId()!=0) {
            vendor.setParent(office);
        } else {
            vendor.setParentIds("%," + supplierId + ",%");
        }
        if (office.getMoblieMoeny() != null && !office.getMoblieMoeny().getMobile().equals("")) {
            //输入手机号查询
            vendor.setMoblieMoeny(office.getMoblieMoeny());
        }
        Page<Office> page = officeService.findPage(new Page<Office>(request, response), vendor);
        if (page.getList().size() == 0) {
            if (office.getQueryMemberGys() != null && !office.getMoblieMoeny().getMobile().equals("") && office.getQueryMemberGys().equals("query")) {
                //列表页输入2个条件查询时
                Office officeUser = new Office();
                officeUser.setQueryMemberGys(office.getName()+"");
                officeUser.setMoblieMoeny(office.getMoblieMoeny());
                page = officeService.findPage(new Page<Office>(request, response), officeUser);
            } else {
                //当点击子节点显示
                page.getList().add(officeService.get(office.getId()));
            }
        }
        model.addAttribute("page", page);
        return "modules/sys/supplierList";
    }

    /**
     * 用于查询采购中心
     * */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "selectTreeList")
    public List<Map<String, Object>> selectTreeList(@RequestParam(required = false) String type, String source, RedirectAttributes redirectAttributes) {
        List<Office> list = null;
        if (StringUtils.isNotBlank(type)) {
            //属于查询采购中心
            list = officeService.CustomerfilerOffice(null, source, OfficeTypeEnum.stateOf(type));
        }
        if (CollectionUtils.isEmpty(list)) {
            addMessage(redirectAttributes, "列表不存在");
        }
        return convertList(list);
    }

    /**
     * 供应商审核状态修改
     * @param id 供应商ID
     * @param status 审核状态
     * @return 操作结果
     */
    @RequestMapping(value = "auditSupplier")
    @RequiresPermissions("sys:supplier:audit")
    public String auditSupplier(int id, int status, RedirectAttributes redirectAttributes) {
        Pair<Boolean, String> result = officeService.auditSupplier(id, status);
        addMessage(redirectAttributes, result.getRight());
        return "redirect:" + adminPath + "/sys/office/supplierListGys";
    }

    /**
     * 会员数据 导出
     * */
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "exportOffice")
    public String exportOffice(Office office,RedirectAttributes redirectAttributes, HttpServletResponse response) {
        String purchasersId = DictUtils.getDictValue("采购商", "sys_office_purchaserId", "");
        Office customer = new Office();
        if (office.getParent() != null && office.getParent().getId() != null && office.getParent().getId() != 0) {
            customer.setParent(office);
        } else {
            customer.setParentIds("%," + purchasersId + ",%");
        }
        if (office.getMoblieMoeny() != null && StringUtils.isNotBlank(office.getMoblieMoeny().getMobile())) {
            customer.setMoblieMoeny(office.getMoblieMoeny());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = "会员数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
        try {
            List<Office> meetingExprotList = officeService.findMeetingExprot(customer);
            if (CollectionUtils.isEmpty(meetingExprotList)) {
                if ("query".equals(office.getQueryMemberGys()) && StringUtils.isNotEmpty(office.getMoblieMoeny().getMobile())) {
                    //列表页输入2个条件查询时
                    Office officeUser = new Office();
                    officeUser.setQueryMemberGys(office.getName());
                    officeUser.setMoblieMoeny(office.getMoblieMoeny());
                    meetingExprotList = officeService.findMeetingExprot(officeUser);
                } else {
                    //当点击子节点显示
                    meetingExprotList.add(officeService.get(office.getId()));
                }
            }
            ArrayList<List<String>> heads = Lists.newArrayList();
            ArrayList<List<String>> chats = Lists.newArrayList();
            BizChatRecord chatRecord = new BizChatRecord();
            User userAdmin = UserUtils.getUser();
            if (!userAdmin.isAdmin()) {
                chatRecord.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
            }
            for (int i = 0; i < meetingExprotList.size(); i++) {
                ArrayList<String> headArr = Lists.newArrayList();
                headArr.add(meetingExprotList.get(i).getName() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getName());
                headArr.add(meetingExprotList.get(i).getCode() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getCode());
                if (meetingExprotList.get(i).getMoblieMoeny() != null && meetingExprotList.get(i).getMoblieMoeny().getMobile() != null) {
                    headArr.add(meetingExprotList.get(i).getMoblieMoeny().getMobile());
                } else {
                    headArr.add(StringUtils.EMPTY);
                }
                Dict dict = new Dict();
                dict.setDescription("机构类型");
                dict.setType("sys_office_type");
                String offtype = "";
                List<Dict> dictList = dictService.findList(dict);
                for (Dict dict1 : dictList) {
                    if (dict1.getValue().equals(meetingExprotList.get(i).getType())) {
                        offtype = dict1.getLabel();
                        break;
                    }
                }
                if (StringUtils.isNotEmpty(offtype)) {
                    headArr.add(offtype);
                } else {
                    headArr.add(StringUtils.EMPTY);
                }
                Dict dictUse = new Dict();
                dictUse.setDescription("是/否");
                dictUse.setType("yes_no");
                String yesNo = "";
                List<Dict> dictUseList = dictService.findList(dictUse);
                for (Dict dict1 : dictUseList) {
                    if (dict1.getValue().equals(meetingExprotList.get(i).getUseable())) {
                        yesNo = dict1.getLabel();
                        break;
                    }
                }
                if (StringUtils.isNotEmpty(yesNo)) {
                    headArr.add(yesNo);
                } else {
                    headArr.add(StringUtils.EMPTY);
                }
                if (meetingExprotList.get(i).getPrimaryPerson() != null && meetingExprotList.get(i).getPrimaryPerson().getName() != null) {
                    headArr.add(meetingExprotList.get(i).getPrimaryPerson().getName());
                } else {
                    headArr.add(StringUtils.EMPTY);
                }
                if (meetingExprotList.get(i).getDeputyPerson() != null && meetingExprotList.get(i).getDeputyPerson().getName() != null) {
                    headArr.add(meetingExprotList.get(i).getDeputyPerson().getName());
                } else {
                    headArr.add(StringUtils.EMPTY);
                }
                headArr.add(meetingExprotList.get(i).getAddress() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getAddress());
                headArr.add(meetingExprotList.get(i).getZipCode() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getZipCode());
                headArr.add(meetingExprotList.get(i).getMaster() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getMaster());
                headArr.add(meetingExprotList.get(i).getPhone() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getPhone());
                headArr.add(meetingExprotList.get(i).getFax() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getFax());
                headArr.add(meetingExprotList.get(i).getEmail() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getEmail());
                //所属采购中心，所属客户专员
                BuyerAdviser buyerAdviser = buyerAdviserService.get(meetingExprotList.get(i).getId());
                if (buyerAdviser!=null && !"0".equals(buyerAdviser.getStatus())) {
                    User user = systemService.getUser(buyerAdviser.getConsultantId());
                    if (user!=null && StringUtils.isNotEmpty(user.getName()) && !"0".equals(user.getDelFlag())) {
                        headArr.add(user.getCompany() == null ? StringUtils.EMPTY : (user.getCompany().getName() == null ? StringUtils.EMPTY : user.getCompany().getName()));
                        headArr.add(user.getName());
                    } else {
                        headArr.add(StringUtils.EMPTY);
                        headArr.add(StringUtils.EMPTY);
                    }
                } else {
                    headArr.add(StringUtils.EMPTY);
                    headArr.add(StringUtils.EMPTY);
                }
                heads.add(headArr);
                chatRecord.setOffice(meetingExprotList.get(i));
                List<BizChatRecord> chatList = bizChatRecordService.findList(chatRecord);
                for (int chat = 0; chat < chatList.size(); chat++) {
                    ArrayList<String> chatArr = Lists.newArrayList();
                    chatArr.add(meetingExprotList.get(i).getName() == null ? StringUtils.EMPTY : meetingExprotList.get(i).getName());
                    //客户专员或品类主管
                    if (chatList.get(chat).getUser() != null && chatList.get(chat).getUser().getName() != null) {
                        chatArr.add(chatList.get(chat).getUser().getName());
                    } else {
                        chatArr.add(StringUtils.EMPTY);
                    }
                    chatArr.add(chatList.get(chat).getChatRecord() == null ? StringUtils.EMPTY : chatList.get(chat).getChatRecord());
                    if (chatList.get(chat).getCreateBy() != null && chatList.get(chat).getCreateBy().getName() != null) {
                        chatArr.add(chatList.get(chat).getCreateBy().getName());
                    } else {
                        chatArr.add(StringUtils.EMPTY);
                    }
                    chatArr.add(String.valueOf(sdf.format(chatList.get(chat).getCreateDate())));
                    chats.add(chatArr);
                }
            }
            String[] orderArr = {"机构名称", "机构编码", "主负责人电话", "机构类型", "是否可用", "主负责人", "副负责人",
                    "联系地址", "邮政编码", "负责人", "电话", "传真", "邮箱", "所属采购中心", "所属客户专员"};
            String[] chatArr = {"机构名称", "品类主管或客户专员", "沟通记录", "创建人", "创建时间"};
            ExportExcelUtils exportUtils = new ExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            exportUtils.exportExcel(workbook, 0, "会员数据", orderArr, heads, fileName);
            exportUtils.exportExcel(workbook, 1, "沟通记录", chatArr, chats, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(redirectAttributes, "导出会员数据失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/office/purchasersList";
    }

}
