/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTransaction;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.BizCustCreditService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SysPlatWalletService;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Controller
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeader")
public class BizOrderHeaderController extends BaseController {

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private OfficeService officeService;
	@Resource
	private BizOrderHeaderDao bizOrderHeaderDao;
	@Autowired
    private BizCustCreditService bizCustCreditService;

	@Autowired
	private BizPayRecordService bizPayRecordService;
	@Autowired
	private SysPlatWalletService sysPlatWalletService;
	@Autowired
	private BizOrderAddressService bizOrderAddressService;
	@Autowired
	private BizCustomCenterConsultantService bizCustomCenterConsultantService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;

	@ModelAttribute
	public BizOrderHeader get(@RequestParam(required=false) Integer id) {
		BizOrderHeader entity = null;
		Double sum = 0.0;
		if (id!=null && id!=0){
			entity = bizOrderHeaderService.get(id);
			BizOrderDetail bizOrderDetail = new BizOrderDetail();
			bizOrderDetail.setOrderHeader(entity);
			List<BizOrderDetail> list = bizOrderDetailService.findList(bizOrderDetail);
			entity.setTotalDetail(entity.getTotalDetail());
			entity.setOrderDetailList(list);
		}
		if (entity == null){
			entity = new BizOrderHeader();
		}
		return entity;
	}

	@RequiresPermissions("biz:order:bizOrderHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderHeaderList";
	}


	@RequiresPermissions("biz:order:bizOrderHeader:view")
	@RequestMapping(value = "form")
	public String form(BizOrderHeader bizOrderHeader, Model model,String orderNoEditable,String orderDetails) {
		if(bizOrderHeader.getCustomer()!=null && bizOrderHeader.getCustomer().getId()!=null){
			Office office=officeService.get(bizOrderHeader.getCustomer().getId());
			bizOrderHeader.setCustomer(office);
			model.addAttribute("entity2", bizOrderHeader);
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
			BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
			if(bizCustomCenterConsultant!=null){
				model.addAttribute("orderCenter", bizCustomCenterConsultant);
			}else{
				model.addAttribute("orderCenter", new BizCustomCenterConsultant());
			}
		}
		BizOrderHeader bizOrderHeaderTwo = bizOrderHeaderService.get(bizOrderHeader.getId());
		if(bizOrderHeader.getId()!=null){
			Double totalDetail = bizOrderHeaderTwo.getTotalDetail();//订单详情总价
			Double totalExp = bizOrderHeaderTwo.getTotalExp();//订单总费用
			Double freight = bizOrderHeaderTwo.getFreight();//运费
			Double orderHeaderTotal=totalDetail+totalExp+freight;
			bizOrderHeader.setTobePaid(orderHeaderTotal-bizOrderHeaderTwo.getReceiveTotal());//页面显示待支付总价
			if(orderNoEditable!=null && orderNoEditable.equals("editable") ){//不可编辑标识符
				bizOrderHeaderTwo.setOrderNoEditable("editable");//待支付页面不能修改
			}
			if(orderDetails!=null && orderDetails.equals("details")){
				bizOrderHeaderTwo.setOrderDetails("details");//查看详情页面不能修改
			}
				BizOrderAddress bizOrderAddress = new BizOrderAddress();
				bizOrderAddress.setId(bizOrderHeaderTwo.getBizLocation().getId());
				List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
				for (BizOrderAddress orderAddress : list) {
//				    收货地址
					if(orderAddress.getType()==1){
						model.addAttribute("orderAddress", orderAddress);
					}
				}
			BizOrderAddress orderAddress = new BizOrderAddress();
			orderAddress.setOrderHeaderID(bizOrderHeaderTwo);
			List<BizOrderAddress> Addresslist = bizOrderAddressService.findList(orderAddress);
			for (BizOrderAddress address : Addresslist) {
//				交货地址
				if(address.getType()==2){
					model.addAttribute("address", address);
				}
			}
		}
		BizOrderHeader boh = new BizOrderHeader();
		if(bizOrderHeader!=null){
			boh.setCustomer(bizOrderHeader.getCustomer());
			boh.setBizStatus(BizOrderDiscount.ONE_ORDER.getOneOr());//条件为0
			List<BizOrderHeader> list = bizOrderHeaderDao.findListFirstOrder(boh);
			if(list.size()==0){
				System.out.println("--是首单--");
				bizOrderHeader.setOneOrder("firstOrder");
			}
		}
        model.addAttribute("entity", bizOrderHeader);
		return "modules/biz/order/bizOrderHeaderForm";
	}

