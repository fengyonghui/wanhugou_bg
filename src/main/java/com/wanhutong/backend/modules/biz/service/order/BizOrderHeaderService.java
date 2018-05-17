/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Service
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {

    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired @Lazy
    private BizOrderDetailService bizOrderDetailService;
    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;


    public List<BizOrderHeader> findListFirstOrder(BizOrderHeader bizOrderHeader) {
        //查询状态 status=0 和 1的所有信息
        return bizOrderHeaderDao.findListFirstOrder(bizOrderHeader);
    }

    public BizOrderHeader get(Integer id) {
        return super.get(id);
    }

    public List<BizOrderHeader> findList(BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
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
        if (UserUtils.getOfficeList() != null){
            for (Office office:UserUtils.getOfficeList()){
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
                    oflag = true;
                }
            }
        }
        if(user.isAdmin()){
            List<BizOrderHeader> bizOrderHeaderList = super.findList(bizOrderHeader);

            return bizOrderHeaderList;
        }else {
            if(oflag){

            }else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
                List<BizOrderHeader> bizOrderHeaderList = super.findList(bizOrderHeader);

            return bizOrderHeaderList;
        }
    }

    public Page<BizOrderHeader> findPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
        if(user.isAdmin()){
            bizOrderHeader.setDataStatus("filter");
           // Integer count= bizOrderHeaderDao.findCount(bizOrderHeader);
            Page<BizOrderHeader> orderHeaderPage = super.findPage(page, bizOrderHeader);
          // page.setCount(count);
            List<BizOrderHeader> orderHeaderList = orderHeaderPage.getList();
            List<BizOrderHeader> bizOrderHeaderList = getTotalBuyPrice(orderHeaderList);
            orderHeaderPage.setList(bizOrderHeaderList);

            return orderHeaderPage;
        }else {

            boolean flag=false;
            boolean roleFlag = false;
            if(user.getRoleList()!=null) {
                for (Role role : user.getRoleList()) {
                    if (RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                        flag = true;
                    }
                    if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())){
                        roleFlag = true;
                    }
                }
            }
            if(flag){
                bizOrderHeader.setCenterId(user.getOffice().getId());
            }else {
                if (roleFlag) {
                    bizOrderHeader.setConsultantId(user.getId());
                }else {
                    bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
                }
            }
            Page<BizOrderHeader> orderHeaderPage=super.findPage(page, bizOrderHeader);
           // Integer count= bizOrderHeaderDao.findCount(bizOrderHeader);
          //  page.setCount(count);
            List<BizOrderHeader> orderHeaderList = orderHeaderPage.getList();
            List<BizOrderHeader> bizOrderHeaderList = getTotalBuyPrice(orderHeaderList);
            orderHeaderPage.setList(bizOrderHeaderList);

            return orderHeaderPage;
        }
    }

    public Page<BizOrderHeader> findPageForSendGoods(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
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
        if (UserUtils.getOfficeList() != null){
            for (Office office:UserUtils.getOfficeList()){
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
                    oflag = true;
                }
            }
        }
        if(user.isAdmin()){
            Page<BizOrderHeader> pageList = super.findPage(page,bizOrderHeader);

            return pageList;
        }else {
            if(oflag){

            }else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            Page<BizOrderHeader> pageList = super.findPage(page, bizOrderHeader);

            return pageList;
        }
    }

    @Transactional(readOnly = false)
    public void save(BizOrderHeader bizOrderHeader) {
        if(bizOrderHeader.getBizType()==null){
            bizOrderHeader.setBizType(1);//订单商品类型默认 2非专营      ----1专营
        }
        if(bizOrderHeader.getOrderType()==null){
            bizOrderHeader.setOrderType(1);//订单类型，默认选中  1普通订单
        }
        BizOrderAddress bizLocation = bizOrderHeader.getBizLocation();
        if (bizLocation.getRegion() == null) {
            bizLocation.setRegion(new SysRegion());
        }
        bizOrderAddressService.save(bizLocation);
        if(bizOrderHeader.getId()==null || bizOrderHeader.getId()==0){
            BizOrderHeader orderHeader=new BizOrderHeader();
            orderHeader.setCustomer(bizOrderHeader.getCustomer());
            orderHeader.setOrderType(Integer.parseInt(OrderTypeEnum.SO.getOrderType()));
            List<BizOrderHeader> bizOrderHeaderList= bizOrderHeaderDao.findList(orderHeader);
            int s=0;
            if(bizOrderHeaderList!=null && bizOrderHeaderList.size()>0){
                s=bizOrderHeaderList.size();
            }
            BizCustomCenterConsultant centerConsultant=bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            int c=0;
            if(centerConsultant!=null){
                c=centerConsultant.getCenters().getId();
            }
            String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()), c, bizOrderHeader.getCustomer().getId(),s+1);
            bizOrderHeader.setOrderNum(orderNum);
        }else{
            bizOrderHeader.setOrderNum(bizOrderHeader.getOrderNum());
        }
        bizOrderHeader.setBizLocation(bizLocation);
        if(bizOrderHeader.getTotalDetail()==null){
            bizOrderHeader.setTotalDetail(0.0);
        }else{
            bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail());
        }
        bizOrderHeader.setBizStatus(bizOrderHeader.getBizStatus());
        super.save(bizOrderHeader);

        BizOrderHeader orderHeader = this.get(bizOrderHeader.getId());
        List<BizOrderDetail> orderDetailList = orderHeader.getOrderDetailList();
        if(orderDetailList != null && !orderDetailList.isEmpty()) {
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

    @Transactional(readOnly = false)
    public void saveOrderHeader(BizOrderHeader bizOrderHeader) {
        super.save(bizOrderHeader);
    }

    @Transactional(readOnly = false)
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

    /**
     * 计算订单利润
     * @param orderHeaderList
     * @return
     */
    public List<BizOrderHeader> getTotalBuyPrice(List<BizOrderHeader> orderHeaderList){
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
    }

    /**
     * 查询线下支付订单
     * @return
     */
    public List<BizOrderHeader> findUnlineOrder() {
        return bizOrderHeaderDao.findUnlineOrder();
    }

    /**
     * 订单发货分页
     * */
    public Page<BizOrderHeader> pageFindList(Page<BizOrderHeader> page,BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
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
        if (UserUtils.getOfficeList() != null){
            for (Office office:UserUtils.getOfficeList()){
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
                    oflag = true;
                }
            }
        }
        if(user.isAdmin()){
            bizOrderHeader.setPage(page);
            page.setList(dao.headerFindList(bizOrderHeader));
            return page;
        }else {
            if(oflag){

            }else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            bizOrderHeader.setPage(page);
            page.setList(dao.headerFindList(bizOrderHeader));
            return page;
        }
    }

    /**
     * C端订单列表
     * */
    public Page<BizOrderHeader> cendfindPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        bizOrderHeader.setPage(page);
        page.setList(dao.cendfindList(bizOrderHeader));
        return page;
    }


    /**
     * C端订单 保存
     * */
    @Transactional(readOnly = false)
    public void CendorderSave(BizOrderHeader bizOrderHeader) {
        if(bizOrderHeader.getBizType()==null){
            bizOrderHeader.setBizType(1);//订单商品类型默认 2非专营      ----1专营
        }
        if(bizOrderHeader.getOrderType()==null){
            bizOrderHeader.setOrderType(4);//订单类型，默认选中  1普通订单
        }
        BizOrderAddress bizLocation = bizOrderHeader.getBizLocation();
        if (bizLocation.getRegion() == null) {
            bizLocation.setRegion(new SysRegion());
        }
        bizOrderAddressService.save(bizLocation);
        if(bizOrderHeader.getId()==null || bizOrderHeader.getId()==0){
            BizOrderHeader orderHeader=new BizOrderHeader();
            orderHeader.setCustomer(bizOrderHeader.getCustomer());
            orderHeader.setOrderType(Integer.parseInt(OrderTypeEnum.CO.getOrderType()));
            List<BizOrderHeader> bizOrderHeaderList= bizOrderHeaderDao.findList(orderHeader);
            int s=0;
            if(bizOrderHeaderList!=null && bizOrderHeaderList.size()>0){
                s=bizOrderHeaderList.size();
            }
            BizCustomCenterConsultant centerConsultant=bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            int c=0;
            if(centerConsultant!=null){
                c=centerConsultant.getCenters().getId();
            }
            String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()), c, bizOrderHeader.getCustomer().getId(),s+1);
            bizOrderHeader.setOrderNum(orderNum);
        }else{
            bizOrderHeader.setOrderNum(bizOrderHeader.getOrderNum());
        }
        bizOrderHeader.setBizLocation(bizLocation);
        if(bizOrderHeader.getTotalDetail()==null){
            bizOrderHeader.setTotalDetail(0.0);
        }else{
            bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail());
        }
        bizOrderHeader.setBizStatus(bizOrderHeader.getBizStatus());
        super.save(bizOrderHeader);

        BizOrderHeader orderHeader = this.get(bizOrderHeader.getId());
        List<BizOrderDetail> orderDetailList = orderHeader.getOrderDetailList();
        if(orderDetailList != null && !orderDetailList.isEmpty()) {
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
     * */
    public List<BizOrderHeader> pageFindListExprot(BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
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
        if (UserUtils.getOfficeList() != null){
            for (Office office:UserUtils.getOfficeList()){
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
                    oflag = true;
                }
            }
        }
        if(user.isAdmin()){
            List<BizOrderHeader> bizOrderHeaderList = super.findList(bizOrderHeader);
            return bizOrderHeaderList;
        }else {
            if(oflag){

            }else {
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            List<BizOrderHeader> bizOrderHeaderList = super.findList(bizOrderHeader);
            return bizOrderHeaderList;
        }
    }


}