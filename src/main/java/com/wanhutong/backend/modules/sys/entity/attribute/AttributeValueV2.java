/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.attribute;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Dict;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 标签属性值Entity
 * @author zx
 * @version 2018-03-21
 */
public class AttributeValueV2 extends DataEntity<AttributeValueV2> {

	private static final long serialVersionUID = 1L;
	private Integer attrId;		// 标签Id
	private AttributeInfoV2 attributeInfo;		// 标签Id
	private String objectName;		// 对象名称，表名称
	private Integer objectId;		// 对应表的主键
	private String value;		// 记录该属性值
	private String code;		// code
    private List<Dict> dictList;        //用于存放特有属性列表

	public Integer getAttrId() {
		return attrId;
	}

	public void setAttrId(Integer attrId) {
		this.attrId = attrId;
	}

	public AttributeValueV2() {
		super();
	}

	public AttributeValueV2(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="对象名称，表名称长度必须介于 1 和 30 之间")
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	
	@Length(min=1, max=100, message="记录该属性值长度必须介于 1 和 100 之间")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Length(min=0, max=20, message="code长度必须介于 0 和 20 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public AttributeInfoV2 getAttributeInfo() {
		return attributeInfo;
	}

	public void setAttributeInfo(AttributeInfoV2 attributeInfo) {
		this.attributeInfo = attributeInfo;
	}

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

    public List<Dict> getDictList() {
        return dictList;
    }

    public void setDictList(List<Dict> dictList) {
        this.dictList = dictList;
    }
}