/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.Menu;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 菜单Controller
 * @author ThinkGem
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@ModelAttribute("menu")
	public Menu get(@RequestParam(required=false) Integer id) {
		if (id!=null){
			return systemService.getMenu(id);
		}else{
			return new Menu();
		}
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model) {
		List<Menu> list = Lists.newArrayList();
		List<Menu> sourcelist = systemService.findAllMenu();
		Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        model.addAttribute("list", list);
		return "modules/sys/menuList";
	}

	/**
	 * 移动端菜单列表接口
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = {"listData"})
	@ResponseBody
	public String listData(HttpServletRequest request, @RequestParam(value = "parentId", required = false, defaultValue = "1")Integer parentId) {
		List<Menu> list = Lists.newArrayList();
		List<Menu> sourcelist = systemService.findAllMenu();

		Menu.sortList(list, sourcelist, parentId, false);
		list.removeIf(o -> !"1".equals(o.getIsShow()));
		List<Map<String, Object>> result = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(o -> {
				Map<String, Object> map = Maps.newHashMap();
				map.put("name", o.getName() == null ? StringUtils.EMPTY : o.getName());
				map.put("id", o.getId() == null ? StringUtils.EMPTY : o.getId());
				map.put("isShow", o.getIsShow() == null ? StringUtils.EMPTY : o.getIsShow());
				map.put("url", o.getHref() == null ? StringUtils.EMPTY : o.getHref());
				map.put("mobileUrl", o.getHref() == null ? StringUtils.EMPTY : o.getMobileHref());
				map.put("target", o.getTarget() == null ? StringUtils.EMPTY : o.getTarget());
				result.add(map);
			});
		}
		return JsonUtil.generateData(result, request.getParameter("callback"));
	}

	/**
	 * 移动端判断用户权限标示
	 * @param
	 * @return
	 */
	@RequestMapping(value = {"permissionList"})
	@ResponseBody
	public String permissionList1(HttpServletRequest request, @RequestParam(value = "marking") String marking) {
		Boolean falg = false;
		User user = UserUtils.getUser();
		if (user.isAdmin()) {
			falg = true;
		} else {
			List<String> permissionAllList = UserUtils.getPermissionAllList();
			for (String permission:permissionAllList) {
				if (permission.equals(marking)){
					falg = true;
					break;
				}
			}
		}
		return JsonUtil.generateData(falg, request.getParameter("callback"));
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = "form")
	public String form(Menu menu, Model model) {
		if (menu.getParent()==null||menu.getParent().getId()==null){
			menu.setParent(new Menu(Menu.getRootId()));
		}
		menu.setParent(systemService.getMenu(menu.getParent().getId()));
		// 获取排序号，最末节点排序号+30
		if (menu.getId()==null){
			List<Menu> list = Lists.newArrayList();
			List<Menu> sourcelist = systemService.findAllMenu();
			Menu.sortList(list, sourcelist, menu.getParentId(), false);
			if (list.size() > 0){
				menu.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("menu", menu);
		return "modules/sys/menuForm";
	}
	
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "save")
	public String save(Menu menu, Model model, RedirectAttributes redirectAttributes) {
		if(!UserUtils.getUser().isAdmin()){
			addMessage(redirectAttributes, "越权操作，只有超级管理员才能添加或修改数据！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
		if (!beanValidator(model, menu)){
			return form(menu, model);
		}
		systemService.saveMenu(menu);
		addMessage(redirectAttributes, "保存菜单'" + menu.getName() + "'成功");
		return "redirect:" + adminPath + "/sys/menu/";
	}
	
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "delete")
	public String delete(Menu menu, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
//		if (Menu.isRoot(id)){
//			addMessage(redirectAttributes, "删除菜单失败, 不允许删除顶级菜单或编号为空");
//		}else{
			systemService.deleteMenu(menu);
			addMessage(redirectAttributes, "删除菜单成功");
//		}
		return "redirect:" + adminPath + "/sys/menu/";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "tree")
	public String tree() {
		return "modules/sys/menuTree";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "treeselect")
	public String treeselect(String parentId, Model model) {
		model.addAttribute("parentId", parentId);
		return "modules/sys/menuTreeselect";
	}
	
	/**
	 * 批量修改菜单排序
	 */
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(Integer[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
    	for (int i = 0; i < ids.length; i++) {
    		Menu menu = new Menu(ids[i]);
    		menu.setSort(sorts[i]);
    		systemService.updateMenuSort(menu);
    	}
    	addMessage(redirectAttributes, "保存菜单排序成功!");
		return "redirect:" + adminPath + "/sys/menu/";
	}
	
	/**
	 * isShowHide是否显示隐藏菜单
	 * @param extId
	 * @param isShowHide
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId,@RequestParam(required=false) String isShowHide, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Menu> list = systemService.findAllMenu();
		for (int i=0; i<list.size(); i++){
			Menu e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				if(isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")){
					continue;
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
}
