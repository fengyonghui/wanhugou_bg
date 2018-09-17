/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.integration;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import com.wanhutong.backend.modules.biz.service.integration.BizMoneyRecodeService;
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
 * 积分流水Controller
 * @author LX
 * @version 2018-09-16
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/integration/bizMoneyRecode")
public class BizMoneyRecodeController extends BaseController {

	@Autowired
	private BizMoneyRecodeService bizMoneyRecodeService;
	
	@ModelAttribute
	public BizMoneyRecode get(@RequestParam(required=false) Integer id) {
		BizMoneyRecode entity = null;
		if (id!=null){
			entity = bizMoneyRecodeService.get(id);
		}
		if (entity == null){
			entity = new BizMoneyRecode();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:integration:bizMoneyRecode:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizMoneyRecode bizMoneyRecode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizMoneyRecode> page = bizMoneyRecodeService.findPage(new Page<BizMoneyRecode>(request, response), bizMoneyRecode);
		model.addAttribute("page", page);
		return "modules/biz/integration/bizMoneyRecodeList";
	}



}