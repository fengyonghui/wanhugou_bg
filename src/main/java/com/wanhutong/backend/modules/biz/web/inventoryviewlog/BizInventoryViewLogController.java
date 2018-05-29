/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventoryviewlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.service.DictService;
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
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 库存盘点记录Controller
 * @author zx
 * @version 2018-03-27
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventoryviewlog/bizInventoryViewLog")
public class BizInventoryViewLogController extends BaseController {

	@Autowired
	private BizInventoryViewLogService bizInventoryViewLogService;
	@Autowired
	private DictService dictService;

	@ModelAttribute
	public BizInventoryViewLog get(@RequestParam(required=false) Integer id) {
		BizInventoryViewLog entity = null;
		if (id!=null){
			entity = bizInventoryViewLogService.get(id);
		}
		if (entity == null){
			entity = new BizInventoryViewLog();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInventoryViewLog bizInventoryViewLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInventoryViewLog> page = bizInventoryViewLogService.findPage(new Page<BizInventoryViewLog>(request, response), bizInventoryViewLog); 
		model.addAttribute("page", page);
		return "modules/biz/inventoryviewlog/bizInventoryViewLogList";
	}

	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:view")
	@RequestMapping(value = "form")
	public String form(BizInventoryViewLog bizInventoryViewLog, Model model) {
		model.addAttribute("bizInventoryViewLog", bizInventoryViewLog);
		return "modules/biz/inventoryviewlog/bizInventoryViewLogForm";
	}

	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:edit")
	@RequestMapping(value = "save")
	public String save(BizInventoryViewLog bizInventoryViewLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInventoryViewLog)){
			return form(bizInventoryViewLog, model);
		}
		bizInventoryViewLogService.save(bizInventoryViewLog);
		addMessage(redirectAttributes, "保存库存盘点记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventoryviewlog/bizInventoryViewLog/?repage";
	}
	
	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInventoryViewLog bizInventoryViewLog, RedirectAttributes redirectAttributes) {
		bizInventoryViewLogService.delete(bizInventoryViewLog);
		addMessage(redirectAttributes, "删除库存盘点记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventoryviewlog/bizInventoryViewLog/?repage";
	}

	/**
	 * 导出
	 * */
	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:view")
	@RequestMapping(value = "listExport")
	public String listExport(BizInventoryViewLog bizInventoryViewLog, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String fileName = "库存盘点记录" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<BizInventoryViewLog> list = bizInventoryViewLogService.findList(bizInventoryViewLog);
			ArrayList<List<String>> header = Lists.newArrayList();
			list.forEach(order->{
				ArrayList<String> headerList = Lists.newArrayList();
				if(order.getInvInfo()!=null && order.getInvInfo().getName()!=null){
					headerList.add(String.valueOf(order.getInvInfo().getName()));
				}else{headerList.add("");}

				Dict dictInv = new Dict();
				dictInv.setDescription("库存中SKU类型");
				dictInv.setType("inv_type");
				List<Dict> dictListInv = dictService.findList(dictInv);
				if(dictListInv.size()!=0) {
					for (Dict dinv : dictListInv) {
						if (dinv.getValue().equals(String.valueOf(order.getInvType()))) {
							//类型
							headerList.add(String.valueOf(dinv.getLabel()));
							break;
						}
					}
				}else{headerList.add("");}
				if(order.getSkuInfo()!=null && order.getSkuInfo().getName()!=null){
					headerList.add(String.valueOf(order.getSkuInfo().getName()));
				}else{headerList.add("");}
				headerList.add(String.valueOf(order.getStockQty()==null?"":order.getStockQty()));
				headerList.add(String.valueOf(order.getNowStockQty()==null?"":order.getNowStockQty()));
				headerList.add(String.valueOf(order.getStockChangeQty()==null?"":order.getStockChangeQty()));
				headerList.add(String.valueOf(order.getCreateBy().getName()));
				headerList.add(String.valueOf(sdf.format(order.getCreateDate())));
				header.add(headerList);
			});
			String[] headerArr = {"仓库", "库存类型", "商品", "原库存数量", "现在库存数量","改变数量", "创建人", "创建时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "库存盘点记录", headerArr, header, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出库存盘点记录数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/biz/inventoryviewlog/bizInventoryViewLog/";
	}

}