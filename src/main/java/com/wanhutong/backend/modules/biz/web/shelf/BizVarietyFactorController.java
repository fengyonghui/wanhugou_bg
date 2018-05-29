/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
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

import java.util.ArrayList;
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
		List<BizVarietyInfo> varietyFactorList = bizVarietyInfoService.findList(new BizVarietyInfo());
		if(bizVarietyFactor.getId()==null){
			ArrayList<BizVarietyInfo> objects = Lists.newArrayList();
			for (int i = 0; i < varietyFactorList.size(); i++) {
				BizVarietyFactor bizCentVarietyFactor = new BizVarietyFactor();
				bizCentVarietyFactor.setVarietyInfo(new BizVarietyInfo(varietyFactorList.get(i).getId()));
				List<BizVarietyFactor> list = bizVarietyFactorService.findList(bizCentVarietyFactor);
				if(list.size()==0){
					objects.add(varietyFactorList.get(i));
				}
			}
			model.addAttribute("varietyList",objects);
		}else{
			model.addAttribute("varietyList",varietyFactorList);
		}
		if(bizVarietyFactor!=null && bizVarietyFactor.getId()!=null){
			BizVarietyFactor varietyFactor = new BizVarietyFactor();
			varietyFactor.setVarietyInfo(new BizVarietyInfo(bizVarietyFactor.getVarietyInfo().getId()));
			List<BizVarietyFactor> list = bizVarietyFactorService.findList(varietyFactor);
			model.addAttribute("variList",list);
		}
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
	public String checkRepeat(Integer variety,BizVarietyFactor bizVarietyFactor,Integer id) {
	    String flag = "true";
		String[] serviceFactorArr = bizVarietyFactor.getServiceFactors().split(",".trim());
		String[] minQtyArr = bizVarietyFactor.getMinQtys().split(",".trim());
		String[] maxQtyArr = bizVarietyFactor.getMaxQtys().split(",".trim());
		for(int i = 0; i < serviceFactorArr.length; i++) {
//		    bizVarietyFactor.getVarietyInfo();
//            Integer serviceFactor = bizVarietyFactor.getServiceFactor();
			BizVarietyFactor bizCentVarietyFactor = new BizVarietyFactor();
			bizCentVarietyFactor.setVarietyInfo(new BizVarietyInfo(variety));
			List<BizVarietyFactor> list = bizVarietyFactorService.findList(bizCentVarietyFactor);
				if (list != null && !list.isEmpty()) {
					if (id != null) {
						list.remove(bizVarietyFactorService.get(id));
					}
				}
			if (list != null && !list.isEmpty()) {
				for (int j = 0; j < list.size(); j++) {
					int minQty = list.get(j).getMinQty();
					int maxQty = list.get(j).getMaxQty();
					if(minQty == Integer.parseInt(minQtyArr[i]) && maxQty == Integer.parseInt(maxQtyArr[i]) ||
							minQty == Integer.parseInt(maxQtyArr[i]) && maxQty == Integer.parseInt(minQtyArr[i])){
//						System.out.println("不修改的数量不用判断");
					}else{
						if (minQty > Integer.parseInt(minQtyArr[i]) && maxQty < Integer.parseInt(maxQtyArr[i]) ||
								minQty < Integer.parseInt(maxQtyArr[i]) && maxQty > Integer.parseInt(minQtyArr[i])) {
							flag = "false";
						}
						if(i>0) {
							if (Integer.parseInt(minQtyArr[i]) <= Integer.parseInt(maxQtyArr[i])) {
								flag = "false";
								break;
							}
						}
						if (Integer.parseInt(minQtyArr[i]) > Integer.parseInt(maxQtyArr[i])) {
							flag = "minMax";
						}
					}
				}
			}else{
				if(i>0){
					if(Integer.parseInt(minQtyArr[i])<=Integer.parseInt(maxQtyArr[i-1])){
						flag = "false";
						break;
					}
				}
				if(Integer.parseInt(minQtyArr[i])>Integer.parseInt(maxQtyArr[i])){
					flag = "minMax";
				}
			}
			if(flag!="" && flag.equals("true") && Integer.parseInt(maxQtyArr[i])!=9999){
				flag = "error";
			}else if(flag!="" && flag.equals("true") && Integer.parseInt(maxQtyArr[i])==9999){
				flag="true";
			}else if(flag!="" && flag.equals("error") && Integer.parseInt(maxQtyArr[i])==9999){
				flag="true";
			}
		}
        return flag;
	}

	/**
	 * 删除不刷新
	 * */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizVarietyFactor:edit")
	@RequestMapping(value = "deleteAjas")
	public String deleteAjas(BizVarietyFactor bizVarietyFactor, RedirectAttributes redirectAttributes) {
		String source="error";
		try {
			bizVarietyFactorService.delete(bizVarietyFactor);
			source="ok";
		}catch (Exception e){
			e.printStackTrace();
		}
		return source;
	}


}