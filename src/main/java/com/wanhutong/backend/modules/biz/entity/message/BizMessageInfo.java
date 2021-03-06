/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.message;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 站内信Entity
 * @author Ma.Qiang
 * @version 2018-07-27
 */
public class BizMessageInfo extends DataEntity<BizMessageInfo> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// 标题
	private String content;		// 内容
	private String url;		// url
	/**
	 * url状态
	 * 0：不是产品链接，1：是产品链接
	 */
	private Integer urlStatus;
	private String createName;		// create_name
	private Date createTime;		// create_time
	private User createId;		// create_id
	private User updateId;		// update_id
	private Date updateTime;		// update_time
	private String type;		// type

	private Integer companyId;

	private String companyName;

	/**
	 * 业务状态 0未发送；1已发送
	 */
	private Integer bizStatus;

	/**
	 * 发布时间
	 */
	private Date releaseTime;

	/**
	 * 按发布时间搜索，选择的起始时间
	 */
	private Date releaseStartTime;

	/**
	 * 按发布时间搜索，选择的终止时间
	 */
	private Date releaseEndTime;

    /**
     * 保存模式 保存暂不发送，保存并发送
     */
	private String saveType;

	/**
	 * 发送用户类型
	 */
	private List<Integer> companyIdTypeList;

	/**
	 * 编辑页面标识符
	 */
	private String str;

	private List<BizMessageOfficeType> bizMessageOfficeTypeList;

	public BizMessageInfo() {
		super();
	}

	public BizMessageInfo(Integer id){
		super(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public User getCreateId() {
		return createId;
	}

	public void setCreateId(User createId) {
		this.createId = createId;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
	
	public User getUpdateId() {
		return updateId;
	}

	public void setUpdateId(User updateId) {
		this.updateId = updateId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Date getReleaseStartTime() {
		return releaseStartTime;
	}

	public void setReleaseStartTime(Date releaseStartTime) {
		this.releaseStartTime = releaseStartTime;
	}

	public Date getReleaseEndTime() {
		return releaseEndTime;
	}

	public void setReleaseEndTime(Date releaseEndTime) {
		this.releaseEndTime = releaseEndTime;
	}

    public String getSaveType() {
        return saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    public enum BizStatus {
	    NO_SEND(0, "未发送"),
        SEND_COMPLETE(1, "已发送");

	    private int status;
	    private String desc;

	    BizStatus(int status, String desc) {
	        this.status = status;
	        this.desc = desc;
        }

        public int getStatus() {
            return status;
        }

        public String getDesc() {
            return desc;
        }
    }

	public Integer getUrlStatus() {
		return urlStatus;
	}

	public void setUrlStatus(Integer urlStatus) {
		this.urlStatus = urlStatus;
	}

	public List<Integer> getCompanyIdTypeList() {
		return companyIdTypeList;
	}

	public void setCompanyIdTypeList(List<Integer> companyIdTypeList) {
		this.companyIdTypeList = companyIdTypeList;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public List<BizMessageOfficeType> getBizMessageOfficeTypeList() {
		return bizMessageOfficeTypeList;
	}

	public void setBizMessageOfficeTypeList(List<BizMessageOfficeType> bizMessageOfficeTypeList) {
		this.bizMessageOfficeTypeList = bizMessageOfficeTypeList;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}