/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private BizSkuInfoV2Service bizSkuInfoService;
	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
	private DictService dictService;
	@Autowired
	private BizPoHeaderService bizPoHeaderService;
	@Autowired
	private BizPoDetailService bizPoDetailService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;

	@Autowired
	private BizOrderStatusService bizOrderStatusService;

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
		String dataFrom = "biz_request_bizRequestHeader";
		bizRequestHeader.setDataFrom(dataFrom);
		Page<BizRequestHeader> page = bizRequestHeaderService.findPage(new Page<BizRequestHeader>(request, response), bizRequestHeader);
        model.addAttribute("page", page);
        //品类名称
		List<BizVarietyInfo> varietyInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());
		model.addAttribute("varietyInfoList", varietyInfoList);
		model.addAttribute("auditStatus", ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());

		return "modules/biz/request/bizRequestHeaderList";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "form")
	public String form(BizRequestHeader bizRequestHeader, Model model) {
		List<BizRequestDetail> reqDetailList = Lists.newArrayList();
		if (bizRequestHeader.getId() != null) {
			BizRequestDetail bizRequestDetail = new BizRequestDetail();
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			if (bizRequestHeader.getBizStatus() >= ReqHeaderStatusEnum.PURCHASING.getState()) {
				/* 查询已生成的采购单 标识*/
				bizRequestDetail.setPoheaderSource("poHeader");
			}
			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findPoRequet(bizRequestDetail);
			for (BizRequestDetail requestDetail : requestDetailList) {
				if (requestDetail.getBizPoHeader() == null) {
					bizRequestHeader.setPoSource("poHeaderSource");
				}
				BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
				requestDetail.setSkuInfo(skuInfo);
				reqDetailList.add(requestDetail);
			}
			if (requestDetailList.size() == 0) {
				bizRequestHeader.setPoSource("poHeaderSource");
			}
		}

		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr())) {
			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
			model.addAttribute("requestOrderProcess", requestOrderProcess);
		}

		if (bizRequestHeader.getId() != null && bizRequestHeader.getId() != 0) {
			BizOrderStatus bizOrderStatus = new BizOrderStatus();
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizRequestHeader.getId());
			bizOrderStatus.setOrderHeader(bizOrderHeader);
			bizOrderStatus.setOrderType(BizOrderStatus.OrderType.REQUEST.getType());
			List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
			statusList.sort((o1, o2) -> o1.getCreateDate().compareTo(o2.getCreateDate()));

			Map<Integer, ReqHeaderStatusEnum> statusMap = ReqHeaderStatusEnum.getStatusMap();

			model.addAttribute("statusList", statusList);
			model.addAttribute("statusMap", statusMap);
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
		Integer bizStatus = bizRequestHeader.getBizStatus();
		bizRequestHeader.setBizStatus(Integer.parseInt(checkStatus));
		boolean boo=false;
		try {
			if(bizRequestHeader.getRemarkReject()!=null && !bizRequestHeader.getRemarkReject().equals("adopt")){
				if(bizRequestHeader.getRemark()!=null && bizRequestHeader.getRemark().contains(":驳回原因：")){
					bizRequestHeader.setRemark(bizRequestHeader.getRemark()+bizRequestHeader.getRemarkReject());
				}else{
					bizRequestHeader.setRemark(bizRequestHeader.getRemark()+"\n"+":驳回原因："+bizRequestHeader.getRemarkReject());
				}
			}else{
				if(bizRequestHeader.getRemark()!=null && bizRequestHeader.getRemark().contains(":驳回原因：")){
					String b="";
					String[] split = bizRequestHeader.getRemark().split("\n:");
					for (int i = 0; i < split.length; i++) {
						if(i==0){
							b= split[i];
							break;
						}
					}
					bizRequestHeader.setRemark(String.valueOf(b));
				}
			}
			bizRequestHeaderService.save(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
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

	/**
	 * 备货清单管理 导出
	 * */
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "requestHeaderExport")
	public String requestHeaderExport(BizRequestHeader bizRequestHeader,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fileName = "备货清单" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			bizRequestHeader.setDataStatus("detailed");
			List<BizRequestHeader> list = bizRequestHeaderService.findListExport(bizRequestHeader);
			//1备货清单
			List<List<String>> data = new ArrayList<List<String>>();
			//2备货清单,商品
			List<List<String>> skuData = new ArrayList<List<String>>();
			if(list.size()!=0){
				for(BizRequestHeader header:list){
					List<String> headerListData = new ArrayList();
					List<BizRequestDetail> reqDetailList=Lists.newArrayList();
					BizRequestDetail bizRequestDetail=new BizRequestDetail();
					bizRequestDetail.setRequestHeader(header);
					List<BizRequestDetail> requestDetailList=bizRequestDetailService.findList(bizRequestDetail);
					if(requestDetailList.size()!=0){
						for(BizRequestDetail requestDetail:requestDetailList){
							BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
							requestDetail.setSkuInfo(skuInfo);
							requestDetail.setRequestHeader(header);
							reqDetailList.add(requestDetail);
						}
					}
					//备货单遍历
					headerListData.add(header.getReqNo());
					if(header.getFromOffice()!=null && header.getFromOffice().getName()!=null){
						//采购中心
						headerListData.add(String.valueOf(header.getFromOffice().getName()));
					}else{
						headerListData.add("");
					}
					//期望收货时间
					headerListData.add(String.valueOf(sdf.format(header.getRecvEta())));
					//	备货商品数量
					headerListData.add(String.valueOf(header.getReqQtys()==null?"":header.getReqQtys()));
					//备货商品总价
					headerListData.add(String.valueOf(header.getTotalMoney()==null?"":header.getTotalMoney()));
					//已收保证金
					headerListData.add(String.valueOf(header.getRecvTotal()==null?"":header.getRecvTotal()));
					headerListData.add(String.valueOf(header.getRecvQtys()==null?"":header.getRecvQtys()));
					headerListData.add(String.valueOf(header.getRemark()==null?"":header.getRemark()));
					Dict dict = new Dict();
					dict.setDescription("备货单业务状态");
					dict.setType("biz_req_status");
					List<Dict> dictList = dictService.findList(dict);
					for (Dict bizDict : dictList) {
						if(bizDict.getValue().equals(String.valueOf(header.getBizStatus()))){
							//业务状态
							headerListData.add(String.valueOf(bizDict.getLabel()));
							break;
						}
					}
					headerListData.add(String.valueOf(sdf.format(header.getCreateDate())));
					if(header.getCreateBy()!=null && header.getCreateBy().getName()!=null){
						headerListData.add(String.valueOf(header.getCreateBy().getName()));
					}else{
						headerListData.add("");
					}
					data.add(headerListData);
					if(reqDetailList.size()!=0){
						reqDetailList.forEach(detail->{
							List<String> detailListData = new ArrayList();
							//商品遍历
							detailListData.add(String.valueOf(detail.getRequestHeader().getReqNo()));
							if(detail.getSkuInfo()!=null && detail.getSkuInfo().getProductInfo()!=null){
								//产品名称，品牌名称
								detailListData.add(String.valueOf(detail.getSkuInfo().getProductInfo().getName()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getProductInfo().getBrandName()));
							}else{
								detailListData.add("");
								detailListData.add("");
							}
							if(detail.getSkuInfo()!=null && detail.getSkuInfo().getName()!=null || detail.getSkuInfo().getPartNo()!=null || detail.getSkuInfo().getItemNo()!=null){
								//商品名称，商品编号，商品货号
								detailListData.add(String.valueOf(detail.getSkuInfo().getName()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getPartNo()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getItemNo()));
							}else{
								detailListData.add("");
								detailListData.add("");
								detailListData.add("");
							}
							if(detail.getSkuInfo()!=null && detail.getSkuInfo().getSkuPropertyInfos()!=null || detail.getSkuInfo().getBuyPrice()!=null){
								//商品属性，工厂价
								detailListData.add(String.valueOf(detail.getSkuInfo().getSkuPropertyInfos()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getBuyPrice()));
							}else{
								detailListData.add("");
								detailListData.add("");
							}
							detailListData.add(String.valueOf(detail.getReqQty()==null?"":detail.getReqQty()));
							if(detail.getRequestHeader()!=null && detail.getRequestHeader().getRecvEta()!=null){
								detailListData.add(String.valueOf(sdf.format(detail.getRequestHeader().getRecvEta())));
							}else{
								detailListData.add("");
							}
							skuData.add(detailListData);
						});
					}
				}
			}
			String[] headers = {"备货单号", "采购中心","期望收货时间", "备货商品数量", "备货商品总价","已收保证金","已到货数量", "备注", "业务状态","下单时间","申请人"};
			String[] details = {"备货单号", "产品名称", "品牌名称", "商品名称","商品编码", "商品货号", "商品属性", "工厂价", "申报数量","期望收货时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "备货单数据", headers, data, fileName);
			eeu.exportExcel(workbook, 1, "商品数据", details, skuData, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出备货清单数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/biz/request/bizRequestHeader/list";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:audit")
	@RequestMapping(value = "audit")
	@ResponseBody
	public String audit(int id, String currentType, int auditType, String description) {
		return bizRequestHeaderService.audit(id, currentType, auditType, description);
	}
}