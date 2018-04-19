/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.attribute;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Dict;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 标签属性Entity
 * @author zx
 * @version 2018-03-21
 */

/**
 * name: 标签名称；
 *dict: sys_dict.id; 非0，可选值由字典表取； 0:此标签需要输入；
 *level：0:系统标签; 1:产品标签; 2:商品标签
 */
public class AttributeInfoV2 extends DataEntity<AttributeInfoV2> {

	private static final long serialVersionUID = 1L;
	private String name;
	private Dict dict;
	private Integer level;
	private List<Dict> dictList;
	private List<AttributeValue> attributeValueList;

	public AttributeInfoV2() {
		super();
	}

	public AttributeInfoV2(Integer id){
		super(id);
	}

	@Length(min=1, max=255, message="标签名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Dict getDict() {
		return dict;
	}

	public void setDict(Dict dict) {
		this.dict = dict;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<Dict> getDictList() {
		return dictList;
	}

	public void setDictList(List<Dict> dictList) {
		this.dictList = dictList;
	}

	public List<AttributeValue> getAttributeValueList() {
		return attributeValueList;
	}

	public void setAttributeValueList(List<AttributeValue> attributeValueList) {
		this.attributeValueList = attributeValueList;
	}

	public enum Level {
		SYS(0, "系统标签", ""),
		PRODUCT(1, "产品标签", "biz_product_info"),
		SKU(2, "商品标签", "biz_sku_info"),

		PRODUCT_FOR_VENDOR(3, "供应商产品标签", "biz_vendor_product_info"),
		SKU_FOR_VENDOR(4, "供应商商品标签", "biz_vendor_sku_info"),
		;
		private int level;
		private String desc;
		private String tableName;

		public int getLevel() {
			return level;
		}

		public String getDesc() {
			return desc;
		}

		public String getTableName() {
			return tableName;
		}

		Level(int level, String desc, String tableName) {
			this.level = level;
			this.desc = desc;
			this.tableName = tableName;
		}
	}
}