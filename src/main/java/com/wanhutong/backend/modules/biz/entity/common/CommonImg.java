/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.common;

import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import net.sf.ehcache.util.ProductInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 通用图片Entity
 * @author zx
 * @version 2017-12-18
 */
public class CommonImg extends DataEntity<CommonImg> {
	
	private static final long serialVersionUID = 1L;
	private BizProductInfo bizProductInfo;
	private String objectName;		// 对象名称，表名称
	private String objectId;		// 对应表的主键
	private String imgType;		// 图片类型
	private String imgSort;		// 排序
	private String imgServer;		// 图片服务器地址
	private String imgPath;		// 图片路径
	private String imgLink;		// 图片连接地址
	private String comment;		// 描述
	private String img;  //图片用来连接服务器地址

	public CommonImg() {
		super();
	}

	public CommonImg(Integer id){
		super(id);
	}

	public BizProductInfo getBizProductInfo() {
		return bizProductInfo;
	}

	public void setBizProductInfo(BizProductInfo bizProductInfo) {
		this.bizProductInfo = bizProductInfo;
	}

	@Length(min=1, max=25, message="对象名称，表名称长度必须介于 1 和 25 之间")
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	@Length(min=1, max=11, message="对应表的主键长度必须介于 1 和 11 之间")
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	@Length(min=1, max=4, message="图片类型长度必须介于 1 和 4 之间")
	public String getImgType() {
		return imgType;
	}

	public void setImgType(String imgType) {
		this.imgType = imgType;
	}
	
	@Length(min=1, max=11, message="排序长度必须介于 1 和 11 之间")
	public String getImgSort() {
		return imgSort;
	}

	public void setImgSort(String imgSort) {
		this.imgSort = imgSort;
	}
	
	@Length(min=1, max=100, message="图片服务器地址长度必须介于 1 和 100 之间")
	public String getImgServer() {
		return imgServer;
	}

	public void setImgServer(String imgServer) {
		this.imgServer = imgServer;
	}
	
	@Length(min=1, max=255, message="图片路径长度必须介于 1 和 255 之间")
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	@Length(min=0, max=500, message="图片连接地址长度必须介于 0 和 500 之间")
	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}
	
	@Length(min=0, max=255, message="描述长度必须介于 0 和 255 之间")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
}