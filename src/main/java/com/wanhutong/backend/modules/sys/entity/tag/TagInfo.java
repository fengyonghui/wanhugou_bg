/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.tag;

import com.wanhutong.backend.modules.sys.entity.Dict;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.util.List;

/**
 * 标签属性表Entity
 * @author zx
 * @version 2018-03-19
 */

/**
 * name: 标签名称；
 *dict: sys_dict.id; 非0，可选值由字典表取； 0:此标签需要输入；
 *level：0:系统标签; 1:产品标签; 2:商品标签
 */
public class TagInfo extends DataEntity<TagInfo> {

	private static final long serialVersionUID = 1L;
	private String name;
	private Dict dict;
	private String level;
	private List<Dict> dictList;
	
	public TagInfo() {
		super();
	}

	public TagInfo(Integer id){
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

	@Length(min=1, max=4, message="0:系统标签; 1:产品标签; 2:商品标签长度必须介于 1 和 4 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}


	public List<Dict> getDictList() {
		return dictList;
	}

	public void setDictList(List<Dict> dictList) {
		this.dictList = dictList;
	}
}