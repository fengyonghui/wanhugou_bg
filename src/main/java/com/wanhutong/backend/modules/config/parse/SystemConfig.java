package com.wanhutong.backend.modules.config.parse;

/**
 * @author Ma.Qiang
 * 2018/5/31
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统数据配置
 *
 * @author ma.qiang
 */
@XStreamAlias("SystemConfig")
public class SystemConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfig.class);


    /**
     * 服务费大于50%的审核权限角色
     * {@link com.wanhutong.backend.modules.enums.RoleEnNameEnum}
     */
    @XStreamAlias("serviceChargeAudit")
    private String serviceChargeAudit;


    @Override
    public SystemConfig parse(String content) throws Exception {
        SystemConfig systemConfig = XmlUtils.fromXml(content);

        return systemConfig;
    }

    public String getServiceChargeAudit() {
        return serviceChargeAudit;
    }
}
