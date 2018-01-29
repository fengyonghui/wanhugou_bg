/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTransaction;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.BizCustCredit;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.SysPlatWallet;
import com.wanhutong.backend.modules.sys.entity.User;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

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
	@Autowired
    private BizCustCreditService bizCustCreditService;

	@Autowired
	private BizPayRecordService bizPayRecordService;
	@Autowired
	private SysPlatWalletService sysPlatWalletService;

	@ModelAttribute
	public BizOrderHeader get(@RequestParam(required=false) Integer id) {
		BizOrderHeader entity = null;
		Double sum = 0.0;
		if (id!=null && id!=0){
			entity = bizOrderHeaderService.get(id);
			BizOrderDetail bizOrderDetail = new BizOrderDetail();
			bizOrderDetail.setOrderHeader(entity);
			List<BizOrderDetail> list = bizOrderDetailService.findList(bizOrderDetail);
			if(list.size()!=0){
				for (BizOrderDetail detail : list) {
					Double price = detail.getUnitPrice();//商品单价
					Integer ordQty = detail.getOrdQty();//采购数量
					if(price==null){
						price=0.0;
					}
					if(ordQty==null){
						ordQty=0;
					}
					sum+=price*ordQty;
				}
			}
			entity.setTotalDetail(sum);
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
//			客户专员跳转需要参数
// 			User user=UserUtils.getUser();
//			if("check_pending".equals(bizOrderHeader.getFlag())){
////			bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.UNPAY.getState());
////			bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.ALL_PAY.getState());
//			if(user.getId()==bizOrderHeader.getConsultantId()){
//				bizOrderHeader.setConsultantId(null);
//			}
//		}
		Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderHeaderList";
	}

	@RequiresPermissions("biz:order:bizOrderHeader:view")
	@RequestMapping(value = "form")
	public String form(BizOrderHeader bizOrderHeader, Model model,String orderNoEditable,String orderDetails) {
//		if(bizOrderHeader.getOrderDetailList()!=null){
//			bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail());
//		}else{
//			bizOrderHeader.setTotalDetail(0.0);
////			bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
//		}
		if(bizOrderHeader.getCustomer()!=null && bizOrderHeader.getCustomer().getId()!=null){
			Office office=officeService.get(bizOrderHeader.getCustomer().getId());
			bizOrderHeader.setCustomer(office);
			model.addAttribute("entity2", bizOrderHeader);
		}
		bizOrderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
		if(bizOrderHeader!=null){
			Double totalDetail = bizOrderHeader.getTotalDetail();//订单详情总价
			Double totalExp = bizOrderHeader.getTotalExp();//订单总费用
			Double freight = bizOrderHeader.getFreight();//运费
			Double orderHeaderTotal=totalDetail+totalExp+freight;
			bizOrderHeader.setTobePaid(orderHeaderTotal-bizOrderHeader.getReceiveTotal());//页面显示待支付总价
			if(orderNoEditable!=null && orderNoEditable.equals("editable") ){//不可编辑标识符
				bizOrderHeader.setOrderNoEditable("editable");//待支付页面不能修改
			}
			if(orderDetails!=null && orderDetails.equals("details")){
				bizOrderHeader.setOrderDetails("details");//查看详情页面不能修改
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
		if(bizOrderHeader.getTotalDetail()==null){
			bizOrderHeader.setTotalDetail(0.0);
		}
		if(bizOrderHeader.getPlatformInfo()==null){
			bizOrderHeader.getPlatformInfo().setId(1);
		}
		bizOrderHeaderService.save(bizOrderHeader);
		addMessage(redirectAttributes, "保存订单信息成功");
		Integer orId = bizOrderHeader.getId();
		String oneOrder = bizOrderHeader.getOneOrder();
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/?repage";
	//	return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderDetail/form?orderHeader.id="+orId+"&orderHeader.oneOrder="+oneOrder;
	}
	
	@RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderHeader bizOrderHeader,Model model ,RedirectAttributes redirectAttributes) {
		bizOrderHeaderService.delete(bizOrderHeader);
		addMessage(redirectAttributes, "删除订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/?repage&orderNum="+bizOrderHeader.getOrderNum();
	}
	
	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = "findByOrder")
	public List<BizOrderHeader> findByOrder(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
		return list;
	}

	@ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveOrderHeader")
    public String saveOrderHeader(BizOrderHeader bizOrderHeader,Double payMentOne, Model model, RedirectAttributes redirectAttributes) {
        BizOrderHeader orderHeaderMent = bizOrderHeaderService.get(bizOrderHeader.getId());
		Double receiveTotal = orderHeaderMent.getReceiveTotal();
		Double Totail=bizOrderHeader.getTobePaid();//获取From方法的计算总价
		BizPayRecord bizPayRecordCredit = new BizPayRecord();//保存客户钱包交易记录
		BizPayRecord bizPayRecordWallet = new BizPayRecord();//保存平台总钱包交易记录
		Integer customId = orderHeaderMent.getCustomer().getId();//采购商ID
		BizCustCredit bizCustCredit = bizCustCreditService.get(customId);//客户钱包
		BigDecimal subtract=null;
		Integer recordBiz=0;//0失败 1成功 支付记录表，支付状态
		String payMent="error";
       try {
		   if(bizCustCredit!=null){
			   BigDecimal wallet = bizCustCredit.getWallet();//钱包总余额
			   if(wallet==null){
				   System.out.println(" 余额不足 ");
				   recordBiz=0;
			   }else {
				   recordBiz=1;
				   BigDecimal payMentTwo = new BigDecimal(payMentOne);//输入支付的值
				   subtract = wallet.subtract(payMentTwo);//计算后结果
				   System.out.println(subtract.compareTo(new BigDecimal(0)));
				   //   以上输出结果是：-1小于、0等于、1大于
				   if(subtract.compareTo(new BigDecimal(0))==1){
					   bizCustCredit.setWallet(subtract);
					   bizCustCreditService.save(bizCustCredit);
					   SysPlatWallet sysPlatWallet = sysPlatWalletService.get(OrderTransaction.TOTAL_PURSE.getOrderId());//平台总钱包 1
					   sysPlatWallet.setAmount(sysPlatWallet.getAmount()+payMentOne);
					   sysPlatWalletService.save(sysPlatWallet);//保存到平台总钱包
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
	public String Commissioner(BizOrderHeader bizOrderHeader, Integer objJsp, Model model, RedirectAttributes redirectAttributes) {
		String commis="comError";
		try {
			if(bizOrderHeader.getId()!=null){
				BizOrderHeader bh = bizOrderHeaderService.get(bizOrderHeader);
				if(bh!=null){
					if(objJsp==OrderHeaderBizStatusEnum.SUPPLYING.getState()){
						bh.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());//15供货中
						bizOrderHeaderService.saveOrderHeader(bh);//保存状态
						commis="ok";
					}else if(objJsp==OrderHeaderBizStatusEnum.UNAPPROVE.getState()){
						bh.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());//45审核失败
						bizOrderHeaderService.saveOrderHeader(bh);//保存状态
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

}