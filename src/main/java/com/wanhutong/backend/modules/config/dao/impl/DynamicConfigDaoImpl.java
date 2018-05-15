package com.wanhutong.backend.modules.config.dao.impl;

import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.wanhutong.backend.modules.config.dao.DynamicConfigDao;
import com.wanhutong.backend.modules.config.entity.DynamicConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("dynamicConfigDaoImpl")
public class DynamicConfigDaoImpl implements DynamicConfigDao {

	@Autowired
	private NamedParameterJdbcTemplate whtJdbcTemplate;

	@Override
	public void insertConfig(String confName, String content) {
		Integer maxVersion = whtJdbcTemplate.getJdbcOperations().queryForObject(
				"select max(version) from common_dynamic_config where confName = ?", Integer.class, confName);
		int currentVersion = maxVersion == null ? 1 : maxVersion + 1;
		whtJdbcTemplate.getJdbcOperations().update(
				"insert into common_dynamic_config(confName,content,create_time,status,version)" + "values(?,?,?,?,?)", confName,
				content, new Date(), 1, currentVersion);
	}

	@Override
	public Map<String, Long> queryAllConfigVersion() {
		List<Map<String, Object>> results = whtJdbcTemplate.getJdbcOperations()
				.queryForList("SELECT confName,max(version) version FROM common_dynamic_config group by confName");
		Map<String, Long> configVersion = Maps.newHashMapWithExpectedSize(results.size());
		for (Map<String, Object> result : results) {
			configVersion.put(result.get("confName").toString(), Longs.tryParse(result.get("version").toString()));
		}
		return configVersion;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public DynamicConfigBean getDynamicConfigBean(String confName) {
		@SuppressWarnings("unchecked")
		DynamicConfigBean dynamicConfigBean = (DynamicConfigBean) whtJdbcTemplate.getJdbcOperations().queryForObject(
				"select * from common_dynamic_config where confName = ? and status = 1 order by version desc limit 1",
				new Object[] { confName }, new BeanPropertyRowMapper(DynamicConfigBean.class));
		return dynamicConfigBean;
	}

	@Override
	public List<DynamicConfigBean> queryDynamicConfByName(String dynamicName) {
		return whtJdbcTemplate.getJdbcOperations().query(
				"SELECT `confName`, `content`, create_time, `status`, `version`, `updated`  FROM `common_dynamic_config` WHERE confName LIKE ? and status = 1 ORDER BY `version` DESC",
				new Object[] { "%" + dynamicName + "%" },
				new BeanPropertyRowMapper<DynamicConfigBean>(DynamicConfigBean.class));
	}

	@Override
	public DynamicConfigBean getDynamicConfigBeanByVersion(String confName, String version) {
		@SuppressWarnings("unchecked")
		DynamicConfigBean dynamicConfigBean = (DynamicConfigBean) whtJdbcTemplate.getJdbcOperations().queryForObject(
				"select * from common_dynamic_config where confName = ? and status = 1 and version = ? ",
				new Object[] { confName, version }, new BeanPropertyRowMapper(DynamicConfigBean.class));
		return dynamicConfigBean;
	}

}
