package com.wanhutong.backend.modules.config.dao;


import com.wanhutong.backend.modules.config.entity.DynamicConfigBean;

import java.util.List;
import java.util.Map;

public interface DynamicConfigDao {

	/**
	 * 增加配置文件
	 * 
	 * @param confName
	 *            配置文件名称
	 * @param content
	 *            配置文件内容
	 */
	void insertConfig(String confName, String content);

	/**
	 * 获取所有配置文件的版本号
	 * 
	 * @return
	 */
	Map<String, Long> queryAllConfigVersion();

	/**
	 * 获取一个配置文件
	 * 
	 * @param confName
	 *            配置文件名称
	 * @return
	 */
	DynamicConfigBean getDynamicConfigBean(String confName);

	/**
	 * 根据名称模糊查询所有配置文件
	 * 
	 * @param dynamicName
	 * @return
	 */
	List<DynamicConfigBean> queryDynamicConfByName(String dynamicName);

	/**
	 * 根据文件名和版本号下载指定配置文件
	 * 
	 * @param fileName
	 * @param version
	 * @return
	 */
	DynamicConfigBean getDynamicConfigBeanByVersion(String fileName, String version);

}
