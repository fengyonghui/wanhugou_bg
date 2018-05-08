/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
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
import com.wanhutong.backend.modules.biz.entity.shelf.BizVarietyFactor;
import com.wanhutong.backend.modules.biz.service.shelf.BizVarietyFactorService;

import java.util.List;

/**
 * 品类阶梯价Controller
 * @author ZhangTengfei
 * @version 2018-04-27
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizVarietyFactor")
public class BizVarietyFactorController extends BaseController {

	@Autowired
	private BizVarietyFactorService bizVarietyFactorService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	
	@ModelAttribute
	public BizVarietyFactor get(@RequestParam(required=false) Integer id) {
		BizVarietyFactor entity = null;
		if (id!=null){
			entity = bizVarietyFactorService.get(id);
		}
		if (entity == null){
			entity = new BizVarietyFactor();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizVarietyFactor:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizVarietyFactor bizVarietyFactor, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizVarietyFactor> page = bizVarietyFactorService.findPage(new Page<BizVarietyFactor>(request, response), bizVarietyFactor);
		model.addAttribute("varietyList",bizVarietyInfoService.findList(new BizVarietyInfo()));
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizVarietyFactorList";
	}

	@RequiresPermissions("biz:shelf:bizVarietyFactor:view")
	@RequestMapping(value = "form")
	public String form(BizVarietyFactor bizVarietyFactor, Model model) {
		model.addAttribute("bizVarietyFactor", bizVarietyFactor);
		model.addAttribute("varietyList",bizVarietyInfoService.findList(new BizVarietyInfo()));
		return "modules/biz/shelf/bizVarietyFactorForm";
	}

	@RequiresPermissions("biz:shelf:bizVarietyFactor:edit")
	@RequestMapping(value = "save")
	public String save(BizVarietyFactor bizVarietyFactor, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizVarietyFactor)){
			return form(bizVarietyFactor, model);
		}
		bizVarietyFactorService.save(bizVarietyFactor);
		addMessage(redirectAttributes, "保存品类阶梯价成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizVarietyFactor/?repage";
	}
	
	@RequiresPermissions("biz:shelf:bizVarietyFactor:edit")
	@RequestMapping(value = "delete")
	public String delete(BizVarietyFactor bizVarietyFactor, RedirectAttributes redirectAttributes) {
		bizVarietyFactorService.delete(bizVarietyFactor);
		addMessage(redirectAttributes, "删除品类阶梯价成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizVarietyFactor/?repage";
	}

    /**
     * 根据品类ID查询品类阶梯价表
     * @param variId
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizVarietyFactor:view")
	@RequestMapping(value = "selectVari")
	public List<BizVarietyFactor> selectVari(Integer variId){
        BizVarietyFactor bizVarietyFactor = new BizVarietyFactor();
        bizVarietyFactor.setVarietyInfo(new BizVarietyInfo(variId));
        List<BizVarietyFactor> list = bizVarietyFactorService.findList(bizVarietyFactor);
        return list;
	}

	@ResponseBody
	@RequiresPermissions("biz:shelf:bizVarietyFactor:view")
	@RequestMapping(value = "checkRepeat")
	public String checkRepeat(BizVarietyFactor bizVarietyFactor) {
	    String flag = "true";
		if (bizVarietyFactor != null) {
		    bizVarietyFactor.getVarietyInfo();
            Integer serviceFactor = bizVarietyFactor.getServiceFactor();
            List<BizVarietyFactor> list = bizVarietyFactorService.findList(bizVarietyFactor);
            if (list != null && !list.isEmpty()) {
                if (bizVarietyFactor.getId() != null) {
                    list.remove(bizVarietyFactor);
                }
            }
            if (list != null && !list.isEmpty()) {
                for (BizVarietyFactor varietyFactor : list) {
                    int minQty = varietyFactor.getMinQty();
                    int maxQty = varietyFactor.getMaxQty();
                    if (minQty >= bizVarietyFactor.getMinQty() && maxQty <= bizVarietyFactor.getMaxQty() ||
                            minQty <= bizVarietyFactor.getMaxQty() && maxQty >= bizVarietyFactor.getMinQty()) {
                        flag = "false";
                    }
                }
            }
        }
        return flag;
	}

}