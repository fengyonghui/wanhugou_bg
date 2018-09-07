package com.wanhutong.backend.common.utils.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.thread.ThreadPoolManager;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云短信发送
 * <p>
 * 短信发送频率上有什么限制？ <br />
 * 短信验证码 ：使用同一个签名，对同一个手机号码发送短信验证码，支持1条/分钟，累计7条/小时；
 * 短信通知：使用同一个签名和同一个短信模板ID，对同一个手机号码发送短信通知，支持50条/日（指自然日）；
 *
 * @author Ma.Qiang
 */
public class AliyunSmsClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(AliyunSmsClient.class);

    private final static Logger DATA_LOGGER = LoggerFactory.getLogger("email");

	// 产品名称:云通信短信API产品,开发者无需替换
	private final static String PRODUCT = "Dysmsapi";
	// 产品域名,开发者无需替换
	private final static String DOMAIN = "dysmsapi.aliyuncs.com";

    private final static String PROPERTIES_NAME = "aliyun";

	private final String REGIONID = "cn-beijing";

    public final static String DEF_SMS_SIGN = "万户购";


    // 验证码模板集合
    private final static List<String> SMS_SIGN_LIST = Lists.newArrayList(DEF_SMS_SIGN);
    // 验证码发送状态获取 timeout
    private final static Long FUTURE_TIME_OUT = 10L;

    private IAcsClient iAcsClient;

    private AliyunSmsClient() {
        create();
    }

    private static class SingletonHolder {
        private static final AliyunSmsClient INSTANCE = new AliyunSmsClient();
    }

    public static AliyunSmsClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 发送短信提醒
     *
     * @param templateCode 管理控制台中配置的审核通过的短信模板的模板CODE（状态必须是验证通过
     * @param recNum       目标手机号，多个手机号可以逗号分隔
     * @param paramsMap    短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。例如:短信模板为：“接受短信验证码${no}
     *                     ”,此参数传递{“no”:”123456”}，用户将接收到[短信签名]接受短信验证码123456
     */
    public void sendSMS(String templateCode, String recNum, Map<String, String> paramsMap) {
        sendSMS(DEF_SMS_SIGN, templateCode, recNum, paramsMap);
    }

    /**
     * 发送短信提醒
     *
     * @param signName     管理控制台中配置的短信签名（状态必须是验证通过）
     * @param templateCode 管理控制台中配置的审核通过的短信模板的模板CODE（状态必须是验证通过
     * @param recNum       目标手机号，多个手机号可以逗号分隔
     * @param paramsMap    短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。例如:短信模板为：“接受短信验证码${no}
     *                     ”,此参数传递{“no”:”123456”}，用户将接收到[短信签名]接受短信验证码123456
     */
    private Future<Boolean> sendSMS(String signName, String templateCode, String recNum, Map<String, String> paramsMap) {
        if (!ConfigGeneral.SYSTEM_CONFIG.get().getSendMessageSwitch()) {
            LOGGER.info("测试短信发送,signName[{}],templateCode[{}],recNum[{}],paramsMap[{}]",signName,templateCode,recNum,paramsMap);
            return null;
        }

		return ThreadPoolManager.getDefaultThreadPool().submit(new Callable<Boolean>() {
			@Override
			public Boolean call() {

				Preconditions.checkArgument(
						StringUtils.isNotBlank(signName) && StringUtils.isNotBlank(templateCode)
								&& StringUtils.isNotBlank(recNum),
						"Parameters can't be empty! signName[%s] templateCode[%s] recNum[%s]", signName, templateCode,
						recNum);

				SendSmsRequest request = new SendSmsRequest();
				request.setPhoneNumbers(recNum);
				request.setSignName(signName);
				request.setTemplateCode(templateCode);
				request.setTemplateParam(JSONObject.fromObject(paramsMap).toString());
                try {
                    SendSmsResponse acsResponse = iAcsClient.getAcsResponse(request);// isp.RAM_PERMISSION_DENY 没有访问权限
                    DATA_LOGGER.info("send SMS {}|{}|{}|{}|{}", acsResponse.getMessage(), recNum, signName, templateCode,
							JSONObject.fromObject(paramsMap).toString());
					return Boolean.TRUE;
                } catch (ClientException e) {
					DATA_LOGGER.error("send SMS Error!{}|{}|{}|{}", recNum, signName, templateCode,
                            JSONObject.fromObject(paramsMap).toString());
					LOGGER.error("【Exception】send sms error!signName: {},templateCode: {},recNum: {},paramsMap: {}",
							signName,
                            templateCode, recNum, JSONObject.fromObject(paramsMap).toString(), e);
                } finally {
                }
				return Boolean.FALSE;
			}
		});
    }

//    /**
//     * 发送验证码
//     * @param signName     管理控制台中配置的短信签名（状态必须是验证通过）
//     * @param recNum    目标手机号，多个手机号可以逗号分隔
//     * @param paramsMap 短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。例如:短信模板为：“接受短信验证码${no}
//     *                  ”,此参数传递{“no”:”123456”}，用户将接收到[短信签名]接受短信验证码123456
//     */
//    public void sendCheckCode(String signName,String recNum, Map<String, String> paramsMap) {
//       try {
//			Set<String> signSet = Sets.newLinkedHashSet();
//			signSet.add(signName);
//			signSet.addAll(SMS_SIGN_LIST);
//			for (String defaultSignName : signSet) {
//				Future<Boolean> future = sendSMS(defaultSignName, SmsTemplateCode.SECURITY_CODE.getCode(), recNum,
//						paramsMap);
//				Boolean sentStatus = future.get(FUTURE_TIME_OUT, TimeUnit.SECONDS);
//				// 如果不是已经完成状态,等待
//				if (future.isDone() && !future.isCancelled()) {
//					if (sentStatus) {
//						return;
//					}
//				}
//			}
//        } catch (Exception e) {
//            LOGGER.error("send checkCode SMS future Error!", e);
//        }
//    }

    /**
	 * 创建smsClient对象
	 * 
	 * @throws ClientException
	 */
	private void create() {
        String accessKeyId = Global.getConfig("sms.default.accessKeyId", StringUtils.EMPTY);
        String accessKeySecret = Global.getConfig("sms.default.accessKeySecret", StringUtils.EMPTY);

        IClientProfile profile = DefaultProfile.getProfile(REGIONID, accessKeyId, accessKeySecret);
		try {
			DefaultProfile.addEndpoint(REGIONID, REGIONID, PRODUCT, DOMAIN);
		} catch (ClientException e) {
			LOGGER.error("AliyunSmsClient create Error!", e);
		}
        IAcsClient client = new DefaultAcsClient(profile);
        this.iAcsClient = client;
    }
}
