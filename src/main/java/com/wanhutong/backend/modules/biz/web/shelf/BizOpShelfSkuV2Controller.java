/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.RoleUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.BizOpShelfSkus;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.shelf.BizShelfUser;
import com.wanhutong.backend.modules.biz.entity.shelf.BizVarietyFactor;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuV2Service;
import com.wanhutong.backend.modules.biz.service.shelf.BizShelfUserService;
import com.wanhutong.backend.modules.biz.service.shelf.BizVarietyFactorService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.SystemConfig;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品上架管理Controller
 * @author liuying
 * @version 2017-12-19
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizOpShelfSkuV2")
public class BizOpShelfSkuV2Controller extends BaseController {

	@Autowired
	private BizOpShelfSkuV2Service bizOpShelfSkuV2Service;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private AttributeValueV2Service attributeValueService;
	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;
	@Autowired
	private BizVarietyFactorService bizVarietyFactorService;
	@Autowired
	private BizShelfUserService bizShelfUserService;

	
	@ModelAttribute
	public BizOpShelfSku get(@RequestParam(required=false) Integer id) {
		BizOpShelfSku entity = null;
		if (id!=null){
			entity = bizOpShelfSkuV2Service.get(id);
		}
		if (entity == null){
			entity = new BizOpShelfSku();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpShelfSku bizOpShelfSku, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpShelfSku> page = bizOpShelfSkuV2Service.findPage(new Page<BizOpShelfSku>(request, response), bizOpShelfSku);
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizOpShelfSkuListV2";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = "form")
	public String form(BizOpShelfSkus bizOpShelfSku, Model model) {
		if (bizOpShelfSku != null && bizOpShelfSku.getId() != null) {
			BizOpShelfSku bizOpShelfSku1 = bizOpShelfSkuV2Service.get(bizOpShelfSku.getId());
			model.addAttribute("bizOpShelfSku", bizOpShelfSku1);
			List<BizOpShelfSku> bizOpShelfSkuList = bizOpShelfSkuV2Service.findShelfSkuList(bizOpShelfSku1);
			model.addAttribute("bizOpShelfSkuList", bizOpShelfSkuList);
		} else {
			model.addAttribute("bizOpShelfSku", bizOpShelfSku);
		}
		model.addAttribute("varietyFactorList",bizVarietyFactorService.findList(new BizVarietyFactor()));
        model.addAttribute("bizSkuInfo", new BizSkuInfo());
		BizOpShelfInfo opShelfInfo=new BizOpShelfInfo();
//		opShelfInfo.setType(BizOpShelfInfoEnum.LOCAL_STOCK.getLocal());
		User user= UserUtils.getUser();
		Role role=new Role();
		role.setEnname(RoleEnNameEnum.DEPT.getState());
		if(user.isAdmin() || user.getRoleList().contains(role)){
			model.addAttribute("shelfList",bizOpShelfInfoService.findList(opShelfInfo));
		}else {
			//货架列表走货架管理员权限
			BizShelfUser bizShelfUser = new BizShelfUser();
			bizShelfUser.setUser(user);
			List<BizShelfUser> bizShelfUserList = bizShelfUserService.findList(bizShelfUser);
			List<BizOpShelfInfo> list = new ArrayList<>();
			for (BizShelfUser bizShelfUser1:bizShelfUserList) {
				list.add(bizShelfUser1.getShelfInfo());
			}
			model.addAttribute("shelfList",list);
		}

		SystemConfig systemConfig = ConfigGeneral.SYSTEM_CONFIG.get();
		boolean hasUnderPriceRole = RoleUtils.hasRole(user, systemConfig.getPutawayUnderCostAudit());
		model.addAttribute("hasUnderPriceRole", hasUnderPriceRole || user.isAdmin());

		return "modules/biz/shelf/bizOpShelfSkuFormV2";
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
        if (bizOpShelfSkus.getShelfs() != null && !"".equals(bizOpShelfSkus.getShelfs())) {
            String[] shelfArr = bizOpShelfSkus.getShelfs().split(",");
            for (int j = 0; j < shelfArr.length; j++) {
                for (int i = 0; i < skuIdArr.length; i++) {
                    BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
                    if (bizOpShelfSkus.getId() != null) {
                        bizOpShelfSku.setId(bizOpShelfSkus.getId());

                        if (flag) {
                            bizOpShelfSku.setId(null);
                        }
                        flag = true;
                    } else {
                        bizOpShelfSku.setId(null);
                    }
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(Integer.parseInt(skuIdArr[i].trim()));
                    bizOpShelfSku.setSkuInfo(bizSkuInfo);
                    bizOpShelfSku.setProductInfo(bizSkuInfo.getProductInfo());
                    bizOpShelfSku.setCenterOffice(bizOpShelfSkus.getCenterOffice());
                    bizOpShelfSku.setOpShelfInfo(new BizOpShelfInfo(Integer.parseInt(shelfArr[j].trim())));
                    bizOpShelfSku.setMaxQty(Integer.parseInt(maxQtyArr[i].trim()));
                    bizOpShelfSku.setMinQty(Integer.parseInt(minQtyArr[i].trim()));
                    bizOpShelfSku.setOrgPrice(Double.parseDouble(orgPriceArr[i].trim()));
                    bizOpShelfSku.setPriority(Integer.parseInt(priorityArr[i].trim()));
                    bizOpShelfSku.setSalePrice(Double.parseDouble(salePriceArr[i].trim()));
                    bizOpShelfSku.setShelfQty(Integer.parseInt(shelfQtyArr[i].trim()));
                    bizOpShelfSku.setShelfTime(DateUtils.parseDate(shelfTimeArr[i].trim()));
                    if (unShelfTimeArr.length > 0) {
                        if (!unShelfTimeArr[i].equals("0")) {
                            bizOpShelfSku.setUnshelfTime(DateUtils.parseDate(unShelfTimeArr[i].trim()));
                        }
                    }
                    bizOpShelfSkuV2Service.save(bizOpShelfSku);
                }
            }
        }
        if (bizOpShelfSkus.getOpShelfInfo() != null) {
            for (int i = 0; i < skuIdArr.length; i++) {
                BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
                if (bizOpShelfSkus.getId() != null) {
                    bizOpShelfSku.setId(bizOpShelfSkus.getId());

                    if (flag) {
                        bizOpShelfSku.setId(null);
                    }
                    flag = true;
                } else {
                    bizOpShelfSku.setId(null);
                }
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(Integer.parseInt(skuIdArr[i].trim()));
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
                if (unShelfTimeArr.length > 0) {
                    if (!unShelfTimeArr[i].equals("0")) {
                        bizOpShelfSku.setUnshelfTime(DateUtils.parseDate(unShelfTimeArr[i].trim()));
                    }
                }
                bizOpShelfSkuV2Service.save(bizOpShelfSku);
            }
        }

		addMessage(redirectAttributes, "保存商品上架成功");
		if(bizOpShelfSkus.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSkuV2/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/form?id=" + bizOpShelfSkus.getOpShelfInfo().getId() ;
	}

	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@ResponseBody
	@RequestMapping(value = "findOpShelfSku")
	public List<BizOpShelfSku> findOpShelfSku(BizOpShelfSku bizOpShelfSku) {
        AttributeValueV2 bizSkuPropValue = new AttributeValueV2();//sku商品属性表
        if (bizOpShelfSku.getSkuInfo().getName().isEmpty() && bizOpShelfSku.getSkuInfo().getPartNo().isEmpty() && bizOpShelfSku.getSkuInfo().getItemNo().isEmpty()) {
            logger.info("修改订单详情和添加、修改备货单时，需要添加商品，由于未输入查询条件，导致不查询商品，点击查询没反应");
			return null;
		}
		List<BizOpShelfSku> list = bizOpShelfSkuV2Service.findList(bizOpShelfSku);
        if (CollectionUtils.isNotEmpty(list)) {
            for (BizOpShelfSku skuValue : list) {
                bizSkuPropValue.setObjectId(skuValue.getSkuInfo().getId());//sku_Id
                bizSkuPropValue.setObjectName("biz_sku_info");
                List<AttributeValueV2> skuValueList = attributeValueService.findList(bizSkuPropValue);
                if (skuValueList.size() != 0) {
                    skuValue.setSkuValueList(skuValueList);
                }
            }
        }
        return list;
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
        List<BizOpShelfSku> opShelfSkuList = bizOpShelfSkuV2Service.findList(bizOpShelfSku);
        if (opShelfSkuList != null && opShelfSkuList.size()>0){
            bizOpShelfSku = opShelfSkuList.get(0);
        }
        return bizOpShelfSku;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpShelfSku bizOpShelfSku, RedirectAttributes redirectAttributes) {
		bizOpShelfSku.setDelFlag(BizOpShelfSku.DEL_FLAG_DELETE);
		bizOpShelfSkuV2Service.delete(bizOpShelfSku);
		addMessage(redirectAttributes, "删除商品上架成功");
		if(bizOpShelfSku.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSkuV2/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"//biz/shelf/bizOpShelfInfo/form?id="+bizOpShelfSku.getOpShelfInfo().getId();
	}
	@RequiresPermissions("biz:shelf:bizOpShelfSku:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizOpShelfSku bizOpShelfSku, RedirectAttributes redirectAttributes) {
		bizOpShelfSku.setDelFlag(BizOpShelfSku.DEL_FLAG_NORMAL);
		bizOpShelfSkuV2Service.delete(bizOpShelfSku);
		addMessage(redirectAttributes, "恢复商品上架成功");
		if(bizOpShelfSku.getShelfSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSkuV2/?repage";
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
		bizOpShelfSkuV2Service.updateDateTime(bizOpShelfSku);
		addMessage(redirectAttributes, "下架成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSkuV2/?repage";
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
		bizOpShelfSkuV2Service.updateShelves(bizOpShelfSku);
		addMessage(redirectAttributes, "上架成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfSkuV2/?repage";
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
	public String checkNum(String skuInfoIds,String minQtys,String maxQtys,Integer shelfSkuId,String shelfInfoIds,Integer centId) {
		String flag = "true";
		String[] skuIdArr = skuInfoIds.split(",");
		String[] maxQtyArr = maxQtys.split(",");
		String[] minQtyArr = minQtys.split(",");
		if (shelfSkuId != null && !shelfSkuId.equals("")) {
			BizOpShelfSku bizOpShelfSku1 = bizOpShelfSkuV2Service.get(shelfSkuId);
			Integer opShelfInfoId = bizOpShelfSku1.getOpShelfInfo().getId();
			shelfInfoIds = String.valueOf(opShelfInfoId);
		}
		String[] shelfs = shelfInfoIds.split(",");
		for (int j = 0; j < shelfs.length; j++) {
			for (int i = 0; i < skuIdArr.length; i++) {
				if (!skuIdArr[i].equals("")) {
					BizSkuInfo skuInfo = new BizSkuInfo();
					skuInfo.setId(Integer.parseInt(skuIdArr[i]));
					BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
					bizOpShelfSku.setSkuInfo(skuInfo);
					BizOpShelfInfo bizOpShelfInfo = new BizOpShelfInfo();
					bizOpShelfInfo.setId(Integer.parseInt(shelfs[j]));
					bizOpShelfSku.setOpShelfInfo(bizOpShelfInfo);
					if (centId != null) {
						Office center = new Office();
						center.setId(centId);
						bizOpShelfSku.setCenterOffice(center);
					}
					List<BizOpShelfSku> list = bizOpShelfSkuV2Service.findList(bizOpShelfSku);
					if (shelfSkuId != null) {
						BizOpShelfSku opShelfSku = bizOpShelfSkuV2Service.get(shelfSkuId);
						list.remove(opShelfSku);
					}
					for (BizOpShelfSku opShelfSku : list) {
						int minQty = opShelfSku.getMinQty();
						int maxQty = opShelfSku.getMaxQty();
						if (minQty >= Integer.parseInt(minQtyArr[i]) && maxQty <= Integer.parseInt(maxQtyArr[i]) ||
								minQty <= Integer.parseInt(maxQtyArr[i]) && maxQty >= Integer.parseInt(minQtyArr[i])) {
							flag = "false";
						}
					}
				}
			}
		}
        return flag;
    }

    @ResponseBody
    @RequiresPermissions("biz:shelf:bizOpShelfSku:view")
    @RequestMapping(value = "sort")
    public String sort() {
        bizOpShelfSkuV2Service.sort();
        return "success";
    }
}

