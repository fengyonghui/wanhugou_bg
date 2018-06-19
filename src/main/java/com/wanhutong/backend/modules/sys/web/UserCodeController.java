package com.wanhutong.backend.modules.sys.web;

import com.alibaba.druid.support.json.JSONUtils;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.web.BaseController;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/sys/userCode")
public class UserCodeController extends BaseController {

    @RequestMapping(value = "genUserQRCode")
    @ResponseBody
    public String genPayQRCode(Integer id){
        CloseableHttpClient httpClient= CloseableHttpClientUtil.createSSLClientDefault();
        String getUrl= DsConfig.getUserGenCode()+id;
        HttpGet httpGet = new HttpGet(getUrl);
        CloseableHttpResponse httpResponse = null;
        String result=null;
        String base64Data=null;
        try {

            httpGet.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
            httpGet.setHeader("Accept", "application/json");

            httpResponse = httpClient.execute(httpGet);

            result= EntityUtils.toString(httpResponse.getEntity(),"utf-8");


            logger.info("返回结果result================="+result);

            Map map= (Map) JSONUtils.parse(result);
            if("1".equals((map.get("status")).toString())){
                base64Data= map.get("data").toString();
                logger.info(base64Data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
       return base64Data;
    }



}
