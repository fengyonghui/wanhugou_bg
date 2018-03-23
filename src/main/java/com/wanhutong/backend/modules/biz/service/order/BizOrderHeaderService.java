/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
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
import java.util.stream.Stream;

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
//    @Autowired
//    private BizOrderDetailService bizOrderDetailService;
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
        Double totalBuyPrice = 0.0;
        boolean flag=false;
        if(user.getRoleList()!=null){
            for(Role role:user.getRoleList()){
                if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
                    flag=true;
                    break;
                }
            }
        }
        if(user.isAdmin()){
            List<BizOrderHeader> headerList = super.findList(bizOrderHeader);
            //用于订单导出的利润
            for (BizOrderHeader orderHeader : headerList) {
                totalBuyPrice = orderHeader.getOrderDetailList().stream().parallel().mapToDouble(orderDetail -> orderDetail.getSkuInfo() == null ? 0 : orderDetail.getSkuInfo().getBuyPrice() * orderDetail.getOrdQty()).sum();
                orderHeader.setTotalBuyPrice(totalBuyPrice);
            }
            return headerList;
        }else {
            if(flag){
                bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            }
            List<BizOrderHeader> headerList = super.findList(bizOrderHeader);
            //用于订单导出的利润
            for (BizOrderHeader orderHeader : headerList) {
                totalBuyPrice = orderHeader.getOrderDetailList().stream().parallel().mapToDouble(orderDetail -> orderDetail.getSkuInfo() == null ? 0 : orderDetail.getSkuInfo().getBuyPrice() * orderDetail.getOrdQty()).sum();
                orderHeader.setTotalBuyPrice(totalBuyPrice);
            }
            return headerList;
        }
    }

    public Page<BizOrderHeader> findPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
        if(user.isAdmin()){
           // Integer count= bizOrderHeaderDao.findCount(bizOrderHeader);
            Page<BizOrderHeader> orderHeaderPage = super.findPage(page, bizOrderHeader);
          // page.setCount(count);
            List<BizOrderHeader> orderHeaderList = orderHeaderPage.getList();
            Double totalBuyPrice = 0.0;
            for (BizOrderHeader orderHeader:orderHeaderList) {

                totalBuyPrice = orderHeader.getOrderDetailList().stream().parallel().mapToDouble(orderDetail -> orderDetail.getSkuInfo()==null?0:orderDetail.getSkuInfo().getBuyPrice()*orderDetail.getOrdQty()).sum();
                orderHeader.setTotalBuyPrice(totalBuyPrice);

            }
            orderHeaderPage.setList(orderHeaderList);

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
            Integer count= bizOrderHeaderDao.findCount(bizOrderHeader);
            page.setCount(count);
            List<BizOrderHeader> orderHeaderList = orderHeaderPage.getList();
            Double totalBuyPrice = 0.0;
            for (BizOrderHeader orderHeader:orderHeaderList) {

                totalBuyPrice = orderHeader.getOrderDetailList().stream().parallel().mapToDouble(orderDetail -> orderDetail.getSkuInfo().getBuyPrice()*orderDetail.getOrdQty()).sum();
                orderHeader.setTotalBuyPrice(totalBuyPrice);
            }
            orderHeaderPage.setList(orderHeaderList);

            return orderHeaderPage;
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
        //利润
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

}