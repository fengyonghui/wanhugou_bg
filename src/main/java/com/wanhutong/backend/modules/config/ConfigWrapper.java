package com.wanhutong.backend.modules.config;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 对配置对象的包装，以满足：
 * 
 * <ol>
 * <li>无锁的原子级的get和set方法</li>
 * <li>immutable</li>
 * </ol>
 * 
 * @author Ma.Qiang
 * @param <T>
 *            必须是  {@link com.wanhutong.backend.modules.config.ConfigGeneral} 的派生类
 */
public class ConfigWrapper<T extends ConfigGeneral> {
	/**
	 * 日志对象
	 */
	protected static final Logger log = Logger.getLogger(ConfigWrapper.class
			.toString());

	/**
	 * 当前版本
	 */
	protected int version = 0;

	/**
	 * 是否已初始化
	 */
	protected boolean inited = false;

	/**
	 * 配置名称，应保持唯一
	 */
	protected final String confName;

	/**
	 * 包装器
	 */
	private final AtomicReference<T> wrapper = new AtomicReference<T>();

	/**
	 * 构造，设置第一个被包装的配置对象实例（可以未初始化）
	 * 
	 * @param confName
	 *            配置名称，必须唯一
	 * @param config
	 *            配置对象实例
	 */
	public ConfigWrapper(String confName, T config) {
		this.confName = confName;
		wrapper.set(config);
	}

	/**
	 * 获得当前的版本信息，如果为 -1 ，则代表从本地配置文件中获取；如果大于0，则代表从数据库中获取
	 * 
	 * @return 版本号
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 是否已初始化，如果已成功载入相关的配置内容，则代表初始化成功
	 * 
	 * @return 初始化成功，则返回true
	 */
	public boolean isInited() {
		return inited;
	}

	/**
	 * 获取配置名称
	 * 
	 * @return 配置名称
	 */
	public String getConfName() {
		return confName;
	}

	/**
	 * 原子级地获取包装的配置对象
	 * 
	 * @return 配置对象。如果尚未设置配置对象或配置对象尚未初始化，扔出 @see RuntimeException
	 */
	public T get() {
		T config = wrapper.get();
		if (config == null) {
			throw new RuntimeException("尚未初始化");
		}
		if (!isInited()) {
			throw new RuntimeException("尚未初始化");
		}
		return config;
	}

	/**
	 * 原子级地设置包装的配置对象
	 * 
	 * @param config
	 *            配置对象。如果尚未设置配置对象或配置对象尚未初始化，扔出 @see RuntimeException
	 */
	public void set(T config) {
		if (config == null) {
			throw new RuntimeException("尚未初始化");
		}
		wrapper.set(config);
	}
	
	/**
	 * 使用配置文件内容重新载入配置对象
	 * 
	 * @param content
	 *            内容，可以为任意文本，不能为空
	 * @param version
	 *            新的版本号
	 * @returns 如果重新载入，返回true
	 */
	@SuppressWarnings("unchecked")
	public boolean reload(String content, int version) {
		try {
			ConfigGeneral config = wrapper.get().parse(content);
			this.version = version;
			this.inited = true;
			set((T)config);
			return true;
		} catch (Exception ex) {
			log.info(ex.getMessage());
			log.log(Level.SEVERE, "重新载入配置文件\"" + getConfName() + "\"失败", ex);
			return false;
		}
	}
}