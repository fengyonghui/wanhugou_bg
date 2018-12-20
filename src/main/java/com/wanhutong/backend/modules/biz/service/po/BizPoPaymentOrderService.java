/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wanhutong.backend.common.thread.ThreadPoolManager;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.dao.po.BizPoPaymentOrderDao;

import javax.annotation.Resource;

/**
 * 采购付款单Service
 * @author Ma.Qiang
 * @version 2018-05-04
 */
@Service
@Transactional(readOnly = true)
public class BizPoPaymentOrderService extends CrudService<BizPoPaymentOrderDao, BizPoPaymentOrder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizPoPaymentOrderService.class);

	@Autowired
	private CommonProcessService commonProcessService;
	@Resource
	private CommonImgService commonImgService;

	private Lock lock = new ReentrantLock();

	public static final String DATABASE_TABLE_NAME = "biz_po_payment_order";






	@Override
	public BizPoPaymentOrder get(Integer id) {
		return super.get(id);
	}
	@Override
	public List<BizPoPaymentOrder> findList(BizPoPaymentOrder bizPoPaymentOrder) {
		return super.findList(bizPoPaymentOrder);
	}
	@Override
	public Page<BizPoPaymentOrder> findPage(Page<BizPoPaymentOrder> page, BizPoPaymentOrder bizPoPaymentOrder) {
		return super.findPage(page, bizPoPaymentOrder);
	}
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(BizPoPaymentOrder bizPoPaymentOrder) {
		super.save(bizPoPaymentOrder);
		saveImg(bizPoPaymentOrder);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveImg(BizPoPaymentOrder bizPoPaymentOrder) {
		String imgStr = bizPoPaymentOrder.getImg();
		if (StringUtils.isBlank(imgStr)) {
			return;
		}
		String[] split = imgStr.split(",");

		for (int i = 0; i < split.length; i++) {
			String img = split[i];
			if (StringUtils.isNotBlank(img)) {
				CommonImg commonImg = new CommonImg();
				commonImg.setObjectName(DATABASE_TABLE_NAME);
				commonImg.setObjectId(bizPoPaymentOrder.getId());
				commonImg.setImgType(ImgEnum.BIZ_PO_HEADER_PAY_OFFLINE.getCode());
				commonImg.setImg(img);
				commonImg.setImgSort(i);
				commonImg.setImgServer(StringUtils.isBlank(img) ? StringUtils.EMPTY : DsConfig.getImgServer());
				commonImg.setImgPath(img.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY));
				commonImgService.save(commonImg);
			}
		}
	}


	
	@Transactional(readOnly = false)
	@Override
	public void delete(BizPoPaymentOrder bizPoPaymentOrder) {
		super.delete(bizPoPaymentOrder);
	}



	/**
	 * @param paymentOrderId
	 * @param currentType
	 * @param auditType
	 * @param description
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String audit(int paymentOrderId, String currentType, int auditType, String description) {
		BizPoPaymentOrder bizPoPaymentOrder = this.get(paymentOrderId);
		CommonProcessEntity cureentProcessEntity = bizPoPaymentOrder.getCommonProcess();
		if (cureentProcessEntity == null) {
			return "操作失败,当前订单无审核状态!";
		}
		cureentProcessEntity = commonProcessService.get(bizPoPaymentOrder.getCommonProcess().getId());
		if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
			LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", paymentOrderId, currentType);
			return "操作失败,当前审核状态异常!";
		}

		PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
		// 当前流程
		com.wanhutong.backend.modules.config.parse.Process currentProcess = purchaseOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
		// 下一流程
		com.wanhutong.backend.modules.config.parse.Process nextProcess = purchaseOrderProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
		if (nextProcess == null) {
			return "操作失败,当前流程已经结束!";
		}


		User user = UserUtils.getUser();
		List<String> roleEnNameEnumList = currentProcess.getRoleEnNameEnum();
		boolean hasRole = false;
		for (String s : roleEnNameEnumList) {
			RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
			Role role = new Role();
			role.setEnname(roleEnNameEnum.getState());
			if (user.getRoleList().contains(role)) {
				hasRole = true;
				break;
			}
		}

		if (!user.isAdmin() && !hasRole) {
			return "操作失败,该用户没有权限!";
		}

		if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
			return "请输入驳回理由!";
		}

		cureentProcessEntity.setBizStatus(auditType);
		cureentProcessEntity.setProcessor(user.getId().toString());
		cureentProcessEntity.setDescription(description);
		commonProcessService.save(cureentProcessEntity);

		CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
		nextProcessEntity.setObjectId(bizPoPaymentOrder.getId().toString());
		nextProcessEntity.setObjectName(DATABASE_TABLE_NAME);
		nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
		nextProcessEntity.setPrevId(cureentProcessEntity.getId());
		commonProcessService.save(nextProcessEntity);
		this.updateProcessId(bizPoPaymentOrder.getId(), nextProcessEntity.getId());

		return "操作成功!";
	}


	/**
	 * 更新流程ID
	 * @param paymentId
	 * @param processId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateProcessId(int paymentId, int processId) {
		return dao.updateProcessId(paymentId, processId);
	}

	public List<BizPoPaymentOrder> getPayMentOrderByReqId(Integer reqId) {
		return dao.getPayMentOrderByReqId(reqId);
	}

	public int findCount(BizPoPaymentOrder bizPoPaymentOrder) {
		return dao.findCount(bizPoPaymentOrder);
	}

	/**
	 * 更新BizPoPaymentOrder审核按钮控制flag
	 * @param page
	 * @return
	 */
	public void updateHasRole(Page<BizPoPaymentOrder> page) {
		PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
		User user = UserUtils.getUser();
		List<Callable<Pair<Boolean, String>>> tasks = new ArrayList<>();
		for (BizPoPaymentOrder b : page.getList()) {
			tasks.add(new Callable<Pair<Boolean, String>>() {
				@Override
				public Pair<Boolean, String> call() {
					boolean hasRole = false;
					try {
						lock.lock();
						int currentType = b.getCommonProcess().getPaymentOrderProcess().getCode();
						PaymentOrderProcessConfig.Process currentProcess = paymentOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
						BigDecimal money = b.getTotal();
						if (currentType != 666 && currentType !=-1 && money.compareTo(BigDecimal.ZERO) > 0) {
							List<PaymentOrderProcessConfig.MoneyRole> moneyRoleList = currentProcess.getMoneyRole();
							PaymentOrderProcessConfig.MoneyRole moneyRole = null;
							for (PaymentOrderProcessConfig.MoneyRole role : moneyRoleList) {
								if (role.getEndMoney().compareTo(money) > 0 && role.getStartMoney().compareTo(money) <= 0) {
									moneyRole = role;
								}
							}
							if (user.isAdmin()) {
								hasRole = true;
							} else {
								for (String s : moneyRole.getRoleEnNameEnum()) {
									RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
									Role role = new Role();
									role.setEnname(roleEnNameEnum.getState());
									if (user.getRoleList().contains(role)) {
										hasRole = true;
										break;
									}
								}
							}
						}
					} catch (Exception e) {
						logger.error("多线程取付款单列表失败", e);
					} finally {
						lock.unlock();
					}
					b.setHasRole(hasRole);
					return Pair.of(Boolean.TRUE, "操作成功");
				}
			});
		}
		try {
			ThreadPoolManager.getDefaultThreadPool().invokeAll(tasks);
		} catch (InterruptedException e) {
			LOGGER.error("init order list data error", e);
		}
	}
}