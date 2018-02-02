/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.shelf.BizShelfUser;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.shelf.BizShelfUserService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.SupportCenterStatusEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
import java.util.List;

/**
 * 运营货架信息Controller
 * @author liuying
 * @version 2017-12-19
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizOpShelfInfo")
public class BizOpShelfInfoController extends BaseController {

	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private BizShelfUserService bizShelfUserService;

	@ModelAttribute
	public BizOpShelfInfo get(@RequestParam(required=false) Integer id) {
		BizOpShelfInfo entity = null;
		if (id!=null){
			entity = bizOpShelfInfoService.get(id);
			BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
			bizOpShelfSku.setOpShelfInfo(entity);
			List<BizOpShelfSku> opShelfSkusList = bizOpShelfSkuService.findList(bizOpShelfSku);
			entity.setOpShelfSkusList(opShelfSkusList);
		}
		if (entity == null){
			entity = new BizOpShelfInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpShelfInfo bizOpShelfInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpShelfInfo> page = bizOpShelfInfoService.findPage(new Page<BizOpShelfInfo>(request, response), bizOpShelfInfo); 
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizOpShelfInfoList";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "form")
	public String form(BizOpShelfInfo bizOpShelfInfo, Model model) {
		model.addAttribute("bizOpShelfInfo", bizOpShelfInfo);
		return "modules/biz/shelf/bizOpShelfInfoForm";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizOpShelfInfo bizOpShelfInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOpShelfInfo)){
			return form(bizOpShelfInfo, model);
		}
		bizOpShelfInfoService.save(bizOpShelfInfo);
		addMessage(redirectAttributes, "保存货架信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/?repage";
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpShelfInfo bizOpShelfInfo, RedirectAttributes redirectAttributes) {
		bizOpShelfInfoService.delete(bizOpShelfInfo);
		addMessage(redirectAttributes, "删除货架信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/?repage";
	}
	
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "findShelf")
	public List<BizOpShelfInfo> findShelf(BizOpShelfInfo bizOpShelfInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user= UserUtils.getUser();
        if(user.isAdmin()){
			return bizOpShelfInfoService.findList(bizOpShelfInfo);
        }else {
            bizOpShelfInfo.getSqlMap().put("shelfInfo", BaseService.dataScopeFilter(user, "so", "suc"));
            return bizOpShelfInfoService.findList(bizOpShelfInfo);
        }
       /* User user = UserUtils.getUser();
        BizShelfUser bizShelfUser = new BizShelfUser();
        bizShelfUser.setUser(user);
        List<BizShelfUser> bizShelfUserList = bizShelfUserService.findList(bizShelfUser);
        List<BizOpShelfInfo> list = new ArrayList<>();
        for (BizShelfUser bizShelfUser1:bizShelfUserList) {
               list.add(bizShelfUser1.getShelfInfo());
        }*/
//        List<BizOpShelfInfo> list = bizOpShelfInfoService.findList(bizOpShelfInfo);
		
//		return list;
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "shelfManagementList")
	public String shelfManagementList(BizOpShelfInfo bizOpShelfInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpShelfInfo> page = bizOpShelfInfoService.findPage(new Page<BizOpShelfInfo>(request, response), bizOpShelfInfo);
        BizShelfUser bizShelfUser = new BizShelfUser();
		BizOpShelfInfo bizOpShelfInfo1 = bizOpShelfInfoService.get(bizOpShelfInfo.getId());
		bizShelfUser.setShelfInfo(bizOpShelfInfo1);
		List<BizShelfUser> bizShelfUserList = bizShelfUserService.findList(bizShelfUser);

		model.addAttribute("page", page);
		model.addAttribute("bizShelfUserList",bizShelfUserList);
        model.addAttribute("bizOpShelfInfo",bizOpShelfInfo);
		return "modules/biz/shelf/bizOpShelfManagementList";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "shelfManagementForm")
	public String shelfManagementForm(BizOpShelfInfo bizOpShelfInfo, Model model) {

		//查询营销中心下的用户
		Office office = officeService.get(SupportCenterStatusEnum.MAKER_CENTER.getState());
		List<User> userList = systemService.findYzUser(office);
//		bizOpShelfInfo.setUserList(userList);
        model.addAttribute("bizOpShelfInfo", bizOpShelfInfo);
		model.addAttribute("userList", userList);
		return "modules/biz/shelf/bizOpShelfManagementForm";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "shelfManagementCommit")
	public String shelfManagementCommit(BizOpShelfInfo bizOpShelfInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
       String userIds= bizOpShelfInfo.getUserIds();
      String[] userIdStr=  StringUtils.split(userIds,",");
        BizShelfUser bizShelfUser = new BizShelfUser();
      for(int i=0;i<userIdStr.length;i++){
          BizShelfUser bizShelfUser1 = new BizShelfUser();
          bizShelfUser1.setShelfInfo(bizOpShelfInfo);
          bizShelfUser1.setUser(systemService.getUser(Integer.parseInt(userIdStr[i].trim())));
          List<BizShelfUser> list = bizShelfUserService.findList(bizShelfUser1);
          //不可重复添加
          if(list == null || list.size()==0) {
              bizShelfUser.setId(null);
              bizShelfUser.setShelfInfo(bizOpShelfInfo);
              User user1 = systemService.getUser(Integer.parseInt(userIdStr[i].trim()));
              bizShelfUser.setUser(user1);
              bizShelfUserService.save(bizShelfUser);
          }
      }

        return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/shelfManagementList?id="+bizOpShelfInfo.getId();
	}

    @RequiresPermissions("biz:shelf:bizOpShelfInfo:edit")
    @RequestMapping(value = "deleteShelfUser")
    public String deleteShelfUser(BizShelfUser bizShelfUser, RedirectAttributes redirectAttributes) {
        bizShelfUserService.delete(bizShelfUser);
        addMessage(redirectAttributes, "删除货架管理员成功");
        return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/shelfManagementList?repage";
    }

//	用于选择货架显示采购中心
	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "findColum")
	public BizOpShelfInfo findColum(BizOpShelfInfo bizOpShelfInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(bizOpShelfInfo.getId()!=null){
			BizOpShelfInfo bizOpShelfInfo1 = bizOpShelfInfoService.get(bizOpShelfInfo.getId());
			return bizOpShelfInfo1;
		}else{
			return null;
		}

	}
}