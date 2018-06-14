/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 采购订单表Controller
 *
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
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private DictService dictService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BizPoHeaderController.class);

    private static final String VEND_IMG_TABLE_NAME = "biz_vend_info";


    @ModelAttribute
    public BizPoHeader get(@RequestParam(required = false) Integer id) {
        BizPoHeader entity = null;
        if (id != null) {
            entity = bizPoHeaderService.get(id);

            if (entity.getCommonProcess() != null && entity.getCommonProcess().getId() != null) {
                List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
                bizPoHeaderService.getCommonProcessListFromDB(entity.getCommonProcess().getId(), commonProcessList);
                Collections.reverse(commonProcessList);
                entity.setCommonProcessList(commonProcessList);
            }

            BizPoDetail bizPoDetail = new BizPoDetail();
            bizPoDetail.setPoHeader(entity);
            List<BizPoDetail> poDetailList = bizPoDetailService.findList(bizPoDetail);
            List<BizPoDetail> poDetails = Lists.newArrayList();
            for (BizPoDetail poDetail : poDetailList) {
                BizSkuInfo bizSkuInfo = poDetail.getSkuInfo();
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(bizSkuInfo.getId()));
                poDetail.setSkuInfo(skuInfo);
                poDetails.add(poDetail);
            }
            entity.setPoDetailList(poDetails);
            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
            bizPoOrderReq.setPoHeader(entity);
            List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
//			List<Map<String,Integer>> poOrderReqs= Lists.newArrayList();
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            BizRequestDetail bizRequestDetail = new BizRequestDetail();
            Map<Integer, ArrayList<BizPoOrderReq>> map = new HashMap<>();
            Map<String, Integer> mapSource = new HashMap<>();
            for (BizPoOrderReq poOrderReq : poOrderReqList) {
                if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())) {
                    BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(poOrderReq.getSoId());
                    String numKey = bizOrderHeader.getOrderNum();
                    if (mapSource.containsKey(numKey)) {
                        int count = mapSource.get(numKey);
                        mapSource.remove(numKey);
                        mapSource.put(numKey, count + 1);
                    } else {
                        mapSource.put(numKey, 1);
                    }
                    poOrderReq.setOrderHeader(bizOrderHeader);
                    bizOrderDetail.setOrderHeader(bizOrderHeader);
                    bizOrderDetail.setLineNo(poOrderReq.getSoLineNo());
                    List<BizOrderDetail> bizOrderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                    if (bizOrderDetailList != null && bizOrderDetailList.size() != 0) {
                        BizOrderDetail orderDetail = bizOrderDetailList.get(0);
                        Integer key = orderDetail.getSkuInfo().getId();
                        if (map.containsKey(key)) {
                            ArrayList<BizPoOrderReq> bizPoOrderReqList = map.get(key);

                            map.remove(key);

                            String orderNumStr = bizOrderHeader.getOrderNum();
                            poOrderReq.setOrderNumStr(orderNumStr);
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(orderDetail.getSkuInfo().getId(), bizPoOrderReqList);
                        } else {
                            ArrayList<BizPoOrderReq> bizPoOrderReqList = Lists.newArrayList();
                            String orderNumStr = bizOrderHeader.getOrderNum();
                            poOrderReq.setOrderNumStr(orderNumStr);
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(orderDetail.getSkuInfo().getId(), bizPoOrderReqList);
                        }

                    }
                } else if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())) {
                    BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(poOrderReq.getSoId());
                    String reqKey = bizRequestHeader.getReqNo();
                    if (mapSource.containsKey(reqKey)) {
                        int count = mapSource.get(reqKey);
                        mapSource.remove(reqKey);
                        mapSource.put(reqKey, count + 1);
                    } else {
                        mapSource.put(reqKey, 1);
                    }
                    poOrderReq.setRequestHeader(bizRequestHeader);
                    bizRequestDetail.setRequestHeader(bizRequestHeader);
                    bizRequestDetail.setLineNo(poOrderReq.getSoLineNo());
                    List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                    if (requestDetailList != null && requestDetailList.size() != 0) {
                        BizRequestDetail requestDetail = requestDetailList.get(0);
                        Integer key = requestDetail.getSkuInfo().getId();
                        if (map.containsKey(key)) {

                            ArrayList<BizPoOrderReq> bizPoOrderReqList = map.get(key);
                            map.remove(key);
                            poOrderReq.setOrderNumStr(bizRequestHeader.getReqNo());
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(requestDetail.getSkuInfo().getId(), bizPoOrderReqList);

                        } else {
                            String orderNumStr = bizRequestHeader.getReqNo();
                            poOrderReq.setOrderNumStr(orderNumStr);
                            ArrayList<BizPoOrderReq> bizPoOrderReqList = Lists.newArrayList();
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(requestDetail.getSkuInfo().getId(), bizPoOrderReqList);
                        }


                    }
                }

                //	poOrderReqs.add(mapSource);
            }

            entity.setOrderSourceMap(mapSource);


            entity.setOrderNumMap(map);
        }
        if (entity == null) {
            entity = new BizPoHeader();
        }
        return entity;
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizPoHeader bizPoHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (roleList.contains(role)) {
            bizPoHeader.setVendOffice(user.getCompany());
        }

        Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<BizPoHeader>(request, response), bizPoHeader);
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }

        List<PurchaseOrderProcessConfig.PurchaseOrderProcess> processList = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessList();

        model.addAttribute("roleSet", roleSet);
        model.addAttribute("processList", processList);
        model.addAttribute("roleEnNameSet", roleEnNameSet);
        model.addAttribute("page", page);
        model.addAttribute("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());
        return "modules/biz/po/bizPoHeaderList";
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = "form")
    public String form(BizPoHeader bizPoHeader, Model model, String prewStatus, String type) {
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null && bizPoHeader.getDeliveryOffice().getId() != 0) {
            Office office = officeService.get(bizPoHeader.getDeliveryOffice().getId());
            if ("8".equals(office.getType())) {
                bizPoHeader.setDeliveryStatus(0);
            } else {
                bizPoHeader.setDeliveryStatus(1);
            }
        }

        if ("audit".equalsIgnoreCase(type) && bizPoHeader.getCommonProcess() != null) {
            PurchaseOrderProcessConfig.PurchaseOrderProcess purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizPoHeader.getCommonProcess().getType()));
            model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
        }

        if (bizPoHeader.getVendOffice() != null && bizPoHeader.getVendOffice().getBizVendInfo() != null) {
            CommonImg compactImg = new CommonImg();
            compactImg.setImgType(ImgEnum.VEND_COMPACT.getCode());
            compactImg.setObjectId(bizPoHeader.getVendOffice().getId());
            compactImg.setObjectName(VEND_IMG_TABLE_NAME);
            List<CommonImg> compactImgList = commonImgService.findList(compactImg);
            model.addAttribute("compactImgList", compactImgList);

            CommonImg identityCardImg = new CommonImg();
            identityCardImg.setImgType(ImgEnum.VEND_IDENTITY_CARD.getCode());
            identityCardImg.setObjectId(bizPoHeader.getVendOffice().getId());
            identityCardImg.setObjectName(VEND_IMG_TABLE_NAME);
            List<CommonImg> identityCardImgList = commonImgService.findList(identityCardImg);
            model.addAttribute("identityCardImgList", identityCardImgList);

        }

        List<Role> roleList = UserUtils.getUser().getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }

        model.addAttribute("roleSet", roleSet);

        model.addAttribute("bizPoHeader", bizPoHeader);
        model.addAttribute("type", type);
        model.addAttribute("prewStatus", prewStatus);
        return "modules/biz/po/bizPoHeaderForm";
    }

    @RequiresPermissions("biz:po:bizPoHeader:audit")
    @RequestMapping(value = "audit")
    @ResponseBody
    public String audit(int id, String currentType, int auditType, String description) {
        return bizPoHeaderService.auditPo(id, currentType, auditType, description);
    }

    @RequiresPermissions("biz:po:bizpopaymentorder:bizPoPaymentOrder:audit")
    @RequestMapping(value = "auditPay")
    @ResponseBody
    public String auditPay(int id, String currentType, int auditType, String description, BigDecimal money) {
        return bizPoHeaderService.auditPay(id, currentType, auditType, description, money);
    }


    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "save")
    public String save(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes, String prewStatus, String type) {
        if ("audit".equalsIgnoreCase(type)) {
            String msg = bizPoHeaderService.genPaymentOrder(bizPoHeader);
            addMessage(redirectAttributes, msg);
            return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
        }

        if (!beanValidator(model, bizPoHeader)) {
            return form(bizPoHeader, model, prewStatus, null);
        }

        Set<Integer> poIdSet= bizPoHeaderService.findPrewPoHeader(bizPoHeader);
        if(poIdSet.size()==1){
            addMessage(redirectAttributes, "prew".equals(prewStatus) ? "采购订单预览信息" : "保存采购订单成功");
            return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/form/?id=" + poIdSet.iterator().next() + "&prewStatus=" + prewStatus;
        }
        int deOfifceId = 0;
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null) {
            deOfifceId = bizPoHeader.getDeliveryOffice().getId();
        }
        String poNo = "0";
        bizPoHeader.setOrderNum(poNo);
        bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
        bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
        Integer id = bizPoHeader.getId();
        bizPoHeaderService.save(bizPoHeader);
        if (id == null) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
        }
        if (bizPoHeader.getOrderNum() == null || "0".equals(bizPoHeader.getOrderNum())) {
            poNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO, deOfifceId, bizPoHeader.getVendOffice().getId(), bizPoHeader.getId());
            bizPoHeader.setOrderNum(poNo);
            bizPoHeaderService.savePoHeader(bizPoHeader);
        }

        addMessage(redirectAttributes, "prew".equals(prewStatus) ? "采购订单预览信息" : "保存采购订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/form/?id=" + bizPoHeader.getId() + "&prewStatus=" + prewStatus;
    }

    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "savePoHeader")
    public String savePoHeader(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes, String prewStatus, String type) {
        if ("createPay".equalsIgnoreCase(type)) {
            String msg = bizPoHeaderService.genPaymentOrder(bizPoHeader);
            addMessage(redirectAttributes, msg);
            return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
        }

        if (!beanValidator(model, bizPoHeader)) {
            return form(bizPoHeader, model, prewStatus, null);
        }
        bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
        bizPoHeaderService.savePoHeader(bizPoHeader);

        addMessage(redirectAttributes, "保存采购订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
    }

    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "delete")
    public String delete(BizPoHeader bizPoHeader, RedirectAttributes redirectAttributes) {
        bizPoHeaderService.delete(bizPoHeader);
        addMessage(redirectAttributes, "删除采购订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
    }

    @RequiresPermissions("biz:po:bizPoHeader:audit")
    @RequestMapping(value = "payOrder")
    @ResponseBody
    public String payOrder(RedirectAttributes redirectAttributes, Integer poHeaderId, Integer paymentOrderId, BigDecimal payTotal, String img) {
        return bizPoHeaderService.payOrder(poHeaderId, paymentOrderId, payTotal, img);
    }

    @RequiresPermissions("biz:po:bizPoHeader:audit")
    @RequestMapping(value = "startAudit")
    @ResponseBody
    public String startAudit(int id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, @RequestParam(defaultValue = "1") Integer auditType, String desc) {
        return bizPoHeaderService.startAudit(id, prew, prewPayTotal, prewPayDeadline, auditType, desc);
    }

    /**
     * 采购单导出
     * @param bizPoHeader
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = "poHeaderExport")
    public String poHeaderExport(BizPoHeader bizPoHeader,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName = "采购单" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizPoHeader> list = bizPoHeaderService.findList(bizPoHeader);
            //1采购单整体数据
            List<List<String>> data = new ArrayList<List<String>>();
            if(list != null && !list.isEmpty()){
                for(BizPoHeader poHeader:list){
//                    List<BizPoDetail> poDetailList=Lists.newArrayList();
                    BizPoDetail bizPoDetail=new BizPoDetail();
                    bizPoDetail.setPoHeader(poHeader);
                    List<BizPoDetail> poDetailList = bizPoDetailService.findList(bizPoDetail);
                    if(poDetailList != null && !poDetailList.isEmpty()){
                        for(BizPoDetail poDetail:poDetailList){
                            BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(poDetail.getSkuInfo().getId()));
                            poDetail.setSkuInfo(skuInfo);
                            poDetail.setPoHeader(poHeader);
                        }
                    }
                    for (BizPoDetail poDetail:poDetailList) {
                        List<String> headerListData = new ArrayList();
                        //采购单遍历
                        headerListData.add(poHeader.getOrderNum());
                        //供应商
                        headerListData.add(poHeader.getVendOffice().getName());
                        //采购总价
                        headerListData.add(String.valueOf(poHeader.getTotalDetail()));
                        //交易费用
                        headerListData.add(String.valueOf(poHeader.getTotalExp()));
                        //应付金额
                        headerListData.add(String.valueOf(poHeader.getTotalDetail() + poHeader.getTotalExp()));
                        //累计支付金额
                        headerListData.add(String.valueOf(poHeader.getPayTotal()));
                        //支付比例
                        headerListData.add(String.valueOf(poHeader.getPayTotal().multiply(new BigDecimal(100)).divide(new BigDecimal(poHeader.getTotalDetail() + poHeader.getTotalExp()), 2, RoundingMode.HALF_UP)) + "%");
                        //订单状态
                        Dict dict = new Dict();
                        dict.setType("biz_po_status");
                        List<Dict> dictList = dictService.findList(dict);
                        String str = "";
                        for (Dict bizDict : dictList) {
                            if (bizDict.getValue().equals(String.valueOf(poHeader.getBizStatus()))) {
                                //业务状态
                                str = bizDict.getLabel();
//                                headerListData.add(String.valueOf(bizDict.getLabel()));
                                break;
                            }
                        }
                        headerListData.add(str);
//                    //采购单来源
//                    headerListData.add(String.valueOf());
                        //审核状态
                        CommonProcessEntity commonProcessEntity = commonProcessService.get(poHeader.getProcessId());
                        if (commonProcessEntity != null) {
                            headerListData.add(commonProcessEntity.getPurchaseOrderProcess().getName()==null?"当前无审批流程":commonProcessEntity.getPurchaseOrderProcess().getName());
                        }else {
                            headerListData.add("当前无审批流程");
                        }
                        //创建时间
                        headerListData.add(String.valueOf(sdf.format(poHeader.getCreateDate())));
                        //所属单号
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        bizPoOrderReq.setPoHeader(poHeader);
                        bizPoOrderReq.setPoLineNo(poDetail.getLineNo());
                        List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                        BizOrderHeader bizOrderHeader = new BizOrderHeader();
                        BizRequestHeader bizRequestHeader = new BizRequestHeader();
                        StringBuffer nums = new StringBuffer();
                        for (BizPoOrderReq poOrderReq : poOrderReqList) {
                            if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())) {
                                bizOrderHeader = bizOrderHeaderService.get(poOrderReq.getSoId());
                                BizOrderDetail bizOrderDetail = new BizOrderDetail();
                                bizOrderDetail.setOrderHeader(bizOrderHeader);
                                bizOrderDetail.setLineNo(poOrderReq.getSoLineNo());
                                List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                                if (orderDetailList != null && !orderDetailList.isEmpty()) {
                                    BizOrderDetail orderDetail = orderDetailList.get(0);
                                    if (orderDetail.getSkuInfo() != null) {
                                        if (orderDetail.getSkuInfo().getId().equals(poDetail.getSkuInfo().getId())) {
                                            nums.append(bizOrderHeader.getOrderNum()).append(";");
                                        }
                                    }
                                }
                            }
                            if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())) {
                                bizRequestHeader = bizRequestHeaderService.get(poOrderReq.getSoId());
                                BizRequestDetail requestDetail = new BizRequestDetail();
                                requestDetail.setRequestHeader(bizRequestHeader);
                                requestDetail.setLineNo(poOrderReq.getSoLineNo());
                                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(requestDetail);
                                if (requestDetailList != null && !requestDetailList.isEmpty()) {
                                    BizRequestDetail bizRequestDetail = requestDetailList.get(0);
                                    if (bizRequestDetail.getSkuInfo() != null && bizRequestDetail.getSkuInfo().getId().equals(poDetail.getSkuInfo().getId())) {
                                        nums.append(bizRequestHeader.getReqNo()).append(";");
                                    }
                                }
                            }
                        }
                        if (!"".equals(nums.toString())) {
                            headerListData.add(nums.toString());
                        }else {
                            headerListData.add("");
                        }
                        //商品名称
                        headerListData.add(String.valueOf(poDetail.getSkuInfo().getName()));
                        //商品货号
                        headerListData.add(String.valueOf(poDetail.getSkuInfo().getItemNo()));
                        //采购数量
                        headerListData.add(String.valueOf(poDetail.getOrdQty()));
                        //已供货数量
                        headerListData.add(String.valueOf(poDetail.getSendQty()));
                        //工厂价
                        headerListData.add(String.valueOf(poDetail.getSkuInfo().getBuyPrice()));
                        data.add(headerListData);
                    }
                }
            }
            String[] headers = {"采购单号", "供应商", "采购总价", "交易费用","应付金额", "累计支付金额", "支付比例","订单状态","审核状态","创建时间","所属单号","商品名称","商品货号","采购数量","已供货数量","工厂价"};
            ExportExcelUtils eeu = new ExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, "采购单数据", headers, data, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        }catch (Exception e){
            logger.error("可能没有采购单详情",e);
            addMessage(redirectAttributes, "导出采购单数据失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/biz/po/bizPoHeader/list";

    }


    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "cancel")
    @ResponseBody
    public String cancel(int id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        bizPoHeaderService.updateBizStatus(id, BizPoHeader.BizStatus.CANCEL);
        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), id);
        return "取消采购订单成功";
    }
}