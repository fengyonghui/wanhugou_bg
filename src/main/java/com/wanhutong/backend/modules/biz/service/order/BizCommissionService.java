/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizCommissionOrder;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.dao.order.BizCommissionDao;

import javax.annotation.Resource;

/**
 * 佣金付款表Service
 * @author wangby
 * @version 2018-10-18
 */
@Service
@Transactional(readOnly = true)
public class BizCommissionService extends CrudService<BizCommissionDao, BizCommission> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizCommissionService.class);
	@Autowired
	private CommonProcessService commonProcessService;

	@Autowired
	private BizCommissionOrderService bizCommissionOrderService;

	@Resource
	private CommonImgService commonImgService;

	@Autowired
	private SystemService systemService;

	public static final String DATABASE_TABLE_NAME = "biz_commission";

	public BizCommission get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCommission> findList(BizCommission bizCommission) {
		return super.findList(bizCommission);
	}
	
	public Page<BizCommission> findPage(Page<BizCommission> page, BizCommission bizCommission) {
		return super.findPage(page, bizCommission);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCommission bizCommission) {
		super.save(bizCommission);
		saveImg(bizCommission);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCommission bizCommission) {
		super.delete(bizCommission);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveImg(BizCommission bizCommission) {
		String imgStr = bizCommission.getImgUrl();
		if (StringUtils.isBlank(imgStr)) {
			return;
		}
		String[] split = imgStr.split(",");

		for (int i = 0; i < split.length; i++) {
			String img = split[i];
			if (StringUtils.isNotBlank(img)) {
				CommonImg commonImg = new CommonImg();
				commonImg.setObjectName(DATABASE_TABLE_NAME);
				commonImg.setObjectId(bizCommission.getId());
				commonImg.setImgType(ImgEnum.BIZ_COMMISSION_HEADER_PAY_OFFLINE.getCode());
				commonImg.setImg(img);
				commonImg.setImgSort(i);
				commonImg.setImgServer(StringUtils.isBlank(img) ? StringUtils.EMPTY : DsConfig.getImgServer());
				commonImg.setImgPath(img.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY));
				commonImgService.save(commonImg);
			}
		}
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Pair<Boolean, String> createCommissionOrder(BizCommission bizCommission, String orderId){
		PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
		PaymentOrderProcessConfig.Process purchaseOrderProcess = null;
		if (paymentOrderProcessConfig.getDefaultBaseMoney().compareTo(bizCommission.getTotalCommission()) > 0) {
			purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getPayProcessId());
		} else {
			purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getDefaultProcessId());
		}

		CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
		commonProcessEntity.setObjectId("0");
		commonProcessEntity.setObjectName(BizCommissionService.DATABASE_TABLE_NAME);
		commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
		commonProcessEntity.setCurrent(1);
		commonProcessService.save(commonProcessEntity);

		bizCommission.setBizStatus(BizCommission.BizStatus.NO_PAY.getStatus());
		this.save(bizCommission);
		commonProcessEntity.setObjectId(String.valueOf(bizCommission.getId()));


		BizCommissionOrder bizCommissionOrder = new BizCommissionOrder();
		bizCommissionOrder.setOrderId(Integer.valueOf(orderId));
		bizCommissionOrder.setCommId(bizCommission.getId());
		bizCommissionOrder.setCommission(bizCommission.getTotalCommission());
		bizCommissionOrderService.save(bizCommissionOrder);

		return Pair.of(Boolean.TRUE, "操作成功!");
	}

	/**
	 * 审批支付单
	 *
	 * @param id
	 * @param currentType
	 * @param auditType
	 * @param description
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Pair<Boolean, String> auditPay(Integer id, String currentType, int auditType, String description, BigDecimal money) {
		BizCommission bizCommission = this.get(id);

		CommonProcessEntity cureentProcessEntity = bizCommission.getCommonProcess();
		if (cureentProcessEntity == null) {
			return Pair.of(Boolean.FALSE,  "操作失败,当前订单无审核状态!");
		}
		cureentProcessEntity = commonProcessService.get(cureentProcessEntity.getId());
		if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
			LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", id, currentType);
			return Pair.of(Boolean.FALSE,   "操作失败,当前审核状态异常!");
		}

		PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
		// 当前流程
		PaymentOrderProcessConfig.Process currentProcess = paymentOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
		// 下一流程
		PaymentOrderProcessConfig.Process nextProcess = CommonProcessEntity.AuditType.PASS.getCode() == auditType ?
				paymentOrderProcessConfig.getPassProcess(money, currentProcess) : paymentOrderProcessConfig.getRejectProcess(money, currentProcess);
		if (nextProcess == null) {
			return Pair.of(Boolean.FALSE,   "操作失败,当前流程已经结束!");
		}

		User user = UserUtils.getUser();
		List<PaymentOrderProcessConfig.MoneyRole> moneyRoleList = currentProcess.getMoneyRole();
		PaymentOrderProcessConfig.MoneyRole moneyRole = null;
		for (PaymentOrderProcessConfig.MoneyRole role : moneyRoleList) {
			if (role.getEndMoney().compareTo(money) > 0 && role.getStartMoney().compareTo(money) <= 0) {
				moneyRole = role;
			}
		}
		if (moneyRole == null) {
			return Pair.of(Boolean.FALSE,"操作失败,当前流程无审批人,请联系技术部!");
		}

		boolean hasRole = false;
		for (String s : moneyRole.getRoleEnNameEnum()) {
			RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
			Role role = new Role();
			role.setEnname(roleEnNameEnum.getState());
			if (user.getRoleList().contains(role)) {
				hasRole = true;
				break;
			}
		}

		if (!user.isAdmin() && !hasRole) {
			return Pair.of(Boolean.FALSE,"操作失败,该用户没有权限!");
		}

		if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
			return Pair.of(Boolean.FALSE,"请输入驳回理由!");
		}

		cureentProcessEntity.setBizStatus(auditType);
		cureentProcessEntity.setProcessor(user.getId().toString());
		cureentProcessEntity.setDescription(description);
		cureentProcessEntity.setCurrent(0);
		commonProcessService.save(cureentProcessEntity);

		CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
		nextProcessEntity.setObjectId(String.valueOf(id));
		nextProcessEntity.setObjectName(DATABASE_TABLE_NAME);
		nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
		nextProcessEntity.setPrevId(cureentProcessEntity.getId());
		nextProcessEntity.setCurrent(1);
		commonProcessService.save(nextProcessEntity);

		try {
			List<PaymentOrderProcessConfig.MoneyRole> nextMoneyRoleList = nextProcess.getMoneyRole();
			PaymentOrderProcessConfig.MoneyRole nextMoneyRole = null;
			for (PaymentOrderProcessConfig.MoneyRole role : nextMoneyRoleList) {
				if (role.getEndMoney() != null && role.getStartMoney() != null && role.getEndMoney().compareTo(money) > 0 && role.getStartMoney().compareTo(money) <= 0) {
					nextMoneyRole = role;
				}
			}

			if (nextMoneyRole != null) {
				StringBuilder phone = new StringBuilder();
				for (String s : nextMoneyRole.getRoleEnNameEnum()) {
					List<User> userList = systemService.findUser(new User(systemService.getRoleByEnname(s)));
					if (CollectionUtils.isNotEmpty(userList)) {
						for (User u : userList) {
							phone.append(u.getMobile()).append(",");
						}
					}
				}

				if (StringUtils.isNotBlank(phone.toString())) {
					AliyunSmsClient.getInstance().sendSMS(
							SmsTemplateCode.PENDING_AUDIT_1.getCode(),
							phone.toString(),
							ImmutableMap.of("order", "零售代佣单支付", "commId", String.valueOf(id)));
				}

			}
		} catch (Exception e) {
			LOGGER.error("[exception]PO支付审批短信提醒发送异常[commId:{}]", id, e);
			EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
			AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
					String.format(email.getBody(),
							"BizPoHeaderService:703",
							e.toString(),
							"PO支付审批短信提醒发送异常",
							LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
					));
		}

		return Pair.of(Boolean.TRUE, "操作成功!");
	}

	/**
	 * 支付订单
	 *
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String payOrder(Integer commId, String img, String remark) {
		// 当前流程
		User user = UserUtils.getUser();
		Role role = new Role();
		role.setEnname(RoleEnNameEnum.FINANCE.getState());
		Role role1 = new Role();
		role1.setEnname(RoleEnNameEnum.TELLER.getState());
		if (!user.isAdmin() && !user.getRoleList().contains(role) && !user.getRoleList().contains(role1)) {
			return "操作失败,该用户没有权限!";
		}

		BizCommission bizCommission = this.get(commId);
		if (bizCommission.getBizStatus() != BizCommission.BizStatus.NO_PAY.getStatus()) {
			LOGGER.warn("[exception]BizCommissionController payOrder currentType mismatching [{}]", commId);
			return "操作失败,当前状态有误!";
		}

		bizCommission.setImgUrl(img);
		bizCommission.setBizStatus(BizCommission.BizStatus.ALL_PAY.getStatus());
		bizCommission.setPayTime(new Date());
		bizCommission.setRemark(remark);
		this.save(bizCommission);


		return "操作成功!";
	}
}