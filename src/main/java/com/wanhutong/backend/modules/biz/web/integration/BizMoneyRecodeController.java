/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.integration;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import com.wanhutong.backend.modules.biz.service.integration.BizMoneyRecodeService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 积分流水Controller
 * @author LX
 * @version 2018-09-16
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/integration/bizMoneyRecode")
public class BizMoneyRecodeController extends BaseController {

	@Autowired
	private BizMoneyRecodeService bizMoneyRecodeService;
	private static Logger LOGGER = LoggerFactory.getLogger(BizIntegrationActivityController.class);


	@ModelAttribute
	public BizMoneyRecode get(@RequestParam(required=false) Integer id) {
		BizMoneyRecode entity = null;
		if (id!=null){
			entity = bizMoneyRecodeService.get(id);
		}
		if (entity == null){
			entity = new BizMoneyRecode();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:integration:bizMoneyRecode:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizMoneyRecode bizMoneyRecode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizMoneyRecode> page = bizMoneyRecodeService.findPage(new Page<BizMoneyRecode>(request, response), bizMoneyRecode);
		model.addAttribute("page", page);
		return "modules/biz/integration/bizMoneyRecodeList";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = {"rules", ""})
	public String rules(BizIntegrationActivity bizIntegrationActivity, Model model){
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		return "modules/biz/integration/bizIntegrationRules";
	}


	@RequestMapping(value = {"detail", ""})
	@ResponseBody
	public BizMoneyRecodeDetail recodeDetail() {
		BizMoneyRecodeDetail bizMoneyRecodeDetail = bizMoneyRecodeService.selectRecordDetail();
		return bizMoneyRecodeDetail;
	}

	/*
	*  活动列表 导出
	* */
	@RequestMapping(value = "recodeExport")
	public String recodeExport(BizMoneyRecode bizMoneyRecode, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		// Page<BizMoneyRecode> page = bizMoneyRecodeService.findPage(new Page<BizMoneyRecode>(request, response), bizMoneyRecode);
		// List<BizMoneyRecode> list = page.getList();
		List<BizMoneyRecode> list = bizMoneyRecodeService.findList(bizMoneyRecode);
		//列表数据
		List<List<String>> data = Lists.newArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if(CollectionUtils.isNotEmpty(list))
			{
				for (BizMoneyRecode biz:list) {
                    List<String> recodeList = Lists.newArrayList();
				    recodeList.add(biz.getId()== null ? "未知" : biz.getId().toString());
					recodeList.add(biz.getOffice().getName()== null ? "未知" : biz.getOffice().getName());
					recodeList.add(biz.getOffice().getMaster()==null ? "未知" : biz.getOffice().getMaster());
					recodeList.add(biz.getOffice().getPhone()==null ? "未知" : biz.getOffice().getPhone());
					recodeList.add(biz.getMoney()== null ? "未知" : biz.getMoney().toString());
					recodeList.add(biz.getStatusName()== null ? "未知" : biz.getStatusName().toString());
					recodeList.add(biz.getComment()== null ? "未知" : biz.getComment().toString());
					recodeList.add(biz.getCreateDate()== null ? "未知" : sdf.format(biz.getCreateDate()).toString());
                    data.add(recodeList);
				}
				String headers[] = {"流水id","经销店名称","负责人","负责人电话","流水数量","流水类型","流水说明","生成时间"};
				ExportExcelUtils eeu = new ExportExcelUtils();
				SXSSFWorkbook workbook = new SXSSFWorkbook();
				String fileName = "万户币流水数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				eeu.exportExcel(workbook, 0, "万户币流水数据", headers, data, fileName);
				response.reset();
				response.setContentType("application/octet-stream; charset=utf-8");
				response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
				workbook.write(response.getOutputStream());
				workbook.dispose();
				return null;
			}
			return null;
		} catch (Exception e) {
			LOGGER.error("流水数据导出失败！");
		}
		return "redirect:" + Global.getAdminPath() + "/biz/integration/bizMoneyRecode/list";
	}




}