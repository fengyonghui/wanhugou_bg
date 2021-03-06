/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoForVendorV2Service;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV3Service;
import com.wanhutong.backend.modules.enums.VarietyAttrEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeInfoV2Service;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
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
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.product.BizVarietyAttr;
import com.wanhutong.backend.modules.biz.service.product.BizVarietyAttrService;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 分类属性中间表Controller
 * @author ZhangTengfei
 * @version 2018-05-28
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/bizVarietyAttr")
public class BizVarietyAttrController extends BaseController {

	private static final Integer REQUIRED = 1;
	private static final Integer NOTREQUIRED = 0;

	@Autowired
	private BizVarietyAttrService bizVarietyAttrService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	@Autowired
	private AttributeInfoV2Service attributeInfoV2Service;
	@Autowired
    private DictService dictService;
	@Autowired
	private AttributeValueV2Service attributeValueV2Service;
	@Autowired
	private BizProductInfoV3Service bizProductInfoV3Service;
	@Autowired
	private BizProductInfoForVendorV2Service bizProductInfoForVendorV2Service;
	
	@ModelAttribute
	public BizVarietyAttr get(@RequestParam(required=false) Integer id) {
		BizVarietyAttr entity = null;
		if (id!=null){
			entity = bizVarietyAttrService.get(id);
		}
		if (entity == null){
			entity = new BizVarietyAttr();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:product:bizVarietyAttr:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizVarietyAttr bizVarietyAttr, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizVarietyAttr> page = bizVarietyAttrService.findPage(new Page<BizVarietyAttr>(request, response), bizVarietyAttr); 
		model.addAttribute("page", page);
		return "modules/biz/product/bizVarietyAttrList";
	}

	@RequiresPermissions("biz:product:bizVarietyAttr:view")
	@RequestMapping(value = "form")
	public String form(BizVarietyAttr bizVarietyAttr, Model model) {
        model.addAttribute("bizVarietyAttr", bizVarietyAttr);
        if (bizVarietyAttr == null || bizVarietyAttr.getId() == null) {
            model.addAttribute("varietyInfoList", bizVarietyInfoService.findNotSpecialList());
        } else {
            model.addAttribute("varietyInfoList", bizVarietyInfoService.findList(new BizVarietyInfo()));
        }
        List<AttributeInfoV2> attributeInfoV2List = attributeInfoV2Service.findList(new AttributeInfoV2());
        for (Iterator<AttributeInfoV2> it = attributeInfoV2List.iterator(); it.hasNext(); ) {
            AttributeInfoV2 attributeInfoV2 = it.next();
            if (VarietyAttrEnum.NOT_VARIETY_ATTR.contains(attributeInfoV2.getId())) {
                it.remove();
            }
        }

        model.addAttribute("attributeInfoList", attributeInfoV2List);
        return "modules/biz/product/bizVarietyAttrForm";
    }

	@RequiresPermissions("biz:product:bizVarietyAttr:edit")
	@RequestMapping(value = "save")
	public String save(BizVarietyAttr bizVarietyAttr, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizVarietyAttr)) {
			return form(bizVarietyAttr, model);
		}
		if (bizVarietyAttr.getId() != null) {
			BizVarietyAttr attr = new BizVarietyAttr();
			attr.setVarietyInfo(bizVarietyAttr.getVarietyInfo());
			List<BizVarietyAttr> list = bizVarietyAttrService.findList(attr);
			for (BizVarietyAttr attr1 : list) {
				bizVarietyAttrService.delete(attr1);
			}
		}
		String attributeIds = bizVarietyAttr.getAttributeIds();
		String requireds = bizVarietyAttr.getRequireds();
		String[] attributeArr = StringUtils.split(attributeIds, ",");
		String[] requiredArr = StringUtils.split(requireds, ",");
		BizVarietyAttr attr = new BizVarietyAttr();
		if (attributeArr == null) {
			addMessage(redirectAttributes, "保存分类属性中间表失败");
			return "redirect:" + Global.getAdminPath() + "/biz/product/bizVarietyAttr/?repage";
		}
		for (int i = 0; i < attributeArr.length; i++) {
			AttributeInfoV2 attributeInfoV2 = attributeInfoV2Service.get(Integer.parseInt(attributeArr[i].trim()));
			attr.setAttributeInfo(attributeInfoV2);
			attr.setVarietyInfo(bizVarietyAttr.getVarietyInfo());
			if (requiredArr == null) {
				attr.setRequired(NOTREQUIRED);
			}
			if (requiredArr != null) {
				for (int j = 0; j < requiredArr.length; j++) {
					if (requiredArr[j].equals(attributeArr[i])) {
						attr.setRequired(REQUIRED);
						break;
					} else {
						attr.setRequired(NOTREQUIRED);
					}
				}
			}
			bizVarietyAttrService.save(attr);
		}
		addMessage(redirectAttributes, "保存分类属性中间表成功");
		return "redirect:" + Global.getAdminPath() + "/biz/product/bizVarietyAttr/?repage";
	}
	
	@RequiresPermissions("biz:product:bizVarietyAttr:edit")
	@RequestMapping(value = "delete")
	public String delete(BizVarietyAttr bizVarietyAttr, RedirectAttributes redirectAttributes) {
		bizVarietyAttrService.delete(bizVarietyAttr);
		addMessage(redirectAttributes, "删除分类属性中间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizVarietyAttr/?repage";
	}

    /**
     * 根据分类ID获取属性
     */
	@ResponseBody
    @RequiresPermissions("biz:product:bizVarietyAttr:view")
    @RequestMapping(value = "findAttribute")
	public List<BizVarietyAttr> findAttribute(Integer id) {
        BizVarietyAttr bizVarietyAttr = bizVarietyAttrService.get(id);
        BizVarietyAttr varietyAttr = new BizVarietyAttr();
        varietyAttr.setVarietyInfo(bizVarietyAttr.getVarietyInfo());
        List<BizVarietyAttr> varietyAttrList = bizVarietyAttrService.findList(varietyAttr);
            return varietyAttrList;
    }

    /**
     * 产品特有属性
     * @param varietyId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "findAttr")
    public List<BizVarietyAttr> findAttr(Integer varietyId,Integer prodId) {

        BizVarietyAttr varietyAttr = new BizVarietyAttr();
        varietyAttr.setVarietyInfo(new BizVarietyInfo(varietyId));
        List<BizVarietyAttr> list = bizVarietyAttrService.findList(varietyAttr);
        Dict dict = new Dict();
        for (BizVarietyAttr bizVarietyAttr:list) {
            AttributeInfoV2 attributeInfoV2 = attributeInfoV2Service.get(bizVarietyAttr.getAttributeInfo().getId());
            bizVarietyAttr.setAttributeInfo(attributeInfoV2);
            dict.setType(attributeInfoV2.getDict().getType());
            List<Dict> dictList = dictService.findList(dict);
            bizVarietyAttr.setDictList(dictList);
			if (prodId == null) {
				continue;
			}
            BizProductInfo bizProductInfo = bizProductInfoV3Service.get(prodId);
			if (!bizProductInfo.getBizVarietyInfo().getId().equals(varietyId)) {
			    continue;
            }
            AttributeValueV2 valueV2 = new AttributeValueV2();
			valueV2.setObjectId(prodId);
			valueV2.setObjectName(AttributeInfoV2.Level.PRODUCT.getTableName());
			List<AttributeValueV2> attributeValueV2List = attributeValueV2Service.findSpecificList(valueV2);
			bizVarietyAttr.setAttributeValueV2List(attributeValueV2List);
        }

        return list;
    }

	/**
	 * 供应商产品特有属性
	 * @param varietyId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "findVendAttr")
	public List<BizVarietyAttr> findVendAttr(Integer varietyId,Integer prodId) {

		BizVarietyAttr varietyAttr = new BizVarietyAttr();
		varietyAttr.setVarietyInfo(new BizVarietyInfo(varietyId));
		List<BizVarietyAttr> list = bizVarietyAttrService.findList(varietyAttr);
		Dict dict = new Dict();
		for (BizVarietyAttr bizVarietyAttr:list) {
			AttributeInfoV2 attributeInfoV2 = attributeInfoV2Service.get(bizVarietyAttr.getAttributeInfo().getId());
			bizVarietyAttr.setAttributeInfo(attributeInfoV2);
			dict.setType(attributeInfoV2.getDict().getType());
			List<Dict> dictList = dictService.findList(dict);
			bizVarietyAttr.setDictList(dictList);
			if (prodId == null) {
				continue;
			}
			BizProductInfo bizProductInfo = bizProductInfoForVendorV2Service.get(prodId);
			if (!bizProductInfo.getBizVarietyInfo().getId().equals(varietyId)) {
				continue;
			}
			AttributeValueV2 valueV2 = new AttributeValueV2();
			valueV2.setObjectId(prodId);
			valueV2.setObjectName(AttributeInfoV2.Level.PRODUCT_FOR_VENDOR.getTableName());
			List<AttributeValueV2> attributeValueV2List = attributeValueV2Service.findSpecificList(valueV2);
			bizVarietyAttr.setAttributeValueV2List(attributeValueV2List);
		}

		return list;
	}

}