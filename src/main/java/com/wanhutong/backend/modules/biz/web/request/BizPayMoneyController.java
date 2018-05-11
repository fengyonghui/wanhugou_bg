package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Maps;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.ZxingHandler;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.dto.RequestQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import com.wanhutong.utils.WhgPaySign;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/biz/payMoney")
public class BizPayMoneyController extends BaseController {
    @Resource
    private BizRequestHeaderService bizRequestHeaderService;


    @RequestMapping(value = "payMoneyIng")
    public String payMoneyIng(@RequestBody Map<String, Object> map){
        String jsonstr = JSONObject.fromObject(map).toString();
        logger.info("payMoneyIng======"+jsonstr);
        return "";
    }
}
