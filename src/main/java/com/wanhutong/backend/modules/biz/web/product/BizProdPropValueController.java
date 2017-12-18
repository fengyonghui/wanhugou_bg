/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropValueService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.List;
import java.util.Map;

/**
 * 记录产品所有属性值Controller
 * @author zx
 * @version 2017-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/bizProdPropValue")
public class BizProdPropValueController extends BaseController {

	@Autowired
	private BizProdPropValueService bizProdPropValueService;
	@Autowired
	private BizProductInfoService bizProductInfoService;
	
	@ModelAttribute
	public BizProdPropValue get(@RequestParam(required=false) Integer id) {
		BizProdPropValue entity = null;
		if (id!=null){
			entity = bizProdPropValueService.get(id);
		}
		if (entity == null){
			entity = new BizProdPropValue();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:product:bizProdPropValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProdPropValue bizProdPropValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProdPropValue> page = bizProdPropValueService.findPage(new Page<BizProdPropValue>(request, response), bizProdPropValue); 
		model.addAttribute("page", page);
		return "modules/biz/product/bizProdPropValueList";
	}
	@ResponseBody
	@RequiresPermissions("biz:product:bizProdPropValue:view")
	@RequestMapping(value = "findList")
	public Map<String,List<BizProdPropValue>> findList(BizProdPropValue bizProdPropValue,String source,Integer prodId) {
		BizProductInfo bizProductInfo=bizProductInfoService.get(prodId);
		BizProdPropertyInfo bizProdPropertyInfo=new BizProdPropertyInfo();
		bizProdPropertyInfo.setProductInfo(bizProductInfo);
		bizProdPropValue.setProdPropertyInfo(bizProdPropertyInfo);
		bizProdPropValue.setSource(source);
		Map<String,List<BizProdPropValue>> map=bizProdPropValueService.findListMap(bizProdPropValue);
		return map;
	}

	@RequiresPermissions("biz:product:bizProdPropValue:view")
	@RequestMapping(value = "form")
	public String form(BizProdPropValue bizProdPropValue, Model model) {
		model.addAttribute("bizProdPropValue", bizProdPropValue);
		return "modules/biz/product/bizProdPropValueForm";
	}

	@RequiresPermissions("biz:product:bizProdPropValue:edit")
	@RequestMapping(value = "save")
	public String save(BizProdPropValue bizProdPropValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProdPropValue)){
			return form(bizProdPropValue, model);
		}
		bizProdPropValueService.save(bizProdPropValue);
		addMessage(redirectAttributes, "保存记录产品所有属性值成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdPropValue/?repage";
	}
	
	@RequiresPermissions("biz:product:bizProdPropValue:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProdPropValue bizProdPropValue, RedirectAttributes redirectAttributes) {
		bizProdPropValueService.delete(bizProdPropValue);
		addMessage(redirectAttributes, "删除记录产品所有属性值成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdPropValue/?repage";
	}

}