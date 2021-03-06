/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.integration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.web.sku.BizSkuInfoController;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.service.integration.BizIntegrationActivityService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * 积分活动Controller
 * @author LX
 * @version 2018-09-16
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/integration/bizIntegrationActivity")
public class BizIntegrationActivityController extends BaseController {

	@Autowired
	private BizIntegrationActivityService bizIntegrationActivityService;

	private static Logger LOGGER = LoggerFactory.getLogger(BizIntegrationActivityController.class);
	@Autowired
	private OfficeService officeService;

	@ModelAttribute
	public BizIntegrationActivity get(@RequestParam(required = false) Integer id) {
		BizIntegrationActivity entity = null;
		if (id != null) {
			entity = bizIntegrationActivityService.get(id);
			String officeIds = bizIntegrationActivityService.selectOfficeIdsByActivityId(id);
			if (StringUtils.isNotBlank(officeIds)) {
				entity.setOfficeIds(officeIds);
			}
		}
		if (entity == null) {
			entity = new BizIntegrationActivity();
		}
		return entity;
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizIntegrationActivity bizIntegrationActivity, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizIntegrationActivity> page = bizIntegrationActivityService.findPage(new Page<BizIntegrationActivity>(request, response), bizIntegrationActivity);
		model.addAttribute("page", page);
		return "modules/biz/integration/bizIntegrationActivityList";
	}


	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = "form")
	public String form(BizIntegrationActivity bizIntegrationActivity, Model model) {
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		List<Office> officeList = officeService.filerOffice(null, null, OfficeTypeEnum.CUSTOMER);
		model.addAttribute("officeList", officeList);
		return "modules/biz/integration/bizIntegrationActivityForm";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = "formA")
	public String formZhu(BizIntegrationActivity bizIntegrationActivity, Model model) {
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		return "modules/biz/integration/bizIntegrationActivityAForm";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = "formB")
	public String formZhi(BizIntegrationActivity bizIntegrationActivity, Model model) {
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		return "modules/biz/integration/bizIntegrationActivityBForm";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:edit")
	@RequestMapping(value = "save")
	public String save(BizIntegrationActivity bizIntegrationActivity, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizIntegrationActivity)) {
			return form(bizIntegrationActivity, model);
		}
		Integer id = bizIntegrationActivity.getId();
		if(!Objects.isNull(id))
		{
			BizIntegrationActivity activity = bizIntegrationActivityService.get(id);
			if(activity.getSendStatus()==1)
			{
				addMessage(redirectAttributes, "已发送的活动不可修改！");
				return "redirect:" + Global.getAdminPath() + "/biz/integration/bizIntegrationActivity/list";
			}
		}
		bizIntegrationActivityService.save(bizIntegrationActivity);
		String str = bizIntegrationActivity.getStr();
		if("zcs".equals(str))
		{
			addMessage(redirectAttributes, "注册送积分设置成功");
			return "redirect:" + Global.getAdminPath() + "/biz/integration/bizIntegrationActivity/formA";
		}
		if("zfs".equals(str))
		{
			addMessage(redirectAttributes, "支付送积分设置成功");
			return "redirect:" + Global.getAdminPath() + "/biz/integration/bizIntegrationActivity/formB";
		}
		if("rules".equals(str))
		{
			addMessage(redirectAttributes, "积分规则设置成功");
			return "redirect:" + Global.getAdminPath() + "/biz/integration/bizMoneyRecode/rules";
		}
		addMessage(redirectAttributes, "保存积分活动成功");
		return "redirect:" + Global.getAdminPath() + "/biz/integration/bizIntegrationActivity/?repage";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:edit")
	@RequestMapping(value = "delete")
	public String delete(BizIntegrationActivity bizIntegrationActivity, RedirectAttributes redirectAttributes) {
		bizIntegrationActivityService.delete(bizIntegrationActivity);
		addMessage(redirectAttributes, "删除积分活动成功");
		return "redirect:" + Global.getAdminPath() + "/biz/integration/bizIntegrationActivity/?repage";
	}

	@ResponseBody
	@RequestMapping("systemActivity")
	public BizIntegrationActivity getIntegrationByCode(@RequestParam("code") String code) {
		return bizIntegrationActivityService.getIntegrationByCode(code);
	}

	@ResponseBody
	@RequestMapping("count")
	public BizMoneyRecodeDetail countOrderUser() {
		return bizIntegrationActivityService.countTotal();
	}


    /**
	 * 活动参与者列表导出
	 *
	 * */
    @RequestMapping("activityOfficesExport")
	@ResponseBody
    public boolean findActivityOfficeList(Integer sendScope,@RequestParam(required = false)String officeIds,HttpServletRequest request,
										 HttpServletResponse response, RedirectAttributes redirectAttributes){
			List<Office> offices;
			switch(sendScope){
					case 0:
						   offices = bizIntegrationActivityService.findAllOffice();
						break;
					case -1:
						   offices = bizIntegrationActivityService.findOrderedOffice();
						break;
					case -2:
						   offices = bizIntegrationActivityService.findUnOrderOffice();
						break;
					default:
						   offices = bizIntegrationActivityService.findCheckedOffice(officeIds);
			}
			//列表数据
			List<List<String>> data = Lists.newArrayList();
	    	try {
			    if(CollectionUtils.isNotEmpty(offices)) {
					for (Office office : offices) {
						List<String> officeList = Lists.newArrayList();
						officeList.add(office.getId() == null ? "未知" : office.getId().toString());
						officeList.add(office.getName() == null ? "未知" : office.getName().toString());
						officeList.add(office.getPrimaryPerson()==null?"未知":office.getPrimaryPerson().getName() == null ? "未知" : office.getPrimaryPerson().getName());
						officeList.add(office.getPhone() == null ? "未知" : office.getPhone().toString());
						data.add(officeList);
					}
					String headers[] = {"经销店编号", "经销店名称", "负责人", "负责人电话"};
					ExportExcelUtils eeu = new ExportExcelUtils();
					SXSSFWorkbook workbook = new SXSSFWorkbook();
					String fileName = "活动参与者列表" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
					eeu.exportExcel(workbook, 0, "活动参与者列表", headers, data, fileName);
					response.reset();
					response.setContentType("application/octet-stream; charset=utf-8");
					response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
					workbook.write(response.getOutputStream());
					workbook.dispose();
				}
			return true;
		} catch (Exception e) {
	    		e.printStackTrace();
			LOGGER.error("数据导出失败！");
		}
		return true;
	}

    /**
	 * 指定用户动态加载
	 *
	 * */
     @ResponseBody
	 @RequestMapping("activity/special/offices")
	 public List<Office> findSpecialOffices(String officeIds){
		 List<Office> offices = bizIntegrationActivityService.findCheckedOffice(officeIds);
		 return offices;
	 }



}