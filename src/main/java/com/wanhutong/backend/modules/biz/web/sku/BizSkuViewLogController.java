/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
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
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuViewLog;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuViewLogService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品出厂价日志表Controller
 * @author Oy
 * @version 2018-04-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizSkuViewLog")
public class BizSkuViewLogController extends BaseController {

	@Autowired
	private BizSkuViewLogService bizSkuViewLogService;
	
	@ModelAttribute
	public BizSkuViewLog get(@RequestParam(required=false) Integer id) {
		BizSkuViewLog entity = null;
		if (id!=null){
			entity = bizSkuViewLogService.get(id);
		}
		if (entity == null){
			entity = new BizSkuViewLog();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:sku:bizSkuViewLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuViewLog bizSkuViewLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuViewLog> page = bizSkuViewLogService.findPage(new Page<BizSkuViewLog>(request, response), bizSkuViewLog); 
		model.addAttribute("page", page);
		model.addAttribute("skuType",bizSkuViewLog.getSkuType());
		return "modules/biz/sku/bizSkuViewLogList";
	}

	@RequiresPermissions("biz:sku:bizSkuViewLog:view")
	@RequestMapping(value = "form")
	public String form(BizSkuViewLog bizSkuViewLog, Model model) {
		model.addAttribute("bizSkuViewLog", bizSkuViewLog);
		return "modules/biz/sku/bizSkuViewLogForm";
	}

	@RequiresPermissions("biz:sku:bizSkuViewLog:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuViewLog bizSkuViewLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuViewLog)){
			return form(bizSkuViewLog, model);
		}
		bizSkuViewLogService.save(bizSkuViewLog);
		addMessage(redirectAttributes, "保存出厂价日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuViewLog/?repage";
	}
	
	@RequiresPermissions("biz:sku:bizSkuViewLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuViewLog bizSkuViewLog, RedirectAttributes redirectAttributes) {
		bizSkuViewLogService.delete(bizSkuViewLog);
		addMessage(redirectAttributes, "删除出厂价日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuViewLog/?repage";
	}


	/**
	 * 导出
	 * */
	@RequiresPermissions("biz:sku:bizSkuViewLog:view")
	@RequestMapping(value = "skuExprot")
	public String skuExprot(BizSkuViewLog bizSkuViewLog, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			String fileName = "出厂价日志" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<BizSkuViewLog> list = bizSkuViewLogService.findList(bizSkuViewLog);
			ArrayList<List<String>> header = Lists.newArrayList();
			list.forEach(order->{
				ArrayList<String> headerList = Lists.newArrayList();
				if(order.getSkuInfo()!=null && order.getSkuInfo().getName()!=null){
					headerList.add(String.valueOf(order.getSkuInfo().getName()));
				}else {
					headerList.add("");
				}
				headerList.add(String.valueOf(order.getItemNo()==null?"":order.getItemNo()));
				headerList.add(String.valueOf(sdf.format(order.getUpdateDate())));
				headerList.add(String.valueOf(order.getUpdateBy().getName()==null?"":order.getUpdateBy().getName()));
				headerList.add(String.valueOf(order.getFrontBuyPrice()==null?"":order.getFrontBuyPrice()));
				headerList.add(String.valueOf(order.getAfterBuyPrice()==null?"":order.getAfterBuyPrice()));
				headerList.add(String.valueOf(order.getChangePrice()==null?"":order.getChangePrice()));
				headerList.add(String.valueOf(sdf.format(order.getCreateDate())));
				header.add(headerList);
			});
			String[] headerArr = {"商品名称", "商品货号", "商品修改时间", "商品修改人", "修改前结算价", "修改后结算价", "改变的价格", "创建时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "出厂价日志", headerArr, header, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出出厂价日志数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuViewLog/?repage";
	}

}