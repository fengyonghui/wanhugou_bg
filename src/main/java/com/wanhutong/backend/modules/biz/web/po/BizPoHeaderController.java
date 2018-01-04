/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;

import java.util.List;

/**
 * 采购订单表Controller
 * @author liuying
 * @version 2017-12-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizPoHeader")
public class BizPoHeaderController extends BaseController {

	@Autowired
	private BizPoHeaderService bizPoHeaderService;
	@Autowired
	private BizPoDetailService bizPoDetailService;
	@Autowired
	private BizPlatformInfoService bizPlatformInfoService;
	
	@ModelAttribute
	public BizPoHeader get(@RequestParam(required=false) Integer id) {
		BizPoHeader entity = null;
		if (id!=null){
			entity = bizPoHeaderService.get(id);
			BizPoDetail bizPoDetail=new BizPoDetail();
			bizPoDetail.setPoHeader(entity);
			List<BizPoDetail> poDetailList=bizPoDetailService.findList(bizPoDetail);
			entity.setPoDetailList(poDetailList);
		}
		if (entity == null){
			entity = new BizPoHeader();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:po:bizPoHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPoHeader bizPoHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<BizPoHeader>(request, response), bizPoHeader); 
		model.addAttribute("page", page);
		return "modules/biz/po/bizPoHeaderList";
	}

	@RequiresPermissions("biz:po:bizPoHeader:view")
	@RequestMapping(value = "form")
	public String form(BizPoHeader bizPoHeader, Model model) {
		model.addAttribute("bizPoHeader", bizPoHeader);
		return "modules/biz/po/bizPoHeaderForm";
	}

	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPoHeader)){
			return form(bizPoHeader, model);
		}
		String poNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO,bizPoHeader.getVendOffice().getId());
		bizPoHeader.setOrderNum(poNo);
		bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
		bizPoHeaderService.save(bizPoHeader);
		addMessage(redirectAttributes, "保存采购订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/?repage";
	}
	
	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPoHeader bizPoHeader, RedirectAttributes redirectAttributes) {
		bizPoHeaderService.delete(bizPoHeader);
		addMessage(redirectAttributes, "删除采购订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/?repage";
	}

}