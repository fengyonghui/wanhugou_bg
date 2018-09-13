package com.wanhutong.backend.modules.config;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;
import com.wanhutong.backend.common.utils.ServiceHelper;
import com.wanhutong.backend.modules.config.dao.impl.DynamicConfigDaoImpl;
import com.wanhutong.backend.modules.config.entity.DynamicConfigBean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicConfig implements Runnable {

	private static final Logger logger = Logger.getLogger(DynamicConfig.class.getName());

	private static final String NAME = "dynamicConfig";

	private DynamicConfigDaoImpl dynamicConfigDaoImpl;

	private static class SingletonHolder {
		private static final DynamicConfig INSTANCE = new DynamicConfig();
	}

	public static DynamicConfig getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private DynamicConfig() {
		dynamicConfigDaoImpl = (DynamicConfigDaoImpl) ServiceHelper.getService("dynamicConfigDaoImpl");
		run(); // 阻塞, 初始化配置文件是系统启动前提.不能异步载入!
	}

	private void reloadOneConfig(ConfigWrapper<ConfigGeneral> config, Long version) {
		DynamicConfigBean configRemote = dynamicConfigDaoImpl.getDynamicConfigBean(config.getConfName());

		//此时版本号和是否已初始化状态还未发生变化，故不需要从本地恢复
		if (configRemote == null || (configRemote.getContent() == null) || (configRemote.getContent().length() == 0)) {
			logger.log(Level.SEVERE, "从db获取配置文件" + config.getConfName() + ":" + config.getVersion() + "失败，请检查");
			return;
		}

		try {
			//从data_center数据加载成功写文件到本地, 写到本地就是方便查看问题, 并没必要需要从本地读取配置文件
			if (config.reload(configRemote.getContent(), version.intValue())) {
//				write(config.getConfName(), configRemote.getContent());
//				logger.log(Level.WARNING, "配置文件" + config.getConfName() + ":" + config.getVersion() + "加载成功并写入文件");
				logger.log(Level.WARNING, "配置文件" + config.getConfName() + ":" + config.getVersion() + "加载成功");
				return;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "配置文件" + config.getConfName() + ":" + config.getVersion() + "加载失败", e);
		}


		//防止没有初始化成功但版本号发生了变化 需要从本地恢复
//		config.reload(read(config.getConfName()), 0);
	}


	public void reloadAllConfig(Predicate<Map.Entry<ConfigWrapper<ConfigGeneral>, Long>> predicate) {

		List<ConfigWrapper<ConfigGeneral>> allConfig = null;
		try {
			allConfig = Arrays.asList(ConfigGeneral.getAllConfigs());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		final Map<String, Long> configAndVersion = dynamicConfigDaoImpl.queryAllConfigVersion();
		Map<ConfigWrapper<ConfigGeneral>, Long> needReload = Maps.filterEntries(Maps.toMap(allConfig, new Function<ConfigWrapper<ConfigGeneral>, Long>() {
			@Override
			public Long apply(ConfigWrapper<ConfigGeneral> input) {
				Long version = configAndVersion.get(input.getConfName());
				if(version == null) {
							logger.log(Level.SEVERE, "Config can not found in db: " + input.getConfName());
				}
				return version != null ? version : -1;
			}
		}), predicate);


		for (Map.Entry<ConfigWrapper<ConfigGeneral>, Long> entry : needReload.entrySet()) {
			reloadOneConfig(entry.getKey(), entry.getValue());
		}

	}


	@Override
	public void run() {
		reloadAllConfig(new Predicate<Map.Entry<ConfigWrapper<ConfigGeneral>, Long>>() {
			@Override
			public boolean apply(Map.Entry<ConfigWrapper<ConfigGeneral>, Long> input) {
				return input.getValue() > input.getKey().getVersion();
			}
		});
	}


	public boolean write(String filename, String str) {

		String name = "../temp/dynamic_conf/" + filename;
		FileWriter writer = null;
		Closer closer = Closer.create();
		try {
			writer = closer.register(new FileWriter(name));
			writer.write(str);
		} catch (Exception e) {
			logger.log(Level.SEVERE,"write file error", e);
			return false;
		} finally {
			try {
				closer.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "close file error", e);
			}
		}

		return true;
	}

	public String read(String filename) {
		String name = "../temp/dynamic_conf/" + filename;
		StringBuilder sb = new StringBuilder();
		FileReader reader = null;
		BufferedReader br = null;
		Closer closer = Closer.create();
		try {
			reader = closer.register(new FileReader(name));
			br = closer.register(new BufferedReader(reader));

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE,"read file error", e);
			return "";
		} finally {
			try {
				closer.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "close file error", e);
			}
		}
	}

	public String getName() {
		return NAME;
	}

}

