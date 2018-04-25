/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
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
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;

import java.util.List;
import java.util.Map;

/**
 * 备货清单Controller
 * @author liuying
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestHeader")
public class BizRequestHeaderController extends BaseController {

	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;

	@ModelAttribute
	public BizRequestHeader get(@RequestParam(required=false) Integer id) {
		BizRequestHeader entity = null;
		if (id!=null){
			entity = bizRequestHeaderService.get(id);
		}
		if (entity == null){
			entity = new BizRequestHeader();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizRequestHeader bizRequestHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizRequestHeader> page = bizRequestHeaderService.findPage(new Page<BizRequestHeader>(request, response), bizRequestHeader);
        List<BizRequestHeader> list = page.getList();
        List<BizRequestHeader> orderList=Lists.newArrayList();
        for (BizRequestHeader bizRequestHeader1:list) {
            BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
            bizRequestDetail1.setRequestHeader(bizRequestHeader1);
            BizSkuInfo bizSkuInfo=new BizSkuInfo();
            bizSkuInfo.setItemNo(bizRequestHeader.getItemNo());
            bizSkuInfo.setVendorName(bizRequestHeader.getName());
			bizRequestDetail1.setSkuInfo(bizSkuInfo);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail1);
            Integer reqQtys = 0;
            Integer recvQtys = 0;
            Double money=0.0;
            for (BizRequestDetail bizRequestDetail:requestDetailList) {
				money+=(bizRequestDetail.getReqQty()==null?0:bizRequestDetail.getReqQty())*(bizRequestDetail.getSkuInfo().getBuyPrice()==null?0:bizRequestDetail.getSkuInfo().getBuyPrice());
                reqQtys += bizRequestDetail.getReqQty();
                recvQtys += bizRequestDetail.getRecvQty();
            }
            bizRequestHeader1.setReqQtys(reqQtys.toString());
            bizRequestHeader1.setRecvQtys(recvQtys.toString());
			bizRequestHeader1.setTotalMoney(money);
			if(requestDetailList!=null&&requestDetailList.size()>0){
				orderList.add(bizRequestHeader1);
			}

        }

			page.setList(orderList);


        model.addAttribute("page", page);
		return "modules/biz/request/bizRequestHeaderList";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "form")
	public String form(BizRequestHeader bizRequestHeader, Model model) {

		List<BizRequestDetail> reqDetailList=Lists.newArrayList();
		if(bizRequestHeader.getId()!=null){
			BizRequestDetail bizRequestDetail=new BizRequestDetail();
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			List<BizRequestDetail> requestDetailList=bizRequestDetailService.findList(bizRequestDetail);
			for(BizRequestDetail requestDetail:requestDetailList){
				BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
				requestDetail.setSkuInfo(skuInfo);
				reqDetailList.add(requestDetail);
			}
		}
		model.addAttribute("entity", bizRequestHeader);
		model.addAttribute("reqDetailList", reqDetailList);
		model.addAttribute("bizSkuInfo", new BizSkuInfo());

		return "modules/biz/request/bizRequestHeaderForm";
	}

	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "findByRequest")
	public List<BizRequestHeader> findByRequest(BizRequestHeader bizRequestHeader) {
		bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
		bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
		List<BizRequestHeader> list= bizRequestHeaderService.findList(bizRequestHeader);
		List<BizRequestHeader> bizRequestHeaderList=Lists.newArrayList();
		BizPoOrderReq bizPoOrderReq=new BizPoOrderReq();
		for (BizRequestHeader bizRequestHeader1:list) {
			BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
			bizRequestDetail1.setRequestHeader(bizRequestHeader1);
			BizSkuInfo bizSkuInfo =new BizSkuInfo();
			bizSkuInfo.setItemNo(bizRequestHeader.getItemNo());
			bizSkuInfo.setPartNo(bizRequestHeader.getPartNo());
			bizSkuInfo.setVendorName(bizRequestHeader.getName());
			bizRequestDetail1.setSkuInfo(bizSkuInfo);

			bizPoOrderReq.setRequestHeader(bizRequestHeader1);

			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail1);
			List<BizRequestDetail> reqDetailList =Lists.newArrayList();
			for (BizRequestDetail requestDetail:requestDetailList){
				bizPoOrderReq.setSoLineNo(requestDetail.getLineNo());
				List<BizPoOrderReq> poOrderReqList= bizPoOrderReqService.findList(bizPoOrderReq);
				if(poOrderReqList!=null && poOrderReqList.size()>0){
					BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
					skuInfo.setVendorName(requestDetail.getSkuInfo().getVendorName());
					requestDetail.setSkuInfo(skuInfo);
					reqDetailList.add(requestDetail);
				}

			}
			if(StringUtils.isNotBlank(bizRequestHeader.getItemNo())&& StringUtils.isNotBlank(bizRequestHeader.getPartNo())){
				if(requestDetailList!=null && requestDetailList.size()>0){
					bizRequestHeader1.setRequestDetailList(reqDetailList);
					bizRequestHeaderList.add(bizRequestHeader1);
				}
			}else if(StringUtils.isNotBlank(bizRequestHeader.getItemNo())){
				if(requestDetailList!=null && requestDetailList.size()>0){
					bizRequestHeader1.setRequestDetailList(reqDetailList);
					bizRequestHeaderList.add(bizRequestHeader1);
				}
			}else if(StringUtils.isNotBlank(bizRequestHeader.getPartNo())){
				if(requestDetailList!=null && requestDetailList.size()>0){
					bizRequestHeader1.setRequestDetailList(reqDetailList);
					bizRequestHeaderList.add(bizRequestHeader1);
				}
			} else if (requestDetailList!=null && requestDetailList.size()>0){
				bizRequestHeader1.setRequestDetailList(reqDetailList);
				bizRequestHeaderList.add(bizRequestHeader1);
			}
		}
		return bizRequestHeaderList;
	}





	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizRequestHeader bizRequestHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRequestHeader)){
			return form(bizRequestHeader, model);
		}
		bizRequestHeaderService.save(bizRequestHeader);
		addMessage(redirectAttributes, "保存备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
	}
	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "saveInfo")
	public boolean saveInfo(BizRequestHeader bizRequestHeader, String checkStatus) {
		bizRequestHeader.setBizStatus(Integer.parseInt(checkStatus));
		boolean boo=false;
		try {
			bizRequestHeaderService.save(bizRequestHeader);
			boo=true;
		}catch (Exception e){
			boo=false;
			logger.error(e.getMessage());
		}
			return boo;

	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRequestHeader bizRequestHeader, RedirectAttributes redirectAttributes) {
		bizRequestHeader.setDelFlag(BizRequestHeader.DEL_FLAG_DELETE);
		bizRequestHeaderService.delete(bizRequestHeader);
		addMessage(redirectAttributes, "删除备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizRequestHeader bizRequestHeader, RedirectAttributes redirectAttributes) {
		bizRequestHeader.setDelFlag(BizRequestHeader.DEL_FLAG_NORMAL);
		bizRequestHeaderService.delete(bizRequestHeader);
		addMessage(redirectAttributes, "删除备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
	}

}