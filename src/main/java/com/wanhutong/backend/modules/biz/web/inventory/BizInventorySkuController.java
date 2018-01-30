/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.biz.entity.dto.BizInventorySkus;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;

import java.util.List;

/**
 * 商品库存详情Controller
 * @author 张腾飞
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInventorySku")
public class BizInventorySkuController extends BaseController {

	@Autowired
	private BizInventorySkuService bizInventorySkuService;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
    private SystemService systemService;


	@ModelAttribute
	public BizInventorySku get(@RequestParam(required=false) Integer id) {
		BizInventorySku entity = null;
		if (id!=null){
			entity = bizInventorySkuService.get(id);
		}
		if (entity == null){
			entity = new BizInventorySku();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizInventorySku:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInventorySku bizInventorySku, HttpServletRequest request, HttpServletResponse response, Model model) {
		String zt = request.getParameter("zt");
		//取出用户所属采购中心
        User user = UserUtils.getUser();
		boolean flag=false;
		if(user.getRoleList()!=null){
			for(Role role:user.getRoleList()){
				if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
					flag=true;
					break;
				}
			}
		}
        Page<BizInventorySku> page =null;
        if (user.isAdmin()) {
            page= bizInventorySkuService.findPage(new Page<BizInventorySku>(request, response), bizInventorySku);
        } else {
        	if(flag){
				bizInventorySku.getSqlMap().put("inventorySku", BaseService.dataScopeFilter(user, "s", "su"));
				Office company = systemService.getUser(user.getId()).getCompany();
				//根据采购中心取出仓库
				BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
				bizInventoryInfo.setCustomer(company);
				bizInventorySku.setInvInfo(bizInventoryInfo);
			}

             page = bizInventorySkuService.findPage(new Page<BizInventorySku>(request, response), bizInventorySku);

        }
        model.addAttribute("zt",zt);
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizInventorySkuList";
	}

	@RequiresPermissions("biz:inventory:bizInventorySku:view")
	@RequestMapping(value = "form")
	public String form(BizInventorySku bizInventorySku,HttpServletRequest request, Model model) {
	    model.addAttribute("invInfoList",bizInventoryInfoService.findList(new BizInventoryInfo()));
        BizInventoryInfo bizInventoryInfo2 = bizInventoryInfoService.get(bizInventorySku.getInvInfo().getId());
		bizInventorySku.setInvInfo(bizInventoryInfo2);
		String zt = request.getParameter("zt");
		model.addAttribute("zt",zt);
		model.addAttribute("entity", bizInventorySku);
		model.addAttribute("bizSkuInfo",new BizSkuInfo());
		return "modules/biz/inventory/bizInventorySkuForm";
	}

	@RequiresPermissions("biz:inventory:bizInventorySku:edit")
	@RequestMapping(value = "save")
	public String save(BizInventorySkus bizInventorySkus, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, bizInventorySku)){
//			return form(bizInventorySku,request,model);
//		}
		if(bizInventorySkus!=null && bizInventorySkus.getSkuInfoIds()!=null ){
			String[] invInfoIdArr =bizInventorySkus.getInvInfoIds().split(",");
			String[] invTypeArr=bizInventorySkus.getInvTypes().split(",");
			String[] skuInfoIdArr=bizInventorySkus.getSkuInfoIds().split(",");
			String[] stockQtyArr=bizInventorySkus.getStockQtys().split(",");
			BizInventorySku bizInventorySku=new BizInventorySku();
			for(int i=0;i<skuInfoIdArr.length;i++){
				bizInventorySku.setId(null);
				bizInventorySku.setSkuInfo(bizSkuInfoService.get(Integer.parseInt(skuInfoIdArr[i].trim())));
				bizInventorySku.setInvInfo(bizInventoryInfoService.get(Integer.parseInt(invInfoIdArr[i].trim())));
				bizInventorySku.setInvType(Integer.parseInt(invTypeArr[i].trim()));
				bizInventorySku.setStockQty(Integer.parseInt(stockQtyArr[i].trim()));
				bizInventorySkuService.save(bizInventorySku);
			}
		}



		String zt = request.getParameter("zt");
		addMessage(redirectAttributes, "保存商品库存详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventorySku/?repage&zt="+zt;
	}
	
	@RequiresPermissions("biz:inventory:bizInventorySku:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInventorySku bizInventorySku,HttpServletRequest request, RedirectAttributes redirectAttributes) {
		bizInventorySkuService.delete(bizInventorySku);
		String zt = request.getParameter("zt");
		addMessage(redirectAttributes, "删除商品库存详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventorySku/?repage&zt="+zt;
	}

	@ResponseBody
	@RequiresPermissions("biz:inventory:bizInventorySku:edit")
	@RequestMapping(value = "delItem")
	public String delItem(BizInventorySku bizInventorySku, RedirectAttributes redirectAttributes) {
		String data="ok";
		try {
			bizInventorySkuService.delete(bizInventorySku);
		}catch (Exception e){
			logger.error(e.getMessage());
			data="error"; }
		return data;


	}

}