	@RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderHeader)){
			return form(bizOrderHeader, model,null,null);
		}
		if(bizOrderHeader.getPlatformInfo()==null){
			bizOrderHeader.getPlatformInfo().setId(1);
		}
		bizOrderHeaderService.save(bizOrderHeader);
		addMessage(redirectAttributes, "保存订单信息成功");
		Integer orId = bizOrderHeader.getId();
		String oneOrder = bizOrderHeader.getOneOrder();
		if(bizOrderHeader.getClientModify()!=null && bizOrderHeader.getClientModify().equals("client_modify")){
//			保存跳回客户专员
			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/list?flag=check_pending&consultantId="+bizOrderHeader.getConsultantId();
		}
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/?oneOrder="+oneOrder;
	//	return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderDetail/form?orderHeader.id="+orId+"&orderHeader.oneOrder="+oneOrder;
	}

	@RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderHeader bizOrderHeader,Model model ,RedirectAttributes redirectAttributes) {
		bizOrderHeaderService.delete(bizOrderHeader);
		addMessage(redirectAttributes, "删除订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/?repage&customer.id="+bizOrderHeader.getCustomer().getId();
	}

	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = "findByOrder")
	public Map<String,Object> findByOrder(BizOrderHeader bizOrderHeader,String flag, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isNotBlank(flag) && "0".equals(flag)){
			bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
			bizOrderHeader.setSuplyIds("0");

		}else {
			bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
			bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());

		}

		List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
		Map<String,Object> map=new HashMap<String, Object>();
		List<BizOrderHeader> bizOrderHeaderList=Lists.newArrayList();
		for (BizOrderHeader orderHeader : list) {
			List<BizOrderDetail> bizOrderDetailList= Lists.newArrayList();
			List<BizOrderDetail> orderDetailList=orderHeader.getOrderDetailList();
			for (BizOrderDetail orderDetail:orderDetailList){
				BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
				orderDetail.setSkuInfo(skuInfo);
				bizOrderDetailList.add(orderDetail);
			}
			orderHeader.setOrderDetailList(bizOrderDetailList);

			bizOrderHeaderList.add(orderHeader);

		}
		List<BizInventoryInfo> inventoryInfoList=bizInventoryInfoService.findList(new BizInventoryInfo());
		map.put("inventoryInfoList",inventoryInfoList);
		map.put("bizOrderHeaderList",bizOrderHeaderList);

		return map;
	}

	@ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveOrderHeader")
    public String saveOrderHeader(BizOrderHeader bizOrderHeader,Double payMentOne, Model model, RedirectAttributes redirectAttributes) {
        BizOrderHeader orderHeaderMent = bizOrderHeaderService.get(bizOrderHeader.getId());
		Double receiveTotal = orderHeaderMent.getReceiveTotal();
		Double Totail=bizOrderHeader.getTobePaid();//获取From方法的计算总价
		BizPayRecord bizPayRecordCredit = new BizPayRecord();//保存客户钱包交易记录
		Integer customId = orderHeaderMent.getCustomer().getId();//采购商ID
		BizCustCredit bizCustCredit = bizCustCreditService.get(customId);//客户钱包
		BigDecimal subtract=null;
                Integer recordBiz=0;//0失败 1成功 支付记录表，支付状态
		String payMent="error";
       try {
		   if(bizCustCredit!=null){
               BigDecimal wallet = bizCustCredit.getWallet();//钱包总余额
			   if(wallet==null){
//				   System.out.println(" 余额不足 ");
				   recordBiz=0;
			   }else {
				   recordBiz=1;
				   BigDecimal payMentTwo = new BigDecimal(payMentOne);//输入支付的值
				   subtract = wallet.subtract(payMentTwo);//计算后结果
//				   System.out.println(subtract.compareTo(new BigDecimal(0)));
				   //   以上输出结果是：-1小于、0等于、1大于
				   if(subtract.compareTo(new BigDecimal(0))==1){
					   bizCustCredit.setWallet(subtract);
					   bizCustCreditService.save(bizCustCredit);

					   bizPayRecordCredit.setOriginalAmount(bizCustCredit.getWallet());//原金额
					   bizPayRecordCredit.setCashAmount(subtract);//现金额
					   if(payMentOne.equals(Totail)){
						   orderHeaderMent.setReceiveTotal(receiveTotal+payMentOne);//订单已收货款
						   orderHeaderMent.setBizStatus(OrderTransaction.WHOLE_PAYMENT.getOrderId());//订单状态 10全部支付
						   bizOrderHeaderService.saveOrderHeader(orderHeaderMent);
					   }else{
						   orderHeaderMent.setReceiveTotal(receiveTotal+payMentOne);//订单已收货款
						   orderHeaderMent.setBizStatus(OrderTransaction.FIRST_PAYMENT.getOrderId());//首付款支付5
						   bizOrderHeaderService.saveOrderHeader(orderHeaderMent);
					   }
					   payMent="ok";
				   }
			   }
		   }
       }catch(Exception e){
           e.printStackTrace();
           payMent="error";
       }finally {
       		if(bizCustCredit!=null){
				if(subtract.compareTo(new BigDecimal(0))==1){
				   bizPayRecordCredit.setPayer(UserUtils.getUser().getId());//支付人
				   bizPayRecordCredit.setPayMoney(payMentOne);//支付金额
				   bizPayRecordCredit.setPayNum(orderHeaderMent.getOrderNum());//订单编号
				   bizPayRecordCredit.setCustomer(orderHeaderMent.getCustomer());//采购商
				   if(recordBiz==1){
					   bizPayRecordCredit.setBizStatus(OrderTransaction.SUCCESS.getOrderId());//成功1
				   }else{
					   bizPayRecordCredit.setBizStatus(OrderTransaction.FAIL.getOrderId());//失败0
				   }
				   bizPayRecordCredit.setRecordType(OrderTransaction.PAYMENT.getOrderId());//交易类型
				   bizPayRecordCredit.setRecordTypeName(OrderTransaction.PAYMENT.getOrderName());//交易名称
				   bizPayRecordCredit.setPayType(OrderTransaction.PLATFORM_PAYMENT.getOrderId());//支付类型
				   bizPayRecordCredit.setPayTypeName(OrderTransaction.PLATFORM_PAYMENT.getOrderName());//类型名称
				   bizPayRecordService.save(bizPayRecordCredit);
				}
			}
	   }
	   return payMent;
    }



