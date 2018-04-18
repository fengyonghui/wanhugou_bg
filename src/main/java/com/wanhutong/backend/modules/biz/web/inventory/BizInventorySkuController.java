/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.dto.BizInventorySkus;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品库存详情Controller
 *
 * @author 张腾飞
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInventorySku")
public class BizInventorySkuController extends BaseController {

    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
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
        int stamp= 0;
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
        if (user.isAdmin()) {
            page = bizInventorySkuService.findPage(new Page<BizInventorySku>(request, response), bizInventorySku);
        } else {
            if (flag) {
                Office company = systemService.getUser(user.getId()).getCompany();
                //根据采购中心取出仓库
                BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
                bizInventoryInfo.setCustomer(company);
                bizInventorySku.setInvInfo(bizInventoryInfo);
            } else {
                if (oflag) {

                } else {
                    bizInventorySku.getSqlMap().put("inventorySku", BaseService.dataScopeFilter(user, "s", "su"));
                }
            }
            page = bizInventorySkuService.findPage(new Page<BizInventorySku>(request, response), bizInventorySku);
        }
        List<BizInventorySku> list = page.getList();
        for (BizInventorySku inventorySku:list){
            if(inventorySku.getCust()!=null && inventorySku.getCust().getId()!=0){
                stamp = 1;
            }
        }
        model.addAttribute("invStatus",stamp);
        model.addAttribute("varietyList",bizVarietyInfoService.findList(new BizVarietyInfo()));
        model.addAttribute("zt", zt);
        model.addAttribute("page", page);
        return "modules/biz/inventory/bizInventorySkuList";
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInventorySku:view")
    @RequestMapping(value = "findInvSku")
    public String findInvSku(String orderHeaders) {
        String flag = "false";
        if (StringUtils.isNotBlank(orderHeaders)) {
            String[] orders = orderHeaders.split(",".trim());
            for (int a = 0; a < orders.length; a++) {
                String[] oheaders = orders[a].split("#".trim());
                String[] odNumArr = oheaders[1].split("\\*");
                for (int i = 0; i < odNumArr.length; i++) {
                    String[] odArr = odNumArr[i].split("-");
                    BizOrderDetail orderDetail = bizOrderDetailService.get(Integer.parseInt(odArr[0]));
                    //商品
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                    BizInventoryInfo inventoryInfo = new BizInventoryInfo();
                    if (odArr.length == 3) {
                        inventoryInfo = bizInventoryInfoService.get(Integer.parseInt(odArr[2]));
                        BizInventorySku bizInventorySku = new BizInventorySku();
                        bizInventorySku.setInvInfo(inventoryInfo);
                        bizInventorySku.setSkuInfo(bizSkuInfo);
                        List<BizInventorySku> invSkuList = bizInventorySkuService.findList(bizInventorySku);
                        if (invSkuList != null && invSkuList.size() > 0) {
                            flag = "true";
                        }
                    }
                }
            }
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
                for (BizSkuPropValue skuPropValue : skuPropValueList) {
                    skuPropName.append("-");
                    skuPropName.append(skuPropValue.getPropValue());
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
        if (centList != null && centList.size() > 0){
            center = centList.get(0);
            office.setCenterId(center.getId());
        }
        List<Office> officeList = officeService.findCapitalList(office);
        model.addAttribute("custList",officeList);

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
        if (bizInventorySkus != null && bizInventorySkus.getSkuInfoIds() != null) {
            String[] invInfoIdArr = bizInventorySkus.getInvInfoIds().split(",");
            String[] customerIdArr = null;
            if (bizInventorySkus.getCustomerIds() != null && !bizInventorySkus.getCustomerIds().isEmpty()) {
                customerIdArr = bizInventorySkus.getCustomerIds().split(",");
            }
            String[] invTypeArr = bizInventorySkus.getInvTypes().split(",");
            String[] skuInfoIdArr = bizInventorySkus.getSkuInfoIds().split(",");
            String[] stockQtyArr = bizInventorySkus.getStockQtys().split(",");
            BizInventorySku bizInventorySku = new BizInventorySku();
            BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
            for (int i = 0; i < skuInfoIdArr.length; i++) {
                bizInventorySku.setId(null);
                bizInventorySku.setSkuInfo(bizSkuInfoService.get(Integer.parseInt(skuInfoIdArr[i].trim())));
                if (bizInventorySkus.getCustomerIds() != null && !bizInventorySkus.getCustomerIds().isEmpty()) {
                    bizInventorySku.setCust(officeService.get(Integer.parseInt(customerIdArr[i].trim())));
                }
                bizInventorySku.setInvInfo(bizInventoryInfoService.get(Integer.parseInt(invInfoIdArr[i].trim())));
                bizInventorySku.setInvType(Integer.parseInt(invTypeArr[i].trim()));
                bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
                bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
                bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
                //查询是否有已删除的该商品库存
                BizInventorySku only = bizInventorySkuService.findOnly(bizInventorySku);
                if (only == null) {
                    bizInventorySku.setStockQty(Integer.parseInt(stockQtyArr[i].trim()));
                    bizInventorySkuService.save(bizInventorySku);
                    bizInventoryViewLog.setStockQty(bizInventorySku.getStockQty());
                    bizInventoryViewLog.setStockChangeQty(bizInventorySku.getStockQty());
                } else {
                    only.setStockQty(Integer.parseInt(stockQtyArr[i].trim()));
                    if (bizInventorySkus.getCustomerIds() != null && !bizInventorySkus.getCustomerIds().isEmpty()) {
                        only.setCust(officeService.get(Integer.parseInt(customerIdArr[i].trim())));
                    }
                    bizInventorySkuService.save(only);
                    bizInventoryViewLog.setStockQty(only.getStockQty());
                    bizInventoryViewLog.setStockChangeQty(only.getStockQty());
                }
                bizInventoryViewLogService.save(bizInventoryViewLog);
            }
        }//修改
        else if (bizInventorySkus != null && bizInventorySkus.getStockQtys() != null && !bizInventorySkus.getStockQtys().equals("")) {
            BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
            BizInventorySku bizInventorySku = bizInventorySkuService.get(bizInventorySkus.getId());
            bizInventoryViewLog.setStockChangeQty(Integer.parseInt(bizInventorySkus.getStockQtys())-bizInventorySku.getStockQty());
            bizInventorySku.setStockQty(Integer.parseInt(bizInventorySkus.getStockQtys()));
            bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
            bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
            bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
            bizInventoryViewLog.setStockQty(bizInventorySku.getStockQty());
            bizInventoryViewLogService.save(bizInventoryViewLog);
            bizInventorySkuService.save(bizInventorySku);
        }


        String zt = request.getParameter("zt");
        addMessage(redirectAttributes, "保存商品库存详情成功");
        return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizInventorySku/?repage&zt=" + zt;
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "delete")
    public String delete(BizInventorySku bizInventorySku, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//        BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
        bizInventorySku.setDelFlag(BizInventorySku.DEL_FLAG_DELETE);
        bizInventorySkuService.delete(bizInventorySku);
        String zt = request.getParameter("zt");
        addMessage(redirectAttributes, "删除商品库存详情成功");
//        bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
//        bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
//        bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
//        bizInventoryViewLog.setStockQty(0);
//        bizInventoryViewLog.setStockChangeQty(0-bizInventorySku.getStockQty());
//        bizInventoryViewLogService.save(bizInventoryViewLog);
        return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizInventorySku/?repage&zt=" + zt;
    }

    @RequiresPermissions("biz:inventory:bizInventorySku:edit")
    @RequestMapping(value = "recovery")
    public String recovery(BizInventorySku bizInventorySku, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        bizInventorySku.setDelFlag(BizInventorySku.DEL_FLAG_NORMAL);
        bizInventorySkuService.delete(bizInventorySku);
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
            List<BizInventorySku> invList = bizInventorySkuService.findList(bizInventorySku);
            //1库存盘点信息
            List<List<String>> data = new ArrayList<List<String>>();
            invList.forEach(tory -> {
                List<String> rowData = new ArrayList();
                //ID
                rowData.add(String.valueOf(tory.getId()));
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
                rowData.add(String.valueOf(tory.getInvInfo().getName()));
                //商品名称
                rowData.add(String.valueOf(tory.getSkuInfo().getName()));
                //商品编号
                rowData.add(String.valueOf(tory.getSkuInfo().getPartNo()));
                //商品货号
                rowData.add(String.valueOf(tory.getSkuInfo().getItemNo()));
                //库存数量
                rowData.add(String.valueOf(tory.getStockQty()));
                //销售订单数量
                rowData.add(String.valueOf(tory.getStockOrdQty()));
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
            String[] toryHeads = {"ID", "库存类型", "仓库名称", "商品名称", "商品编号", "商品货号", "库存数量", "销售订单数量", "调入数量",
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
    public Integer invSkuCount(Integer centId){
        Integer count = bizInventorySkuService.invSkuCount(centId);
        return count;
    }

}