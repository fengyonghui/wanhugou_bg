package com.wanhutong.backend.modules.config;

import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.SystemConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 热加载配置的基类，所有配置类应从此类派生，需要注意的是：
 * 
 * <ol>
 * <li>所有的配置子类的动态加载属性应定义为实例化域，而不应该是静态域</li>
 * <li>{@link #parse(String)} 方法中无需实现同步、Lock等机制</li>
 * <li>在一段连续的逻辑中使用某个配置对象时，应该先获取该对象的一个引用，并且在下面的处理代码中使用该引用，从而避免代码的不同部位使用不同版本的属性的问题
 * </li>
 * </ol>
 * 
 * 如何添加新的配置对象：
 * 
 * <ol>
 * <li>自 @see ConfigGeneral 派生</li>
 * <li>实现 {@link #parse(String)} 方法，仅需考虑配置内容的解析，无需额外的同步处理</li>
 * <li>在 @see ConfigGeneral 中加入一个 public static
 * 属性，指向新加配置对象的实例包装器，并在其中指定配置文件的名称（注意不能与已有配置文件同名）</li>
 * <li>在 /htdocs/ROOT/WEB-INF/conf 下加入第一个版本的配置文件，使用上一步指定的配置文件名称</li>
 * </ol>
 * 
 * 可进行的进一步的改进：
 * <ol>
 * <li>限制confName不能重复</li>
 * </ol>
 * 
 * @author Ma.Qiang
 * @version
 * 
 */
public abstract class ConfigGeneral {


	public static final ConfigWrapper<PurchaseOrderProcessConfig> PURCHASE_ORDER_PROCESS_CONFIG = new ConfigWrapper<>(
			"PurchaseOrderProcessConfig.xml",new PurchaseOrderProcessConfig());

	public static final ConfigWrapper<RequestOrderProcessConfig> REQUEST_ORDER_PROCESS_CONFIG = new ConfigWrapper<RequestOrderProcessConfig>(
			"RequestOrderProcessConfig.xml",new RequestOrderProcessConfig());

	public static final ConfigWrapper<PaymentOrderProcessConfig> PAYMENT_ORDER_PROCESS_CONFIG = new ConfigWrapper<>(
			"PaymentOrderProcessConfig.xml",new PaymentOrderProcessConfig());

	public static final ConfigWrapper<SystemConfig> SYSTEM_CONFIG = new ConfigWrapper<>(
			"SystemConfig.xml",new SystemConfig());

	public static final ConfigWrapper<EmailConfig> EMAIL_CONFIG = new ConfigWrapper<>(
			"EmailConfig.xml",new EmailConfig());

	/**
	 * 获取所有的配置对象实例所属的父容器，使用反射实现，由于仅在更新配置的内存版本时调用，属于可容忍的性能损耗
	 * 
	 * @return 如果无配置对象，返回空数组
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static ConfigWrapper<ConfigGeneral>[] getAllConfigs()
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = ConfigGeneral.class.getDeclaredFields();
		ConfigWrapper<ConfigGeneral>[] tmp = new ConfigWrapper[fields.length];
		short c = 0;
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) && (field.getType().equals(ConfigWrapper.class))) {
				tmp[c++] = (ConfigWrapper<ConfigGeneral>) field.get(ConfigGeneral.class);
			}
		}
		ConfigWrapper<ConfigGeneral>[] ret = new ConfigWrapper[c];
		System.arraycopy(tmp, 0, ret, 0, c);
		return ret;
	}

	/**
	 * 解析配置文件，转换为一个新的配置对象
	 * 
	 * @param content
	 *            配置文件内容，不能为空
	 * @return 新创建的配置对象，不可能为null
	 * @throws Exception
	 *             如果解析失败或其它异常，扔出此类异常
	 */
	protected abstract ConfigGeneral parse(String content) throws Exception;
}
