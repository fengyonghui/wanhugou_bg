/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.modules.biz.entity.dto.BizOpShelfSkus;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.sys.entity.Office;
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
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;

import java.util.List;

/**
 * 商品上架管理Controller
 * @author liuying
 * @version 2017-12-19
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizOpShelfSku")
public class BizOpShelfSkuController extends BaseController {

	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private BizProductInfoService bizProductInfoService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	
	@ModelAttribute
	public BizOpShelfSku get(@RequestParam(required=false) Integer id) {
		BizOpShelfSku entity = null;
		if (id!=null){
			entity = bizOpShelfSkuService.get(id);
		}
		if (entity == null){
			entity = new BizOpShelfSku();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpShelfSku bizOpShelfSku, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpShelfSku> page = bizOpShelfSkuService.findPage(new Page<BizOpShelfSku>(request, response), bizOpShelfSku); 
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizOpShelfSkuList";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = "form")
	public String form(BizOpShelfSkus bizOpShelfSku, Model model) {
		if (bizOpShelfSku != null && bizOpShelfSku.getId() != null){
            BizOpShelfSku bizOpShelfSku1 = bizOpShelfSkuService.get(bizOpShelfSku.getId());
            model.addAttribute("bizOpShelfSku",bizOpShelfSku1);
        }else {
            model.addAttribute("bizOpShelfSku", bizOpShelfSku);
        }
		model.addAttribute("bizSkuInfo", new BizSkuInfo());
		return "modules/biz/shelf/bizOpShelfSkuForm";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "save")
	public String save(BizOpShelfSkus bizOpShelfSkus, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOpShelfSkus)){
			return form(bizOpShelfSkus, model);
		}
		String skuIds=bizOpShelfSkus.getSkuInfoIds();
		String[] skuIdArr=skuIds.split(",");
		String[] maxQtyArr=bizOpShelfSkus.getMaxQtys().split(",");
		String[] minQtyArr=bizOpShelfSkus.getMinQtys().split(",");
		String[] orgPriceArr=bizOpShelfSkus.getOrgPrices().split(",");
		String[] priorityArr=bizOpShelfSkus.getPrioritys().split(",");
		String[] salePriceArr=bizOpShelfSkus.getSalePrices().split(",");
		String[] shelfQtyArr=bizOpShelfSkus.getShelfQtys().split(",");
		String[] shelfTimeArr=bizOpShelfSkus.getShelfTimes().split(",");
		String[] unShelfTimeArr=bizOpShelfSkus.getUnshelfTimes().split(",");
		BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
		for(int i=0;i<skuIdArr.length;i++){
			bizOpShelfSku.setId(null);
			BizSkuInfo bizSkuInfo=bizSkuInfoService.get(Integer.parseInt(skuIdArr[i].trim()));
			bizOpShelfSku.setSkuInfo(bizSkuInfo);
			bizOpShelfSku.setProductInfo(bizSkuInfo.getProductInfo());
			bizOpShelfSku.setCenterOffice(bizOpShelfSkus.getCenterOffice());
			bizOpShelfSku.setOpShelfInfo(bizOpShelfSkus.getOpShelfInfo());
			bizOpShelfSku.setMaxQty(Integer.parseInt(maxQtyArr[i].trim()));
			bizOpShelfSku.setMinQty(Integer.parseInt(minQtyArr[i].trim()));
			bizOpShelfSku.setOrgPrice(Double.parseDouble(orgPriceArr[i].trim()));
			bizOpShelfSku.setPriority(Integer.parseInt(priorityArr[i].trim()));
			bizOpShelfSku.setSalePrice(Double.parseDouble(salePriceArr[i].trim()));
			bizOpShelfSku.setShelfQty(Integer.parseInt(shelfQtyArr[i].trim()));
			bizOpShelfSku.setShelfTime(DateUtils.parseDate(shelfTimeArr[i].trim()));
			bizOpShelfSku.setUnshelfTime(DateUtils.parseDate(unShelfTimeArr[i].trim()));
			bizOpShelfSkuService.save(bizOpShelfSku);

		}
//		if (skuIds!=null && !"".equals(skuIds)){
//			String[] ids = skuIds.split(",".trim());
//			for(int i = 0; i < ids.length; i++){
//                BizSkuInfo bizSkuInfo = new BizSkuInfo();
//                bizSkuInfo.setId(Integer.parseInt(ids[i]));
//                bizOpShelfSku.setSkuInfo(bizSkuInfo);
//                bizOpShelfSkuService.save(bizOpShelfSku);
//            }
//        }
		addMessage(redirectAttributes, "保存商品上架成功");
		if(bizOpShelfSkus.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/form?id=" + bizOpShelfSkus.getOpShelfInfo().getId() ;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpShelfSku bizOpShelfSku, RedirectAttributes redirectAttributes) {
		bizOpShelfSkuService.delete(bizOpShelfSku);
		addMessage(redirectAttributes, "删除商品上架成功");
		if(bizOpShelfSku.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"//biz/shelf/bizOpShelfInfo/form?id="+bizOpShelfSku.getOpShelfInfo().getId();
	}
	}

