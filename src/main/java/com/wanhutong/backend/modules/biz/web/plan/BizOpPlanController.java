/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;
import com.wanhutong.backend.modules.biz.service.plan.BizOpPlanService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 运营计划Controller
 * @author 张腾飞
 * @version 2018-03-15
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/plan/bizOpPlan")
public class BizOpPlanController extends BaseController {

	@Autowired
	private BizOpPlanService bizOpPlanService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private BizCustomCenterConsultantService bizCustomCenterConsultantService;
	
	@ModelAttribute
	public BizOpPlan get(@RequestParam(required=false) Integer id) {
		BizOpPlan entity = null;
		if (id!=null){
			entity = bizOpPlanService.get(id);
		}
		if (entity == null){
			entity = new BizOpPlan();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:plan:bizOpPlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpPlan bizOpPlan, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpPlan> page = bizOpPlanService.findPage(new Page<BizOpPlan>(request, response), bizOpPlan);
		model.addAttribute("page", page);
		return "modules/biz/plan/bizOpPlanList";
	}

	@RequiresPermissions("biz:plan:bizOpPlan:view")
	@RequestMapping(value = "form")
	public String form(BizOpPlan bizOpPlan, Model model) {
		BizOpPlan opPlan =null;
		String a="sys_office";
		String b="sys_user";
		if(bizOpPlan.getId()!=null){
			opPlan = bizOpPlanService.get(bizOpPlan);
		}
		if (opPlan != null) {
			if (opPlan.getObjectName() != null && opPlan.getObjectName().equals(a)) {
				Office office = officeService.get(Integer.parseInt(bizOpPlan.getObjectId()));
				bizOpPlan.setObjectName1(office.getName());
				bizOpPlan.setObjectName2(null);
			} else if (opPlan.getObjectName() != null && opPlan.getObjectName().equals(b)) {
				BizCustomCenterConsultant bizCustomCenterConsultant = new BizCustomCenterConsultant();
				User user1 = new User();
				User user = systemService.getUser(Integer.parseInt(bizOpPlan.getObjectId()));
				user1.setId(user.getId());
				bizCustomCenterConsultant.setConsultants(user1);
				List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(bizCustomCenterConsultant);
				if(list.size()!=0){
					for (BizCustomCenterConsultant customCenterConsultant : list) {
						bizOpPlan.setObjectId(String.valueOf(customCenterConsultant.getCenters().getId()));
						bizOpPlan.setObjectName1(customCenterConsultant.getCenters().getName());
						break;
					}
				}else{
					bizOpPlan.setObjectName1(null);
				}
				bizOpPlan.setUser(user1);
				bizOpPlan.setObjectName2(user.getName());
			}
		}
		model.addAttribute("bizOpPlan", bizOpPlan);
		return "modules/biz/plan/bizOpPlanForm";
	}

	@RequiresPermissions("biz:plan:bizOpPlan:edit")
	@RequestMapping(value = "save")
	public String save(BizOpPlan bizOpPlan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOpPlan)){
			return form(bizOpPlan, model);
		}
		bizOpPlanService.save(bizOpPlan);
		addMessage(redirectAttributes, "保存运营计划成功");
		return "redirect:"+Global.getAdminPath()+"/biz/plan/bizOpPlan/?repage";
	}
	
	@RequiresPermissions("biz:plan:bizOpPlan:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpPlan bizOpPlan, RedirectAttributes redirectAttributes) {
		bizOpPlanService.delete(bizOpPlan);
		addMessage(redirectAttributes, "删除运营计划成功");
		return "redirect:"+Global.getAdminPath()+"/biz/plan/bizOpPlan/?repage";
	}

	@RequiresPermissions("biz:plan:bizOpPlan:view")
	@RequestMapping(value = "planExprot")
	public String planExprot(BizOpPlan bizOpPlan, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			String fileName = "运营计划" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<BizOpPlan> list = bizOpPlanService.findList(bizOpPlan);
			ArrayList<List<String>> header = Lists.newArrayList();
			list.forEach(order->{
				ArrayList<String> headerList = Lists.newArrayList();
				if(order.getObjectName().indexOf("sys_office")!=-1){
					Office office = officeService.get(Integer.parseInt(order.getObjectId()));
					headerList.add(String.valueOf(office==null?"":office.getName()));
				}else if(order.getObjectName().indexOf("sys_user")!=-1){
					User user = systemService.getUser(Integer.parseInt(order.getObjectId()));
					headerList.add(String.valueOf(user==null?"":user.getName()));
				}else{
					headerList.add("");
				}
				headerList.add(String.valueOf(order.getYear()==null?"":order.getYear()));
				headerList.add(String.valueOf(order.getMonth()==null?"":order.getMonth()));
				headerList.add(String.valueOf(order.getDay()==null?"":order.getDay()));
				headerList.add(String.valueOf(order.getAmount()==null?"":order.getAmount()));
				headerList.add(String.valueOf(sdf.format(order.getCreateDate())));
				header.add(headerList);
			});
			String[] headerArr = {"名称", "年", "月", "日", "总额", "创建时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "运营计划", headerArr, header, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出运营计划数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/biz/plan/bizOpPlan/?repage";
	}
}