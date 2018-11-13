/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSkuTransferDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV3Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.TransferProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.BizOrderTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.enums.TransferStatusEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransfer;
import com.wanhutong.backend.modules.biz.service.inventory.BizSkuTransferService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 库存调拨Controller
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizSkuTransfer")
public class BizSkuTransferController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizSkuTransferController.class);

	@Autowired
	private BizSkuTransferService bizSkuTransferService;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizSkuInfoV3Service bizSkuInfoService;
	@Autowired
	private BizSkuTransferDetailService bizSkuTransferDetailService;
	@Autowired
	private BizPoHeaderService bizPoHeaderService;
	@Autowired
	private BizOrderStatusService bizOrderStatusService;
	
	@ModelAttribute
	public BizSkuTransfer get(@RequestParam(required=false) Integer id) {
		BizSkuTransfer entity = null;
		if (id!=null){
			entity = bizSkuTransferService.get(id);
			if (entity.getCommonProcess() != null && entity.getCommonProcess().getId() != null) {
				List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
				bizPoHeaderService.getCommonProcessListFromDB(entity.getCommonProcess().getId(), commonProcessList);
				Collections.reverse(commonProcessList);
				entity.setCommonProcessList(commonProcessList);
			}
		}
		if (entity == null){
			entity = new BizSkuTransfer();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizSkuTransfer:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuTransfer bizSkuTransfer, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuTransfer> page = bizSkuTransferService.findPage(new Page<BizSkuTransfer>(request, response), bizSkuTransfer);
		Map<Integer, String> transferMap = Maps.newHashMap();
		Map<Integer, TransferProcessConfig.TransferProcess> transMap = ConfigGeneral.SKU_TRANSFER_PROCESS_CONFIG.get().processMap;
		for (Map.Entry<Integer, TransferProcessConfig.TransferProcess> trans : transMap.entrySet()) {
			transferMap.put(trans.getKey(),transMap.get(trans.getKey()).getName());
		}
		User user = UserUtils.getUser();
		List<Role> roleList = user.getRoleList();
		Set<String> roleSet = Sets.newHashSet();
		for (Role r : roleList) {
			RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
			if (parse != null) {
				roleSet.add(parse.name());
			}
		}
		model.addAttribute("roleSet", roleSet);
		model.addAttribute("transferMap",transferMap);
		model.addAttribute("fromInvList",bizInventoryInfoService.findAllList(new BizInventoryInfo()));
		model.addAttribute("toInvList",bizInventoryInfoService.findList(new BizInventoryInfo()));
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizSkuTransferList";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransfer:view")
	@RequestMapping(value = "form")
	public String form(BizSkuTransfer bizSkuTransfer, Model model) {

		if (bizSkuTransfer.getId() != null && bizSkuTransfer.getId() != 0) {
			BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
			bizSkuTransferDetail.setTransfer(bizSkuTransfer);
			List<BizSkuTransferDetail> skuTransferDetailList = bizSkuTransferDetailService.findList(bizSkuTransferDetail);
			if (CollectionUtils.isNotEmpty(skuTransferDetailList)) {
				model.addAttribute("transferDetailList",skuTransferDetailList);
			}
			BizOrderStatus bizOrderStatus = new BizOrderStatus();
			bizOrderStatus.setOrderHeader(new BizOrderHeader(bizSkuTransfer.getId()));
			bizOrderStatus.setOrderType(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getState());
			List<BizOrderStatus> statusList = bizOrderStatusService.findStatusList(bizOrderStatus);
			statusList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
			Map<Integer, TransferStatusEnum> statusMap = TransferStatusEnum.getStatusMap();
			model.addAttribute("statusList", statusList);
			model.addAttribute("statusMap", statusMap);
		}
		if ("audit".equalsIgnoreCase(bizSkuTransfer.getStr())) {
			TransferProcessConfig.TransferProcess transferProcess =
					ConfigGeneral.SKU_TRANSFER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizSkuTransfer.getCommonProcess().getType()));
			model.addAttribute("transferProcess", transferProcess);
		}
		model.addAttribute("fromInvList",bizInventoryInfoService.findAllList(new BizInventoryInfo()));
		model.addAttribute("toInvList",bizInventoryInfoService.findList(new BizInventoryInfo()));
		model.addAttribute("bizSkuTransfer", bizSkuTransfer);
		return "modules/biz/inventory/bizSkuTransferForm";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransfer:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuTransfer bizSkuTransfer, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuTransfer)){
			return form(bizSkuTransfer, model);
		}
		bizSkuTransferService.save(bizSkuTransfer);
		addMessage(redirectAttributes, "保存库存调拨成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSkuTransfer/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizSkuTransfer:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuTransfer bizSkuTransfer, RedirectAttributes redirectAttributes) {
		bizSkuTransferService.delete(bizSkuTransfer);
		addMessage(redirectAttributes, "删除库存调拨成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSkuTransfer/?repage";
	}

	@ResponseBody
	@RequestMapping("findInvSkuList")
	public List<BizSkuInfo> findInvSkuList(BizSkuInfo skuInfo, Integer fromInv) {
		if (skuInfo == null ||
				(StringUtils.isBlank(skuInfo.getName()) &&
				 StringUtils.isBlank(skuInfo.getItemNo()) &&
				 StringUtils.isBlank(skuInfo.getPartNo()) &&
				 StringUtils.isBlank(skuInfo.getVendorName())) || fromInv ==null) {
			return null;

		}
		return bizSkuInfoService.findInvSkuList(skuInfo,fromInv);
	}

	/**
	 * 异步删除调拨单详情
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "deleteAjax")
	public String deleteAjax(Integer id) {
		if (id == null) {
			return "error";
		}
		try {
			bizSkuTransferDetailService.delete(new BizSkuTransferDetail(id));
		} catch (Exception e) {
			LOGGER.error("调拨单详情删除失败，调拨单详情ID【{}】",id);
			return "error";
		}
		return "ok";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransfer:audit")
	@RequestMapping(value = "audit")
	@ResponseBody
	public String audit(HttpServletRequest request, int id, String currentType, int auditType, String description) {
		Pair<Boolean, String> result = bizSkuTransferService.auditTransfer(id, currentType, auditType, description);
		if (result.getLeft()) {
			return JsonUtil.generateData(result, request.getParameter("callback"));
		}
		return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
	}

}