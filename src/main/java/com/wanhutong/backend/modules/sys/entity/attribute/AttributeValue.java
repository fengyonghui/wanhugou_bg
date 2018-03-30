/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.attribute;

import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.util.List;

/**
 * 标签属性值Entity
 *
 * @author zx
 * @version 2018-03-21
 */
public class AttributeValue extends DataEntity<AttributeValue> {

    private static final long serialVersionUID = 1L;
    private Integer tagId;        // 标签Id
    private String objectName;        // 对象名称，表名称
    private Integer objectId;        // 对应表的主键
    private String value;        // 记录该属性值
    private String code;        // code

    /**
     * 关系属性，获取属性名
     */
    private AttributeInfo attributeInfo;

    public AttributeValue() {
        super();
    }

    public AttributeValue(Integer id) {
        super(id);
    }

    @Length(min = 0, max = 11, message = "标签Id长度必须介于 0 和 11 之间")
    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    @Length(min = 1, max = 30, message = "对象名称，表名称长度必须介于 1 和 30 之间")
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Length(min = 1, max = 11, message = "对应表的主键长度必须介于 1 和 11 之间")
    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    @Length(min = 1, max = 100, message = "记录该属性值长度必须介于 1 和 100 之间")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Length(min = 0, max = 20, message = "code长度必须介于 0 和 20 之间")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AttributeInfo getAttributeInfo() {
        return attributeInfo;
    }

    public void setAttributeInfo(AttributeInfo attributeInfo) {
        this.attributeInfo = attributeInfo;
    }
}