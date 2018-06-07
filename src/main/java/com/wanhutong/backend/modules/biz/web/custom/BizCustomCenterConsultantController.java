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
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = "form")
    public String form(BizCustomCenterConsultant bizCustomCenterConsultant, Model model) {
        model.addAttribute("entity", bizCustomCenterConsultant);
        return "modules/biz/common/commonImgForm";
    }

    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model) {
        BizCustomCenterConsultant BCC = new BizCustomCenterConsultant();
        User userBcc = new User();
        User user = systemService.getUser(bizCustomCenterConsultant.getConsultants().getId());
        Office office = officeService.get(user.getOffice());
        if(bizCustomCenterConsultant.getConsultants()!=null){
            if(bizCustomCenterConsultant.getQueryCustomes()!=null && bizCustomCenterConsultant.getQueryCustomes().equals("query_Custome")){
                if(bizCustomCenterConsultant.getCustoms()!=null && bizCustomCenterConsultant.getCustoms().getId()!=null){
                    BCC.setCustoms(bizCustomCenterConsultant.getCustoms());//采购商
                }
                if(bizCustomCenterConsultant.getConsultants()!=null && !bizCustomCenterConsultant.getConsultants().getMobile().equals("")){
                    userBcc.setMobile(bizCustomCenterConsultant.getConsultants().getMobile());
                    BCC.setConsultants(userBcc);//电话查询
                }
            }
            BCC.setCenters(office);//采购中心
            userBcc.setId(user.getId());
            BCC.setConsultants(userBcc);//客户专员
            model.addAttribute("bcUser", BCC);
            List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.userFindList(BCC);
            List<Callable<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>> tasks =new ArrayList<>();
            Map<BizCustomCenterConsultant, BizOrderHeader> resultMap = Maps.newLinkedHashMap();
            BizChatRecord bizChatRecord = new BizChatRecord();
            User userAdmin = UserUtils.getUser();
            if(bizCustomCenterConsultant.getOrdrHeaderStartTime()!=null){
                bizChatRecord.setOrdrHeaderStartTime(bizCustomCenterConsultant.getOrdrHeaderStartTime());
            }
            if(bizCustomCenterConsultant.getOrderHeaderEedTime()!=null){
                bizChatRecord.setOrderHeaderEedTime(bizCustomCenterConsultant.getOrderHeaderEedTime());
            }
            if(userAdmin.isAdmin()){
            }else{
                bizChatRecord.getSqlMap().put("chat", BaseService.dataScopeFilter(userAdmin, "so", "su"));
            }
            if(list.size()!=0){
                for (BizCustomCenterConsultant customCenterConsultant : list) {
                    tasks.add(new Callable<Pair<BizCustomCenterConsultant, List<BizOrderHeader>>>() {
                        @Override
                        public Pair<BizCustomCenterConsultant, List<BizOrderHeader>> call() throws Exception {
                            bizChatRecord.setOffice(customCenterConsultant.getCustoms());
                            return Pair.of(customCenterConsultant, bizOrderHeaderDao.findUserOrderCountSecond(bizChatRecord));
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
                        resultMap.put(data.getLeft(), orHeaders ==null?new BizOrderHeader():orHeaders);
                    } catch (Exception e) {
                        LOGGER.error("多线程取订单频率异常[{}]",bizCustomCenterConsultant.getCustoms().getId(), e);
                    }
                }
            }
            model.addAttribute("resultMap", resultMap);
        }
        return "modules/biz/custom/bizCustomCenterConsultantList";
    }

//    @RequiresPermissions("sys:office:view")
//    @RequestMapping(value = "returnConnIndex")
//    public String returnConnIndex(BizCustomCenterConsultant bizCustomCenterConsultant,HttpServletRequest request, HttpServletResponse response, Model model){
////        Page<BizCustomCenterConsultant> page = bizCustomCenterConsultantService.findPage(new Page<BizCustomCenterConsultant>(request, response), bizCustomCenterConsultant);
//        if(bizCustomCenterConsultant.getCenters()==null || bizCustomCenterConsultant.getConsultants()==null){
//            model.addAttribute("entity", bizCustomCenterConsultant);
//        }else {
//            BizCustomCenterConsultant BCC = new BizCustomCenterConsultant();
//            Office Centers = officeService.get(bizCustomCenterConsultant.getCenters());
//            User Consultants = systemService.getUser(bizCustomCenterConsultant.getConsultants().getId());
//            BCC.setCenters(Centers);//采购中心
//            BCC.setConsultants(Consultants);//客户专员
//            List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(BCC);
//            bizCustomCenterConsultant.setBccList(list);
//            model.addAttribute("entity", bizCustomCenterConsultant);
//        }
//        return "modules/biz/custom/bizCustomCenterConsultantList";
//    }

    //    关联采购商
    @RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
    @RequestMapping(value = "connOfficeForm")
    public String connOfficeForm(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Office off = new Office();
//        Office parentOff = new Office();
//        String socID = DictUtils.getDictValue("部门", "sys_office_centerId","");
        String center = DictUtils.getDictValue("采购中心", "sys_office_type","");
//        parentOff.setId(Integer.parseInt(socID));
//        off.setParent(parentOff);
        off.setType(center);
        off.setCustomerTypeTen("10");
        off.setCustomerTypeEleven("11");
        List<Office> officeList = officeService.queryCenterList(off);
        for (int i = 0; i < officeList.size(); i++) {
            if(officeList.get(i).getId().equals(user.getOffice().getId()) ){//关联时，判断采购中心
                officeList.add(officeList.get(i));//把采购中心放到集合最后一位，保证能默认显示采购中心
                officeList.remove(i);//删除之前采购中心
                break;
            }
        }
        user = systemService.getUser(user.getId());
//      Integer aaa=user.getOffice().getId();
        BizCustomCenterConsultant bc = new BizCustomCenterConsultant();
        bc.setCenters(user.getOffice());//采购中心
        bc.setConsultants(user);
        List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(bc);
        if(list.size() != 0){
            bc.setConsultants(systemService.getUser(user.getId()));
            bc.setCenters(officeService.get(user.getOffice()));
        }
        model.addAttribute("office", user);
        model.addAttribute("bcc", bc);
        model.addAttribute("officeList", officeList);
        return "modules/biz/custom/bizCustomMembershipVolumeDATE";
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

}