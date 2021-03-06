/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.beanvalidator.BeanValidators;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcel;
import com.wanhutong.backend.common.utils.excel.ImportExcel;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.variety.BizVarietyUserInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.variety.BizVarietyUserInfoService;
import com.wanhutong.backend.modules.biz.web.statistics.BizStatisticsPlatformController;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户Controller
 * @author ThinkGem
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

    //用来判断仓储专员
    private static final String WAREHOUSESPECIALIST = "stoIndex";

	private final static Logger LOGGER = LoggerFactory.getLogger(BizStatisticsPlatformController.class);
	/**
	 * 默认超时时间
	 */
	private static final Long DEFAULT_TIME_OUT = TimeUnit.SECONDS.toMillis(60);

	@Autowired
	private SystemService systemService;

	@Autowired
	private OfficeService officeService;
	@Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
	@Autowired
	private BizOrderHeaderDao bizOrderHeaderDao;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	@Autowired
	private BizVarietyUserInfoService bizVarietyUserInfoService;
	@Autowired
	private CommonImgService commonImgService;

	@ModelAttribute
	public User get(@RequestParam(required=false) Integer id) {
		if (id!=null){
			return systemService.getUser(id);
		}else{
			return new User();
		}
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"index"})
	public String index(User user, Model model) {
		return "modules/sys/userIndex";
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"list", ""})
	public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model,Date ordrHeaderStartTime,Date orderHeaderEedTime) {
		//用户保存后，重定向到list页面清空归属公司type
		if ("saveUser".equals(user.getSearchScr()) && user.getCompany() != null) {
			user.getCompany().setType(null);
		}
		if (user.getCompany() != null && user.getCompany().getSource() != null && "officeConnIndex".equals(user.getCompany().getSource())) {
			//属于客户专员左边点击菜单查询
			Office queryOffice = officeService.get(user.getCompany().getId());
			if (queryOffice != null) {
				if (queryOffice.getType().equals(String.valueOf(OfficeTypeEnum.PURCHASINGCENTER.getType()))) {//采购中心8
					user.getCompany().setType(queryOffice.getType());
				} else if (queryOffice.getType().equals(String.valueOf(OfficeTypeEnum.WITHCAPITAL.getType()))) { //配资中心 10
					user.getCompany().setCustomerTypeTen(queryOffice.getType());
				} else if (queryOffice.getType().equals(String.valueOf(OfficeTypeEnum.NETWORKSUPPLY.getType()))) {    //网供中心 11
					user.getCompany().setCustomerTypeEleven(queryOffice.getType());
					user.getCompany().setCustomerTypeThirteen(String.valueOf(OfficeTypeEnum.NETWORK.getType()));
				} else {
					user.getCompany().setType(String.valueOf(OfficeTypeEnum.PURCHASINGCENTER.getType()));
					user.getCompany().setCustomerTypeTen(String.valueOf(OfficeTypeEnum.WITHCAPITAL.getType()));
					user.getCompany().setCustomerTypeEleven(String.valueOf(OfficeTypeEnum.NETWORKSUPPLY.getType()));
					user.getCompany().setCustomerTypeThirteen(String.valueOf(OfficeTypeEnum.NETWORK.getType()));
				}
			}
		}
		if (UserUtils.getUser().isAdmin()) {
			user.setDataStatus("filter");
		}
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		if (user.getConn() != null && "connIndex".equals(user.getConn())) {
			//客户专员统计
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			User userAdmin = UserUtils.getUser();
			if (ordrHeaderStartTime != null) {
				bizOrderHeader.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
			}
			if (orderHeaderEedTime != null) {
				bizOrderHeader.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
			}
			if (!userAdmin.isAdmin()) {
				bizOrderHeader.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
			}
			for (int i = 0; i < page.getList().size(); i++) {
				bizOrderHeader.setCon(page.getList().get(i));
				BizOrderHeader orderUserCount = bizOrderHeaderDao.findOrderUserCount(bizOrderHeader);
				if (orderUserCount != null) {
					page.getList().get(i).setUserOrder(orderUserCount);
				}
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("ordrHeaderStartTime", ordrHeaderStartTime);
		model.addAttribute("orderHeaderEedTime", orderHeaderEedTime);
		return "modules/sys/userList";
	}


	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"listData4mobile"})
	@ResponseBody
	public String listData4mobile(User user, HttpServletRequest request, HttpServletResponse response, Date ordrHeaderStartTime,Date orderHeaderEedTime,
								  Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo
								  ) {
		if (user.getCompany() != null && user.getCompany().getSource() != null && "officeConnIndex".equals(user.getCompany().getSource())) {
			//属于客户专员左边点击菜单查询
			Office queryOffice = officeService.get(user.getCompany().getId());
			if (queryOffice != null) {
				if (queryOffice.getType().equals(String.valueOf(OfficeTypeEnum.PURCHASINGCENTER.getType()))) {//采购中心8
					user.getCompany().setType(queryOffice.getType());
				} else if (queryOffice.getType().equals(String.valueOf(OfficeTypeEnum.WITHCAPITAL.getType()))) { //配资中心 10
					user.getCompany().setCustomerTypeTen(queryOffice.getType());
				} else if (queryOffice.getType().equals(String.valueOf(OfficeTypeEnum.NETWORKSUPPLY.getType()))) {    //网供中心 11
					user.getCompany().setCustomerTypeEleven(queryOffice.getType());
					user.getCompany().setCustomerTypeThirteen(String.valueOf(OfficeTypeEnum.NETWORK.getType()));
				} else {
					user.getCompany().setType(String.valueOf(OfficeTypeEnum.PURCHASINGCENTER.getType()));
					user.getCompany().setCustomerTypeTen(String.valueOf(OfficeTypeEnum.WITHCAPITAL.getType()));
					user.getCompany().setCustomerTypeEleven(String.valueOf(OfficeTypeEnum.NETWORKSUPPLY.getType()));
					user.getCompany().setCustomerTypeThirteen(String.valueOf(OfficeTypeEnum.NETWORK.getType()));
				}
			}
		}
		if (UserUtils.getUser().isAdmin()) {
			user.setDataStatus("filter");
		}

		Page<User> userPage = new Page<>(request, response);
		userPage.setPageNo(pageNo);
		Page<User> page = systemService.findUser(userPage, user);
		if (user.getConn() != null && "connIndex".equals(user.getConn())) {
			//客户专员统计
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			User userAdmin = UserUtils.getUser();
			if (ordrHeaderStartTime != null) {
				bizOrderHeader.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
			}
			if (orderHeaderEedTime != null) {
				bizOrderHeader.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
			}
			if (!userAdmin.isAdmin()) {
				bizOrderHeader.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
			}
			for (int i = 0; i < page.getList().size(); i++) {
				bizOrderHeader.setCon(page.getList().get(i));
				BizOrderHeader orderUserCount = bizOrderHeaderDao.findOrderUserCount(bizOrderHeader);
				if (orderUserCount != null) {
					page.getList().get(i).setUserOrder(orderUserCount);
				}
			}
		}
//		model.addAttribute("page", page);
//		model.addAttribute("ordrHeaderStartTime", ordrHeaderStartTime);
//		model.addAttribute("orderHeaderEedTime", orderHeaderEedTime);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("page", page);
		resultMap.put("ordrHeaderStartTime", ordrHeaderStartTime);
		resultMap.put("orderHeaderEedTime", orderHeaderEedTime);
		return JsonUtil.generateData(resultMap, request.getParameter("callback"));
	}

	@ResponseBody
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"listData"})
	public Page<User> listData(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		return page;
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	public String form(User user, Model model,String flag) {
		if (user.getCompany() != null) {
			Office searchCompany = user.getCompany();
			Integer searchCompanyId = searchCompany.getSearchCompanyId();
			String searchCompanyName = searchCompany.getSearchCompanyName();
			model.addAttribute("searchCompanyId",searchCompanyId);
			model.addAttribute("searchCompanyName",searchCompanyName);
		}
		if (user.getOffice() != null) {
			Office searchOffice = user.getOffice();
			Integer searchOfficeId = searchOffice.getSearchOfficeId();
			String searchOfficeName = searchOffice.getSearchOfficeName();
			model.addAttribute("searchOfficeId",searchOfficeId);
			model.addAttribute("searchOfficeName",searchOfficeName);
		}
		if (user.getId() != null) {
			CommonImg commonImg = new CommonImg();
			commonImg.setObjectId(user.getId());
			commonImg.setObjectName(ImgEnum.USER_PHOTO.getTableName());
			commonImg.setImgType(ImgEnum.USER_PHOTO.getCode());
			List<CommonImg> headPhotoList = commonImgService.findList(commonImg);
			if (CollectionUtils.isNotEmpty(headPhotoList)) {
				model.addAttribute("headPhotoList",headPhotoList);
			}
		}
		if (user.getOffice()==null || user.getOffice().getId()==null){
			user.setCompany(UserUtils.getUser().getCompany());
			user.setOffice(UserUtils.getUser().getOffice());
		}else{
			Office off = new Office();
			off.setParentIds("%"+user.getOffice().getId()+",");
			List<Office> list = officeService.findList(off);
			Office office = officeService.get(user.getOffice().getId());
			if(list == null || list.isEmpty()){
//				if(OfficeTypeEnum.PURCHASINGCENTER.getType().equals(office.getType())){
//					user.setCompany(office);
//					user.setOffice(office);
//				}else if(OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
//					user.setCompany(office);
//					user.setOffice(office);
//				}else {
//					Office parentOffice = officeService.get(office.getParentId());
//					user.setCompany(parentOffice);
					user.setCompany(office);
					user.setOffice(office);
//				}

			}else{
				if(user.getConn()!=null && user.getConn().equals("connIndex")){//客户专员标识符
					user.setCompany(office);
					user.setOffice(office);
				}else if(user.getOffice().getId()!=null || user.getCompany().getId()!=null){
					user.setCompany(user.getCompany());
					user.setOffice(user.getOffice());
				}else{
					user.setCompany(office);
					user.setOffice(null);
				}
			}
		}
		if (flag != null && !"".equals(flag)){
			model.addAttribute("flag",flag);
		}
		if(user.getConn()!=null && user.getConn().equals("office_user_save")) {
			//用户列表默认选择部门
			if(user.getId()==null && user.getUserFlag().equals("")){
				user.setCompany(null);
				user.setOffice(null);
			}
			model.addAttribute("user", user);
		}else {
			model.addAttribute("user", user);
		}
		model.addAttribute("allRoles", systemService.findAllRole());

		String officeUser="office_user_save";
		if(user.getConn()!=null && user.getConn().equals(officeUser)){
			//用于用户管理添加跳转
			return "modules/sys/officeUserForm";
		}
		return "modules/sys/userForm";
	}

	@RequestMapping(value = "getAdvisers")
	@ResponseBody
	public List<User> getAdvisers(User user, HttpServletRequest request, HttpServletResponse response, Model model){
		List<User> list;
		if(user.getOffice().getId() == null){
			list = new ArrayList<>();
		}else{
			Role role = new Role();
			role.setId(Integer.valueOf(DictUtils.getDictValue("角色", "sys_user_role_adviser","")));
			user.setRole(role);
			list = systemService.selectUserByOfficeId(user);
		}
		return list;
	}

    @RequestMapping(value = "getAdvisers4mobile")
    @ResponseBody
    public String getAdvisers4mobile(User user, HttpServletRequest request, HttpServletResponse response, Model model){
        List<User> list;
        if(user.getOffice().getId() == null){
            list = new ArrayList<>();
        }else{
            Role role = new Role();
            role.setId(Integer.valueOf(DictUtils.getDictValue("角色", "sys_user_role_adviser","")));
            user.setRole(role);
            list = systemService.selectUserByOfficeId(user);
        }
        return JsonUtil.generateData(list, request.getParameter("callback"));
    }

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	public String save(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		//user.setCompany(new Office(Integer.valueOf(request.getParameter("company.id"))));
		Office company = new Office(Integer.valueOf(request.getParameter("company.id")));
		company.setSearchCompanyId(user.getCompany().getSearchCompanyId());
		company.setSearchCompanyName(user.getCompany().getSearchCompanyName());
		user.setCompany(company);

		//user.setOffice(new Office(Integer.valueOf(request.getParameter("office.id"))));
		Office department = new Office(Integer.valueOf(request.getParameter("office.id")));
		department.setSearchOfficeId(user.getOffice().getSearchOfficeId());
		department.setSearchOfficeName(user.getOffice().getSearchOfficeName());
		user.setOffice(department);

		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
		}
		if (!beanValidator(model, user)){
			return form(user, model,null);
		}
		if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))){
			addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			return form(user, model,null);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		List<Integer> roleIdList = user.getRoleIdList();
		String ua="office_user_save";
		if(user.getConn()!=null && user.getConn().equals(ua)){
			//用于用户列表不展示用户角色
			if(roleIdList.size()==0){
				String purchasersId = DictUtils.getDictValue("采购商", "sys_office_purchaserId", "");
				Office office = officeService.get(Integer.parseInt(purchasersId));
				Role roleByName = systemService.getRoleByName(String.valueOf(office.getName()));
				roleList.add(roleByName);
				user.setRoleList(roleList);
			}
		}else{
			for (Role r : systemService.findAllRole()){
				if (roleIdList.contains(r.getId())){
					roleList.add(r);
				}
			}
		}
		user.setRoleList(roleList);
		// 保存用户信息
		systemService.saveUser(user);
		// 清除当前用户缓存
		if (user.getLoginName().equals(UserUtils.getUser().getLoginName())){
			UserUtils.clearCache();
			//UserUtils.getCacheMap().clear();
		}else {
			UserUtils.clearCache(user);
		}

		addMessage(redirectAttributes, "保存用户'" + user.getLoginName() + "'成功");

		String Commissioner="connIndex";
		String officeUser="office_user_save";
		if(user.getConn()!=null && user.getConn().equals(Commissioner)){
//			添加 跳回客户专员管理
			return "redirect:" + adminPath + "/sys/user/list?company.type=8&company.customerTypeTen=10&company.customerTypeEleven=11&conn="+user.getConn();
		}
		if(user.getConn() != null && user.getConn().equals(WAREHOUSESPECIALIST)) {
		    //跳回仓储专员界面
            return "redirect:" + adminPath + "/sys/user/list?company.type=8&company.customerTypeTen=10&company.customerTypeEleven=11&conn="+user.getConn();
        }
		if(user.getConn() != null && user.getConn().equals("selectIndex")) {
			//跳回选品专员界面
			return "redirect:" + adminPath + "/sys/user/seleIndexList?conn="+user.getConn();
		}
		if(user.getConn()!=null && user.getConn().equals(officeUser)){
//			添加 跳回用户管理列表
			return "redirect:" + adminPath + "/sys/user/officeUserList?repage";
		}
		if(user.getConn()!=null && user.getConn().equals("contact_ck")){
			//跳回会员搜索
			return "redirect:" + adminPath + "/sys/user/contact";
		}

		redirectAttributes.addAttribute("company.searchCompanyId", user.getCompany().getSearchCompanyId());
		redirectAttributes.addAttribute("company.searchCompanyName", user.getCompany().getSearchCompanyName());
		redirectAttributes.addAttribute("office.searchOfficeId", user.getOffice().getSearchOfficeId());
		redirectAttributes.addAttribute("office.searchOfficeName", user.getOffice().getSearchOfficeName());
		redirectAttributes.addAttribute("searchName", user.getSearchName());
		redirectAttributes.addAttribute("searchMobile", user.getSearchMobile());
		redirectAttributes.addAttribute("searchLoginName", user.getSearchLoginName());
		redirectAttributes.addAttribute("searchScr", "saveUser");

		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "delete")
	public String delete(User user, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		if (UserUtils.getUser().getId().equals(user.getId())){
			addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
		}else if (User.isAdmin(user.getId())){
			addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
		}else{
			systemService.deleteUser(user);
			addMessage(redirectAttributes, "删除用户成功");
		}
		String Commissioner="connIndex";
		String officeUser="office_user_save";
		if(user.getConn() !=null && user.getConn().equals(Commissioner)){
//			跳回客户专员管理
			return "redirect:" + adminPath + "/sys/user/list?company.type="+user.getCompany().getType()+"&company.customerTypeTen="+user.getCompany().getCustomerTypeTen()
					+"&company.customerTypeEleven="+user.getCompany().getCustomerTypeEleven()+"&conn="+user.getConn();
		}
        if(user.getConn() != null && user.getConn().equals(WAREHOUSESPECIALIST)) {
            //跳回仓储专员界面
            return "redirect:" + adminPath + "/sys/user/list?company.type="+user.getCompany().getType()+"&company.customerTypeTen="+user.getCompany().getCustomerTypeTen()
                    +"&company.customerTypeEleven="+user.getCompany().getCustomerTypeEleven()+"&conn="+user.getConn();
        }
		if(user.getConn() != null && user.getConn().equals("selectIndex")) {
			//跳回选品专员界面
			return "redirect:" + adminPath + "/sys/user/seleIndexList?conn="+user.getConn();
		}
		if(user.getConn() !=null && user.getConn().equals(officeUser)){
//			跳回用户管理
			return "redirect:" + adminPath + "/sys/user/officeUserList?repage";
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "recovery")
	public String recovery(User user, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		systemService.recovery(user);
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}
	/**
	 * 导出用户数据
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "export", method=RequestMethod.POST)
	public String exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
			Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
			new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 导入用户数据
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "import", method=RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list){
				try{
					if ("true".equals(checkLoginName("", user.getLoginName()))){
						user.setPassword(SystemService.entryptPassword("123456"));
						BeanValidators.validateWithException(validator, user);
						systemService.saveUser(user);
						successNum++;
					}else{
						failureMsg.append("<br/>登录名 "+user.getLoginName()+" 已存在; ");
						failureNum++;
					}
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 下载导入用户数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据导入模板.xlsx";
			List<User> list = Lists.newArrayList(); list.add(UserUtils.getUser());
			new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 用户信息显示及保存
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(User user, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());
			currentUser.setMobile(user.getMobile());
			currentUser.setRemarks(user.getRemarks());
			currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			model.addAttribute("message", "保存用户信息成功");
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfo";
	}

	/**
	 * 返回用户信息
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public User infoData() {
		return UserUtils.getUser();
	}

	/**
	 * 修改个人用户密码
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userModifyPwd";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			}else{
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) Integer officeId, @RequestParam(required=false) Integer type,HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(officeId,type);
		for (int i=0; i<list.size(); i++){
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}

//	@InitBinder
//	public void initBinder(WebDataBinder b) {
//		b.registerCustomEditor(List.class, "roleList", new PropertyEditorSupport(){
//			@Autowired
//			private SystemService systemService;
//			@Override
//			public void setAsText(String text) throws IllegalArgumentException {
//				String[] ids = StringUtils.split(text, ",");
//				List<Role> roles = new ArrayList<Role>();
//				for (String id : ids) {
//					Role role = systemService.getRole(Long.valueOf(id));
//					roles.add(role);
//				}
//				setValue(roles);
//			}
//			@Override
//			public String getAsText() {
//				return Collections3.extractToString((List) getValue(), "id", ",");
//			}
//		});
//	}

	/**
	 * 客户专员管理
	 * */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"conIndex"})
	public String conIndex(User user, Model model) {
		return "modules/sys/conIndex";
	}

	/**
	 * 仓储专员管理
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"stoIndex"})
	public String stoIndex(User user, Model model) {
		return "modules/sys/stoIndex";
	}

	/**
	 * 选品专员管理
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"seleIndexList"})
	public String seleIndex(User user, Model model,HttpServletRequest request, HttpServletResponse response,Date ordrHeaderStartTime,Date orderHeaderEedTime) {
		Page<User> page = systemService.findUserSele(new Page<User>(request, response), user);
		User userAdmin = UserUtils.getUser();
		User ordUser = new User();
		if (ordrHeaderStartTime != null) {
			ordUser.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
		}
		if (orderHeaderEedTime != null) {
			ordUser.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
		}
		if (!userAdmin.isAdmin()) {
			ordUser.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
		}
		for (User user1 : page.getList()) {
			ordUser.setId(user1.getId());
			BizOrderHeader orderHeades = bizOrderHeaderDao.categorySkuStatistics(ordUser);
			if (orderHeades != null) {
				user1.setUserOrder(orderHeades);
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("ordrHeaderStartTime", ordrHeaderStartTime);
		model.addAttribute("orderHeaderEedTime", orderHeaderEedTime);
		return "modules/sys/userSeleIndexList";
	}

	/**
	 * 会员搜索
	 * flag = ck 查看
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "contact")
	public String contact(User user, HttpServletRequest request, HttpServletResponse response, Model model,Date ordrHeaderStartTime,Date orderHeaderEedTime){
		Office company = new Office();
		if (user.getCompany() != null && user.getCompany().getName() != null && !"".equals(user.getCompany().getName())) {
			company.setName(user.getCompany().getName());
		}
		company.setType(null);
		company.setTypes(Lists.newArrayList(OfficeTypeEnum.CUSTOMER.getType(), OfficeTypeEnum.SHOPKEEPER.getType(), OfficeTypeEnum.COMMISSION_MERCHANT.getType()));
		user.setCompany(company);
		if (ordrHeaderStartTime != null) {
			user.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
		}
		if (orderHeaderEedTime != null) {
			user.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
		}
		Page<User> page = systemService.contact(new Page<User>(request, response), user);
		User userAdmin = UserUtils.getUser();
		BizChatRecord bizChatRecord = new BizChatRecord();
		if (ordrHeaderStartTime != null) {
			bizChatRecord.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
		}
		if (orderHeaderEedTime != null) {
			bizChatRecord.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
		}
		if (!userAdmin.isAdmin()) {
			bizChatRecord.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
		}
		for (User user1 : page.getList()) {
			bizChatRecord.setOffice(user1.getCompany());
			List<BizOrderHeader> userOrderCountSecond = bizOrderHeaderDao.findUserOrderCountSecond(bizChatRecord);
			for (BizOrderHeader bizOrderHeader : userOrderCountSecond) {
				user1.setUserOrder(bizOrderHeader);
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("flag", "ck");
		model.addAttribute("ordrHeaderStartTime", ordrHeaderStartTime);
		model.addAttribute("orderHeaderEedTime", orderHeaderEedTime);
		return "modules/sys/office/sysOfficeContact";
	}

	/**
	 * 运营计划
	 * flag = ck 查看
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "bizOpPlanUser")
	public List<User> bizOpPlanUser(@RequestParam(required = false) Integer officeId, @RequestParam(required = false) Integer type, HttpServletResponse response) {
		List<User> list = null;
		if (officeId != null) {
			list = systemService.findUserByOfficeId(officeId, type);
		}
		return list;
	}

	/**
	 * 用户管理模块
	 * */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"officeUserIndex"})
	public String officeUserIndex(User user, Model model) {
		return "modules/sys/officeUserIndex";
	}

	/**
	 * 用户列表查询及点击左侧查询
	 * */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "officeUserList")
	public String officeUserList(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		String purchasersId = DictUtils.getDictValue("采购商", "sys_office_purchaserId", "");
		Office customer = new Office();
		customer.setType(String.valueOf(OfficeTypeEnum.CUSTOMER.getType()));
		customer.setParentIds("%," + purchasersId + ",%");
		user.setCompany(customer);
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		model.addAttribute("page", page);
		return "modules/sys/OfficeUserList";
	}

	/**
	 * 查找供应商负责人的登陆名是否存在
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:view")
    @RequestMapping(value = "findVendorUser")
	public String findVendorUser(String loginName){
	    String flag = "true";
        User user = systemService.getUserByLoginName(loginName);
        if (user != null){
            flag = "false";
        }
        return flag;
    }


    @ResponseBody
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "selectConsultant")
    public String selectConsultant(String userId,String companyId,String userRoleIds) {
		String sflag = "true";
		boolean flag = false;
		boolean bflag = true;
		if (userId != null && !userId.equals("")) {
            User user = systemService.getUser(Integer.parseInt(userId));
            String[] roleIds = userRoleIds.split(",".trim());
            for (String id:roleIds) {
                Integer roleId = Integer.parseInt(id);
                Role role = systemService.getRole(roleId);
                if (role.getEnname().equals(RoleEnNameEnum.BUYER.getState())) {
                    bflag = false;
                }
            }
            if (user.getCompany().getId()!=Integer.parseInt(companyId)) {
                flag = true;
            }
            if (flag || bflag) {
                BizCustomCenterConsultant ccc = new BizCustomCenterConsultant();
                User consultant = new User();
                consultant.setId(user.getId());
                ccc.setConsultants(consultant);
                List<BizCustomCenterConsultant> cccList = bizCustomCenterConsultantService.findList(ccc);
                if (!cccList.isEmpty() && cccList.size() > 0) {
                    sflag = "false";
                }
            }
        }
		return sflag;
	}

	/**
	 * 沟通记录 查询 品类主管或者客户专员
	 * */
	@RequiresPermissions("sys:user:view")
	@ResponseBody
	@RequestMapping(value = "userSelectTreeData")
	public List<User> userSelectTreeData(String names) {
		Role role = new Role();
		if(names!=null){
			role.setName(names);
		}
		User user = new User();
		user.setRole(role);
		List<User> list =null;
		if(names!=null){
			list = systemService.userSelectCompany(user);
		}
		return list;
	}

	/**
     * 品类主管 管理
     * */
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "userSeleForm")
    public String userSeleForm(User user, Model model,String flag) {
		if (user.getOffice() == null || user.getOffice().getId() == null) {
			String purchasersId = DictUtils.getDictValue("采销部", "office_suppl", "");
			Office office = officeService.get(Integer.parseInt(purchasersId));
			user.setCompany(office);
			user.setOffice(office);
		} else {
			Office off = new Office();
			off.setParentIds("%" + user.getOffice().getId() + ",");
			List<Office> list = officeService.findList(off);
			Office office = officeService.get(user.getOffice().getId());
			if (list == null || list.isEmpty()) {
				user.setCompany(office);
				user.setOffice(office);
			} else {
				user.setCompany(user.getCompany());
				user.setOffice(user.getOffice());
			}
		}
		if (flag != null && StringUtils.isNotEmpty(flag)) {
			model.addAttribute("flag", flag);
		}
		if(user.getId()!=null){
			BizVarietyUserInfo bizVarietyUserInfo = new BizVarietyUserInfo();
			bizVarietyUserInfo.setUser(user);
			//页面已勾选的多选框 品类
			String supplierExpressids = "";
			List<BizVarietyUserInfo> list = bizVarietyUserInfoService.findList(bizVarietyUserInfo);
			if (CollectionUtils.isNotEmpty(list)) {
				for (BizVarietyUserInfo varietyUserInfo : list) {
					supplierExpressids += varietyUserInfo.getVarietyInfo().getId() + ",";
				}
			}
			model.addAttribute("supplierExpressids", supplierExpressids);
		}
		model.addAttribute("user", user);
		model.addAttribute("allRoles", systemService.findAllRole());
		List<BizVarietyInfo> varietyFactorList = bizVarietyInfoService.findList(new BizVarietyInfo());
		model.addAttribute("varietyList", varietyFactorList);
		return "modules/sys/userSeleForm";
    }

    /**
	 * 品类主管 保存
	 * */
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "userInfosave")
	public String userInfosave(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		user.setCompany(new Office(Integer.valueOf(request.getParameter("company.id"))));
		user.setOffice(new Office(Integer.valueOf(request.getParameter("office.id"))));
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
		}
		if (!beanValidator(model, user)) {
			return form(user, model, null);
		}
		if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
			addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			return form(user, model, null);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		List<Integer> roleIdList = user.getRoleIdList();
		for (Role r : systemService.findAllRole()) {
			if (roleIdList.contains(r.getId())) {
				roleList.add(r);
			}
		}
		user.setRoleList(roleList);
		// 保存用户信息
		systemService.saveUser(user);
		if (CollectionUtils.isNotEmpty(user.getVarityList())) {
			BizVarietyUserInfo bizVarietyUserInfo = new BizVarietyUserInfo();
			bizVarietyUserInfo.setUser(user);
			List<BizVarietyUserInfo> list = bizVarietyUserInfoService.findList(bizVarietyUserInfo);
			if (CollectionUtils.isNotEmpty(list)) {
				for (BizVarietyUserInfo varietyUserInfo : list) {
					bizVarietyUserInfoService.delete(varietyUserInfo);
				}
			}
			List<BizVarietyInfo> varInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());
			List<Integer> varIdList = user.getVarIdList();
			for (BizVarietyInfo r : varInfoList) {
				if (varIdList.contains(r.getId())) {
					bizVarietyUserInfo.setVarietyInfo(r);
					bizVarietyUserInfoService.save(bizVarietyUserInfo);
				}
			}
		}
		// 清除当前用户缓存
		if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
			UserUtils.clearCache();
		} else {
			UserUtils.clearCache(user);
		}
		addMessage(redirectAttributes, "保存品类主管'" + user.getLoginName() + "'成功");
		return "redirect:" + adminPath + "/sys/user/seleIndexList?conn=" + user.getConn();
	}


}
