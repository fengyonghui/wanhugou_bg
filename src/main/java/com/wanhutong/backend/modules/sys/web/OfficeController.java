/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;

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
        String purchasersId = DictUtils.getDictValue("采购商", "sys_office_purchaserId", "");
        Office customer = new Office();
        if (office.getId() != null || office.getParentIds() != null) {
            customer.setParent(office);
        } else {
//            查所有
            customer.setParentIds("%," + purchasersId + ",%");
        }
        if (office.getMoblieMoeny() != null && !office.getMoblieMoeny().getMobile().equals("")) {
            customer.setMoblieMoeny(office.getMoblieMoeny());
        }
        Page<Office> page = officeService.findPage(new Page<Office>(request, response), customer);
        if (page.getList().size() == 0) {
            if (office.getQueryMemberGys() != null && office.getQueryMemberGys().equals("query") && !office.getMoblieMoeny().getMobile().equals("")) {
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
//		if(conn.equals("connIndex")){
//			//TODO 客户专员管理页面跳转没解决：添加客户专员跳转，关联采购商跳转，修改跳转
////			跳回客户专员管理
//		System.out.println(centers);//必填
//		System.out.println(consultants);//必填
//			return "redirect:" + adminPath + "/biz/custom/bizCustomCenterConsultant/returnConnIndex?centers.id="+centers+"&consultants.id="+consultants;
//		}
        return "modules/sys/purchasersList";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "purchasersForm")
    public String purchasersForm(Office office, Model model) {
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
        model.addAttribute("office", office);
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
//		List<Map<String, Object>> mapList = Lists.newArrayList();
//		String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId","");
//		Office office = new Office();
//		office.setParentIds("%,"+supplierId+",");
//		List<Office> list = officeService.findList(office);
//		Office off = officeService.get(Integer.valueOf(supplierId));
//		list.add(off);
        List<Map<String, Object>> mapList = Lists.newArrayList();
//        List<Office> list = officeService.filerOffice(null, "supplier", OfficeTypeEnum.VENDOR);

        String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId", "");
        List<Office> list = officeService.findVendor(supplierId);
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

    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "purchaserSave")
    public String purchaserSave(Office office, Model model, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/";
        }
        if (!beanValidator(model, office)) {
            return form(office, model, null, null);
        }
//        BizCustCredit bizCustCredit = new BizCustCredit();
//        bizCustCredit.setLevel(office.getLevel());
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
        Integer id = office.getParentId() == 0 ? null : office.getParentId();
        if(office.getSource()!=null && office.getSource().equals("chatRecordSave")){
            return "redirect:" + adminPath + "/biz/chat/bizChatRecord/form?office.id="+office.getId()+"&office.name="+office.getName();
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
        if (office.getId() != null) {
            vendor.setParent(office);
        } else {
//            查所有
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
}