//    用于客户专员页面-订单管理列表中的待审核验证
	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "Commissioner")
	public String Commissioner(BizOrderHeader bizOrderHeader,String localSendIds,String boo, Integer objJsp, Model model, RedirectAttributes redirectAttributes) {
		String commis="comError";
		try {
			if(bizOrderHeader.getId()!=null){
				BizOrderHeader bh = bizOrderHeaderService.get(bizOrderHeader);
				if(bh!=null){
					if(objJsp==OrderHeaderBizStatusEnum.SUPPLYING.getState()){
						bh.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());//15供货中
						bizOrderHeaderService.saveOrderHeader(bh);//保存状态
						BizOrderAddress OrderAddressTwo = new BizOrderAddress();
						OrderAddressTwo.setOrderHeaderID(bh);
						List<BizOrderAddress> list = bizOrderAddressService.findList(OrderAddressTwo);
						for (BizOrderAddress bizOrderAddress : list) {
							if(bizOrderAddress.getType()==2){
								OrderAddressTwo.setId(bizOrderAddress.getId());
							}
						}
						OrderAddressTwo.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
						OrderAddressTwo.setProvince(bizOrderHeader.getBizLocation().getProvince());
						OrderAddressTwo.setRegion(bizOrderHeader.getBizLocation().getRegion());
						OrderAddressTwo.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
						OrderAddressTwo.setCity(bizOrderHeader.getBizLocation().getCity());
						OrderAddressTwo.setAddress(bizOrderHeader.getBizLocation().getAddress());
						OrderAddressTwo.setPhone(bizOrderHeader.getBizLocation().getPhone());
						OrderAddressTwo.setType(2);
						bizOrderAddressService.save(OrderAddressTwo);
						if(StringUtils.isNotBlank(localSendIds)&&StringUtils.isNotBlank(boo)){
							String[]sidArr=localSendIds.split(",");
							String[] booStr=boo.split(",");
								for(int i=0;i<sidArr.length;i++){
                                    BizOrderDetail bizOrderDetail= bizOrderDetailService.get(Integer.parseInt(sidArr[i].trim()));
								    if("false".equals(booStr[i].trim())){
                                        bizOrderDetail.setSuplyis(officeService.get(0));
                                    }else {
                                        BizOrderHeader orderHeader=bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                                        BizCustomCenterConsultant bizCustomCenterConsultant= bizCustomCenterConsultantService.get(orderHeader.getCustomer().getId());
                                        bizOrderDetail.setSuplyis(officeService.get(bizCustomCenterConsultant.getCenters().getId()));

                                    }
									bizOrderDetailService.saveStatus(bizOrderDetail);
								}
                        }
						commis="ok";
					}else if(objJsp==OrderHeaderBizStatusEnum.UNAPPROVE.getState()){
						bh.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());//45审核失败
						bizOrderHeaderService.saveOrderHeader(bh);//保存状态
						BizOrderAddress OrderAddressTwo = new BizOrderAddress();
						OrderAddressTwo.setOrderHeaderID(bh);
						List<BizOrderAddress> list = bizOrderAddressService.findList(OrderAddressTwo);
						for (BizOrderAddress bizOrderAddress : list) {
							if(bizOrderAddress.getType()==2){
								OrderAddressTwo.setId(bizOrderAddress.getId());
							}
						}
						OrderAddressTwo.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
						OrderAddressTwo.setProvince(bizOrderHeader.getBizLocation().getProvince());
						OrderAddressTwo.setRegion(bizOrderHeader.getBizLocation().getRegion());
						OrderAddressTwo.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
						OrderAddressTwo.setCity(bizOrderHeader.getBizLocation().getCity());
						OrderAddressTwo.setAddress(bizOrderHeader.getBizLocation().getAddress());
						OrderAddressTwo.setPhone(bizOrderHeader.getBizLocation().getPhone());
						OrderAddressTwo.setType(2);
						bizOrderAddressService.save(OrderAddressTwo);
						commis="ok";
					}
				}
			}
		}catch (Exception e){
			commis="comError";
			e.printStackTrace();
		}
		return commis;
	}
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "saveBizOrderHeader")
	public String saveBizOrderHeader(BizOrderHeader bizOrderHeader,Integer orderId,Double money){
        String flag="";
	    try {
	        BizOrderHeader orderHeader=bizOrderHeaderService.get(orderId);
            orderHeader.setTotalExp(money);
            bizOrderHeaderService.saveOrderHeader(orderHeader);
            flag="ok";

        }catch (Exception e){
	        flag="error";
            logger.error(e.getMessage());
        }
        return flag;

    }
}