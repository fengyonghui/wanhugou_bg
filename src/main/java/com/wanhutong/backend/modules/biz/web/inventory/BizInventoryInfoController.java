/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;

import java.util.List;
import java.util.Map;

/**
 * 仓库信息表Controller
 * @author zhangtengfei
 * @version 2017-12-28
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInventoryInfo")
public class BizInventoryInfoController extends BaseController {

	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
    private OfficeService officeService;
	
	@ModelAttribute
	public BizInventoryInfo get(@RequestParam(required=false) Integer id) {
		BizInventoryInfo entity = null;
		if (id!=null){
			entity = bizInventoryInfoService.get(id);
		}
		if (entity == null){
			entity = new BizInventoryInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizInventoryInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInventoryInfo bizInventoryInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInventoryInfo> page = bizInventoryInfoService.findPage(new Page<BizInventoryInfo>(request, response), bizInventoryInfo);
		String zt = request.getParameter("zt");//zt表示请求状态：1、商品库存管理 2、库存盘点
		model.addAttribute("zt",zt);
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizInventoryInfoList";
	}

	@RequiresPermissions("biz:inventory:bizInventoryInfo:view")
	@RequestMapping(value = "form")
	public String form(BizInventoryInfo bizInventoryInfo, Model model, HttpServletRequest request) {
		String zt = request.getParameter("zt");
		model.addAttribute("zt",zt);
		model.addAttribute("entity", bizInventoryInfo);
		return "modules/biz/inventory/bizInventoryInfoForm";
	}

	@RequiresPermissions("biz:inventory:bizInventoryInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizInventoryInfo bizInventoryInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInventoryInfo)){
			return form(bizInventoryInfo, model,request);
		}
		bizInventoryInfoService.save(bizInventoryInfo);
		String zt = request.getParameter("zt");
		addMessage(redirectAttributes, "保存仓库信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventoryInfo/?repage&zt="+zt;
	}
	
	@RequiresPermissions("biz:inventory:bizInventoryInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInventoryInfo bizInventoryInfo, RedirectAttributes redirectAttributes) {
		bizInventoryInfoService.delete(bizInventoryInfo);
		addMessage(redirectAttributes, "删除仓库信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventoryInfo/?repage";
	}

    /**
     * 用于配资中心仓库查询采购商
     * @param invInfoId
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:inventory:bizInventoryInfo:view")
	@RequestMapping(value = "findInventorySku")
	public BizInventoryInfo findInventorySku(Integer invInfoId){
        BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
		if (invInfoId != null) {
            bizInventoryInfo = bizInventoryInfoService.get(invInfoId);
            Office cent = officeService.get(bizInventoryInfo.getCustomer().getId());
            bizInventoryInfo.setCustomer(cent);
        }
        return bizInventoryInfo;
	}

	/**
	 * 库存管理 查询仓库名称
	 * */
	@RequiresPermissions("biz:inventory:bizInventorySku:view")
	@ResponseBody
	@RequestMapping(value = "warehouseData")
	public List<Map<String, Object>> warehouseData() {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		User user = UserUtils.getUser();
		BizInventoryInfo inventoryInfo = new BizInventoryInfo();
		List<BizInventoryInfo> list = null;
		if (user.isAdmin()) {
			list = bizInventoryInfoService.findList(inventoryInfo);
		} else {
			inventoryInfo.getSqlMap().put("inventorySku", BaseService.dataScopeFilter(user, "s", "su"));
			list = bizInventoryInfoService.findList(inventoryInfo);
		}
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				BizInventoryInfo e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}

}