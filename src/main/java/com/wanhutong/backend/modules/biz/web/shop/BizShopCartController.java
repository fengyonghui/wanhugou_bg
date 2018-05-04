/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shop;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopCart;
import com.wanhutong.backend.modules.biz.service.shop.BizShopCartService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品购物车Controller
 * @author OuyangXiutian
 * @version 2018-01-03
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shop/bizShopCart")
public class BizShopCartController extends BaseController {

	@Autowired
	private BizShopCartService bizShopCartService;
	
	@ModelAttribute
	public BizShopCart get(@RequestParam(required=false) Integer id) {
		BizShopCart entity = null;
		if (id!=null){
			entity = bizShopCartService.get(id);
		}
		if (entity == null){
			entity = new BizShopCart();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shop:bizShopCart:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizShopCart bizShopCart, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizShopCart> page = bizShopCartService.findPage(new Page<BizShopCart>(request, response), bizShopCart);
		model.addAttribute("page", page);
		return "modules/biz/shop/bizShopCartList";
	}

	@RequiresPermissions("biz:shop:bizShopCart:view")
	@RequestMapping(value = "form")
	public String form(BizShopCart bizShopCart, Model model) {
		model.addAttribute("bizShopCart", bizShopCart);
		return "modules/biz/shop/bizShopCartForm";
	}

	@RequiresPermissions("biz:shop:bizShopCart:edit")
	@RequestMapping(value = "save")
	public String save(BizShopCart bizShopCart, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizShopCart)){
			return form(bizShopCart, model);
		}
		bizShopCartService.save(bizShopCart);
		addMessage(redirectAttributes, "保存购物车成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopCart/?repage";
	}
	
	@RequiresPermissions("biz:shop:bizShopCart:edit")
	@RequestMapping(value = "delete")
	public String delete(BizShopCart bizShopCart, RedirectAttributes redirectAttributes) {
		bizShopCart.setDelFlag(BizShopCart.DEL_FLAG_DELETE);
		bizShopCartService.delete(bizShopCart);
		addMessage(redirectAttributes, "删除购物车成功");
		String a="cendShopDele";
		if(bizShopCart.getCendDele()!=null && bizShopCart.getCendDele().equals(a)){
			return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopCart/CendList?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopCart/?repage";
	}
	@RequiresPermissions("biz:shop:bizShopCart:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizShopCart bizShopCart, RedirectAttributes redirectAttributes) {
		bizShopCart.setDelFlag(BizShopCart.DEL_FLAG_NORMAL);
		bizShopCartService.delete(bizShopCart);
		addMessage(redirectAttributes, "恢复购物车成功");
		String a="cendShopDele";
		if(bizShopCart.getCendDele()!=null && bizShopCart.getCendDele().equals(a)){
			return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopCart/CendList?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopCart/?repage";
	}

	/**
	 * C端购物车
	 * */
	@RequiresPermissions("biz:shop:bizShopCart:view")
	@RequestMapping(value ="CendList")
	public String CendList(BizShopCart bizShopCart, HttpServletRequest request, HttpServletResponse response, Model model) {
		String supplierId = DictUtils.getDictValue("微信端", "biz_shop_cartType", "");
		bizShopCart.setCustType(Integer.parseInt(supplierId));
		Page<BizShopCart> page = bizShopCartService.findPage(new Page<BizShopCart>(request, response), bizShopCart);
		model.addAttribute("page", page);
		return "modules/biz/shop/bizShopCartCendList";
	}
}