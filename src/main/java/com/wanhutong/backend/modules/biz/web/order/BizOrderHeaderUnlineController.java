/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderUnlineService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 线下支付订单(线下独有)Controller
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeaderUnline")
public class BizOrderHeaderUnlineController extends BaseController {

	private static final Byte BIZSTATUSONE = 1;
	private static final Byte BIZSTATUSTWO = 2;
	@Autowired
	private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;
	@Autowired
	private CommonImgService commonImgService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	
	@ModelAttribute
	public BizOrderHeaderUnline get(@RequestParam(required=false) Integer id) {
		BizOrderHeaderUnline entity = null;
		if (id!=null){
			entity = bizOrderHeaderUnlineService.get(id);
		}
		if (entity == null){
			entity = new BizOrderHeaderUnline();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderHeaderUnline bizOrderHeaderUnline, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderHeaderUnline> page = bizOrderHeaderUnlineService.findPage(new Page<BizOrderHeaderUnline>(request, response), bizOrderHeaderUnline); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderHeaderUnlineList";
	}

	@RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
	@RequestMapping(value = "form")
	public String form(BizOrderHeaderUnline bizOrderHeaderUnline, Model model) {

		if (bizOrderHeaderUnline != null && bizOrderHeaderUnline.getId() != null) {
			CommonImg commonImg = new CommonImg();
			commonImg.setImgType(ImgEnum.UNlINE_PAYMENT_VOUCHER.getCode());
			commonImg.setObjectName(ImgEnum.UNlINE_PAYMENT_VOUCHER.getTableName());
			commonImg.setObjectId(bizOrderHeaderUnline.getId());
			List<CommonImg> commonImgList = commonImgService.findList(commonImg);
			if (commonImgList != null && !commonImgList.isEmpty()) {
				List<String> imgUrlList = new ArrayList<>();
				for (CommonImg comImg:commonImgList) {
					StringBuffer sb = new StringBuffer();
					sb.append(comImg.getImgServer()).append(comImg.getImgPath());
					imgUrlList.add(sb.toString());
				}
				model.addAttribute("imgUrlList",imgUrlList);
			}
		}
		model.addAttribute("bizOrderHeaderUnline", bizOrderHeaderUnline);
		return "modules/biz/order/bizOrderHeaderUnlineForm";
	}

	@RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderHeaderUnline)){
			return form(bizOrderHeaderUnline, model);
		}
        bizOrderHeaderUnline = bizOrderHeaderUnlineService.get(bizOrderHeaderUnline.getId());
        bizOrderHeaderUnline.setRealMoney(bizOrderHeaderUnline.getUnlinePayMoney());
        bizOrderHeaderUnline.setBizStatus(BIZSTATUSONE);
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
            bizOrderHeaderUnlineService.save(bizOrderHeaderUnline);
        bizOrderHeader.setReceiveTotal(bizOrderHeader.getReceiveTotal()+bizOrderHeaderUnline.getRealMoney().doubleValue());
        bizOrderHeaderService.save(bizOrderHeader);
        if (bizOrderHeader.getBizStatus() == OrderHeaderBizStatusEnum.UNPAY.getState()) {
            if (bizOrderHeader.getTotalDetail().compareTo(bizOrderHeader.getReceiveTotal())==0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }else {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.INITIAL_PAY.getState());
            }
        }
        if (bizOrderHeader.getBizStatus() == OrderHeaderBizStatusEnum.INITIAL_PAY.getState()) {
            if (bizOrderHeader.getTotalDetail().compareTo(bizOrderHeader.getReceiveTotal())==0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
        }
        bizOrderHeaderService.save(bizOrderHeader);
        addMessage(redirectAttributes, "保存线下支付订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeaderUnline/?repage&orderHeader.id="+bizOrderHeader.getId();
	}
	
	@RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderHeaderUnline bizOrderHeaderUnline, RedirectAttributes redirectAttributes) {
		bizOrderHeaderUnlineService.delete(bizOrderHeaderUnline);
		addMessage(redirectAttributes, "删除线下支付订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeaderUnline/?repage";
	}

    /**
     * 财务对线下支付订单的驳回
     * @param id
     * @return
     */
    @ResponseBody
	@RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
	@RequestMapping(value = "changeOrderReceive")
	public String changeOrderReceive(Integer id) {

			BizOrderHeaderUnline orderHeaderUnline = bizOrderHeaderUnlineService.get(id);
			orderHeaderUnline.setBizStatus(BIZSTATUSTWO);
            bizOrderHeaderUnlineService.save(orderHeaderUnline);
        return "ok";
    }

}