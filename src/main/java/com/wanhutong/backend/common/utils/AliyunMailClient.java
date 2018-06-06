package com.wanhutong.backend.common.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.thread.ThreadPoolManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 阿里云邮件推送
 * 
 * @author Ma.Qiang
 *
 */
public class AliyunMailClient {
	
	private final Map<String, List<String>> BATCH_QUEUE = Maps.newConcurrentMap();

	private final static Logger LOGGER = LoggerFactory.getLogger(AliyunMailClient.class);

	private final static Logger DATA_LOGGER = LoggerFactory.getLogger("email");

	private final String PROPERTIES_NAME = "aliyun";

	private final String PROPERTIES_NAME_MAIL = "admin@sms.marschariot.com";

	private final String REGIONID = "cn-beijing";

	private IAcsClient iAcsClient;

	private AliyunMailClient() {
		create();
	}

	private static class SingletonHolder {
		private static final AliyunMailClient INSTANCE = new AliyunMailClient();
	}

	public static AliyunMailClient getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 发送txt信息
	 * 
	 * @param address
	 *            目标地址
	 * @param subject
	 *            标题
	 * @param body
	 *            内容
	 */
	public void sendTxt(String address, String subject, String body) {
		List<String> addrList = Splitter.on(",").splitToList(PROPERTIES_NAME_MAIL);
		sendTxt(RandomUtil.randomEle(addrList), address, subject, body);
	}

	/**
	 * 批量发送txt信息
	 * 
	 * @param address
	 *            目标地址
	 * @param subject
	 *            标题
	 * @param body
	 *            内容
	 * @param batch
	 *            数量
	 */
	public void sendTxtBatch(String address, String subject, String body, long batch) {
		List<String> mails = BATCH_QUEUE.get(subject);
		if (mails == null) {
			mails = Lists.newArrayList();
			BATCH_QUEUE.put(subject, mails);
		}
		mails.add(body);
		if (mails.size() > batch) {
			List<String> addrList = Splitter.on(",").splitToList(PROPERTIES_NAME_MAIL);
			sendTxt(RandomUtil.randomEle(addrList), address, subject, body);
			BATCH_QUEUE.get(subject).clear();
		}
	}

	/**
	 * 发送txt信息
	 * 
	 * @param accountName
	 *            发送者账号
	 * @param address
	 *            目标地址
	 * @param subject
	 *            标题
	 * @param body
	 *            内容
	 */
	public void sendTxt(String accountName, String address, String subject, String body) {
		ThreadPoolManager.getDefaultThreadPool().execute(() -> {
			SingleSendMailRequest request = new SingleSendMailRequest();
			request.setAccountName(accountName);
			request.setAddressType(1);
			request.setReplyToAddress(true);
			request.setToAddress(address);
			request.setSubject(subject);
			request.setHtmlBody(body);
			try {
				iAcsClient.getAcsResponse(request);
				DATA_LOGGER.info("send Mail OK,{}|{}|{}", accountName, address, subject);
			} catch (ClientException e) {
				DATA_LOGGER.error("send Mail Error!{}|{}|{}", accountName, address, subject);
				LOGGER.error("【Exception】send mail error!,accountName: {},address: {},subject: {},body :{}",
						accountName, address, subject, body, e);
			} finally {
			}
		});
	}

	/**
	 * 创建mailClient对象
	 */
	private void create() {
		String accessKeyId = Global.getConfig("mail.default.accessKeyId", StringUtils.EMPTY);
		String accessKeySecret = Global.getConfig("mail.default.accessKeySecret", StringUtils.EMPTY);

		IClientProfile profile = DefaultProfile.getProfile(REGIONID, accessKeyId, accessKeySecret);
		IAcsClient client = new DefaultAcsClient(profile);
		this.iAcsClient = client;
	}
}
