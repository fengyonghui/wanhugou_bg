/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.custom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.thread.ThreadPoolManager;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.BizCustomCenterConsultantDto;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsPlatformService;
import com.wanhutong.backend.modules.biz.web.statistics.BizStatisticsPlatformController;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.BuyerAdviser;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
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

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客户专员管理Controller 采购商客户专营关联
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/custom/bizCustomCenterConsultant")
public class BizCustomCenterConsultantController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(BizStatisticsPlatformController.class);
    /**
     * 默认超时时间
     */
    private static final Long DEFAULT_TIME_OUT = TimeUnit.SECONDS.toMillis(60);

    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;

    @Autowired
    private OfficeService officeService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizOrderHeaderDao bizOrderHeaderDao;

    private Lock lock = new ReentrantLock();

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = "form")
    public String form(BizCustomCenterConsultant bizCustomCenterConsultant, Model model) {
        model.addAttribute("entity", bizCustomCenterConsultant);
        return "modules/biz/common/commonImgForm";
    }

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model,Date ordrHeaderStartTime,Date orderHeaderEedTime) {
        BizCustomCenterConsultant centersCust = new BizCustomCenterConsultant();
        User userBcc = new User();
        User user = systemService.getUser(bizCustomCenterConsultant.getConsultants().getId());
        Office office = officeService.get(user.getOffice());
        if (bizCustomCenterConsultant.getConsultants() != null) {
            if (bizCustomCenterConsultant.getQueryCustomes() != null && "query_Custome".equals(bizCustomCenterConsultant.getQueryCustomes())) {
                if (bizCustomCenterConsultant.getCustoms() != null && bizCustomCenterConsultant.getCustoms().getId() != null) {
                    centersCust.setCustoms(bizCustomCenterConsultant.getCustoms());//采购商
                }
                if (bizCustomCenterConsultant.getConsultants() != null && StringUtils.isNotEmpty(bizCustomCenterConsultant.getConsultants().getMobile())) {
                    userBcc.setMobile(bizCustomCenterConsultant.getConsultants().getMobile());
                    centersCust.setConsultants(userBcc);//电话查询
                }
            }
            centersCust.setCenters(office);//采购中心
            userBcc.setId(user.getId());
            centersCust.setConsultants(userBcc);//客户专员
            model.addAttribute("bcUser", centersCust);
            if (ordrHeaderStartTime != null) {
                centersCust.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
            }
            if (orderHeaderEedTime != null) {
                centersCust.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
            }
            List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.userFindList(centersCust);

            List<Callable<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>> tasks = new ArrayList<>();
            Map<BizCustomCenterConsultant, BizOrderHeader> resultMap = Maps.newLinkedHashMap();
            BizChatRecord bizChatRecord = new BizChatRecord();
            User userAdmin = UserUtils.getUser();
            if (ordrHeaderStartTime != null) {
                bizChatRecord.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
            }
            if (orderHeaderEedTime != null) {
                bizChatRecord.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
            }
            if (!userAdmin.isAdmin()) {
                bizChatRecord.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
            }
            if (list.size() != 0) {
                for (BizCustomCenterConsultant customCenterConsultant : list) {
                    tasks.add(new Callable<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>() {
                        @Override
                        public Pair<BizCustomCenterConsultant, List<BizOrderHeader>> call() throws Exception {
                            List<BizOrderHeader> orderHeaderList = null;
                            try {
                                lock.lock();
                                bizChatRecord.setOffice(customCenterConsultant.getCustoms());
                                orderHeaderList = bizOrderHeaderDao.findUserOrderCountSecond(bizChatRecord);
                            } catch (Exception e) {
                                logger.error("多线程取订单列表失败", e);
                            } finally {
                                lock.unlock();
                            }
                            return Pair.of(customCenterConsultant, orderHeaderList);
                        }
                    });
                }
                bizCustomCenterConsultant.setBccList(list);
            }
            List<Future<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>> futures = null;
            try {
                futures = ThreadPoolManager.getDefaultThreadPool().invokeAll(tasks);
            } catch (InterruptedException e) {
                LOGGER.error("get user error (tasks)", e);
            }
            if (futures != null) {
                for (Future<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>> future : futures) {
                    try {
                        Pair<BizCustomCenterConsultant, List<BizOrderHeader>> data = future.get(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
                        List<BizOrderHeader> countList = data.getRight();
                        BizOrderHeader orHeaders = new BizOrderHeader();
                        int totalCount = 0;
                        for (BizOrderHeader b : countList) {
                            totalCount += b.getOrderCount();
                            orHeaders.setUserOfficeReceiveTotal(b.getUserOfficeReceiveTotal());
                            orHeaders.setUserOfficeDeta(b.getUserOfficeDeta());
                            orHeaders.setBizLocation(b.getBizLocation());
                        }
                        orHeaders.setOrderCount(totalCount);
                        resultMap.put(data.getLeft(), orHeaders == null ? new BizOrderHeader() : orHeaders);
                    } catch (Exception e) {
                        LOGGER.error("多线程取订单频率异常[{}]", bizCustomCenterConsultant.getCustoms().getId(), e);
                    }
                }
            }
            model.addAttribute("resultMap", resultMap);
        }
        model.addAttribute("ordrHeaderStartTime", ordrHeaderStartTime);
        model.addAttribute("orderHeaderEedTime", orderHeaderEedTime);
        return "modules/biz/custom/bizCustomCenterConsultantList";
    }

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = {"listData4mobile"})
    @ResponseBody
    public String listData4mobile(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model,Date ordrHeaderStartTime,Date orderHeaderEedTime) {
        BizCustomCenterConsultant centersCust = new BizCustomCenterConsultant();
        User userBcc = new User();
        User user = systemService.getUser(bizCustomCenterConsultant.getConsultants().getId());
        Office office = officeService.get(user.getOffice());
        Map<String, Object> jsonResuleMap = Maps.newHashMap();
        List<BizCustomCenterConsultantDto> resultData = Lists.newArrayList();
        if (bizCustomCenterConsultant.getConsultants() != null) {
            if (bizCustomCenterConsultant.getQueryCustomes() != null && "query_Custome".equals(bizCustomCenterConsultant.getQueryCustomes())) {
                if (bizCustomCenterConsultant.getCustoms() != null && bizCustomCenterConsultant.getCustoms().getId() != null) {
                    centersCust.setCustoms(bizCustomCenterConsultant.getCustoms());//采购商
                }
                if (bizCustomCenterConsultant.getConsultants() != null && StringUtils.isNotEmpty(bizCustomCenterConsultant.getConsultants().getMobile())) {
                    userBcc.setMobile(bizCustomCenterConsultant.getConsultants().getMobile());
                    centersCust.setConsultants(userBcc);//电话查询
                }
            }
            centersCust.setCenters(office);//采购中心
            userBcc.setId(user.getId());
            centersCust.setConsultants(userBcc);//客户专员
            model.addAttribute("bcUser", centersCust);
            if (ordrHeaderStartTime != null) {
                centersCust.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
            }
            if (orderHeaderEedTime != null) {
                centersCust.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
            }
            List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.userFindList(centersCust);

            List<Callable<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>> tasks = new ArrayList<>();
            Map<BizCustomCenterConsultant, BizOrderHeader> resultMap = Maps.newLinkedHashMap();

            BizChatRecord bizChatRecord = new BizChatRecord();
            User userAdmin = UserUtils.getUser();
            if (ordrHeaderStartTime != null) {
                bizChatRecord.setOrdrHeaderStartTime(DateUtils.formatDate(ordrHeaderStartTime, "yyyy-MM-dd"));
            }
            if (orderHeaderEedTime != null) {
                bizChatRecord.setOrderHeaderEedTime(DateUtils.formatDate(orderHeaderEedTime, "yyyy-MM-dd") + " 23:59:59");
            }
            if (!userAdmin.isAdmin()) {
                bizChatRecord.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
            }
            if (list.size() != 0) {
                for (BizCustomCenterConsultant customCenterConsultant : list) {
                    tasks.add(new Callable<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>() {
                        @Override
                        public Pair<BizCustomCenterConsultant, List<BizOrderHeader>> call() throws Exception {
                            List<BizOrderHeader> orderHeaderList = null;
                            try {
                                lock.lock();
                                bizChatRecord.setOffice(customCenterConsultant.getCustoms());
                                orderHeaderList = bizOrderHeaderDao.findUserOrderCountSecond(bizChatRecord);
                            } catch (Exception e) {
                                logger.error("多线程取订单列表失败", e);
                            } finally {
                                lock.unlock();
                            }
                            return Pair.of(customCenterConsultant, orderHeaderList);
                        }
                    });
                }
                bizCustomCenterConsultant.setBccList(list);
            }
            List<Future<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>> futures = null;
            try {
                futures = ThreadPoolManager.getDefaultThreadPool().invokeAll(tasks);
            } catch (InterruptedException e) {
                LOGGER.error("get user error (tasks)", e);
            }
            if (futures != null) {
                for (Future<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>> future : futures) {
                    try {
                        Pair<BizCustomCenterConsultant, List<BizOrderHeader>> data = future.get(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
                        List<BizOrderHeader> countList = data.getRight();
                        BizOrderHeader orHeaders = new BizOrderHeader();
                        int totalCount = 0;
                        for (BizOrderHeader b : countList) {
                            totalCount += b.getOrderCount();
                            orHeaders.setUserOfficeReceiveTotal(b.getUserOfficeReceiveTotal());
                            orHeaders.setUserOfficeDeta(b.getUserOfficeDeta());
                            orHeaders.setBizLocation(b.getBizLocation());
                        }
                        orHeaders.setOrderCount(totalCount);
                        resultMap.put(data.getLeft(), orHeaders == null ? new BizOrderHeader() : orHeaders);
                        if (orHeaders == null) {
                            orHeaders = new BizOrderHeader();
                        }
                        BizCustomCenterConsultant consultant = data.getLeft() != null ? data.getLeft() : new BizCustomCenterConsultant();
                        BizCustomCenterConsultantDto dto = new BizCustomCenterConsultantDto();


                        Office customs = consultant.getCustoms();
                        dto.setCustomsId(customs != null ? customs.getId() : null);
                        dto.setCustomsName(customs != null ? customs.getName() : null);
                        if (customs != null) {
                            dto.setCustomsPrimaryPersonName(customs.getPrimaryPerson() != null ? customs.getPrimaryPerson().getName() : null);
                        } else {
                            dto.setCustomsPrimaryPersonName(null);
                        }

                        dto.setCentersName(consultant.getCenters() != null ? consultant.getCenters().getName() : null);
                        dto.setConsultantsId(consultant.getConsultants() != null ? consultant.getConsultants().getId() : null);
                        dto.setConsultantsName(consultant.getConsultants() != null ? consultant.getConsultants().getName() : null);
                        dto.setConsultantsMobile(consultant.getConsultants() !=null ? consultant.getConsultants().getMobile() : null);
                        dto.setUserOfficeDeta(orHeaders.getUserOfficeDeta());
                        dto.setUserOfficeReceiveTotal(orHeaders.getUserOfficeReceiveTotal());
                        BizOrderAddress location = orHeaders.getBizLocation();
                        if (location != null) {
                            dto.setProvinceName(location.getProvince() != null ? location.getProvince().getName() : null);
                            dto.setCityName(location.getCity() != null ? location.getCity().getName() : null);
                            dto.setRegionName(location.getRegion() != null ? location.getRegion().getName() : null);
                            dto.setAddress(location.getAddress());
                        } else {
                            dto.setProvinceName(null);
                            dto.setCityName(null);
                            dto.setRegionName(null);
                            dto.setAddress(null);
                        }
                        dto.setOrderCount(orHeaders.getOrderCount());

                        resultData.add(dto);
                    } catch (Exception e) {
                        LOGGER.error("多线程取订单频率异常[{}]", bizCustomCenterConsultant.getCustoms().getId(), e);
                    }
                }
            }
            model.addAttribute("resultMap", resultMap);
        }
        jsonResuleMap.put("resultData", resultData);
        //model.addAttribute("ordrHeaderStartTime", ordrHeaderStartTime);
        //model.addAttribute("orderHeaderEedTime", orderHeaderEedTime);
        jsonResuleMap.put("ordrHeaderStartTime", ordrHeaderStartTime);
        jsonResuleMap.put("orderHeaderEedTime", orderHeaderEedTime);

        return JsonUtil.generateData(jsonResuleMap, request.getParameter("callback"));
    }


    /**
     * 关联经销店
     * */
    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = "connOfficeForm")
    public String connOfficeForm(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Office off = new Office();
        String center = DictUtils.getDictValue("采购中心", "sys_office_type", "");
        off.setType(center);
        off.setCustomerTypeTen("10");
        off.setCustomerTypeEleven("11");
        List<Office> officeList = officeService.queryCenterList(off);
        for (int i = 0; i < officeList.size(); i++) {
            if (officeList.get(i).getId().equals(user.getOffice().getId())) {//关联时，判断采购中心
                officeList.add(officeList.get(i));//把采购中心放到集合最后一位，保证能默认显示采购中心
                officeList.remove(i);//删除之前采购中心
                break;
            }
        }
        user = systemService.getUser(user.getId());
        BizCustomCenterConsultant bc = new BizCustomCenterConsultant();
        bc.setCenters(user.getOffice());//采购中心
        bc.setConsultants(user);
        List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(bc);
        if (CollectionUtils.isNotEmpty(list)) {
            bc.setConsultants(systemService.getUser(user.getId()));
            bc.setCenters(officeService.get(user.getOffice()));
        }
        model.addAttribute("office", user);
        model.addAttribute("bcc", bc);
        model.addAttribute("officeList", officeList);
        return "modules/biz/custom/bizCustomMembershipVolumeDATE";
    }

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = "connOfficeForm4mobile")
    @ResponseBody
    public String connOfficeForm4mobile(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        Office off = new Office();
        String center = DictUtils.getDictValue("采购中心", "sys_office_type", "");
        off.setType(center);
        off.setCustomerTypeTen("10");
        off.setCustomerTypeEleven("11");
        List<Office> officeList = officeService.queryCenterList(off);
        for (int i = 0; i < officeList.size(); i++) {
            if (officeList.get(i).getId().equals(user.getOffice().getId())) {//关联时，判断采购中心
                officeList.add(officeList.get(i));//把采购中心放到集合最后一位，保证能默认显示采购中心
                officeList.remove(i);//删除之前采购中心
                break;
            }
        }
        user = systemService.getUser(user.getId());
        BizCustomCenterConsultant bc = new BizCustomCenterConsultant();
        bc.setCenters(user.getOffice());//采购中心
        bc.setConsultants(user);
        List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(bc);
        if (CollectionUtils.isNotEmpty(list)) {
            bc.setConsultants(systemService.getUser(user.getId()));
            bc.setCenters(officeService.get(user.getOffice()));
        }
        model.addAttribute("office", user);
        model.addAttribute("bcc", bc);
        model.addAttribute("officeList", officeList);

        resultMap.put("office", user);
        resultMap.put("bcc", bc);
        resultMap.put("officeList", officeList);

        //return "modules/biz/custom/bizCustomMembershipVolumeDATE";
        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    //    保存状态给 officeController
    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:edit")
    @RequestMapping(value = "save")
    @ResponseBody
    public String save(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model) {
        if(bizCustomCenterConsultant == null || bizCustomCenterConsultant.getCustoms() == null || bizCustomCenterConsultant.getConsultants() == null){
            return "0";
        }
//		BizCustomCenterConsultant BCC = bizCustomCenterConsultantService.get(bizCustomCenterConsultant.getCustoms().getId());
//		if(BCC!=null && BCC.getDelFlag().equals(1)){
//            bizCustomCenterConsultant.setIsNewRecord(true);//新记录 insert
//		}else{
//            bizCustomCenterConsultant.setIsNewRecord(false);//不是新记录,update
//        }
        bizCustomCenterConsultantService.save(bizCustomCenterConsultant);
        return "1";
    }

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:edit")
    @RequestMapping(value = "delete")
    public String delete(BizCustomCenterConsultant bizCustomCenterConsultant, RedirectAttributes redirectAttributes) {
        bizCustomCenterConsultantService.delete(bizCustomCenterConsultant);
        addMessage(redirectAttributes, "删除客户专员成功");
        return "redirect:"+Global.getAdminPath()+"/biz/custom/bizCustomCenterConsultant/list?consultants.id="+bizCustomCenterConsultant.getConsultants().getId();
    }

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:edit")
    @RequestMapping(value = "delete4mobile")
    @ResponseBody
    public String delete4mobile(BizCustomCenterConsultant bizCustomCenterConsultant, RedirectAttributes redirectAttributes) {
        String message = "error";
        try {
            bizCustomCenterConsultantService.delete(bizCustomCenterConsultant);
            addMessage(redirectAttributes, "删除客户专员成功");
            message = "ok";
        } catch (Exception e) {
            logger.error("删除客户专员失败", e);
        }
        return JsonUtil.generateData(message, null);
    }

}