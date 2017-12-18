/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuPropValueService;

import java.util.List;

/**
 * sku属性Controller
 * @author zx
 * @version 2017-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizSkuPropValue")
public class BizSkuPropValueController extends BaseController {

	@Autowired
	private BizSkuPropValueService bizSkuPropValueService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	
	@ModelAttribute
	public BizSkuPropValue get(@RequestParam(required=false) Integer id) {
		BizSkuPropValue entity = null;
		if (id!=null){
			entity = bizSkuPropValueService.get(id);
		}
		if (entity == null){
			entity = new BizSkuPropValue();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:sku:bizSkuPropValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuPropValue bizSkuPropValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuPropValue> page = bizSkuPropValueService.findPage(new Page<BizSkuPropValue>(request, response), bizSkuPropValue); 
		model.addAttribute("page", page);
		return "modules/biz/sku/bizSkuPropValueList";
	}

	@RequiresPermissions("biz:sku:bizSkuPropValue:view")
	@RequestMapping(value ="findList")
	@ResponseBody
	public List<BizSkuPropValue> findList(BizSkuPropValue bizSkuPropValue, Integer skuId) {
		bizSkuPropValue.setSkuInfo(bizSkuInfoService.get(skuId));
		List<BizSkuPropValue> list = bizSkuPropValueService.findList(bizSkuPropValue);
		return list;
	}

	@RequiresPermissions("biz:sku:bizSkuPropValue:view")
	@RequestMapping(value = "form")
	public String form(BizSkuPropValue bizSkuPropValue, Model model) {
		model.addAttribute("bizSkuPropValue", bizSkuPropValue);
		return "modules/biz/sku/bizSkuPropValueForm";
	}

	@RequiresPermissions("biz:sku:bizSkuPropValue:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuPropValue bizSkuPropValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuPropValue)){
			return form(bizSkuPropValue, model);
		}
		bizSkuPropValueService.save(bizSkuPropValue);
		addMessage(redirectAttributes, "保存sku属性成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuPropValue/?repage";
	}
	
	@RequiresPermissions("biz:sku:bizSkuPropValue:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuPropValue bizSkuPropValue, RedirectAttributes redirectAttributes) {
		bizSkuPropValueService.delete(bizSkuPropValue);
		addMessage(redirectAttributes, "删除sku属性成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuPropValue/?repage";
	}

}