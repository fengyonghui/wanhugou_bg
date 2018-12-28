/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shelf;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValue;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.User;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 商品上架管理Entity
 * @author liuying
 * @version 2017-12-19
 */
public class BizOpShelfSku extends DataEntity<BizOpShelfSku> {
	
	private static final long serialVersionUID = 1L;
	private BizOpShelfInfo opShelfInfo;		// 货架ID
	private BizSkuInfo skuInfo;		// 上架商品ID
	private BizProductInfo productInfo; //prod_id  上架产品【SPU】ID
	private Office centerOffice;		// 采购中心ID（sys_office.id） 0:代表平台商品
	private User shelfUser;		// 上架人
	private Integer shelfQty;		// 上架数量
	private Double orgPrice;		// 原价
	private Double salePrice;		// 销售单价-现价
	private Integer minQty;		// 此单价所对应的最低销售数量；
	private Integer maxQty;		// 此单价所对应的最高销售数量；9999：不限制
	private Date shelfTime;		// 上架时间
	private User unshelfUser;		// 下架人
	private Date unshelfTime;		// 下架时间
	private Integer priority;		// 显示次序

	private String udshelf;		//上下架按钮状态

	private Date shelfStartTime;  //上架开始时间
	private Date shelfEndTime;  //上架结束时间
	private List<AttributeValueV2> skuValueList;		//查询sku下有多少属性值


	private Date unShelfStartTime; //下架查询开始时间
	private Date unShelfEndTime; //下架查询结束时间

	private int shelfSign; //货架删除返回标志

	private SkuProd skuProd;
	private String skuIds;
	private List<BizSkuInfo> skuInfoList = Lists.newArrayList();

	/**
	 * C端商品上下架
	 * */
	private String cendShelf;

    /**
     * 批量下架的上架商品ID
     */
	private String opShelfSkuIds;

	/**
	 * 用来筛选查询未下架的商品
	 */
	private String batchDownShelf;

	/**
	 * 市场参考价
	 */
	private BigDecimal marketPrice;

    /**
     * 佣金比（百分比）存入百分号之前的数值
     */
	private BigDecimal commissionRatio;

	/**
	 * 订单详情搜索商品标识符
	 */
	private String orderDetailForm;

	public BizOpShelfSku() {
		super();
	}

	public BizOpShelfSku(Integer id){
		super(id);
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="上架时间不能为空")
	public Date getShelfTime() {
		return shelfTime;
	}

	/**
	 * 商品已删除标识符
	 */
	private String skuDeleteFlag;

	public void setShelfTime(Date shelfTime) {
		this.shelfTime = shelfTime;
	}

	public BizOpShelfInfo getOpShelfInfo() {
		return opShelfInfo;
	}

	public void setOpShelfInfo(BizOpShelfInfo opShelfInfo) {
		this.opShelfInfo = opShelfInfo;
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public Office getCenterOffice() {
		return centerOffice;
	}

	public void setCenterOffice(Office centerOffice) {
		this.centerOffice = centerOffice;
	}

	public User getShelfUser() {
		return shelfUser;
	}

	public void setShelfUser(User shelfUser) {
		this.shelfUser = shelfUser;
	}

	public Integer getShelfQty() {
		return shelfQty;
	}

	public void setShelfQty(Integer shelfQty) {
		this.shelfQty = shelfQty;
	}

	public Double getOrgPrice() {
		return orgPrice;
	}

	public void setOrgPrice(Double orgPrice) {
		this.orgPrice = orgPrice;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public Integer getMinQty() {
		return minQty;
	}

	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}

	public Integer getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(Integer maxQty) {
		this.maxQty = maxQty;
	}

	public User getUnshelfUser() {
		return unshelfUser;
	}

	public void setUnshelfUser(User unshelfUser) {
		this.unshelfUser = unshelfUser;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUnshelfTime() {
		return unshelfTime;
	}

	public void setUnshelfTime(Date unshelfTime) {
		this.unshelfTime = unshelfTime;
	}

	public Date getShelfStartTime() {
		return shelfStartTime;
	}

	public void setShelfStartTime(Date shelfStartTime) {
		this.shelfStartTime = shelfStartTime;
	}

	public Date getShelfEndTime() {
		return shelfEndTime;
	}

	public void setShelfEndTime(Date shelfEndTime) {
		this.shelfEndTime = shelfEndTime;
	}

	public Date getUnShelfStartTime() {
		return unShelfStartTime;
	}

	public void setUnShelfStartTime(Date unShelfStartTime) {
		this.unShelfStartTime = unShelfStartTime;
	}

	public Date getUnShelfEndTime() {
		return unShelfEndTime;
	}

	public void setUnShelfEndTime(Date unShelfEndTime) {
		this.unShelfEndTime = unShelfEndTime;
	}

	public int getShelfSign() {
		return shelfSign;
	}

	public void setShelfSign(int shelfSign) {
		this.shelfSign = shelfSign;
	}

	public SkuProd getSkuProd() {
		return skuProd;
	}

	public void setSkuProd(SkuProd skuProd) {
		this.skuProd = skuProd;
	}

	public BizProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(BizProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	public String getSkuIds() {
		return skuIds;
	}

	public void setSkuIds(String skuIds) {
		this.skuIds = skuIds;
	}

	public List<BizSkuInfo> getSkuInfoList() {
		return skuInfoList;
	}

	public void setSkuInfoList(List<BizSkuInfo> skuInfoList) {
		this.skuInfoList = skuInfoList;
	}

	public List<AttributeValueV2> getSkuValueList() {
		return skuValueList;
	}

	public void setSkuValueList(List<AttributeValueV2> skuValueList) {
		this.skuValueList = skuValueList;
	}

	public String getUdshelf() {
		return udshelf;
	}

	public void setUdshelf(String udshelf) {
		this.udshelf = udshelf;
	}

	public String getCendShelf() {
		return cendShelf;
	}

	public void setCendShelf(String cendShelf) {
		this.cendShelf = cendShelf;
	}

    public String getOpShelfSkuIds() {
        return opShelfSkuIds;
    }

    public void setOpShelfSkuIds(String opShelfSkuIds) {
        this.opShelfSkuIds = opShelfSkuIds;
    }

	public String getBatchDownShelf() {
		return batchDownShelf;
	}

	public void setBatchDownShelf(String batchDownShelf) {
		this.batchDownShelf = batchDownShelf;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

    public BigDecimal getCommissionRatio() {
        return commissionRatio;
    }

    public void setCommissionRatio(BigDecimal commissionRatio) {
        this.commissionRatio = commissionRatio;
    }

	public String getOrderDetailForm() {
		return orderDetailForm;
	}

	public void setOrderDetailForm(String orderDetailForm) {
		this.orderDetailForm = orderDetailForm;
	}

	public String getSkuDeleteFlag() {
		return skuDeleteFlag;
	}

	public void setSkuDeleteFlag(String skuDeleteFlag) {
		this.skuDeleteFlag = skuDeleteFlag;
	}
}