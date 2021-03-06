/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.BaseEntity;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.BizInventorySkus;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizOutTreasuryEntity;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSkuTransferDetailService;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV3Service;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV3Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.InventorySkuRequestProcessConfig;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.InventorySkuTypeEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品库存详情Controller
 *
 * @author 张腾飞
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInventorySku")
public class BizInventorySkuController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizInventorySkuController.class);

    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private BizSkuInfoV3Service bizSkuInfoService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private DictService dictService;
    @Autowired
    private BizInventoryViewLogService bizInventoryViewLogService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private BizVarietyInfoService bizVarietyInfoService;
    @Autowired
    private BizProductInfoService bizProductInfoService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;
    @Autowired
    private BizSkuTransferDetailService bizSkuTransferDetailService;

    @ModelAttribute
    public BizInventorySku get(@RequestParam(required = false) Integer id) {
        BizInventorySku entity = null;
        if (id != null) {
            entity = bizInventorySkuService.get(id);
        }
        if (entity == null) {
            entity = new BizInventorySku();
        }
        return entity;
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizInventorySku bizInventorySku, HttpServletRequest request, HttpServletResponse response, Model model) {
        int stamp = 0;
        String zt = request.getParameter("zt");
        //取出用户所属采购中心
        User user = UserUtils.getUser();
        boolean flag = false;
        boolean oflag = false;
        if (user.getRoleList() != null) {
            for (Role role : user.getRoleList()) {
                if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        if (UserUtils.getOfficeList() != null) {
            for (Office office : UserUtils.getOfficeList()) {
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                    oflag = true;
                }
            }
        }
        Page<BizInventorySku> page = null;

        if (!user.isAdmin()) {
            bizInventorySku.setDataStatus(BaseEntity.DEL_FLAG_NORMAL);
            if (flag) {
                Office company = systemService.getUser(user.getId()).getCompany();
                //根据采购中心取出仓库
                BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
                if (bizInventorySku.getInvInfo() != null) {
                    bizInventoryInfo = bizInventorySku.getInvInfo();
                }
                bizInventoryInfo.setCustomer(company);
                bizInventorySku.setInvInfo(bizInventoryInfo);
            } else if (!oflag) {
                bizInventorySku.getSqlMap().put("inventorySku", BaseService.dataScopeFilter(user, "s", "su"));
            }
        }
        page = bizInventorySkuService.findPage(new Page<BizInventorySku>(request, response), bizInventorySku);
        List<BizInventorySku> list = page.getList();
        for (BizInventorySku inventorySku : list) {
            if (inventorySku.getCust() != null && inventorySku.getCust().getId() != 0) {
                stamp = 1;
            }
        }
        model.addAttribute("invStatus", stamp);
        model.addAttribute("varietyList", bizVarietyInfoService.findList(new BizVarietyInfo()));
        model.addAttribute("zt", zt);
        model.addAttribute("page", page);
        return "modules/biz/inventory/bizInventorySkuList";
    }


    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "findInvSku")
    public String findInvSku(String orderHeaders) {
        String flag = "false";
        if (StringUtils.isBlank(orderHeaders)) {
            return flag;
        }
        String[] orders = orderHeaders.split(",");
        for (int a = 0; a < orders.length; a++) {
            String[] oheaders = orders[a].split("#");
            if (oheaders.length < 2) {
                continue;
            }
            String[] odNumArr = oheaders[1].split("\\*");
            for (int i = 0; i < odNumArr.length; i++) {
                String[] odArr = odNumArr[i].split("-");
                BizOrderDetail orderDetail = bizOrderDetailService.get(Integer.parseInt(odArr[0]));
                //商品
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                BizInventoryInfo inventoryInfo = new BizInventoryInfo();
                if (odArr.length != 4) {
                    continue;
                }
                inventoryInfo = bizInventoryInfoService.get(Integer.parseInt(odArr[2]));
                BizInventorySku bizInventorySku = new BizInventorySku();
                bizInventorySku.setInvInfo(inventoryInfo);
                bizInventorySku.setSkuInfo(bizSkuInfo);
                bizInventorySku.setSkuType(Integer.valueOf(odArr[3]));
                if (InventorySkuTypeEnum.VENDOR_TYPE.getType().equals(Integer.valueOf(odArr[3]))) {
                    bizInventorySku.setVendor(bizSkuInfo.getProductInfo().getOffice());
                }
                List<BizInventorySku> invSkuList = bizInventorySkuService.findList(bizInventorySku);
                if (invSkuList != null && invSkuList.size() > 0) {
                    flag = "true";
                }
            }
        }
        return flag;
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "findInvSkuV2")
    public String findInvSkuV2(int invId, int skuId, int count, int skuType) {
        String flag = "false";

        // 仓库
        BizInventoryInfo inventoryInfo = bizInventoryInfoService.get(invId);
        // SKU
        BizSkuInfo bizSkuInfo = bizSkuInfoService.get(skuId);

        // 仓库内数量
        BizInventorySku bizInventorySku = new BizInventorySku();
        bizInventorySku.setInvInfo(inventoryInfo);
        bizInventorySku.setSkuInfo(bizSkuInfo);
        bizInventorySku.setSkuType(skuType);
        if (InventorySkuTypeEnum.VENDOR_TYPE.getType().equals(skuType)) {
            bizInventorySku.setVendor(bizSkuInfo.getProductInfo().getOffice());
        }
        List<BizInventorySku> invSkuList = bizInventorySkuService.findList(bizInventorySku);
        if (invSkuList != null && invSkuList.size() >= count) {
            return "true";
        }
        return flag;
    }


    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "form")
    public String form(BizInventorySku bizInventorySku, HttpServletRequest request, Model model) {
        model.addAttribute("invInfoList", bizInventoryInfoService.findList(new BizInventoryInfo()));
        if (bizInventorySku != null && bizInventorySku.getSkuInfo() != null) {
            BizSkuInfo skuInfo = bizSkuInfoService.get(bizInventorySku.getSkuInfo().getId());
            List<BizSkuInfo> list = bizSkuInfoService.findListByParam(skuInfo);
            for (BizSkuInfo sku : list) {
                List<BizSkuPropValue> skuPropValueList = sku.getSkuPropValueList();
                StringBuffer skuPropName = new StringBuffer();
                if (CollectionUtils.isNotEmpty(skuPropValueList)) {
                    for (BizSkuPropValue skuPropValue : skuPropValueList) {
                        skuPropName.append("-");
                        skuPropName.append(skuPropValue.getPropValue());
                    }
                }
                String propNames = "";
                if (skuPropName.toString().length() > 1) {
                    propNames = skuPropName.toString().substring(1);
                }
                BizProductInfo productInfo = bizProductInfoService.get(skuInfo.getProductInfo().getId());
                skuInfo.setSkuPropertyInfos(propNames);
                skuInfo.setVendorName(productInfo.getVendorName());
                bizInventorySku.setSkuInfo(skuInfo);
            }
        }
        Office office = new Office();
        office.setType(OfficeTypeEnum.CUSTOMER.getType());
        Office center = new Office();
        center.setType(OfficeTypeEnum.WITHCAPITAL.getType());
        List<Office> centList = officeService.queryList(center);
        if (centList != null && centList.size() > 0) {
            center = centList.get(0);
            office.setCenterId(center.getId());
        }
        List<Office> officeList = officeService.findCapitalList(office);
        model.addAttribute("custList", officeList);

        BizInventoryInfo bizInventoryInfo2 = bizInventoryInfoService.get(bizInventorySku.getInvInfo().getId());
        bizInventorySku.setInvInfo(bizInventoryInfo2);
        String zt = request.getParameter("zt");
        model.addAttribute("zt", zt);
        model.addAttribute("entity", bizInventorySku);
        model.addAttribute("bizSkuInfo", new BizSkuInfo());
        return "modules/biz/inventory/bizInventorySkuForm";
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "save")
    public String save(BizInventorySkus bizInventorySkus, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, bizInventorySku)){
//			return form(bizInventorySku,request,model);
//		}
        bizInventorySkuService.saveBizInventorySku(bizInventorySkus);
        String zt = request.getParameter("zt");
        addMessage(redirectAttributes, "保存商品库存详情成功");
        return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizInventorySku/?repage&zt=" + zt;
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "delete")
    public String delete(BizInventorySku bizInventorySku, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
        bizInventorySku.setDelFlag(BizInventorySku.DEL_FLAG_DELETE);
        String zt = request.getParameter("zt");
        addMessage(redirectAttributes, "删除商品库存详情成功");
        bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
        bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
        bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
        bizInventoryViewLog.setStockQty(bizInventorySku.getStockQty());
        bizInventoryViewLog.setStockChangeQty(0 - bizInventorySku.getStockQty());
        bizInventoryViewLog.setNowStockQty(0);
        bizInventorySkuService.delete(bizInventorySku);
        bizInventoryViewLogService.save(bizInventoryViewLog);
        return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizInventorySku/?repage&zt=" + zt;
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "recovery")
    public String recovery(BizInventorySku bizInventorySku, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        bizInventorySku.setDelFlag(BizInventorySku.DEL_FLAG_NORMAL);
        bizInventorySkuService.delete(bizInventorySku);
        BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
        bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
        bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
        bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
        bizInventoryViewLog.setStockQty(0);//原
        bizInventoryViewLog.setStockChangeQty(bizInventorySku.getStockQty());
        bizInventoryViewLog.setNowStockQty(bizInventorySku.getStockQty());//现
        bizInventoryViewLogService.save(bizInventoryViewLog);
        String zt = request.getParameter("zt");
        addMessage(redirectAttributes, "恢复商品库存详情成功");

        return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizInventorySku/?repage&zt=" + zt;
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "delItem")
    public String delItem(BizInventorySku bizInventorySku, RedirectAttributes redirectAttributes) {
        String data = "ok";
        try {
            bizInventorySkuService.delete(bizInventorySku);
        } catch (Exception e) {
            logger.error(e.getMessage());
            data = "error";
        }
        return data;
    }

    /**
     * 用于库存盘点表格导出
     */
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "torySkuExport", method = RequestMethod.POST)
    public String torySkuExport(BizInventorySku bizInventorySku, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName = "库存盘点数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            User user = UserUtils.getUser();
            boolean flag = false;
            boolean oflag = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        flag = true;
                        break;
                    }
                }
            }
            if (UserUtils.getOfficeList() != null) {
                for (Office office : UserUtils.getOfficeList()) {
                    if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                        oflag = true;
                        break;
                    }
                }
            }
            List<BizInventorySku> invList;
            if (!user.isAdmin()) {
                if (flag) {
                    Office company = systemService.getUser(user.getId()).getCompany();
                    //根据采购中心取出仓库
                    BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
                    bizInventoryInfo.setCustomer(company);
                    bizInventorySku.setInvInfo(bizInventoryInfo);
                }
                if (!oflag && !flag) {
                    bizInventorySku.getSqlMap().put("inventorySku", BaseService.dataScopeFilter(user, "s", "su"));
                }
            }
            bizInventorySku.setDataStatus("filter");
            invList = bizInventorySkuService.findList(bizInventorySku);
            //1库存盘点信息
            List<List<String>> data = new ArrayList<List<String>>();
            invList.forEach(tory -> {
                List<String> rowData = new ArrayList<>();
                Dict dict = new Dict();
                dict.setDescription("库存中SKU类型");
                dict.setType("inv_type");
                List<Dict> dictList = dictService.findList(dict);
                for (Dict d : dictList) {
                    if (d.getValue().equals(String.valueOf(tory.getInvType()))) {
                        //库存类型
                        rowData.add(String.valueOf(d.getLabel()));
                        break;
                    }
                }
                //仓库名称
                if (tory.getInvInfo() != null && tory.getInvInfo().getName() != null) {
                    rowData.add(String.valueOf(tory.getInvInfo().getName()));
                } else {
                    rowData.add("");
                }
                if (tory.getSkuInfo() != null) {
                    if (tory.getSkuInfo().getName() != null) {
                        //商品名称
                        rowData.add(String.valueOf(tory.getSkuInfo().getName()));
                    } else {
                        rowData.add("");
                    }
                    if (tory.getSkuInfo().getPartNo() != null) {
                        //商品编号
                        rowData.add(String.valueOf(tory.getSkuInfo().getPartNo()));
                    } else {
                        rowData.add("");
                    }
                    if (tory.getSkuInfo().getItemNo() != null) {
                        //商品货号
                        rowData.add(String.valueOf(tory.getSkuInfo().getItemNo()));
                    } else {
                        rowData.add("");
                    }
                    if (tory.getSkuInfo().getBuyPrice() != null && tory.getStockQty() != null) {
                        BigDecimal buyPrice = new BigDecimal(tory.getSkuInfo().getBuyPrice());
                        BigDecimal stockQty = new BigDecimal(tory.getStockQty());
                        //商品总值
                        BigDecimal totalValue = buyPrice.multiply(stockQty).setScale(1, BigDecimal.ROUND_HALF_UP);
                        rowData.add(String.valueOf(totalValue));
                    } else {
                        rowData.add("");
                    }
                    if (tory.getSkuInfo().getVendorName() != null) {
                        //供应商
                        rowData.add(String.valueOf(tory.getSkuInfo().getVendorName()));
                    } else {
                        rowData.add("");
                    }
                } else {
                    rowData.add("");
                    rowData.add("");
                    rowData.add("");
                    rowData.add("");
                    rowData.add("");
                }
                //库存数量
                rowData.add(String.valueOf(tory.getStockQty()));
                //销售订单数量
                rowData.add(String.valueOf(tory.getStockOrdQty()));
                //出库量
                rowData.add(tory.getOutWarehouse() == null ? "" : String.valueOf(tory.getOutWarehouse()));
                //入库量
                rowData.add(tory.getInWarehouse() == null ? "" : String.valueOf(tory.getInWarehouse()));
                //供货量
                rowData.add(tory.getSendGoodsNum() == null ? "" : String.valueOf(tory.getSendGoodsNum()));
                //调入数量
                rowData.add(String.valueOf(tory.getTransInQty()));
                //调出数量
                rowData.add(String.valueOf(tory.getTransOutQty()));
                //创建人
                rowData.add(String.valueOf(tory.getCreateBy().getName()));
                //创建时间
                rowData.add(String.valueOf(sdf.format(tory.getCreateDate())));
                //更新人
                rowData.add(String.valueOf(tory.getUpdateBy().getName()));
                //更新时间
                rowData.add(String.valueOf(sdf.format(tory.getUpdateDate())));
                data.add(rowData);
            });
            String[] toryHeads = {"库存类型", "仓库名称", "商品名称", "商品编号", "商品货号", "商品总值", "供应商", "库存数量", "销售订单数量", "出库量", "入库量", "供货量", "调入数量",
                    "调出数量", "创建人", "创建时间", "更新人", "更新时间"};
            ExportExcelUtils eeu = new ExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, "库存盘点数据", toryHeads, data, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出库存盘点数据失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/biz/inventory/bizInventorySku/";
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping("invSkuCount")
    public Integer invSkuCount(Integer centId) {
        Integer count = bizInventorySkuService.invSkuCount(centId);
        return count;
    }


    @RequiresPermissions("biz:inventory:inventoryAge:view")
    @RequestMapping("showInventoryAge")
    public String showInventoryAge(HttpServletRequest request, Integer skuId, Integer centId, Integer invInfoId) {
        Map<String, Object> resultMap = bizInventorySkuService.getInventoryAge(skuId, centId, invInfoId);
        request.setAttribute("data", resultMap);
        return "modules/biz/inventory/bizInventoryAge";
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "inventorySkuDetail")
    public String inventorySkuDetail(Integer id, Model model) {
        BizInventorySku inventorySku = bizInventorySkuService.get(id);
        List<BizCollectGoodsRecord> bcgrList = bizInventorySkuService.getInventoryDetail(id, inventorySku.getSkuInfo().getId());
        if (CollectionUtils.isNotEmpty(bcgrList)) {
            for (BizCollectGoodsRecord collectGoodsRecord : bcgrList) {
                List<AttributeValueV2> colorList = bizSkuInfoService.getSkuProperty(collectGoodsRecord.getSkuInfo().getId(), BizProductInfoV3Service.SKU_TABLE, "颜色");
                if (CollectionUtils.isNotEmpty(colorList)) {
                    collectGoodsRecord.getSkuInfo().setColor(colorList.get(0).getValue());
                }
                List<AttributeValueV2> sizeList = bizSkuInfoService.getSkuProperty(collectGoodsRecord.getSkuInfo().getId(), BizProductInfoV3Service.SKU_TABLE, "尺寸");
                if (CollectionUtils.isNotEmpty(sizeList)) {
                    collectGoodsRecord.getSkuInfo().setSize(sizeList.get(0).getValue());
                }
                List<CommonImg> imgList = bizSkuInfoService.getImg(collectGoodsRecord.getSkuInfo().getId(), ImgEnum.SKU_TYPE.getTableName(), ImgEnum.SKU_TYPE.getCode());
                if (CollectionUtils.isNotEmpty(imgList)) {
                    collectGoodsRecord.getSkuInfo().setSkuImgUrl(imgList.get(0).getImgServer() + imgList.get(0).getImgPath());
                }
            }
        }
        model.addAttribute("outWarehouse",bizInventorySkuService.findOutWarehouse(id));
        model.addAttribute("sendGoodsNum",bizInventorySkuService.findSendGoodsNum(id));
        model.addAttribute("inWarehouse",bizInventorySkuService.findInWarehouse(id));
        model.addAttribute("bcgrList",bcgrList);
        return "modules/biz/inventory/bizInventorySkuDetail";
    }

    /**
     * 盘点列表
     * @param requestHeader
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "inventory")
    public String inventory(BizRequestHeader requestHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizRequestHeader> requestHeaderPage = bizInventorySkuService.inventoryPage(new Page<BizRequestHeader>(request, response),requestHeader);
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> rolsSet = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (Role role : roleList) {
                RoleEnNameEnum parse = RoleEnNameEnum.parse(role.getEnname());
                if (parse != null) {
                    rolsSet.add(parse.name());
                }
            }
        }
        List<InventorySkuRequestProcessConfig.InvRequestProcess> processList = ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getProcessList();
        List<BizVarietyInfo> varietyList= bizVarietyInfoService.findList(new BizVarietyInfo());
        model.addAttribute("processList", processList);
        model.addAttribute("roleSet",rolsSet);
        model.addAttribute("page",requestHeaderPage);
        model.addAttribute("requestHeader",requestHeader);
        model.addAttribute("varietyList",varietyList);
        return "modules/biz/inventory/bizInventory";
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "inventoryForm")
    public String inventoryForm(BizRequestHeader requestHeader,String source, Integer invId, Model model) {
        BizRequestDetail requestDetail = new BizRequestDetail();
        requestDetail.setRequestHeader(requestHeader);
        List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(requestDetail);
        if (CollectionUtils.isNotEmpty(requestDetailList)) {
            for (BizRequestDetail bizRequestDetail : requestDetailList) {
                List<AttributeValueV2> colorList = bizSkuInfoService.getSkuProperty(bizRequestDetail.getSkuInfo().getId(), BizProductInfoV3Service.SKU_TABLE, "颜色");
                if (CollectionUtils.isNotEmpty(colorList)) {
                    bizRequestDetail.getSkuInfo().setColor(colorList.get(0).getValue());
                }
                List<AttributeValueV2> sizeList = bizSkuInfoService.getSkuProperty(bizRequestDetail.getSkuInfo().getId(), BizProductInfoV3Service.SKU_TABLE, "尺寸");
                if (CollectionUtils.isNotEmpty(sizeList)) {
                    bizRequestDetail.getSkuInfo().setSize(sizeList.get(0).getValue());
                }
                List<CommonImg> imgList = bizSkuInfoService.getImg(bizRequestDetail.getSkuInfo().getId(), ImgEnum.SKU_TYPE.getTableName(), ImgEnum.SKU_TYPE.getCode());
                if (CollectionUtils.isNotEmpty(imgList)) {
                    bizRequestDetail.getSkuInfo().setSkuImgUrl(imgList.get(0).getImgServer() + imgList.get(0).getImgPath());
                }
                if ("pChange".equals(source)) {
                    Integer sumSendNum = bizSendGoodsRecordService.getSumSendNumByReqDetailId(bizRequestDetail.getId());
                    bizRequestDetail.setSumSendNum(sumSendNum);
                }
            }
        }
        BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(requestHeader.getId());
        if (bizRequestHeader.getInvCommonProcess() != null && bizRequestHeader.getInvCommonProcess().getId() != null) {
            List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
            bizPoHeaderService.getCommonProcessListFromDB(bizRequestHeader.getInvCommonProcess().getId(), commonProcessList);
            Collections.reverse(commonProcessList);
            bizRequestHeader.setInvCommonProcessList(commonProcessList);
        }
        bizRequestHeader.setInvInfo(bizInventoryInfoService.get(invId));
        Integer autProcessId = ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getAutProcessId();
        model.addAttribute("autProcessId",autProcessId);
        model.addAttribute("source",source);
        model.addAttribute("requestHeader",bizRequestHeader);
        model.addAttribute("requestDetailList",requestDetailList);
        return "modules/biz/inventory/bizInventoryForm";
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:audit")
    @RequestMapping(value = "audit")
    public String audit(HttpServletRequest request, int id, int invId, String currentType, int auditType, String description) {
        Pair<Boolean, String> result = bizInventorySkuService.auditInventory(id, invId, currentType, auditType, description);
        if (result.getLeft()) {
            return JsonUtil.generateData(result, request.getParameter("callback"));
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "inventorySave")
    public String inventorySave(BizRequestHeader requestHeader, RedirectAttributes redirectAttributes) {
        String invReqDetail = requestHeader.getInvReqDetail();
        if (StringUtils.isBlank(invReqDetail)) {
            addMessage(redirectAttributes,"保存库存失败");
            return "redirect:" + adminPath + "/biz/inventory/bizInventorySku/inventory";
        }
        bizInventorySkuService.inventorySave(requestHeader);
        return "redirect:" + adminPath + "/biz/inventory/bizInventorySku/inventory";
    }

    /**
     * 初始化备货详情出库数量
     */
    @RequestMapping("correctOutQty")
    public void correctOutQty() {
        bizInventorySkuService.correctOutQty();
    }

    /**
     * 用于备货单盘点导出
     */
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "requestInventoryExport")
    public String requestInventoryExport(BizRequestHeader requestHeader, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String fileName = "备货单盘点数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
        List<List<String>> data = Lists.newArrayList();
        List<List<String>> detailData = Lists.newArrayList();
        List<BizRequestHeader> requestHeaderList = bizInventorySkuService.inventory(requestHeader);

        if (CollectionUtils.isNotEmpty(requestHeaderList)) {
            for (BizRequestHeader bizRequestHeader : requestHeaderList) {
                List<String> rowList = Lists.newArrayList();
                rowList.add(DictUtils.getDictLabel(bizRequestHeader.getHeaderType().toString(),"req_header_type","未知类型"));
                rowList.add(bizRequestHeader.getInvInfo().getName() == null ? "" : bizRequestHeader.getInvInfo().getName());
                rowList.add(bizRequestHeader.getFromOffice().getName() == null ? "" : bizRequestHeader.getFromOffice().getName());
                rowList.add(DictUtils.getDictLabel(bizRequestHeader.getFromType().toString(),"req_from_type","未知"));
                rowList.add(bizRequestHeader.getReqNo() == null ? "" : bizRequestHeader.getReqNo());
                rowList.add(bizRequestHeader.getVendName() == null ? "" : bizRequestHeader.getVendName());
                String processName = "";
                if (bizRequestHeader.getInvCommonProcess() != null && bizRequestHeader.getInvCommonProcess().getType() != null) {
                    processName = ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getInvCommonProcess().getType())).getName();
                }
                rowList.add(processName);
                data.add(rowList);
                BizRequestDetail requestDetail = new BizRequestDetail();
                requestDetail.setRequestHeader(bizRequestHeader);
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(requestDetail);
                if (CollectionUtils.isNotEmpty(requestDetailList)) {
                    for (BizRequestDetail bizRequestDetail : requestDetailList){
                        List<String> detailRowList = Lists.newArrayList();
                        detailRowList.add(bizRequestHeader.getReqNo());
                        detailRowList.add(bizRequestDetail.getSkuInfo().getVendorName());
                        BizSkuInfo skuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                        String variName = bizProductInfoService.get(skuInfo.getProductInfo().getId()).getBizVarietyInfo().getName();
                        detailRowList.add(variName == null ? "" : variName);
                        detailRowList.add(bizRequestDetail.getSkuInfo().getItemNo());
                        List<AttributeValueV2> sizeList = bizSkuInfoService.getSkuProperty(bizRequestDetail.getSkuInfo().getId(), "biz_sku_info", "尺寸");
                        detailRowList.add(CollectionUtils.isEmpty(sizeList) ? "" : sizeList.get(0).getValue());
                        List<AttributeValueV2> colorList = bizSkuInfoService.getSkuProperty(bizRequestDetail.getSkuInfo().getId(), "biz_sku_info", "颜色");
                        detailRowList.add(CollectionUtils.isEmpty(colorList) ? "" : colorList.get(0).getValue());
                        detailRowList.add("个/套");
                        detailRowList.add(bizRequestDetail.getSkuInfo().getBuyPrice().toString());
                        detailRowList.add(String.valueOf(bizRequestDetail.getRecvQty() - (bizRequestDetail.getOutQty() == null ? 0 : bizRequestDetail.getOutQty())));
                        detailRowList.add(String.valueOf(bizRequestDetail.getSkuInfo().getBuyPrice() * bizRequestDetail.getRecvQty() - (bizRequestDetail.getOutQty() == null ? 0 : bizRequestDetail.getOutQty())));
                        detailRowList.add("");
                        detailData.add(detailRowList);
                    }
                }
            }
        }
        String[] toryHeads = {"类型", "仓库名称", "采购中心", "备货方", "备货单号", "供应商", "审核状态"};
        String[] detailHeads = {"备货单号","供应商","品类","货号","规格","颜色","单位","单价","盘点数量","盘点金额","备注"};
        ExportExcelUtils eeu = new ExportExcelUtils();
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        try {
            eeu.exportExcel(workbook, 0, "备货单盘点数据", toryHeads, data, fileName);
            eeu.exportExcel2(workbook,1,"备货单商品详情数据",detailHeads,detailData,fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
        } catch (Exception e) {
            LOGGER.error("导出备货单盘点数据失败",e);
            addMessage(redirectAttributes, "导出备货单盘点数据失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/biz/inventory/bizInventorySku/inventory";
    }

    @ResponseBody
    @RequestMapping(value = "checkSku")
    public String checkSku(Integer id) {
        BizInventorySku bizInventorySku = bizInventorySkuService.get(id);
        return bizInventorySkuService.checkSku(bizInventorySku);
    }

    @RequestMapping(value = "skuSplitForm")
    public String skuSplitForm(BizInventorySku inventorySku,Model model) {
        List<BizRequestDetail> requestDetailList = bizRequestDetailService.findInventorySkuByskuIdAndcentId(inventorySku.getInvInfo().getCustomer().getId(),inventorySku.getSkuInfo().getId());
        List<BizSkuTransferDetail> transferDetailList = bizSkuTransferDetailService.findInventorySkuByskuIdAndcentId(inventorySku.getInvInfo().getCustomer().getId(),inventorySku.getSkuInfo().getId());
        model.addAttribute("inventorySku",inventorySku);
        model.addAttribute("requestDetailList",requestDetailList);
        model.addAttribute("transferDetailList",transferDetailList);
        return "modules/biz/inventory/skuSplitForm";
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:split")
    @RequestMapping(value = "skuSplit")
    public String skuSplit(@RequestBody String data) {
        try {
            data = URLDecoder.decode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JsonUtil.parseJson(data);
        String treasuryList = jsonObject.getString("treasuryList");
        List<BizOutTreasuryEntity> skuSplitList = JsonUtil.parseArray(treasuryList,new TypeReference<List<BizOutTreasuryEntity>>() {});
        if (CollectionUtils.isEmpty(skuSplitList)) {
            return "error";
        }
        return bizInventorySkuService.doSkuSplit(skuSplitList);
    }

    @ResponseBody
    @RequestMapping(value = "checkMergeSku")
    public String checkMergeSku(Integer id) {
        BizInventorySku bizInventorySku = bizInventorySkuService.get(id);
        String itemNo = bizInventorySku.getSkuInfo().getItemNo();
        String middle = itemNo.substring(itemNo.indexOf("/") + 1,itemNo.lastIndexOf("/"));
        if (middle.contains("-") || middle.contains("*") || !middle.matches("[0-9]{2}.*")) {
            return "该商品不属于单个尺寸的商品，不能合并";
        }
        List<Dict> mergeSkuOne = DictUtils.getDictList("merge_sku_one");
        for (Dict dict : mergeSkuOne) {
            if (middle.contains(dict.getValue())) {
                return "merge_sku_one";
            }
        }
        List<Dict> mergeSkuTwo = DictUtils.getDictList("merge_sku_two");
        for (Dict dict : mergeSkuTwo) {
            if (middle.contains(dict.getValue())) {
                return "merge_sku_two";
            }
        }
        return "该商品不在可合并范围，请联系系统管理员";
    }

    @RequestMapping(value = "skuMergeForm")
    public String skuMergeForm(Integer id,Model model,String range) {
        BizInventorySku inventorySku = get(id);
        String itemNo = inventorySku.getSkuInfo().getItemNo();
        String before = itemNo.substring(0,itemNo.indexOf("/") + 1);
        String middle = itemNo.substring(itemNo.indexOf("/") + 1,itemNo.lastIndexOf("/"));
        String after = itemNo.substring(itemNo.lastIndexOf("/"));
        Map<Integer,List<BizRequestDetail>> reqMap = Maps.newHashMap();
        Map<Integer,List<BizSkuTransferDetail>> transMap = Maps.newHashMap();
        List<Dict> dictList = DictUtils.getDictList(range);
        List<Integer> sizeList = Lists.newArrayList();
        for (Dict dict : dictList) {
            sizeList.add(Integer.valueOf(dict.getValue()));
            String s = before.concat(dict.getValue()).concat(middle.substring(2)).concat(after);
            BizSkuInfo sku = bizSkuInfoService.getSkuByItemNo(s);
            if (sku != null) {
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findInventorySkuByskuIdAndcentId(inventorySku.getInvInfo().getCustomer().getId(),sku.getId());
                List<BizSkuTransferDetail> transferDetailList = bizSkuTransferDetailService.findInventorySkuByskuIdAndcentId(inventorySku.getInvInfo().getCustomer().getId(), sku.getId());
                if (CollectionUtils.isNotEmpty(requestDetailList)) {
                    reqMap.put(Integer.valueOf(dict.getValue()),requestDetailList);
                }
                if (CollectionUtils.isNotEmpty(transferDetailList)) {
                    transMap.put(Integer.valueOf(dict.getValue()),transferDetailList);
                }
            }
        }
        model.addAttribute("reqMap",reqMap);
        model.addAttribute("transMap",transMap);
        model.addAttribute("inventorySku",inventorySku);
        model.addAttribute("sizeList",sizeList);
        return "modules/biz/inventory/skuMergeForm";
    }

    @ResponseBody
    @RequestMapping(value = "skuMerge")
    public String skuMerge(@RequestBody String data) {
        LinkedHashMap<String,List<BizOutTreasuryEntity>> map = Maps.newLinkedHashMap();
        HashMap maps=  JSONObject.parseObject(data,LinkedHashMap.class);
        for (Object entry : maps.entrySet()) {
            List<BizOutTreasuryEntity> parseArray = JsonUtil.parseArray(((Map.Entry)entry).getValue().toString(),new TypeReference<List<BizOutTreasuryEntity>>() {});
            map.put((String) ((Map.Entry)entry).getKey(),parseArray);
        }
        return bizInventorySkuService.skuMerge(map);
    }

}