/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
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
 * 商品颜色Controller
 * @author ZhangTengfei
 * @version 2018-04-09
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/color")
public class ColorController extends BaseController {

	//颜色
	private static final String DICTTYPE = "color";
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public Dict get(@RequestParam(required=false) Integer id) {
		if (id!=null){
			return dictService.get(id);
		}else{
			return new Dict();
		}
	}
	
	@RequiresPermissions("biz:sku:color:view")
	@RequestMapping(value = {"list", ""})
	public String list(Dict dict, HttpServletRequest request, HttpServletResponse response, Model model) {
		dict.setType(DICTTYPE);
        Page<Dict> page = dictService.findPage(new Page<Dict>(request, response), dict); 
        model.addAttribute("page", page);
		return "modules/biz/sku/bizSkuColorList";
	}

	@RequiresPermissions("biz:sku:color:view")
	@RequestMapping(value = "form")
	public String form(Dict dict, Model model) {
		model.addAttribute("dict", dict);
		return "modules/biz/sku/bizSkuColorForm";
	}

	@RequiresPermissions("biz:sku:color:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(Dict dict, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/biz/sku/color/?repage&type="+dict.getType();
		}
		if (!beanValidator(model, dict)){
			return form(dict, model);
		}
		String code = HanyuPinyinHelper.getFirstLetters(dict.getLabel(), HanyuPinyinCaseType.UPPERCASE);
		dict.setValue(code);
		dictService.save(dict);
		addMessage(redirectAttributes, "保存商品颜色'" + dict.getLabel() + "'成功");
		return "redirect:" + adminPath + "/biz/sku/color/?repage&type="+dict.getType();
	}
	
	@RequiresPermissions("biz:sku:color:edit")
	@RequestMapping(value = "delete")
	public String delete(Dict dict, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/biz/sku/color/?repage";
		}
		dictService.delete(dict);
		addMessage(redirectAttributes, "删除商品颜色成功");
		return "redirect:" + adminPath + "/biz/sku/color/?repage&type="+dict.getType();
	}
}
