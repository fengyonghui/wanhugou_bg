/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

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
import com.wanhutong.backend.modules.biz.entity.product.BizProdViewLog;
import com.wanhutong.backend.modules.biz.service.product.BizProdViewLogService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品查看日志Controller
 * @author zx
 * @version 2018-02-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/bizProdViewLog")
public class BizProdViewLogController extends BaseController {

	@Autowired
	private BizProdViewLogService bizProdViewLogService;
	
	@ModelAttribute
	public BizProdViewLog get(@RequestParam(required=false) Integer id) {
		BizProdViewLog entity = null;
		if (id!=null){
			entity = bizProdViewLogService.get(id);
		}
		if (entity == null){
			entity = new BizProdViewLog();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:product:bizProdViewLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProdViewLog bizProdViewLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProdViewLog> page = bizProdViewLogService.findPage(new Page<BizProdViewLog>(request, response), bizProdViewLog); 
		model.addAttribute("page", page);
		return "modules/biz/product/bizProdViewLogList";
	}

	@RequiresPermissions("biz:product:bizProdViewLog:view")
	@RequestMapping(value = "form")
	public String form(BizProdViewLog bizProdViewLog, Model model) {
		model.addAttribute("bizProdViewLog", bizProdViewLog);
		return "modules/biz/product/bizProdViewLogForm";
	}

	@RequiresPermissions("biz:product:bizProdViewLog:edit")
	@RequestMapping(value = "save")
	public String save(BizProdViewLog bizProdViewLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProdViewLog)){
			return form(bizProdViewLog, model);
		}
		bizProdViewLogService.save(bizProdViewLog);
		addMessage(redirectAttributes, "保存日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdViewLog/?repage";
	}
	
	@RequiresPermissions("biz:product:bizProdViewLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProdViewLog bizProdViewLog, RedirectAttributes redirectAttributes) {
		bizProdViewLogService.delete(bizProdViewLog);
		addMessage(redirectAttributes, "删除日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdViewLog/?repage";
	}

	/**
	 * 导出
	 * */
	@RequiresPermissions("biz:product:bizProdViewLog:view")
	@RequestMapping(value = "prodExprot")
	public String prodExprot(BizProdViewLog bizProdViewLog, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String fileName = "产品查看日志" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<BizProdViewLog> list = bizProdViewLogService.findList(bizProdViewLog);
			ArrayList<List<String>> header = Lists.newArrayList();
			list.forEach(order->{
				ArrayList<String> headerList = Lists.newArrayList();
				if(order.getOpShelfInfo()!=null && order.getOpShelfInfo().getName()!=null){
					headerList.add(String.valueOf(order.getOpShelfInfo().getName()));
				}else{
					headerList.add("");
				}
				if(order.getCenter()!=null){
					if(order.getCenter().getId()==0){
						headerList.add("平台商品");
					}else if(order.getCenter().getName()!=null){
						headerList.add(String.valueOf(order.getCenter().getName()));
					}else{headerList.add("");}
				}else{
					headerList.add("");
				}
				if(order.getProductInfo()!=null && order.getProductInfo().getName()!=null){
					headerList.add(String.valueOf(order.getProductInfo().getName()));
				}else{
					headerList.add("");
				}
				if(order.getUser()!=null && order.getUser().getName()!=null){
					headerList.add(String.valueOf(order.getUser().getName()));
				}else{
					headerList.add("");
				}
				headerList.add(String.valueOf(sdf.format(order.getCreateDate())));
				header.add(headerList);
			});
			String[] headerArr = {"货架", "采购中心", "产品名称", "用户", "创建时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "产品查看日志", headerArr, header, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出产品查看日志数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdViewLog/?repage";
	}
}