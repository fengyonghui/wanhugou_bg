/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
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
import com.wanhutong.backend.modules.biz.entity.order.BizFreightConfig;
import com.wanhutong.backend.modules.biz.service.order.BizFreightConfigService;

import java.util.List;

/**
 * 服务费设置Controller
 * @author Tengfei.Zhang
 * @version 2018-12-04
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizFreightConfig")
public class BizFreightConfigController extends BaseController {

	@Autowired
	private BizFreightConfigService bizFreightConfigService;
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public BizFreightConfig get(@RequestParam(required=false) Integer id) {
		BizFreightConfig entity = null;
		if (id!=null){
			entity = bizFreightConfigService.get(id);
		}
		if (entity == null){
			entity = new BizFreightConfig();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizFreightConfig:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizFreightConfig bizFreightConfig, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizFreightConfig> page = bizFreightConfigService.findPage(new Page<BizFreightConfig>(request, response), bizFreightConfig);
		model.addAttribute("page", page);
		return "modules/biz/order/bizFreightConfigList";
	}

	@RequiresPermissions("biz:order:bizFreightConfig:view")
	@RequestMapping(value = "form")
	public String form(BizFreightConfig bizFreightConfig, Model model) {
	    if (bizFreightConfig.getOffice() != null && bizFreightConfig.getOffice().getId() != null && bizFreightConfig.getVarietyInfo() != null && bizFreightConfig.getVarietyInfo().getId() != null) {
            BizFreightConfig freightConfig = new BizFreightConfig();
            freightConfig.setOffice(bizFreightConfig.getOffice());
            freightConfig.setVarietyInfo(bizFreightConfig.getVarietyInfo());
            List<BizFreightConfig> freightList = bizFreightConfigService.findFreightList(freightConfig);
            model.addAttribute("freightList",freightList);
        }
		model.addAttribute("typeList",DictUtils.getDictList("service_cha"));
		model.addAttribute("bizFreightConfig", bizFreightConfig);
		return "modules/biz/order/bizFreightConfigForm";
	}

	@RequiresPermissions("biz:order:bizFreightConfig:edit")
	@RequestMapping(value = "save")
	public String save(BizFreightConfig bizFreightConfig, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizFreightConfig)){
			return form(bizFreightConfig, model);
		}
		bizFreightConfigService.save(bizFreightConfig);
		addMessage(redirectAttributes, "保存服务费设置成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizFreightConfig/?repage";
	}
	
	@RequiresPermissions("biz:order:bizFreightConfig:edit")
	@RequestMapping(value = "delete")
	public String delete(BizFreightConfig bizFreightConfig, RedirectAttributes redirectAttributes) {
		bizFreightConfigService.delete(bizFreightConfig);
		addMessage(redirectAttributes, "删除服务费设置成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizFreightConfig/?repage";
	}

	@ResponseBody
    @RequestMapping(value = "selectFreightConfig")
    public String selectFreightConfig(Integer officeId, Integer variId) {
        List<BizFreightConfig> freightConfigs = bizFreightConfigService.findListByOfficeAndVari(officeId, variId);
        if (CollectionUtils.isEmpty(freightConfigs)) {
            return "ok";
        }
        return "error";
    }

}