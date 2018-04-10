/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web.wx;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
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
import com.wanhutong.backend.modules.sys.entity.wx.SysWxPersonalUser;
import com.wanhutong.backend.modules.sys.service.wx.SysWxPersonalUserService;

import java.util.List;
import java.util.Map;

/**
 * 注册用户Controller
 * @author Oy
 * @version 2018-04-10
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/wx/sysWxPersonalUser")
public class SysWxPersonalUserController extends BaseController {

	@Autowired
	private SysWxPersonalUserService sysWxPersonalUserService;
	@Autowired
	private SystemService systemService;
	
	@ModelAttribute
	public SysWxPersonalUser get(@RequestParam(required=false) Integer id) {
		SysWxPersonalUser entity = null;
		if (id!=null){
			entity = sysWxPersonalUserService.get(id);
		}
		if (entity == null){
			entity = new SysWxPersonalUser();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:wx:sysWxPersonalUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysWxPersonalUser sysWxPersonalUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysWxPersonalUser> page = sysWxPersonalUserService.findPage(new Page<SysWxPersonalUser>(request, response), sysWxPersonalUser); 
		model.addAttribute("page", page);
		return "modules/sys/wx/sysWxPersonalUserList";
	}

	@RequiresPermissions("sys:wx:sysWxPersonalUser:view")
	@RequestMapping(value = "form")
	public String form(SysWxPersonalUser sysWxPersonalUser, Model model) {
		model.addAttribute("sysWxPersonalUser", sysWxPersonalUser);
		return "modules/sys/wx/sysWxPersonalUserForm";
	}

	@RequiresPermissions("sys:wx:sysWxPersonalUser:edit")
	@RequestMapping(value = "save")
	public String save(SysWxPersonalUser sysWxPersonalUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysWxPersonalUser)){
			return form(sysWxPersonalUser, model);
		}
		sysWxPersonalUserService.save(sysWxPersonalUser);
		addMessage(redirectAttributes, "保存注册用户成功");
		return "redirect:"+Global.getAdminPath()+"/sys/wx/sysWxPersonalUser/?repage";
	}
	
	@RequiresPermissions("sys:wx:sysWxPersonalUser:edit")
	@RequestMapping(value = "delete")
	public String delete(SysWxPersonalUser sysWxPersonalUser, RedirectAttributes redirectAttributes) {
		sysWxPersonalUserService.delete(sysWxPersonalUser);
		addMessage(redirectAttributes, "删除注册用户成功");
		return "redirect:"+Global.getAdminPath()+"/sys/wx/sysWxPersonalUser/?repage";
	}

	/**
	 * 查询C端已注册用户
	 * */
	@RequiresPermissions("sys:wx:sysWxPersonalUser:view")
	@ResponseBody
	@RequestMapping(value = "userTreeData")
	public List<Map<String, Object>> treeData() {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = sysWxPersonalUserService.findUserByOffice();
		for (int i=0; i<list.size(); i++){
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", null);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}
}