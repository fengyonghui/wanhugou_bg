package com.wanhutong.backend.modules.config.parse;

/**
 * @author Ma.Qiang
 * 2017/10/8
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;

/**
 * 首页数据配置
 *
 * @author ma.qiang
 *
 */
@XStreamAlias("PurchaseOrderProcessConfig")
public class PurchaseOrderProcessConfig extends ConfigGeneral {


    @XStreamAlias("bossStoryImg")
    private String bossStoryImg;

    @Override
    public PurchaseOrderProcessConfig parse(String content) throws Exception {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
        return purchaseOrderProcessConfig;
    }


    public String getBossStoryImg() {
        return bossStoryImg;
    }
}
