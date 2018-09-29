/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.order.BizDrawBackDao;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.order.*;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.DoOrderHeaderProcessFifthConfig;
import com.wanhutong.backend.modules.config.parse.JointOperationOrderProcessLocalConfig;
import com.wanhutong.backend.modules.config.parse.JointOperationOrderProcessOriginConfig;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.Process;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderPayProportionStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.config.parse.JointOperationOrderProcessLocalConfig;
import com.wanhutong.backend.modules.config.parse.JointOperationOrderProcessOriginConfig;
import com.wanhutong.backend.modules.config.parse.Process;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Service
 *
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {

    /**
     * 默认表名
     */
    public static final String DATABASE_TABLE_NAME = "biz_order_header";
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    @Lazy
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private OfficeService officeService;
    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private BizOrderCommentService bizOrderCommentService;
    @Autowired
    private BizDrawBackDao bizDrawBackDao;
    @Resource
    private CommonProcessService commonProcessService;
    @Resource
    private CommonImgService commonImgService;
    @Resource
    private SystemService systemService;

    public static final String ORDER_TABLE = "biz_order_header";

    protected Logger log = LoggerFactory.getLogger(getClass());//日志

    public List<BizOrderHeader> findListFirstOrder(BizOrderHeader bizOrderHeader) {
        //查询状态 status=0 和 1的所有信息
        return bizOrderHeaderDao.findListFirstOrder(bizOrderHeader);
    }

    @Override
    public BizOrderHeader get(Integer id) {
        return super.get(id);
    }

    @Override
    public List<BizOrderHeader> findList(BizOrderHeader bizOrderHeader) {
        JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();
        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        DoOrderHeaderProcessFifthConfig doOrderHeaderProcessFifthConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

        String selectAuditStatus = bizOrderHeader.getSelectAuditStatus();
        if (StringUtils.isNotBlank(selectAuditStatus)) {
            List<String> originConfigValue = Lists.newArrayList();
            List<String> localConfigValue = Lists.newArrayList();
            List<String> doFifthConfigValue = Lists.newArrayList();

            //////////////////////////////////////////////////////////////////
            for (Process process : originConfig.getProcessList()) {
                if (process.getName().contains(selectAuditStatus)) {
                    originConfigValue.add(String.valueOf(process.getCode()));
                }
            }
//////////////////////////////////////////////////////////////////
            for (Process process : localConfig.getProcessList()) {
                if (process.getName().contains(selectAuditStatus)) {
                    localConfigValue.add(String.valueOf(process.getCode()));
                }
            }
//////////////////////////////////////////////////////////////////
            for (DoOrderHeaderProcessFifthConfig.OrderHeaderProcess process : doOrderHeaderProcessFifthConfig.getProcessList()) {
                if (process.getName().contains(selectAuditStatus)) {
                    doFifthConfigValue.add(String.valueOf(process.getCode()));
                }
            }

            bizOrderHeader.setOriginCode(CollectionUtils.isEmpty(originConfigValue) ? null : originConfigValue);
            bizOrderHeader.setLocalCode(CollectionUtils.isEmpty(localConfigValue) ? null : localConfigValue);
            bizOrderHeader.setDoFifthCode(CollectionUtils.isEmpty(doFifthConfigValue) ? null : doFifthConfigValue);
        }
        User user = UserUtils.getUser();
        boolean oflag = false;
        if (UserUtils.getOfficeList() != null) {
            for (Office office : UserUtils.getOfficeList()) {
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                    oflag = true;
                }
            }
        }
        if (user.isAdmin()) {
            return super.findList(bizOrderHeader);
        } else {
            if (oflag) {
                //     bizOrderHeader.setConsultantId(user.getId());
            } else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            return super.findList(bizOrderHeader);
        }
    }

    @Override
    public Page<BizOrderHeader> findPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();
        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        DoOrderHeaderProcessFifthConfig doOrderHeaderProcessFifthConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

        String selectAuditStatus = bizOrderHeader.getSelectAuditStatus();
        if (StringUtils.isNotBlank(selectAuditStatus)) {
            List<String> originConfigValue = Lists.newArrayList();
            List<String> localConfigValue = Lists.newArrayList();
            List<String> doFifthConfigValue = Lists.newArrayList();

            //////////////////////////////////////////////////////////////////
            for (Process process : originConfig.getProcessList()) {
                if (process.getName().contains(selectAuditStatus)) {
                    originConfigValue.add(String.valueOf(process.getCode()));
                }
            }
//////////////////////////////////////////////////////////////////
            for (Process process : localConfig.getProcessList()) {
                if (process.getName().contains(selectAuditStatus)) {
                    localConfigValue.add(String.valueOf(process.getCode()));
                }
            }
//////////////////////////////////////////////////////////////////
            for (DoOrderHeaderProcessFifthConfig.OrderHeaderProcess process : doOrderHeaderProcessFifthConfig.getProcessList()) {
                if (process.getName().contains(selectAuditStatus)) {
                    doFifthConfigValue.add(String.valueOf(process.getCode()));
                }
            }

            bizOrderHeader.setOriginCode(CollectionUtils.isEmpty(originConfigValue) ? null : originConfigValue);
            bizOrderHeader.setLocalCode(CollectionUtils.isEmpty(localConfigValue) ? null : localConfigValue);
            bizOrderHeader.setDoFifthCode(CollectionUtils.isEmpty(doFifthConfigValue) ? null : doFifthConfigValue);
        }

        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            bizOrderHeader.setDataStatus("filter");
            if(StringUtils.isNotBlank(bizOrderHeader.getStr())&&"orderAudit".equals(bizOrderHeader.getStr())){

                bizOrderHeader.setPage(page);
                page.setList(bizOrderHeaderDao.findListNotCompleteAudit(bizOrderHeader));
                return page;
            }else {
                return super.findPage(page, bizOrderHeader);
            }

        } else {
            boolean flag = false;
            boolean roleFlag = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;
                    }
                    if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        roleFlag = true;
                    }
                }
            }
            if(flag){
                bizOrderHeader.setCenterId(user.getCompany().getId());
            }else if (roleFlag) {
                bizOrderHeader.setConsultantId(user.getId());
            } else if (bizOrderHeader.getSource() != null && "vendor".equals(bizOrderHeader.getSource())) {
                bizOrderHeader.getSqlMap().put("order",BaseService.dataScopeFilter(user,"vend","su"));
            } else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            return super.findPage(page, bizOrderHeader);
        }
    }

    public Page<BizOrderHeader> findPageForSendGoods(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
        boolean oflag = false;
        if (UserUtils.getOfficeList() != null) {
            for (Office office : UserUtils.getOfficeList()) {
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                    oflag = true;
                }
            }
        }
        List<Role> roleList = user.getRoleList();
        List<String> enNameList = Lists.newArrayList();
        for (Role role : roleList) {
            enNameList.add(role.getEnname());
        }
        if (!user.isAdmin() && !oflag && !enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
            bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
        } else if (!user.isAdmin() && enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
            bizOrderHeader.getSqlMap().put("order",BaseService.dataScopeFilter(user,"vend","su"));
        }
        return super.findPage(page, bizOrderHeader);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void save(BizOrderHeader bizOrderHeader) {
        if (bizOrderHeader.getBizType() == null) {
            bizOrderHeader.setBizType(1);
        }
        if (bizOrderHeader.getOrderType() == null) {
            bizOrderHeader.setOrderType(1);
        }
        BizOrderAddress bizLocation = bizOrderHeader.getBizLocation();
        if (bizLocation.getRegion() == null) {
            bizLocation.setRegion(new SysRegion());
        }
        bizOrderAddressService.save(bizLocation);
        if (bizOrderHeader.getId() == null || bizOrderHeader.getId() == 0) {
            BizOrderHeader orderHeader = new BizOrderHeader();
            orderHeader.setCustomer(bizOrderHeader.getCustomer());
            orderHeader.setOrderType(Integer.parseInt(OrderTypeEnum.SO.getOrderType()));
            List<BizOrderHeader> bizOrderHeaderList = bizOrderHeaderDao.findList(orderHeader);
            int s = 0;
            if (bizOrderHeaderList != null && bizOrderHeaderList.size() > 0) {
                s = bizOrderHeaderList.size();
            }
            BizCustomCenterConsultant centerConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            int c = 0;
            if (centerConsultant != null) {
                c = centerConsultant.getCenters().getId();
            }
            String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()), c, bizOrderHeader.getCustomer().getId(), s + 1);
            bizOrderHeader.setOrderNum(orderNum);
        } else {
            bizOrderHeader.setOrderNum(bizOrderHeader.getOrderNum());
        }

        bizOrderHeader.setBizLocation(bizLocation);
        if (bizOrderHeader.getTotalDetail() == null) {
            bizOrderHeader.setTotalDetail(0.0);
        } else {
            bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail());
        }
        bizOrderHeader.setBizStatus(bizOrderHeader.getBizStatus());
        if (bizOrderHeader.getReceiveTotal() == null) {
            bizOrderHeader.setReceiveTotal(0.0);
        } else {
            bizOrderHeader.setReceiveTotal(bizOrderHeader.getReceiveTotal());
        }
        super.save(bizOrderHeader);

        if (bizOrderHeader.getOrderComment() != null && StringUtils.isNotBlank(bizOrderHeader.getOrderComment().getComments())) {
            BizOrderComment bizOrderComment = new BizOrderComment();
            bizOrderComment.setId(bizOrderHeader.getOrderComment().getId());
            bizOrderComment.setOrder(bizOrderHeader);
            bizOrderComment.setComments(bizOrderHeader.getOrderComment().getComments());
            bizOrderCommentService.save(bizOrderComment);
        }
        bizOrderStatusService.saveOrderStatus(bizOrderHeader);
        BizOrderHeader orderHeader = this.get(bizOrderHeader.getId());
        List<BizOrderDetail> orderDetailList = orderHeader.getOrderDetailList();
        if (CollectionUtils.isNotEmpty(orderDetailList)) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                bizOrderDetail.setSkuInfo(bizSkuInfo);
            }
            bizOrderHeader.setOrderDetailList(orderDetailList);
            super.save(bizOrderHeader);
        }

        saveCommonImg(bizOrderHeader);

        bizLocation.setId(bizOrderHeader.getBizLocation().getId());
        bizLocation.setOrderHeaderID(bizOrderHeader);
        bizOrderAddressService.save(bizLocation);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveCommonProcess(OrderPayProportionStatusEnum orderPayProportionStatusEnum, BizOrderHeader bizOrderHeader, boolean reGen){
        Integer code = null;
        //处理角色
        String roleEnNameEnum = null;
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizOrderHeader.getId().toString());
        commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
        List<CommonProcessEntity> processList = commonProcessService.findList(commonProcessEntity);
        if (CollectionUtils.isEmpty(processList) || reGen) {
            commonProcessEntity.setCurrent(1);
            DoOrderHeaderProcessFifthConfig doOrderHeaderProcessFifthConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();
            Integer processCode = null;
            switch (orderPayProportionStatusEnum) {
                case FIFTH:
                    processCode = doOrderHeaderProcessFifthConfig.getFifthDefaultProcessId();
                    break;
                case ALL:
                    processCode = doOrderHeaderProcessFifthConfig.getAllDefaultProcessId();
                    break;
                default:
                    break;
            }
            commonProcessEntity.setType(String.valueOf(processCode));
            List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
            if (CollectionUtils.isEmpty(list)) {
                commonProcessService.updateCurrentByObject(bizOrderHeader.getId(), BizOrderHeaderService.DATABASE_TABLE_NAME, 0);
                commonProcessService.save(commonProcessEntity);
            }
            DoOrderHeaderProcessFifthConfig.OrderHeaderProcess purchaseOrderProcess = doOrderHeaderProcessFifthConfig.processMap.get(processCode);
            roleEnNameEnum = purchaseOrderProcess.getRoleEnNameEnum();
        }

        StringBuilder phone = new StringBuilder();
        User user=UserUtils.getUser();
        User sendUser=new User(systemService.getRoleByEnname(roleEnNameEnum==null?"":roleEnNameEnum.toLowerCase()));
        sendUser.setCent(user.getCompany());
        List<User> userList = systemService.findUser(sendUser);
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User u : userList) {
                phone.append(u.getMobile()).append(",");
            }
        }

        if (StringUtils.isNotBlank(phone.toString())) {
            AliyunSmsClient.getInstance().sendSMS(
                    SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                    phone.toString(),
                    ImmutableMap.of("order","代采清单", "orderNum", bizOrderHeader.getOrderNum()));
        }
    }

    @Transactional(readOnly = false)
    public void saveOrderHeader(BizOrderHeader bizOrderHeader) {
        super.save(bizOrderHeader);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(BizOrderHeader bizOrderHeader) {
        super.delete(bizOrderHeader);
    }

    @Transactional(readOnly = false)
    public void updateMoney(BizOrderHeader bizOrderHeader) {
        bizOrderHeaderDao.updateMoney(bizOrderHeader);
    }

    @Transactional(readOnly = false)
    public void saveOrderHea(BizOrderHeader bizOrderHeader) {

    }

//    @Transactional(readOnly = false, rollbackFor = Exception.class)
//    public int updateProcessId(Integer headerId, Integer processId) {
//        return dao.updateProcessId(headerId, processId);
//    }

    public void getCommonProcessListFromDB(Integer id, List<CommonProcessEntity> list) {
        if (id == null || id == 0) {
            return;
        }
        CommonProcessEntity commonProcessEntity = commonProcessService.get(id);
        if (commonProcessEntity != null) {
            list.add(commonProcessEntity);
            if (commonProcessEntity.getPrevId() != 0) {
                getCommonProcessListFromDB(commonProcessEntity.getPrevId(), list);
            }
        }
    }

    /**
     * 计算订单利润
     * @param orderHeaderList
     * @return
     */
    /*public List<BizOrderHeader> getTotalBuyPrice(List<BizOrderHeader> orderHeaderList){
        for (BizOrderHeader orderHeader:orderHeaderList) {
            Double totalBuyPrice = 0.0;
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(orderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            if (orderDetailList != null && orderDetailList.size() > 0){
                for (BizOrderDetail orderDetail:orderDetailList){
                    BizSkuInfo skuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                    if(skuInfo!=null){
                        if(orderDetail.getBuyPrice()!=null && orderDetail.getBuyPrice()!=0){
                            totalBuyPrice += orderDetail.getBuyPrice() * (orderDetail.getOrdQty()==null?0:orderDetail.getOrdQty());
                        }else {
                            totalBuyPrice += skuInfo.getBuyPrice() * (orderDetail.getOrdQty() == null ? 0 : orderDetail.getOrdQty());
                        }
                    }

                }
            }
//                totalBuyPrice = orderDetailList.stream().parallel().mapToDouble(orderDetail -> orderDetail.getSkuInfo() == null ? 0 : orderDetail.getSkuInfo().getBuyPrice() * orderDetail.getOrdQty()).sum();
            orderHeader.setTotalBuyPrice(totalBuyPrice);
        }
        return orderHeaderList;
    }*/

    /**
     * 查询线下支付订单
     *
     * @return
     */
    public List<BizOrderHeader> findUnlineOrder() {
        return bizOrderHeaderDao.findUnlineOrder();
    }

    /**
     * 订单发货分页
     */
    public Page<BizOrderHeader> pageFindList(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
//        boolean flag=false;
        boolean oflag = false;
        /*if(user.getRoleList()!=null){
            for(Role role:user.getRoleList()){
                if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
                    flag=true;
                    break;
                }
            }
        }*/
        if (UserUtils.getOfficeList() != null) {
            for (Office office : UserUtils.getOfficeList()) {
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                    oflag = true;
                }
            }
        }
        if (user.isAdmin()) {
            bizOrderHeader.setPage(page);
            page.setList(dao.headerFindList(bizOrderHeader));
            return page;
        } else {
            if (oflag) {

            } else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            bizOrderHeader.setPage(page);
            page.setList(dao.headerFindList(bizOrderHeader));
            return page;
        }
    }

    /**
     * 订单发货分页
     */
    public Page<BizOrderHeader> pageFindListForPhotoOrder(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
//        boolean flag=false;
        boolean oflag = false;
        /*if(user.getRoleList()!=null){
            for(Role role:user.getRoleList()){
                if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
                    flag=true;
                    break;
                }
            }
        }*/
        if (UserUtils.getOfficeList() != null) {
            for (Office office : UserUtils.getOfficeList()) {
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                    oflag = true;
                }
            }
        }
        if (user.isAdmin()) {
            bizOrderHeader.setPage(page);
            page.setList(dao.headerFindListForPhotoOrder(bizOrderHeader));
            return page;
        } else {
            if (oflag) {

            } else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            bizOrderHeader.setPage(page);
            page.setList(dao.headerFindListForPhotoOrder(bizOrderHeader));
            return page;
        }
    }

    /**
     * C端订单列表
     */
    public Page<BizOrderHeader> cendfindPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        bizOrderHeader.setPage(page);
        page.setList(dao.cendfindList(bizOrderHeader));
        return page;
    }


    /**
     * C端订单 保存
     */
    @Transactional(readOnly = false)
    public void CendorderSave(BizOrderHeader bizOrderHeader) {
        if (bizOrderHeader.getBizType() == null) {
            bizOrderHeader.setBizType(1);
        }
        if (bizOrderHeader.getOrderType() == null) {
            bizOrderHeader.setOrderType(4);
        }
        BizOrderAddress bizLocation = bizOrderHeader.getBizLocation();
        if (bizLocation.getRegion() == null) {
            bizLocation.setRegion(new SysRegion());
        }
        bizOrderAddressService.save(bizLocation);
        if (bizOrderHeader.getId() == null || bizOrderHeader.getId() == 0) {
            BizOrderHeader orderHeader = new BizOrderHeader();
            orderHeader.setCustomer(bizOrderHeader.getCustomer());
            orderHeader.setOrderType(Integer.parseInt(OrderTypeEnum.CO.getOrderType()));
            List<BizOrderHeader> bizOrderHeaderList = bizOrderHeaderDao.findList(orderHeader);
            int s = 0;
            if (bizOrderHeaderList != null && bizOrderHeaderList.size() > 0) {
                s = bizOrderHeaderList.size();
            }
            BizCustomCenterConsultant centerConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            int c = 0;
            if (centerConsultant != null) {
                c = centerConsultant.getCenters().getId();
            }
            String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()), c, bizOrderHeader.getCustomer().getId(), s + 1);
            bizOrderHeader.setOrderNum(orderNum);
        } else {
            bizOrderHeader.setOrderNum(bizOrderHeader.getOrderNum());
        }
        bizOrderHeader.setBizLocation(bizLocation);
        if (bizOrderHeader.getTotalDetail() == null) {
            bizOrderHeader.setTotalDetail(0.0);
        } else {
            bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail());
        }
        bizOrderHeader.setBizStatus(bizOrderHeader.getBizStatus());
        super.save(bizOrderHeader);

        if (bizOrderHeader.getOrderComment() != null && StringUtils.isNotBlank(bizOrderHeader.getOrderComment().getComments())) {
            BizOrderComment bizOrderComment = new BizOrderComment();
            bizOrderComment.setId(bizOrderHeader.getOrderComment().getId() == null ? null : bizOrderHeader.getOrderComment().getId());
            bizOrderComment.setOrder(bizOrderHeader);
            bizOrderComment.setComments(bizOrderHeader.getOrderComment().getComments());
            bizOrderCommentService.save(bizOrderComment);
        }

        BizOrderHeader orderHeader = this.get(bizOrderHeader.getId());
        List<BizOrderDetail> orderDetailList = orderHeader.getOrderDetailList();
        if (CollectionUtils.isNotEmpty(orderDetailList)) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                bizOrderDetail.setSkuInfo(bizSkuInfo);
            }
            bizOrderHeader.setOrderDetailList(orderDetailList);
            super.save(bizOrderHeader);
        }
        bizLocation.setId(bizOrderHeader.getBizLocation().getId());
        bizLocation.setOrderHeaderID(bizOrderHeader);
        bizOrderAddressService.save(bizLocation);
    }

    /**
     * 导出，订单出库，订单发货导出
     */
    public List<BizOrderHeader> pageFindListExprot(BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
//        boolean flag=false;
        boolean oflag = false;
        /*if(user.getRoleList()!=null){
            for(Role role:user.getRoleList()){
                if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
                    flag=true;
                    break;
                }
            }
        }*/
        if (UserUtils.getOfficeList() != null) {
            for (Office office : UserUtils.getOfficeList()) {
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())) {
                    oflag = true;
                }
            }
        }
        if (user.isAdmin()) {
            List<BizOrderHeader> bizOrderHeaderList = super.findList(bizOrderHeader);
            return bizOrderHeaderList;
        } else {
            if (oflag) {

            } else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            List<BizOrderHeader> bizOrderHeaderList = super.findList(bizOrderHeader);
            return bizOrderHeaderList;
        }
    }

    /**
     * 查询供应商主负责人
     *
     * @return
     */
    public User findVendUser(Integer orderId) {
        return userDao.findVendUser(orderId);
    }

    /**
     * 查询供应商主负责人
     *
     * @return
     */
    public List<User> findVendUserV2(Integer orderId) {
        return userDao.findVendUserV2(orderId);
    }

    private List<CommonImg> getImgList(Integer imgType, Integer prodId) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(prodId);
        commonImg.setObjectName("biz_order_header");
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    //保存上传凭证
    @Transactional(readOnly = false)
    public void saveCommonImg(BizOrderHeader bizOrderHeader) {
        String photos = null;
        try {
            photos = StringUtils.isNotBlank(bizOrderHeader.getPhotos()) ? URLDecoder.decode(bizOrderHeader.getPhotos(), "utf-8") : StringUtils.EMPTY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("主图转换编码异常." + e.getMessage(), e);
        }
        String imgPhotosSorts = bizOrderHeader.getImgPhotosSorts();
        String[] photosSort = StringUtils.split(imgPhotosSorts, ",");

        if (photos != null) {
            String[] photoArr = photos.split("\\|");
            saveProdImg(ImgEnum.UNlINE_REFUND_VOUCHER.getCode(), bizOrderHeader, photoArr);
        }

        //设置主图和图片次序
        List<CommonImg> commonImgs = getImgList(ImgEnum.UNlINE_REFUND_VOUCHER.getCode(), bizOrderHeader.getId());
        for (int i = 0; i < commonImgs.size(); i++) {
            CommonImg commonImg = commonImgs.get(i);
            if (photosSort != null && photosSort.length > 0) {
                commonImg.setImgSort(Integer.parseInt(photosSort[i]));
            }
            commonImgService.save(commonImg);
        }

    }

    public void saveProdImg(Integer imgType, BizOrderHeader bizOrderHeader, String[] photoArr) {
        if (bizOrderHeader.getId() == null) {
            log.error("Can't save product image without product ID!");
            return;
        }

        List<CommonImg> commonImgs = getImgList(imgType, bizOrderHeader.getId());

        Set<String> existSet = new LinkedHashSet<>();
        for (CommonImg commonImg1 : commonImgs) {
            existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
        }
        Set<String> newSet = new LinkedHashSet<>(Arrays.asList(photoArr));

        Set<String> result = new LinkedHashSet<>();
        //差集，结果做删除操作
        result.clear();
        result.addAll(existSet);
        result.removeAll(newSet);
        for (String url : result) {
            for (CommonImg commonImg1 : commonImgs) {
                if (url.equals(commonImg1.getImgServer() + commonImg1.getImgPath())) {
                    commonImg1.setDelFlag("0");
                    commonImgService.delete(commonImg1);
                }
            }
        }
        //差集，结果做插入操作
        result.clear();
        result.addAll(newSet);
        result.removeAll(existSet);

        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName(ORDER_TABLE);
        commonImg.setImgType(imgType);
        commonImg.setImgSort(20);

        List<CommonImg> oldImgList = null;
        if (ImgEnum.LIST_PRODUCT_TYPE.getCode() == imgType) {
            CommonImg oldCommonImg = new CommonImg();
            oldCommonImg.setImgType(ImgEnum.LIST_PRODUCT_TYPE.getCode());
            oldCommonImg.setObjectId(bizOrderHeader.getId());
            oldCommonImg.setObjectName(ORDER_TABLE);
            oldImgList = commonImgService.findList(oldCommonImg);
        }

        for (String name : result) {
            if (StringUtils.isNotBlank(name) || (CollectionUtils.isEmpty(oldImgList) && (ImgEnum.LIST_PRODUCT_TYPE.getCode() == imgType))) {
                commonImg.setId(null);
                commonImg.setImgPath(name.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY).replaceAll(DsConfig.getOldImgServer(), StringUtils.EMPTY));
                commonImg.setImgServer(name.contains(DsConfig.getOldImgServer()) ? DsConfig.getOldImgServer() : DsConfig.getImgServer());
                commonImgService.save(commonImg);
                continue;
            }

            if (name.trim().length() == 0 || name.contains(DsConfig.getImgServer()) || name.contains(DsConfig.getOldImgServer())) {
                continue;
            }
            String pathFile = Global.getUserfilesBaseDir() + name;
            String ossPath = AliOssClientUtil.uploadFile(pathFile, ImgEnum.LIST_PRODUCT_TYPE.getCode() != imgType);

            commonImg.setId(null);
            commonImg.setImgPath("/" + ossPath);
            commonImg.setImgServer(DsConfig.getImgServer());
            commonImgService.save(commonImg);
        }
    }

    /**
     * 导出
     */
    public List<BizOrderHeader> findListExport(BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            bizOrderHeader.setDataStatus("filter");
            return super.findList(bizOrderHeader);
        } else {
            boolean flag = false;
            boolean roleFlag = false;
            if (user.getRoleList() != null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;
                    }
                    if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())) {
                        roleFlag = true;
                    }
                }
            }
            if (flag) {
                bizOrderHeader.setCenterId(user.getOffice().getId());
            } else {
                if (roleFlag) {
                    bizOrderHeader.setConsultantId(user.getId());
                } else {
                    bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
                }
            }
            return super.findList(bizOrderHeader);
        }
    }

    /**
     * 更新退款订单退款状态
     *
     * @param bizOrderHeader
     */
    @Transactional(readOnly = false)
    public void updateDrawbackStatus(BizOrderHeader bizOrderHeader) {
        BizDrawBack bizDrawBack = bizOrderHeaderDao.findDrawBack(bizOrderHeader);
        Integer drawbackStatus = bizOrderHeader.getDrawBack().getDrawbackStatus();
        bizDrawBack.setDrawbackStatus(drawbackStatus);
        bizDrawBack.preUpdate();
        bizDrawBackDao.update(bizDrawBack);
    }

    /**
     * 通过orderNum获取订单Entity
     *
     * @param orderNum
     */
    public BizOrderHeader getByOrderNum(String orderNum) {
        return bizOrderHeaderDao.getByOrderNum(orderNum);
    }

    /**
     * 代采订单审核
     * @param orderHeaderId
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> auditFifty(Integer orderHeaderId, String currentType, int auditType, String description) {
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(orderHeaderId));
        commonProcessEntity.setObjectName(DATABASE_TABLE_NAME);
        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
        if (list.size() != 1) {
            return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常! current process 不为 1");
        }
        CommonProcessEntity cureentProcessEntity = list.get(0);
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常!");
        }
        if (cureentProcessEntity == null) {
            return Pair.of(Boolean.FALSE, "操作失败,当前订单无审核状态!");
        }

        BizOrderHeader bizOrderHeader = this.get(orderHeaderId);
        DoOrderHeaderProcessFifthConfig doOrderHeaderProcessConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

        Integer passProcessCode = null;
        // 当前流程
        DoOrderHeaderProcessFifthConfig.OrderHeaderProcess currentProcess = doOrderHeaderProcessConfig.processMap.get(Integer.valueOf(currentType));
        switch (OrderPayProportionStatusEnum.parse(bizOrderHeader)) {
            case FIFTH:
                passProcessCode = currentProcess.getFifthPassCode();
                break;
            case ALL:
                passProcessCode = currentProcess.getAllPassCode();
                break;
            default:
                break;
        }
        if (passProcessCode == null || passProcessCode == 0) {
            return Pair.of(Boolean.FALSE, "操作失败,没有下级流程!");
        }

        // 下一流程
        DoOrderHeaderProcessFifthConfig.OrderHeaderProcess nextProcess = doOrderHeaderProcessConfig.processMap.get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? passProcessCode : currentProcess.getRejectCode());
        if (nextProcess == null) {
            return Pair.of(Boolean.FALSE, "操作失败,当前流程已经结束!");
        }
        User user = UserUtils.getUser();
        RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(currentProcess.getRoleEnNameEnum());
        Role role = new Role();
        role.setEnname(roleEnNameEnum.getState());
        if (!user.isAdmin() && !user.getRoleList().contains(role)) {
            return Pair.of(Boolean.FALSE, "操作失败,该用户没有权限!");
        }

        if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
            return Pair.of(Boolean.FALSE, "请输入驳回理由!");
        }

        cureentProcessEntity.setBizStatus(auditType);
        cureentProcessEntity.setProcessor(user.getId().toString());
        cureentProcessEntity.setDescription(description);
        cureentProcessEntity.setCurrent(0);
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(bizOrderHeader.getId().toString());
        nextProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setCurrent(1);
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());

        commonProcessService.save(nextProcessEntity);

        StringBuilder phone = new StringBuilder();

        User sendUser=new User(systemService.getRoleByEnname(nextProcess.getRoleEnNameEnum()==null?"":nextProcess.getRoleEnNameEnum().toLowerCase()));

        List<User> userList = systemService.findUser(sendUser);
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User u : userList) {
                phone.append(u.getMobile()).append(",");
            }
        }
        if (StringUtils.isNotBlank(phone.toString())) {
            AliyunSmsClient.getInstance().sendSMS(
                    SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                    phone.toString(),
                    ImmutableMap.of("order","代采清单", "orderNum", bizOrderHeader.getOrderNum()));
        }

        return Pair.of(Boolean.TRUE, "操作成功!");
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateBizStatus(Integer id, Integer status) {
        return dao.updateBizStatus(id, status,UserUtils.getUser(),new Date());
    }

    /**
     * 根据商品id获取相应orderDetailId
     * @param poHeaderId
     * @param skuInfoId
     * @return
     */
    public Integer getOrderDetailIdBySkuInfoId(Integer poHeaderId, Integer skuInfoId){
        return bizOrderHeaderDao.getOrderDetailIdBySkuInfoId(poHeaderId, skuInfoId);
    }
}