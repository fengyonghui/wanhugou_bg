/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.thread.ThreadPoolManager;
import com.wanhutong.backend.common.utils.*;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.logistic.LogisticEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.inventory.BizDetailInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizPhotoOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wanhutong.backend.modules.sys.utils.DictUtils.getDictList;

/**
 * 发货单Controller
 * @author 张腾飞
 * @version 2018-03-05
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInvoice")
public class BizInvoiceController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizInvoiceController.class);
    private static final Logger LOGISTICS_LOGGER = LoggerFactory.getLogger("logistics");

    private Lock lock = new ReentrantLock();

	@Autowired
	private BizInvoiceService bizInvoiceService;
	@Autowired
	private BizLogisticsService bizLogisticsService;
	@Autowired
    private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
    private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
    private BizOrderDetailService bizOrderDetailService;
	@Autowired
    private BizRequestDetailService bizRequestDetailService;
	@Autowired
    private BizDetailInvoiceService bizDetailInvoiceService;
	@Autowired
    private SystemService systemService;
	@Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private DictService dictService;
    @Autowired
    private BizPhotoOrderHeaderService bizPhotoOrderHeaderService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
    @Autowired
    private OfficeService officeService;

    private static final String DEF_EN_NAME = "shipper";


	@ModelAttribute
	public BizInvoice get(@RequestParam(required=false) Integer id) {
		BizInvoice entity = null;
		if (id!=null){
			entity = bizInvoiceService.get(id);
		}
		if (entity == null){
			entity = new BizInvoice();
		}
		return entity;
	}

    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizInvoice bizInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
//	    bizInvoice.setBizStatus(Integer.parseInt(bizStatu));
//	    bizInvoice.setShip(Integer.parseInt(ship));
        Page<BizInvoice> page = bizInvoiceService.findPage(new Page<BizInvoice>(request, response), bizInvoice);
        Map<Integer, List<Map<Integer, String>>> orderMap = new HashMap<Integer, List<Map<Integer, String>>>();
        List<Callable<Pair<Boolean, String>>> tasks = new ArrayList<>();
        for (BizInvoice b : page.getList()) {
            tasks.add(new Callable<Pair<Boolean, String>>() {
                @Override
                public Pair<Boolean, String> call() {
                    try {
                        lock.lock();
                        List<BizOrderHeader> orderHeaderList = bizInvoiceService.findOrderHeaderByInvoiceId(b.getId());
                        String orderNums = "";
                        if (CollectionUtils.isNotEmpty(orderHeaderList)){
                            List<Map<Integer, String>> orderIdNumMapList = new ArrayList<Map<Integer, String>>();
                            for (BizOrderHeader bizOrderHeader : orderHeaderList) {
                                if (bizOrderHeader == null || bizOrderHeader.getOrderNum() == null) {
                                    continue;
                                }
                                String orderNum = bizOrderHeader.getOrderNum();
                                orderNums += orderNum + ",";
                                Map<Integer, String> orderIdNumMap = new HashMap<Integer, String>();
                                orderIdNumMap.put(bizOrderHeader.getId(), bizOrderHeader.getOrderNum());
                                orderIdNumMapList.add(orderIdNumMap);
                            }
                            orderMap.put(b.getId(), orderIdNumMapList);
                        }
                        if (StringUtils.isNotBlank(orderNums)) {
                            b.setOrderHeaders(orderNums.substring(0, orderNums.length()-1));
                        }
                    } catch (Exception e) {
                        logger.error("多线程给发货单添加订单号失败", e);
                    } finally {
                        lock.unlock();
                    }
                    return Pair.of(Boolean.TRUE, "操作成功");
                }
            });
        }
        try {
            ThreadPoolManager.getDefaultThreadPool().invokeAll(tasks);
        } catch (InterruptedException e) {
            LOGGER.error("init order list data error", e);
        }


        model.addAttribute("orderMap", orderMap);
        model.addAttribute("page", page);
        model.addAttribute("targetPage", bizInvoice.getTargetPage() == null ? "" : bizInvoice.getTargetPage());
//		model.addAttribute("ship",ship);
//		model.addAttribute("bizStatu",bizStatu);
        String targetPage = bizInvoice.getTargetPage();
        if (targetPage != null && "logistics".equals(targetPage) ) {
            return "modules/biz/inventory/bizInvoiceLogisticsList";
        }
        return "modules/biz/inventory/bizInvoiceList";
    }

	@ResponseBody
	@RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "findOrderDetail")
    public List<BizOrderDetail> findOrderDetail(Integer id){
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(id);
        BizOrderDetail bizOrderDetail = new BizOrderDetail();
        bizOrderDetail.setOrderHeader(bizOrderHeader);
        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
        return orderDetailList;
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "findRequestDetail")
    public List<BizRequestDetail> findRequestDetail(Integer id){
        BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(id);
        BizRequestDetail bizRequestDetail = new BizRequestDetail();
        bizRequestDetail.setRequestHeader(bizRequestHeader);
        List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
        return requestDetailList;
    }

	@RequiresPermissions("biz:inventory:bizInvoice:view")
	@RequestMapping(value = "form")
	public String form(BizInvoice bizInvoice, Model model) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        boolean flag = false;
        for (Role role:roleList) {
            if (role.getEnname().equals(RoleEnNameEnum.DEPT.getState())) {
                flag = true;
                break;
            }
        }
        List<User> inspectorList = systemService.findUserByRoleEnName(RoleEnNameEnum.INSPECTOR.getState());
        model.addAttribute("inspectorList",inspectorList);
        if (StringUtils.isBlank(bizInvoice.getCarrier())) {
            bizInvoice.setCarrier(user.getName());
        }

		model.addAttribute("bizInvoice", bizInvoice);
        if (flag && bizInvoice.getBizStatus()==1) {
            List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
            model.addAttribute("userList", userList);
        }else if (flag && bizInvoice.getBizStatus()==0) {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
            model.addAttribute("userList", userList);
        }else {
            model.addAttribute("userList",null);
        }
		model.addAttribute("bizOrderHeader",new BizOrderHeader());
		if(bizInvoice.getShip() != null && bizInvoice.getShip()==1 ){
			model.addAttribute("bizRequestHeader",new BizRequestHeader());
		    return "modules/biz/inventory/bizInvoiceRequestForm";
        }
        //获取集货地点列表
        List<Dict> collLocateList = getOfficeList(user);
        model.addAttribute("collLocateList", collLocateList);
		return "modules/biz/inventory/bizInvoiceForm";
	}

	@RequiresPermissions("biz:inventory:bizInvoice:view")
	@RequestMapping(value = "formV2")
	public String formV2(HttpServletRequest request, int id, BizInvoice bizInvoice, Model model) {
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(id);
//        List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
        model.addAttribute("userList", Lists.newArrayList(UserUtils.getUser()));
        bizInvoice.setId(null);
        request.setAttribute("orderNum", bizOrderHeader.getOrderNum());
        request.setAttribute("bizStatus", bizOrderHeader.getBizStatus());

        return "modules/biz/inventory/bizInvoiceFormV2";
	}

    /**
     * 订单所属发货单详情
     * @param bizInvoice
     * @param model
     * @return
     */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "invoiceOrderDetail")
    public String invoiceOrderDetail(BizInvoice bizInvoice, String str, String source, Model model) {
        bizInvoice.setStr(str);
        CommonImg commonImg=new CommonImg();
        commonImg.setImgType(ImgEnum.LOGISTICS_TYPE.getCode());
        commonImg.setObjectId(bizInvoice.getId());
        commonImg.setObjectName("biz_invoice");
        if(bizInvoice.getId()!=null){
            String photos="";
            List<CommonImg> imgList=commonImgService.findList(commonImg);
            model.addAttribute("imgList",imgList);
            for(CommonImg img:imgList){
                photos+="|"+img.getImgServer()+img.getImgPath();
            }
            bizInvoice.setImgUrl(photos);
        }
        BizLogistics bizLogistics = new BizLogistics();
        List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
        bizDetailInvoice.setInvoice(bizInvoice);
        List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
        boolean photoFlag = false;
        if (DetailInvoiceList != null && !DetailInvoiceList.isEmpty()){
            for (BizDetailInvoice detailInvoice:DetailInvoiceList) {
                BizOrderHeader bizorderHeader = detailInvoice.getOrderHeader();
                BizOrderHeader orderHeader = bizOrderHeaderService.get(bizorderHeader.getId());
                if (!orderHeader.getOrderType().equals(BizOrderTypeEnum.PHOTO_ORDER.getState())) {
                    List<BizOrderDetail> orderDetailList = bizOrderDetailService.findOrderDetailList(bizInvoice.getId());
                    model.addAttribute("orderDetailList",orderDetailList);
                    break;
                } else {
                    photoFlag = true;
                    List<BizOrderHeader> photoOrderList = bizPhotoOrderHeaderService.findPhotoOrderList(bizInvoice.getId());
                    model.addAttribute("photoOrderList",photoOrderList);
                    break;
                }
            }
        }
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        boolean flag = false;
        for (Role role:roleList) {
            if (role.getEnname().equals(RoleEnNameEnum.DEPT.getState())) {
                flag = true;
                break;
            }
        }
        List<User> inspectorList = systemService.findUserByRoleEnName(RoleEnNameEnum.INSPECTOR.getState());
        model.addAttribute("inspectorList",inspectorList);
        if (flag && bizInvoice.getBizStatus()==1) {
            List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
            model.addAttribute("userList", userList);
        }else if (flag && bizInvoice.getBizStatus()==0) {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
            model.addAttribute("userList", userList);
        }else {
            model.addAttribute("userList",null);
        }
        BizDetailInvoice detailInvoice = new BizDetailInvoice();
        detailInvoice.setInvoice(bizInvoice);
        List<BizDetailInvoice> list = bizDetailInvoiceService.findList(detailInvoice);
        BizDetailInvoice deInvoice = list.get(0);
        BizOrderHeader orderHeader = bizOrderHeaderService.get(deInvoice.getOrderHeader().getId());
        if (orderHeader != null) {
            model.addAttribute("orderHeader",orderHeader);
        }

        model.addAttribute("logisticsList",logisticsList);
        model.addAttribute("source",source);
//        model.addAttribute("orderHeaderList",orderHeaderList);
        model.addAttribute("bizInvoice", bizInvoice);
        if (photoFlag) {
            return "modules/biz/inventory/bizDeliverGoodsDeForm";
        } else {
            //获取集货地点列表
            List<Dict> collLocateList = getOfficeList(user);
            model.addAttribute("collLocateList", collLocateList);
            return "modules/biz/inventory/bizInvoiceDeForm";
        }
    }

    /**
     * 备货单所属发货单详情
     * @param bizInvoice
     * @param model
     * @return
     */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "invoiceRequestDetail")
    public String invoiceRequestDetail(BizInvoice bizInvoice,String str, String source, Model model) {
        bizInvoice.setStr(str);
        CommonImg commonImg=new CommonImg();
        commonImg.setImgType(ImgEnum.LOGISTICS_TYPE.getCode());
        commonImg.setObjectId(bizInvoice.getId());
        commonImg.setObjectName("biz_invoice");
        if(bizInvoice.getId()!=null){
            String photos="";
            List<CommonImg> imgList=commonImgService.findList(commonImg);
            model.addAttribute("imgList",imgList);
            for(CommonImg img:imgList){
                photos+="|"+img.getImgServer()+img.getImgPath();
            }
            bizInvoice.setImgUrl(photos);
        }
        BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
        bizDetailInvoice.setInvoice(bizInvoice);
        List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
        List<BizRequestHeader> requestHeaderList = new ArrayList<>();
        if (DetailInvoiceList != null && !DetailInvoiceList.isEmpty()){
            for (BizDetailInvoice detailInvoice:DetailInvoiceList) {
                BizRequestHeader bizRequestHeader = detailInvoice.getRequestHeader();
                BizRequestHeader requestHeader = bizRequestHeaderService.get(bizRequestHeader.getId());
                BizRequestDetail bizRequestDetail = new BizRequestDetail();
                bizRequestDetail.setRequestHeader(requestHeader);
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                if (requestDetailList != null && !requestDetailList.isEmpty()){
                    for (BizRequestDetail requestDetail:requestDetailList) {
                        BizSkuInfo skuInfo = bizSkuInfoService.get(requestDetail.getSkuInfo());
                        BizSkuInfo sku = bizSkuInfoService.findListProd(skuInfo);
                        requestDetail.setSkuInfo(sku);
                    }
                    requestHeader.setRequestDetailList(requestDetailList);
                }
                requestHeaderList.add(requestHeader);
            }
        }
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        boolean flag = false;
        for (Role role:roleList) {
            if (role.getEnname().equals(RoleEnNameEnum.DEPT.getState())) {
                flag = true;
                break;
            }
        }
        List<User> inspectorList = systemService.findUserByRoleEnName(RoleEnNameEnum.INSPECTOR.getState());
        model.addAttribute("inspectorList",inspectorList);
        if (flag && bizInvoice.getBizStatus()==1) {
            List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
            model.addAttribute("userList", userList);
        }else if (flag && bizInvoice.getBizStatus()==0) {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
            model.addAttribute("userList", userList);
        }else {
            model.addAttribute("userList",null);
        }
        BizLogistics bizLogistics = new BizLogistics();
        List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        model.addAttribute("logisticsList",logisticsList);
        model.addAttribute("source",source);
        model.addAttribute("requestHeaderList",requestHeaderList);
        model.addAttribute("bizInvoice", bizInvoice);
        return "modules/biz/inventory/bizInvoiceReqDeForm";
    }

	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoice bizInvoice, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInvoice)){
			return form(bizInvoice, model);
		}
		bizInvoiceService.save(bizInvoice);
		addMessage(redirectAttributes, "发货成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage&bizStatus="+bizInvoice.getBizStatus()+"&ship="+bizInvoice.getShip();
	}
	
	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoice bizInvoice, RedirectAttributes redirectAttributes) {
		bizInvoiceService.delete(bizInvoice);
		addMessage(redirectAttributes, "删除发货单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage";
	}

    /**
     * 导出
     * */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "exportList",method = RequestMethod.POST)
    public String exportList(BizInvoice bizInvoice, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName =null;
            Boolean pageFlag = false;
            String targetPage = request.getParameter("targetPage");
            bizInvoice.setTargetPage(targetPage);
            if (StringUtils.isBlank(targetPage)){
                pageFlag = true;
            }
            if(bizInvoice.getBizStatus()!=null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip()!=null && bizInvoice.getShip().equals(1)) {
                fileName = "备货单发货信息数据";
            } else {
                fileName = pageFlag ? "订单发货信息数据" : "物流单信息数据";
            }
            fileName += DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizInvoice> list = bizInvoiceService.findList(bizInvoice);
            //1订单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            for (BizInvoice invoice : list) {
                List<String> headData = new ArrayList<>();
                if (pageFlag) {
                    headData.add(String.valueOf(invoice.getSendNumber()));

                    if (invoice.getLogistics() != null && invoice.getLogistics().getName() != null) {
                        headData.add(String.valueOf(invoice.getLogistics().getName()));
                    } else {
                        headData.add("");
                    }

                    headData.add(String.valueOf(invoice.getFreight() == null ? "" : invoice.getFreight()));
                    headData.add(String.valueOf(invoice.getOperation() == null ? "" : invoice.getOperation()));
                    headData.add(String.valueOf(invoice.getValuePrice() == null ? "" : invoice.getValuePrice()));

                    double price = 0.0;
                    if (invoice.getFreight() != null && invoice.getValuePrice() != null && invoice.getValuePrice() != 0) {
                        price = invoice.getFreight() * 100 / invoice.getValuePrice();
                    }//运费/货值
                    BigDecimal b = new BigDecimal(price);
                    double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                    headData.add(String.valueOf(f1 + "%"));

                    headData.add(String.valueOf(invoice.getCarrier() == null ? "" : invoice.getCarrier()));

                    Dict dict = new Dict();
                    dict.setDescription("物流结算方式");
                    dict.setType("biz_settlement_status");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(invoice.getSettlementStatus()))) {
                            //结算方式
                            headData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    headData.add(String.valueOf(sdf.format(invoice.getSendDate())));
                } else {
                    headData.add(String.valueOf(invoice.getTrackingNumber()));
                    headData.add(String.valueOf(invoice.getLogisticsFreight() == null ? "" : invoice.getLogisticsFreight()));
                    headData.add(String.valueOf(invoice.getLogisticsOperation() == null ? "" : invoice.getLogisticsOperation()));
                    headData.add(String.valueOf(invoice.getLogisticsValuePrice() == null ? "" : invoice.getLogisticsValuePrice()));

                    double price = 0.0;
                    if (invoice.getLogisticsFreight() != null && invoice.getLogisticsValuePrice() != null && invoice.getLogisticsValuePrice() != 0) {
                        price = invoice.getLogisticsFreight() * 100 / invoice.getLogisticsValuePrice();
                    }//运费/货值
                    BigDecimal b = new BigDecimal(price);
                    double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                    headData.add(String.valueOf(f1 + "%"));
                }
                data.add(headData);

                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(invoice);
                if (pageFlag) {
                    List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
                    if (DetailInvoiceList.size() != 0) {
                        for (BizDetailInvoice detailInvoice : DetailInvoiceList) {
                            if (bizInvoice.getBizStatus() != null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip() != null && bizInvoice.getShip().equals(1)) {
                                //备货单发货信息
                                List<BizRequestDetail> requestDetailList = null;
                                BizRequestHeader requestHeader = bizRequestHeaderService.get(detailInvoice.getRequestHeader().getId());
                                if (detailInvoice.getRequestHeader() != null && requestHeader != null) {
                                    BizRequestDetail bizRequestDetail = new BizRequestDetail();
                                    bizRequestDetail.setRequestHeader(requestHeader);
                                    requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                                }
                                if (requestHeader != null && CollectionUtils.isNotEmpty(requestDetailList)) {
                                    for (BizRequestDetail requeDetail : requestDetailList) {
                                        List<String> detaData = new ArrayList<>();
                                        requeDetail.setRequestHeader(requestHeader);
                                        BizSkuInfo skuInfo = bizSkuInfoService.get(requeDetail.getSkuInfo());
                                        BizSkuInfo sku = bizSkuInfoService.findListProd(skuInfo);
                                        requeDetail.setSkuInfo(sku);
                                        detaData.add(String.valueOf(invoice.getSendNumber() == null ? "" : invoice.getSendNumber()));
                                        if (invoice.getLogistics() != null && invoice.getLogistics().getName() != null) {
                                            detaData.add(String.valueOf(invoice.getLogistics().getName()));
                                        } else {
                                            detaData.add("");
                                        }
                                        detaData.add(String.valueOf(requestHeader.getReqNo() == null ? "" : requestHeader.getReqNo()));
                                        if (requestHeader.getFromOffice() != null && requestHeader.getFromOffice().getName() != null) {
                                            detaData.add(String.valueOf(requestHeader.getFromOffice().getName()));
                                        }
                                        Dict detadict = new Dict();
                                        detadict.setDescription("备货单业务状态");
                                        detadict.setType("biz_req_status");
                                        List<Dict> detadictList = dictService.findList(detadict);
                                        if (detadictList.size() != 0) {
                                            for (Dict di : detadictList) {
                                                if (di.getValue().equals(String.valueOf(requestHeader.getBizStatus()))) {
                                                    //业务状态
                                                    detaData.add(String.valueOf(di.getLabel()));
                                                    break;
                                                }
                                            }
                                        } else {
                                            detaData.add("");
                                        }
                                        if (requeDetail.getSkuInfo() != null) {
                                            if (requeDetail.getSkuInfo() != null && requeDetail.getSkuInfo().getName() != null) {
                                                detaData.add(String.valueOf(requeDetail.getSkuInfo().getName()));
                                                detaData.add(String.valueOf(requeDetail.getSkuInfo().getPartNo() == null ? "" : requeDetail.getSkuInfo().getPartNo()));
                                                detaData.add(String.valueOf(requeDetail.getSkuInfo().getSkuPropertyInfos() == null ? "" : requeDetail.getSkuInfo().getSkuPropertyInfos()));
                                            } else {
                                                detaData.add("");
                                                detaData.add("");
                                                detaData.add("");
                                            }
                                            detaData.add(String.valueOf(requeDetail.getReqQty() == null ? "" : requeDetail.getReqQty()));
                                            detaData.add(String.valueOf(requeDetail.getSendQty() == null ? "" : requeDetail.getSendQty()));
                                            detailData.add(detaData);
                                        }
                                    }
                                }

                            } else {
                                BizOrderDetail bizOrderDetail = new BizOrderDetail();
                                List<BizOrderDetail> orderDetailList = null;
                                BizOrderHeader orderHeader = bizOrderHeaderService.get(detailInvoice.getOrderHeader().getId());
                                if (orderHeader != null && !orderHeader.getOrderType().equals(BizOrderTypeEnum.PHOTO_ORDER.getState())) {
                                    bizOrderDetail.setOrderHeader(orderHeader);
                                    orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                                }
                                if (orderHeader != null && orderHeader.getOrderType().equals(BizOrderTypeEnum.PHOTO_ORDER.getState())) {
                                    List<String> detaData = new ArrayList<>();
                                    detaData.add(String.valueOf(invoice.getSendNumber() == null ? "" : invoice.getSendNumber()));
                                    detaData.add((invoice.getLogistics() != null && invoice.getLogistics().getName() != null) ? String.valueOf(invoice.getLogistics().getName()) : "");
                                    detaData.add(orderHeader.getOrderNum() == null ? "" : orderHeader.getOrderNum());
                                    detaData.add((orderHeader.getCustomer() != null && orderHeader.getCustomer().getName() != null) ? String.valueOf(orderHeader.getCustomer().getName()) : "");
                                    detaData.add(getDict(orderHeader.getBizStatus(), "业务状态", "biz_order_status"));
                                    detaData.add("");
                                    detaData.add("");
                                    detaData.add("");
                                    detaData.add("");
                                    detaData.add("");
                                    detailData.add(detaData);
                                }
                                if (orderDetailList != null && orderDetailList.size() != 0) {
                                    for (BizOrderDetail orderDetail : orderDetailList) {
                                        List<String> detaData = new ArrayList<>();
                                        BizSkuInfo sku = bizSkuInfoService.get(orderDetail.getSkuInfo());
                                        BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                                        orderDetail.setSkuInfo(skuInfo);
                                        detaData.add(String.valueOf(invoice.getSendNumber() == null ? "" : invoice.getSendNumber()));
                                        if (invoice.getLogistics() != null && invoice.getLogistics().getName() != null) {
                                            detaData.add(String.valueOf(invoice.getLogistics().getName()));
                                        } else {
                                            detaData.add("");
                                        }
                                        detaData.add(String.valueOf(orderDetail.getOrderHeader().getOrderNum()));
                                        detaData.add((orderHeader.getCustomer() != null && orderHeader.getCustomer().getName() != null) ? String.valueOf(orderHeader.getCustomer().getName()) : "");
                                        detaData.add(getDict(orderHeader.getBizStatus(), "业务状态", "biz_order_status"));
                                        detaData.add(String.valueOf(orderDetail.getSkuName() == null ? "" : orderDetail.getSkuName()));
                                        detaData.add(String.valueOf(orderDetail.getPartNo() == null ? "" : orderDetail.getPartNo()));
                                        detaData.add((orderDetail.getSkuInfo() != null && orderDetail.getSkuInfo().getSkuPropertyInfos() != null) ? String.valueOf(orderDetail.getSkuInfo().getSkuPropertyInfos()) : "");
                                        detaData.add(String.valueOf(orderDetail.getOrdQty() == null ? "" : orderDetail.getOrdQty()));
                                        detaData.add(String.valueOf(orderDetail.getSentQty() == null ? "" : orderDetail.getSentQty()));
                                        detailData.add(detaData);
                                    }
                                }
                            }
                        }
                    } else {
                        List<String> detaData = new ArrayList();
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detailData.add(detaData);
                    }
                } else {
                    List<BizOrderDetail> logisticsDetailList = bizInvoiceService.findLogisticsDetail(invoice);
                    if (logisticsDetailList.size() != 0) {
                        for (BizOrderDetail orderDetail : logisticsDetailList) {
                            List<String> detaData = new ArrayList<>();
                            if (orderDetail.getOrderHeader() != null) {
                                detaData.add(String.valueOf(orderDetail.getOrderHeader().getSendNum() == null ? "" : orderDetail.getOrderHeader().getSendNum()));
                                detaData.add(String.valueOf(orderDetail.getOrderHeader().getOrderNum() == null ? "" : orderDetail.getOrderHeader().getOrderNum()));
                            } else {
                                detaData.add("");
                                detaData.add("");
                            }
                            detaData.add(String.valueOf(orderDetail.getSkuName() == null ? "" : orderDetail.getSkuName()));
                            detaData.add(String.valueOf(orderDetail.getPartNo() == null ? "" : orderDetail.getPartNo()));
                            if (orderDetail.getSkuInfo() != null) {
                                detaData.add(String.valueOf(orderDetail.getSkuInfo().getItemNo() == null ? "" : orderDetail.getSkuInfo().getItemNo()));
                            } else {
                                detaData.add("");
                            }
                            detaData.add(String.valueOf(orderDetail.getBuyPrice() == null ? "" : orderDetail.getBuyPrice()));
                            if (orderDetail.getVendor() != null) {
                                detaData.add(String.valueOf(orderDetail.getVendor().getName() == null ? "" : orderDetail.getVendor().getName()));
                            } else {
                                detaData.add("");
                            }
                            detaData.add(String.valueOf(orderDetail.getUnitPrice() == null ? "" : orderDetail.getUnitPrice()));
                            detaData.add(String.valueOf(orderDetail.getOrdQty() == null ? "" : orderDetail.getOrdQty()));
                            if (orderDetail.getUnitPrice() != null && orderDetail.getOrdQty() != null) {
                                BigDecimal totalPrice = new BigDecimal(orderDetail.getUnitPrice()).multiply(new BigDecimal(orderDetail.getOrdQty())).setScale(1, BigDecimal.ROUND_UP);
                                detaData.add(String.valueOf(totalPrice));
                            } else {
                                detaData.add("");
                            }
                            detaData.add(String.valueOf(orderDetail.getSentQty() == null ? "" : orderDetail.getSentQty()));

                            detaData.add(String.valueOf(orderDetail.getCreateDate() == null ? "" : orderDetail.getCreateDate()));
                            detailData.add(detaData);
                        }
                    } else {
                        List<String> detaData = new ArrayList();
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detaData.add("");
                        detailData.add(detaData);
                    }
                }
            }
            OrderHeaderExportExcelUtils eeu = new OrderHeaderExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            String[] headers = {"发货号", "物流商", "运费", "操作费", "货值", "运费/货值", "发货人", "物流结算方式", "发货时间"};
            if (bizInvoice.getBizStatus() != null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip() != null && bizInvoice.getShip().equals(1)) {
                String[] details = {"发货号", "物流商", "备货单编号", "采购中心", "业务状态", "商品名称", "商品编码", "商品属性", "采购数量", "已发货数量"};
                eeu.exportExcel(workbook, 0, "发货数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "已发货详情", details, detailData, fileName);
            } else if (bizInvoice.getBizStatus() != null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip() != null && bizInvoice.getShip().equals(0)) {
                if (pageFlag) {
                    String[] details = {"发货号", "物流商", "订单编号", "经销店名称", "业务状态", "商品名称", "商品编码", "商品属性", "采购数量", "已发货数量"};
                    eeu.exportExcel(workbook, 0, "发货数据", headers, data, fileName);
                    eeu.exportExcel(workbook, 1, "已发货详情", details, detailData, fileName);
                } else {
                    String[] headersLogistics = {"物流单号", "运费", "操作费", "货值", "运费/货值"};
                    String[] details = {"发货号", "订单编号", "商品名称", "商品编号", "商品货号", "商品出厂价", "供应商", "商品单价", "采购数量", "总 额", "已发货数量", "发货方", "创建时间"};
                    eeu.exportExcel(workbook, 0, "物流单数据", headersLogistics, data, fileName);
                    eeu.exportExcel(workbook, 1, "物流单详情", details, detailData, fileName);
                }
            }
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            addMessage(redirectAttributes, "导出发货信息数据失败！失败信息：" + e.getMessage());
        }
        return "modules/biz/inventory/bizInvoiceList";
    }

    /**
     * 导出获取订单状态名称
     * @param bizStatus
     * @return
     */
    public String getDict(Integer bizStatus,String description,String type) {
        if (bizStatus != null) {
            return "";
        }
        Dict detadict = new Dict();
        detadict.setDescription(description);
        detadict.setType(type);
        List<Dict> detadictList = dictService.findList(detadict);
        if (detadictList.size() != 0) {
            for (Dict di : detadictList) {
                if (di.getValue().equals(String.valueOf(bizStatus))) {
                    //业务状态
                    return String.valueOf(di.getLabel());
                }
            }
        }
        return "";
    }

    /**
     * 物流单信息详情
     * @param bizInvoice
     * @param model
     * @return
     */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "logisticsOrderDetail")
    public String logisticsOrderDetail(BizInvoice bizInvoice, Model model) {
        List<BizOrderDetail> logisticsDetailList = bizInvoiceService.findLogisticsDetail(bizInvoice);
        model.addAttribute("logisticsDetailList", logisticsDetailList);
        return "modules/biz/inventory/bizInvoiceLogisticsDeForm";
    }

    /**
     * 物流信息对接的详情页跳转
     * @param trackingNumber
     * @return
     */
    @RequestMapping(value = "logisticDetail")
    public String logisticDetail(String trackingNumber,Model model) {
        model.addAttribute("trackingNumber",trackingNumber);
        return "modules/biz/logistic/logisticDetail";
    }

    /**
     * 查询运单信息
     * @param trackingNumber
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "selectLogistic")
    public String selectLogistic(String trackingNumber) {
        if (StringUtils.isBlank(trackingNumber)) {
            JsonUtil.generateErrorData(0,"运单信息请求参数为空",null);
            return null;
        }
        String logisticUrl = DsConfig.getLogisticUrl();
        CloseableHttpClient httpClient = CloseableHttpClientUtil.createSSLClientDefault();
        try {
            HttpPost httpPost = new HttpPost(logisticUrl);
            JSONObject param = new JSONObject();
            param.put("logisticCode", trackingNumber);
            httpPost.setEntity(new StringEntity(param.toString(), Charset.forName("UTF-8")));
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            LogisticEntity parse = JsonUtil.parse(result, LogisticEntity.class);
            LOGISTICS_LOGGER.info("物流单号：[{}]---运单信息---begin--", trackingNumber);
            LOGISTICS_LOGGER.info(JsonUtil.generateData(parse , null));
            LOGISTICS_LOGGER.info("物流单号：[{}]---运单信息---end--", trackingNumber);
            return JsonUtil.generateData(parse , null);
        } catch (Exception e) {
            LOGGER.error("物流运单信息异常",e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    LOGGER.error("运单信息httpClient关闭异常，710",e);
                }
            }
        }
        JsonUtil.generateErrorData(-1,"运单信息请求失败",null);
        return null;
    }

    /**
     * 确认发货单时，先check发货单对应的备货单是否审核完毕
     * @param invoiceId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "checkProcess")
    public String checkProcess(Integer invoiceId) {
        BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
        bizDetailInvoice.setInvoice(new BizInvoice(invoiceId));
        List<BizDetailInvoice> list = bizDetailInvoiceService.findList(bizDetailInvoice);

        if (CollectionUtils.isNotEmpty(list)) {
            bizDetailInvoice = list.get(0);
            BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(bizDetailInvoice.getRequestHeader().getId());
            String processType = bizRequestHeader.getCommonProcess().getType();
            RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
                    ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
            return requestOrderProcess.getName();
        }
        return  "";
    }

    public List<Dict> getOfficeList(User user) {
        List<Dict> collLocateList = getDictList("coll_locate");
        if (user.getCompany() != null && !user.isAdmin()) {
            Office office = officeService.get(user.getCompany().getId());
            String type = office.getType();
            if (office !=null && office.getId() != null && (OfficeTypeEnum.PURCHASINGCENTER.getType().equals(type)
                    || OfficeTypeEnum.WITHCAPITAL.getType().equals(type)
                    || OfficeTypeEnum.NETWORKSUPPLY.getType().equals(type)
                    || OfficeTypeEnum.NETWORK.getType().equals(type))) {
                Dict dict  = new Dict();
                dict.setValue(String.valueOf(office.getId()));
                dict.setLabel(office.getName());
                collLocateList.add(dict);
            }
        } else if (user.isAdmin()) {
            List<Office> listByType = officeService.findListByTypeList(Lists.newArrayList(
                    OfficeTypeEnum.PURCHASINGCENTER.getType(),
                    OfficeTypeEnum.WITHCAPITAL.getType(),
                    OfficeTypeEnum.NETWORKSUPPLY.getType(),
                    OfficeTypeEnum.NETWORK.getType()
            ));
            if (CollectionUtils.isNotEmpty(listByType)) {
                for (Office officeTemp :listByType) {
                    Dict dict  = new Dict();
                    dict.setValue(String.valueOf(officeTemp.getId()));
                    dict.setLabel(officeTemp.getName());
                    collLocateList.add(dict);
                }
            }
        }
        return collLocateList;
    }
}