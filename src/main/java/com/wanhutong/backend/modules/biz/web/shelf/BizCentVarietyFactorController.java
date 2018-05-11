/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.sys.entity.Office;
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
import com.wanhutong.backend.modules.biz.entity.shelf.BizCentVarietyFactor;
import com.wanhutong.backend.modules.biz.service.shelf.BizCentVarietyFactorService;

import java.util.List;

/**
 * 采购中心品类阶梯价Controller
 * @author ZhangTengfei
 * @version 2018-04-27
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizCentVarietyFactor")
public class BizCentVarietyFactorController extends BaseController {

	@Autowired
	private BizCentVarietyFactorService bizCentVarietyFactorService;
	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	
	@ModelAttribute
	public BizCentVarietyFactor get(@RequestParam(required=false) Integer id) {
		BizCentVarietyFactor entity = null;
		if (id!=null){
			entity = bizCentVarietyFactorService.get(id);
		}
		if (entity == null){
			entity = new BizCentVarietyFactor();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizCentVarietyFactor:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCentVarietyFactor bizCentVarietyFactor, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCentVarietyFactor> page = bizCentVarietyFactorService.findPage(new Page<BizCentVarietyFactor>(request, response), bizCentVarietyFactor); 
		model.addAttribute("shelfList",bizOpShelfInfoService.findList(new BizOpShelfInfo()));
		model.addAttribute("varietyList",bizVarietyInfoService.findList(new BizVarietyInfo()));
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizCentVarietyFactorList";
	}

	@RequiresPermissions("biz:shelf:bizCentVarietyFactor:view")
	@RequestMapping(value = "form")
	public String form(BizCentVarietyFactor bizCentVarietyFactor, Model model) {
		model.addAttribute("shelfList",bizOpShelfInfoService.findList(new BizOpShelfInfo()));
		model.addAttribute("varietyList",bizVarietyInfoService.findList(new BizVarietyInfo()));
		model.addAttribute("bizCentVarietyFactor", bizCentVarietyFactor);
		return "modules/biz/shelf/bizCentVarietyFactorForm";
	}

	@RequiresPermissions("biz:shelf:bizCentVarietyFactor:edit")
	@RequestMapping(value = "save")
	public String save(BizCentVarietyFactor bizCentVarietyFactor, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCentVarietyFactor)){
			return form(bizCentVarietyFactor, model);
		}
		bizCentVarietyFactorService.save(bizCentVarietyFactor);
		addMessage(redirectAttributes, "保存采购中心品类阶梯价成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizCentVarietyFactor/?repage";
	}
	
	@RequiresPermissions("biz:shelf:bizCentVarietyFactor:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCentVarietyFactor bizCentVarietyFactor, RedirectAttributes redirectAttributes) {
		bizCentVarietyFactorService.delete(bizCentVarietyFactor);
		addMessage(redirectAttributes, "删除采购中心品类阶梯价成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizCentVarietyFactor/?repage";
	}

    /**
     * 检查阶梯价是否已经存在
     * @param centId
     * @param shelfId
     * @param varietyId
     * @param serviceFactors
     * @param minQtys
     * @param maxQtys
     * @param id
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizCentVarietyFactor:view")
	@RequestMapping(value = "checkRepeat")
	public String checkRepeat(Integer centId,Integer shelfId,Integer varietyId,String serviceFactors,String minQtys,String maxQtys,Integer id) {
	    String flag = "true";
        String[] serviceFactorArr = serviceFactors.split(",".trim());
        String[] minQtyArr = minQtys.split(",".trim());
        String[] maxQtyArr = maxQtys.split(",".trim());
        for(int i = 0; i < serviceFactorArr.length; i++) {
            BizCentVarietyFactor bizCentVarietyFactor = new BizCentVarietyFactor();
            bizCentVarietyFactor.setCenter(new Office(centId));
            bizCentVarietyFactor.setShelfInfo(new BizOpShelfInfo(shelfId));
            bizCentVarietyFactor.setVarietyInfo(new BizVarietyInfo(varietyId));
            List<BizCentVarietyFactor> bcvfList = bizCentVarietyFactorService.findList(bizCentVarietyFactor);
            if (bcvfList != null && !bcvfList.isEmpty()) {
                if (id != null) {
                    bcvfList.remove(bizCentVarietyFactorService.get(id));
                }
            }
			if (bcvfList != null && !bcvfList.isEmpty()) {
				for (BizCentVarietyFactor centVarietyFactor : bcvfList) {
					int minQty = centVarietyFactor.getMinQty();
					int maxQty = centVarietyFactor.getMaxQty();
					if (minQty >= Integer.parseInt(minQtyArr[i]) && maxQty <= Integer.parseInt(maxQtyArr[i]) ||
							minQty <= Integer.parseInt(maxQtyArr[i]) && maxQty >= Integer.parseInt(minQtyArr[i])) {
						flag = "false";
					}
				}
			}
        }
        return flag;
    }

}