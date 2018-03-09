/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;

import java.util.Date;
import java.util.List;

/**
 * 供货记录表Controller
 * @author 张腾飞
 * @version 2018-01-03
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizSendGoodsRecord")
public class BizSendGoodsRecordController extends BaseController {

	@Autowired
	private BizSendGoodsRecordService bizSendGoodsRecordService;
    @Autowired
    private BizInventorySkuService bizInventorySkuService;

	@ModelAttribute
	public BizSendGoodsRecord get(@RequestParam(required=false) Integer id) {
		BizSendGoodsRecord entity = null;
		if (id!=null){
			entity = bizSendGoodsRecordService.get(id);
		}
		if (entity == null){
			entity = new BizSendGoodsRecord();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(Integer bizStatu, BizSendGoodsRecord bizSendGoodsRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
//		if ("0".equals(bizStatu)){
			bizSendGoodsRecord.setBizStatus(bizStatu);
//		}
//		if ("1".equals(bizStatu)){
//			bizSendGoodsRecord.setBizStatus(1);
//		}
		Page<BizSendGoodsRecord> page = bizSendGoodsRecordService.findPage(new Page<BizSendGoodsRecord>(request, response), bizSendGoodsRecord);
		model.addAttribute("page", page);
		model.addAttribute("bizStatus",bizStatu);
		return "modules/biz/inventory/bizSendGoodsRecordList";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:view")
	@RequestMapping(value = "form")
	public String form(BizSendGoodsRecord bizSendGoodsRecord, Model model) {

		model.addAttribute("bizSendGoodsRecord", bizSendGoodsRecord);
		return "modules/biz/request/bizRequestHeaderKcForm";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping(value = "save")
	public String save(BizSendGoodsRecord bizSendGoodsRecord,String source,String bizStatu,String ship, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, bizSendGoodsRecord)){
//			return form(bizSendGoodsRecord, model);
//		}
//      点击确认供货 增加库存销售订单数量
        /*BizInventorySku inventorySku = new BizInventorySku();//库存
        List<BizSendGoodsRecord> sendGoodsRecordList = bizSendGoodsRecord.getBizSendGoodsRecordList();//用于判断订单下有没有商品
        if(sendGoodsRecordList!=null && sendGoodsRecordList.size()!=0){
            int i=1;
            for (BizSendGoodsRecord oodsRecord : sendGoodsRecordList) {
                if(oodsRecord.getBizOrderDetail().getSentQty()!=null && oodsRecord.getBizOrderDetail().getSentQty()==0){//订单中的商品供货数量
                    inventorySku.setSkuInfo(oodsRecord.getSkuInfo());
                    List<BizInventorySku> list = bizInventorySkuService.findList(inventorySku);
                    if(list!=null && list.size()!=0){
                        for (BizInventorySku bizInventorySku : list) {
                            inventorySku.setId(bizInventorySku.getId());
                            inventorySku.setStockOrdQty(bizInventorySku.getStockOrdQty()+i);
                            bizInventorySkuService.orderSave(inventorySku);
//                            break;
                        }
                    }
//                    break;
                }
            }
        }*/
        bizSendGoodsRecordService.save(bizSendGoodsRecord);
			addMessage(redirectAttributes, "保存供货记录成功");
//			return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizSendGoodsRecord/?repage&bizStatu="+bizSendGoodsRecord.getBizStatus();
//		跳回订单发货列表
		return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestAll?source="+source+"&bizStatu="+bizStatu+"&ship="+ship;
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSendGoodsRecord bizSendGoodsRecord, RedirectAttributes redirectAttributes) {
		bizSendGoodsRecordService.delete(bizSendGoodsRecord);
		addMessage(redirectAttributes, "删除供货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSendGoodsRecord/?repage";
	}

}