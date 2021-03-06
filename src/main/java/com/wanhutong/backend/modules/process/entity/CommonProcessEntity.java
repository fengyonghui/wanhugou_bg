/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.process.entity;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.DoOrderHeaderProcessAllConfig;
import com.wanhutong.backend.modules.config.parse.DoOrderHeaderProcessFifthConfig;
import com.wanhutong.backend.modules.config.parse.InventorySkuRequestProcessConfig;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.Process;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.TransferProcessConfig;
import com.wanhutong.backend.modules.config.parse.VendorRequestOrderProcessConfig;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;
import sun.security.krb5.Config;

/**
 * 通用流程Entity
 * @author Ma.Qiang
 * @version 2018-04-28
 */
public class CommonProcessEntity extends DataEntity<CommonProcessEntity> {

	private static final long serialVersionUID = 1L;
	public static final Integer CURRENT = 1;
	public static final Integer NOT_CURRENT = 0;
	private String objectId;		// object_id
	private String objectName;		// object_name
	private int prevId = 0;		// 前一个ID.起始为0
	private int bizStatus = 0;		// 处理结果 0:未处理 1:通过 2:驳回
	private String processor;		// 处理人
	private String description;		// 描述
	private String type;		// 类型, 对应JAVA中的枚举数据
	private Date createTime;		// 创建时间
	private User user;
	private Date updateTime;
	private Integer current;

	private com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess;
	private com.wanhutong.backend.modules.config.parse.Process purchaseOrderForOrderHeaderProcess;
	private RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess;
	private PaymentOrderProcessConfig.Process paymentOrderProcess;
	private VendorRequestOrderProcessConfig.RequestOrderProcess vendRequestOrderProcess;
	private DoOrderHeaderProcessAllConfig.OrderHeaderProcess doOrderHeaderProcessAll;
	private DoOrderHeaderProcessFifthConfig.OrderHeaderProcess doOrderHeaderProcessFifth;
	private com.wanhutong.backend.modules.config.parse.Process jointOperationLocalProcess;
	private com.wanhutong.backend.modules.config.parse.Process jointOperationOriginProcess;
	private com.wanhutong.backend.modules.config.parse.Process invRequestProcess;
	private com.wanhutong.backend.modules.config.parse.Process transferProcess;

	/**
	 * 前一个流程
	 */
	private CommonProcessEntity prevProcess;

	public CommonProcessEntity() {
		super();
	}

	public CommonProcessEntity(Integer id){
		super(id);
	}

	@Length(min=1, max=11, message="object_id长度必须介于 1 和 11 之间")
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Length(min=1, max=32, message="object_name长度必须介于 1 和 32 之间")
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	@Length(min=1, max=11, message="起始为0长度必须介于 1 和 11 之间")
	public int getPrevId() {
		return prevId;
	}

	public void setPrevId(int prevId) {
		this.prevId = prevId;
	}

	@Length(min=1, max=4, message="处理结果 0:未处理 1:通过 2:驳回 长度必须介于 1 和 4 之间")
	public int getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(int bizStatus) {
		this.bizStatus = bizStatus;
	}

	@Length(min=1, max=11, message="处理人长度必须介于 1 和 11 之间")
	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Length(min=1, max=11, message="类型, 对应JAVA中的枚举数据长度必须介于 1 和 11 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="创建时间不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public CommonProcessEntity getPrevProcess() {
		return prevProcess;
	}

	public void setPrevProcess(CommonProcessEntity prevProcess) {
		this.prevProcess = prevProcess;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public com.wanhutong.backend.modules.config.parse.Process getPurchaseOrderProcess() {
		if (StringUtils.isBlank(type)) {
			return new Process();
		}
		return ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(type));
	}

//	public com.wanhutong.backend.modules.config.parse.Process getPurchaseOrderForOrderHeaderProcess() {
//		return ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG_FOR_ORDER_HEADER.get().getProcessMap().get(Integer.valueOf(type));
//	}

	public PaymentOrderProcessConfig.Process getPaymentOrderProcess() {
		if (StringUtils.isBlank(type)) {
			return new PaymentOrderProcessConfig.Process();
		}
		return ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(type));
	}

	public com.wanhutong.backend.modules.config.parse.Process getJointOperationLocalProcess() {
		if (StringUtils.isBlank(type)) {
			return new Process();
		}
		return ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get().getProcessMap().get(Integer.valueOf(type));
	}

	public com.wanhutong.backend.modules.config.parse.Process getJointOperationOriginProcess() {
		if (StringUtils.isBlank(type)) {
			return new Process();
		}
		return ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap().get(Integer.valueOf(type));
	}

	public RequestOrderProcessConfig.RequestOrderProcess getRequestOrderProcess() {
		if (StringUtils.isBlank(type)) {
			return new RequestOrderProcessConfig.RequestOrderProcess();
		}
		return ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(type));
	}

	public Integer getCurrent() {
		return current;
	}

	public void setCurrent(Integer current) {
		this.current = current;
	}
	public VendorRequestOrderProcessConfig.RequestOrderProcess getVendRequestOrderProcess() {
		if (StringUtils.isBlank(type)) {
			return new VendorRequestOrderProcessConfig.RequestOrderProcess();
		}
		return ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(type));
	}

	public DoOrderHeaderProcessAllConfig.OrderHeaderProcess getDoOrderHeaderProcessAll() {
		if (StringUtils.isBlank(type)) {
			return new DoOrderHeaderProcessAllConfig.OrderHeaderProcess();
		}
		return ConfigGeneral.DO_ORDER_HEADER_PROCESS_All_CONFIG.get().processMap.get(Integer.valueOf(type));
	}

	public DoOrderHeaderProcessFifthConfig.OrderHeaderProcess getDoOrderHeaderProcessFifth() {
		if (StringUtils.isBlank(type)) {
			return new DoOrderHeaderProcessFifthConfig.OrderHeaderProcess();
		}
		return ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get().processMap.get(Integer.valueOf(type));
	}

	public InventorySkuRequestProcessConfig.InvRequestProcess getInvRequestProcess() {
		if (StringUtils.isBlank(type)) {
			return new InventorySkuRequestProcessConfig.InvRequestProcess();
		}
		return ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(type));
	}

	public TransferProcessConfig.TransferProcess getTransferProcess() {
		if (StringUtils.isBlank(type)) {
			return new TransferProcessConfig.TransferProcess();
		}
		return ConfigGeneral.SKU_TRANSFER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(type));
	}

	public enum AuditType {
		/**
		 * 通过
		 */
		PASS(1),
		/**
		 * 拒绝
		 */
		REJECT(2)
		;
		private int code;

		public int getCode() {
			return code;
		}

		AuditType(int code) {
			this.code = code;
		}

		public static AuditType parse(int code) {
			if (code == 1) {
				return PASS;
			}
			return REJECT;
		}
	}
}