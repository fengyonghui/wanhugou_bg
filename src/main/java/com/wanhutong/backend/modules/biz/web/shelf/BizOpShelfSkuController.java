/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.BizOpShelfSkus;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品上架管理Controller
 * @author liuying
 * @version 2017-12-19
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizOpShelfSku")
public class BizOpShelfSkuController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizOpShelfSkuController.class);
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private BizSkuInfoV2Service bizSkuInfoService;
	@Autowired
	private AttributeValueV2Service attributeValueService;

	
	@ModelAttribute
	public BizOpShelfSku get(@RequestParam(required=false) Integer id) {
		BizOpShelfSku entity = null;
		if (id!=null){
			entity = bizOpShelfSkuService.get(id);
		}
		if (entity == null){
			entity = new BizOpShelfSku();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpShelfSku bizOpShelfSku, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpShelfSku> page = bizOpShelfSkuService.findPage(new Page<BizOpShelfSku>(request, response), bizOpShelfSku); 
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizOpShelfSkuList";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = "form")
	public String form(BizOpShelfSkus bizOpShelfSku, Model model) {
		if (bizOpShelfSku != null && bizOpShelfSku.getId() != null){
            BizOpShelfSku bizOpShelfSku1 = bizOpShelfSkuService.get(bizOpShelfSku.getId());
            model.addAttribute("bizOpShelfSku",bizOpShelfSku1);
        }else {
            model.addAttribute("bizOpShelfSku", bizOpShelfSku);
        }
        model.addAttribute("bizSkuInfo", new BizSkuInfo());
		return "modules/biz/shelf/bizOpShelfSkuForm";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "save")
	public String save(BizOpShelfSkus bizOpShelfSkus, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOpShelfSkus)){
			return form(bizOpShelfSkus, model);
		}
		String skuIds=bizOpShelfSkus.getSkuInfoIds();
		String[] skuIdArr=skuIds.split(",");
		String[] maxQtyArr=bizOpShelfSkus.getMaxQtys().split(",");
		String[] minQtyArr=bizOpShelfSkus.getMinQtys().split(",");
		String[] orgPriceArr=bizOpShelfSkus.getOrgPrices().split(",");
		String[] priorityArr=bizOpShelfSkus.getPrioritys().split(",");
		String[] salePriceArr=bizOpShelfSkus.getSalePrices().split(",");
		String[] shelfQtyArr=bizOpShelfSkus.getShelfQtys().split(",");
		String[] shelfTimeArr=bizOpShelfSkus.getShelfTimes().split(",");
		String[] unShelfTimeArr=bizOpShelfSkus.getUnshelfTimes().split(",");
		boolean flag = false;
		for(int i=0;i<skuIdArr.length;i++){
			BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
			if(bizOpShelfSkus.getId()!=null){
                bizOpShelfSku.setId(bizOpShelfSkus.getId());

				if (flag) {
					bizOpShelfSku.setId(null);
				}
				flag = true;
            }else {
                bizOpShelfSku.setId(null);
            }
			BizSkuInfo bizSkuInfo=bizSkuInfoService.get(Integer.parseInt(skuIdArr[i].trim()));
			bizOpShelfSku.setSkuInfo(bizSkuInfo);
			bizOpShelfSku.setProductInfo(bizSkuInfo.getProductInfo());
			bizOpShelfSku.setCenterOffice(bizOpShelfSkus.getCenterOffice());
			bizOpShelfSku.setOpShelfInfo(bizOpShelfSkus.getOpShelfInfo());
			bizOpShelfSku.setMaxQty(Integer.parseInt(maxQtyArr[i].trim()));
			bizOpShelfSku.setMinQty(Integer.parseInt(minQtyArr[i].trim()));
			bizOpShelfSku.setOrgPrice(Double.parseDouble(orgPriceArr[i].trim()));
			bizOpShelfSku.setPriority(Integer.parseInt(priorityArr[i].trim()));
			bizOpShelfSku.setSalePrice(Double.parseDouble(salePriceArr[i].trim()));
			bizOpShelfSku.setShelfQty(Integer.parseInt(shelfQtyArr[i].trim()));
			bizOpShelfSku.setShelfTime(DateUtils.parseDate(shelfTimeArr[i].trim()));
			if (unShelfTimeArr.length > 0 && i<unShelfTimeArr.length) {

				bizOpShelfSku.setUnshelfTime(DateUtils.parseDate(unShelfTimeArr[i].trim()));

            }
			bizOpShelfSkuService.save(bizOpShelfSku);
		}
		addMessage(redirectAttributes, "保存商品上架成功");
		String a="cend_save";
		if(bizOpShelfSkus.getCendShelf()!=null && bizOpShelfSkus.getCendShelf().equals(a)){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/cendList";
		}
		if(bizOpShelfSkus.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/form?id=" + bizOpShelfSkus.getOpShelfInfo().getId() ;
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@ResponseBody
	@RequestMapping(value = "findOpShelfSku")
	public List<BizOpShelfSku> findOpShelfSku(BizOpShelfSku bizOpShelfSku){
		AttributeValueV2 bizSkuPropValue = new AttributeValueV2();//sku商品属性表
		List<BizOpShelfSku> list=null;
		boolean emptyName = bizOpShelfSku.getSkuInfo().getName().isEmpty();//商品名称
		boolean emptyPart = bizOpShelfSku.getSkuInfo().getPartNo().isEmpty();//商品编码
		boolean emptyItemNo = bizOpShelfSku.getSkuInfo().getItemNo().isEmpty();//商品货号
		if(emptyName==true && emptyPart==true && emptyItemNo==true){
			System.out.println("为空 不查询sku商品");
		}else {
			bizOpShelfSku.setSkuDeleteFlag("true");
			list = bizOpShelfSkuService.findList(bizOpShelfSku);
		}
		if(list!=null){
			for (BizOpShelfSku skuValue : list) {
				bizSkuPropValue.setObjectId(skuValue.getSkuInfo().getId());//sku_Id
				bizSkuPropValue.setObjectName("biz_sku_info");
				List<AttributeValueV2> skuValueList = attributeValueService.findList(bizSkuPropValue);
				if(skuValueList.size()!=0){
					skuValue.setSkuValueList(skuValueList);
				}
			}
		}
		return 	list;
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@ResponseBody
	@RequestMapping(value = "findOpShelfSku4Mobile")
	public String findOpShelfSku4Mobile(BizOpShelfSku bizOpShelfSku){
		Map<String, Object> resultMap = Maps.newHashMap();
		AttributeValueV2 bizSkuPropValue = new AttributeValueV2();//sku商品属性表
		List<BizOpShelfSku> list=null;
		boolean emptyName = false;//商品名称
		boolean emptyPart = false;//商品编码
		boolean emptyItemNo = false;//商品货号
		if (StringUtils.isBlank(bizOpShelfSku.getSkuInfo().getName())){
			emptyName = true;
		}
		if (StringUtils.isBlank(bizOpShelfSku.getSkuInfo().getPartNo())) {
			emptyPart = true;
		}
		if (StringUtils.isBlank(bizOpShelfSku.getSkuInfo().getItemNo())) {
			emptyItemNo = true;
		}
		if(emptyName==true && emptyPart==true && emptyItemNo==true){
			System.out.println("为空 不查询sku商品");
		}else {
			bizOpShelfSku.setSkuDeleteFlag("true");
			list = bizOpShelfSkuService.findList(bizOpShelfSku);
		}
		if(list!=null){
			for (BizOpShelfSku skuValue : list) {
				bizSkuPropValue.setObjectId(skuValue.getSkuInfo().getId());//sku_Id
				bizSkuPropValue.setObjectName("biz_sku_info");
				List<AttributeValueV2> skuValueList = attributeValueService.findList(bizSkuPropValue);
				if(skuValueList.size()!=0){
					skuValue.setSkuValueList(skuValueList);
				}
			}
		}
		resultMap.put("list", list);
		return JsonUtil.generateData(resultMap, null);
	}

    /**
     * 根据商品ID查询上架商品
     * @param id
     * @return
     */
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@ResponseBody
	@RequestMapping(value = "findOpShelfSkuList")
	public BizOpShelfSku findOpShelfSkuList(Integer id){
        BizSkuInfo bizSkuInfo = bizSkuInfoService.get(id);
        BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();

        bizOpShelfSku.setSkuInfo(bizSkuInfo);
        List<BizOpShelfSku> opShelfSkuList = bizOpShelfSkuService.findList(bizOpShelfSku);
        if (opShelfSkuList != null && opShelfSkuList.size()>0){
            bizOpShelfSku = opShelfSkuList.get(0);
        }
        return bizOpShelfSku;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpShelfSku bizOpShelfSku, RedirectAttributes redirectAttributes) {
		bizOpShelfSkuService.delete(bizOpShelfSku);
		addMessage(redirectAttributes, "删除商品上架成功");
		if(bizOpShelfSku.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"//biz/shelf/bizOpShelfInfo/form?id="+bizOpShelfSku.getOpShelfInfo().getId();
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "dateTimeSave")
	public String dateTimeSave(BizOpShelfSku bizOpShelfSku, Model model, RedirectAttributes redirectAttributes) {
		Date day=new Date();//当前时间
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
//		System.out.println(df.format(day));
		bizOpShelfSku.setUnshelfTime(day);
		bizOpShelfSkuService.updateDateTime(bizOpShelfSku);
		addMessage(redirectAttributes, "下架成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/?repage";
	}

	/**
	 * 商品下架后上架
	 * @param bizOpShelfSku
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "shelvesSave")
	public String shelvesSave(BizOpShelfSku bizOpShelfSku, Model model, RedirectAttributes redirectAttributes) {

        User user = UserUtils.getUser();
        bizOpShelfSku.setShelfUser(user);
        bizOpShelfSku.setShelfTime(new Date());
        bizOpShelfSku.setUnshelfTime(null);
		bizOpShelfSkuService.updateShelves(bizOpShelfSku);
		addMessage(redirectAttributes, "上架成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSku/?repage";
	}

    /**
     * 验证上架数量区间是否已经存在
     * @param skuInfoIds
     * @param minQtys
     * @param maxQtys
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
    @RequestMapping(value = "checkNum")
	public String checkNum(String skuInfoIds,String minQtys,String maxQtys,Integer shelfSkuId,Integer shelfInfoId,Integer centId){
	    String flag = "true";
        String[] skuIdArr=skuInfoIds.split(",");
        String[] maxQtyArr=maxQtys.split(",");
        String[] minQtyArr=minQtys.split(",");
        for(int i=0;i<skuIdArr.length;i++){
            if (!skuIdArr[i].equals("")){
                BizSkuInfo skuInfo = new BizSkuInfo();
                skuInfo.setId(Integer.parseInt(skuIdArr[i]));
                BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
                bizOpShelfSku.setSkuInfo(skuInfo);
                BizOpShelfInfo bizOpShelfInfo = new BizOpShelfInfo();
                bizOpShelfInfo.setId(shelfInfoId);
                bizOpShelfSku.setOpShelfInfo(bizOpShelfInfo);
                if (centId != null){
                    Office center = new Office();
                    center.setId(centId);
                    bizOpShelfSku.setCenterOffice(center);
                }
                List<BizOpShelfSku> list = bizOpShelfSkuService.findList(bizOpShelfSku);
                if (shelfSkuId != null){
                    BizOpShelfSku opShelfSku = bizOpShelfSkuService.get(shelfSkuId);
                    list.remove(opShelfSku);
                }
                for(BizOpShelfSku opShelfSku:list){
                    int minQty = opShelfSku.getMinQty();
                    int maxQty = opShelfSku.getMaxQty();
                    if (minQty >= Integer.parseInt(minQtyArr[i]) && maxQty <= Integer.parseInt(maxQtyArr[i]) ||
                            minQty <= Integer.parseInt(maxQtyArr[i]) && maxQty >= Integer.parseInt(minQtyArr[i])){
                        flag = "false";
                    }
                }
            }
        }
        return flag;
    }

	/**
	 * C端上下架管理列表
	 * */
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value ="cendList")
	public String cendList(BizOpShelfSku bizOpShelfSku, HttpServletRequest request, HttpServletResponse response, Model model) {
		String supplierId = DictUtils.getDictValue("微店", "biz_opshel_cend", "");
		BizOpShelfInfo bizOpShelfInfo = new BizOpShelfInfo();
		bizOpShelfInfo.setId(Integer.parseInt(supplierId));
		bizOpShelfSku.setOpShelfInfo(bizOpShelfInfo);
		Page<BizOpShelfSku> page = bizOpShelfSkuService.findPage(new Page<BizOpShelfSku>(request, response), bizOpShelfSku);
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizOpShelfSkuCendList";
	}

	/**
	 * C端上下架管理form
	 * */
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = "cendForm")
	public String cendForm(BizOpShelfSkus bizOpShelfSku, Model model) {
		if (bizOpShelfSku != null && bizOpShelfSku.getId() != null){
			BizOpShelfSku bizOpShelfSku1 = bizOpShelfSkuService.get(bizOpShelfSku.getId());
			model.addAttribute("bizOpShelfSku",bizOpShelfSku1);
		}else {
			model.addAttribute("bizOpShelfSku", bizOpShelfSku);
		}
		model.addAttribute("bizSkuInfo", new BizSkuInfo());
		return "modules/biz/shelf/bizOpShelfSkuCendForm";
	}

	/**
	 * 删除不刷新
	 * */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "deleteShelfSku")
	public String deleteShelfSku(BizOpShelfSku bizOpShelfSku, RedirectAttributes redirectAttributes) {
		String shelfSku = "Error";
		try {
			bizOpShelfSkuService.delete(bizOpShelfSku);
			shelfSku = "OK";
		} catch (Exception e) {
			LOGGER.error("删除上架商品失败,bizOpShelfSkuId{}",bizOpShelfSku.getId(),e);
		}
		return shelfSku;
	}

	/**
	 * 下架不刷新
	 * */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "dateTimeLower")
	public String dateTimeLower(BizOpShelfSku bizOpShelfSku, Model model, RedirectAttributes redirectAttributes) {
		String Lower = "Error";
		try {
			Date day = new Date();
			bizOpShelfSku.setUnshelfTime(day);
			bizOpShelfSkuService.updateDateTime(bizOpShelfSku);
			Lower = "OK";
		} catch (Exception e) {
			LOGGER.error("商品下架失败，bizOpshelfSkuId{}",bizOpShelfSku.getId(),e);
		}
		return Lower;
	}

	/**
	 * 上架不刷新
	 * */
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "shelvesLower")
	public String shelvesLower(BizOpShelfSku bizOpShelfSku, Model model, RedirectAttributes redirectAttributes) {
		String sheLve = "Error";
		try {
			User user = UserUtils.getUser();
			bizOpShelfSku.setShelfUser(user);
			bizOpShelfSku.setShelfTime(new Date());
			bizOpShelfSku.setUnshelfTime(null);
			bizOpShelfSkuService.updateShelves(bizOpShelfSku);
			sheLve = "OK";
		} catch (Exception e) {
			LOGGER.error("商品重新上架失败，bizOpshelfSkuId{}",bizOpShelfSku.getId(),e);
		}
		return sheLve;
	}

}

