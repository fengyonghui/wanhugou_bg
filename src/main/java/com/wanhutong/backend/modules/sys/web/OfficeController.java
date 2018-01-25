/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
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
import com.wanhutong.backend.modules.sys.entity.BizCustCredit;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.BizCustCreditService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;

/**
 * 机构Controller
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
	
	@ModelAttribute("office")
	public Office get(@RequestParam(required=false) Integer id) {
		if (id!=null){
			return officeService.get(id);
		}else{
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
	public String purchasersList(Office office, String conn,Integer centers,Integer consultants, Model model) {
		if(office.getId() == null || office.getParentIds() == null){
			String purchasersId = DictUtils.getDictValue("部门", "sys_office_purchaserId","");
			Office off = officeService.get(Integer.valueOf(purchasersId));
			office.setParentIds("%,"+purchasersId+",");
			List<Office> findList = officeService.findList(office);
			findList.add(off);
			model.addAttribute("list", findList);
		}else{
			model.addAttribute("list", officeService.findList(office));
		}
//		System.out.println(conn);
//		if(conn.equals("connIndex")){
//			//TODO 客户专员管理页面跳转没解决：添加客户专员跳转，关联采购商跳转，修改跳转
////			跳回客户专员管理
//		System.out.println(centers);//必填
//		System.out.println(consultants);//必填
//			return "redirect:" + adminPath + "/biz/custom/bizCustomCenterConsultant/returnConnIndex?centers.id="+centers+"&consultants.id="+consultants;
//		}
		return "modules/sys/purchasersList";
//		return "redirect:" + adminPath + "/biz/custom/bizCustomCenterConsultant/returnConnIndex?centers.id="+centers+"&consultants.id="+consultants;
	}
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "purchasersForm")
	public String purchasersForm(Office office, Model model) {
		User user = UserUtils.getUser();
		if (office.getParent()==null || office.getParent().getId()==null){
			office.setParent(user.getOffice());
		}
		office.setParent(officeService.get(office.getParent().getId()));
		if (office.getArea()==null){
			office.setArea(user.getOffice().getArea());
		}
		// 自动获取排序号
		if (office.getId()==null&&office.getParent()!=null){
			int size = 0;
			List<Office> list = officeService.findAll();
			for (int i=0; i<list.size(); i++){
				Office e = list.get(i);
				if (e.getParent()!=null && e.getParent().getId()!=null
						&& e.getParent().getId().equals(office.getParent().getId())){
					size++;
				}
			}
			office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size+1 : 1), 3, "0"));
		}
		BizCustCredit bizCustCredit = bizCustCreditService.get(office.getId());
		if(bizCustCredit != null){
			office.setLevel(bizCustCredit.getLevel());
		}
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
	public String supplierList(Office office,Model model) {
		if(office.getId() == null || office.getParentIds() == null){
			String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId","");
			Office off = officeService.get(Integer.valueOf(supplierId));
			office.setParentIds("%,"+supplierId+",");
			List<Office> findList = officeService.findList(office);
			findList.add(off);
			model.addAttribute("list", findList);
		}else{
			model.addAttribute("list", officeService.findList(office));
		}
		return "modules/sys/supplierList";
	}
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "supplierForm")
	public String supplierForm(Office office, Model model) {
		User user = UserUtils.getUser();
		if (office.getParent()==null || office.getParent().getId()==null){
			office.setParent(user.getOffice());
		}
		office.setParent(officeService.get(office.getParent().getId()));
		if (office.getArea()==null){
			office.setArea(user.getOffice().getArea());
		}
		// 自动获取排序号
		if (office.getId()==null&&office.getParent()!=null){
			int size = 0;
			List<Office> list = officeService.findAll();
			for (int i=0; i<list.size(); i++){
				Office e = list.get(i);
				if (e.getParent()!=null && e.getParent().getId()!=null
						&& e.getParent().getId().equals(office.getParent().getId())){
					size++;
				}
			}
			office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size+1 : 1), 3, "0"));
		}
		model.addAttribute("office", office);
		return "modules/sys/supplierForm";
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "purchaserTreeData")
	public List<Map<String, Object>> purchaserTreeData(@RequestParam(required=false) String extId, @RequestParam(required=false) String type,
			@RequestParam(required=false) Long grade, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		String purchasersId = DictUtils.getDictValue("部门", "sys_office_purchaserId","");
		Office office = new Office();
		office.setParentIds("%,"+purchasersId+",");
		List<Office> list = officeService.findList(office);
		Office off = officeService.get(Integer.valueOf(purchasersId));
		list.add(off);
		for (int i=0; i<list.size(); i++){
			Office e = list.get(i);
			if ((StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1))
					&& (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
					&& (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
					&& Global.YES.equals(e.getUseable())){
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
	public List<Map<String, Object>> supplierTreeData(@RequestParam(required=false) String extId, @RequestParam(required=false) String type,
			@RequestParam(required=false) Long grade, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		String supplierId = DictUtils.getDictValue("部门", "sys_office_supplierId","");
		Office office = new Office();
		office.setParentIds("%,"+supplierId+",");
		List<Office> list = officeService.findList(office);
		Office off = officeService.get(Integer.valueOf(supplierId));
		list.add(off);
		for (int i=0; i<list.size(); i++){
			Office e = list.get(i);
			if ((StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1))
					&& (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
					&& (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
					&& Global.YES.equals(e.getUseable())){
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
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/office/";
		}
		if (!beanValidator(model, office)){
			return form(office, model);
		}
		BizCustCredit bizCustCredit = new BizCustCredit();
		bizCustCredit.setLevel(office.getLevel());
		bizCustCredit.setOfficeId(office.getId());
		officeService.save(office,bizCustCredit);
		if(office.getChildDeptList()!=null){
			Office childOffice = null;
			for(String id : office.getChildDeptList()){
				childOffice = new Office();
				childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
				childOffice.setParent(office);
				childOffice.setArea(office.getArea());
				childOffice.setType("2");
				childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade())+1));
				childOffice.setUseable(Global.YES);
				officeService.save(childOffice);
			}
		}
		addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
		Integer id = office.getParentId()==0 ? null : office.getParentId();
		return "redirect:" + adminPath + "/sys/office/purchasersList?id="+id+"&parentIds="+office.getParentIds();
	}
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = {"list"})
	public String list(Office office, Model model) {
        model.addAttribute("list", officeService.findList(office));
		return "modules/sys/officeList";
	}
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "form")
	public String form(Office office, Model model) {
		User user = UserUtils.getUser();
		if (office.getParent()==null || office.getParent().getId()==null){
			office.setParent(user.getOffice());
		}
		office.setParent(officeService.get(office.getParent().getId()));
		if (office.getArea()==null){
			office.setArea(user.getOffice().getArea());
		}
		// 自动获取排序号
		if (office.getId()==null&&office.getParent()!=null){
			int size = 0;
			List<Office> list = officeService.findAll();
			for (int i=0; i<list.size(); i++){
				Office e = list.get(i);
				if (e.getParent()!=null && e.getParent().getId()!=null
						&& e.getParent().getId().equals(office.getParent().getId())){
					size++;
				}
			}
			office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size+1 : 1), 3, "0"));
		}
		model.addAttribute("office", office);
		return "modules/sys/officeForm";
	}
	
	@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "save")
	public String save(Office office, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/office/";
		}
		if (!beanValidator(model, office)){
			return form(office, model);
		}
		officeService.save(office);
		
		if(office.getChildDeptList()!=null){
			Office childOffice = null;
			for(String id : office.getChildDeptList()){
				childOffice = new Office();
				childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
				childOffice.setParent(office);
				childOffice.setArea(office.getArea());
				childOffice.setType("2");
				childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade())+1));
				childOffice.setUseable(Global.YES);
				officeService.save(childOffice);
			}
		}
		
		addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
		Integer id = office.getParentId()==0 ? null : office.getParentId();
		return "redirect:" + adminPath + "/sys/office/list?id="+id+"&parentIds="+office.getParentIds();
	}
	
	@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "delete")
	public String delete(Office office, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/office/list";
		}
//		if (Office.isRoot(id)){
//			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构或编号空");
//		}else{
			officeService.delete(office);
			addMessage(redirectAttributes, "删除机构成功");
//		}
		return "redirect:" + adminPath + "/sys/office/list?id="+office.getParentId()+"&parentIds="+office.getParentIds();
	}

	/**
	 * 获取机构JSON数据。
	 * @param extId 排除的ID
	 * @param type	类型（1：公司；2：部门/小组/其它：3：用户）
	 * @param grade 显示级别
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, @RequestParam(required=false) String type,
			@RequestParam(required=false) Long grade, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Office> list = officeService.findList(isAll);
		for (int i=0; i<list.size(); i++){
			Office e = list.get(i);
			if ((StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1))
					&& (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true))||(type != null && (type.equals("8") ? type.equals(e.getType()) : true)))
					&& (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
					&& Global.YES.equals(e.getUseable())){
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
	public List<Map<String, Object>> getImgTreeList(@RequestParam(required = false) String type,RedirectAttributes redirectAttributes) {
		List<Office> list = null;
		if(StringUtils.isNotBlank(type)){
			list = officeService.filerOffice(null,OfficeTypeEnum.stateOf(type));
		}
		if(list == null || list.size() == 0){
			addMessage(redirectAttributes, "列表不存在");
		}
		return convertList(list);
	}

	private List<Map<String, Object>> convertList(List<Office> list){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if(list != null && list.size() > 0 ){
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
}
