package com.wanhutong.backend.modules.config.parse;

/**
 * @author Ma.Qiang
 * 2018/5/31
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    @XStreamImplicit(itemFieldName = "serviceChargeAudit")
    private List<String> serviceChargeAudit;

    /**
     * 首单优惠后金额低于成本价格的审核权限角色
     * {@link com.wanhutong.backend.modules.enums.RoleEnNameEnum}
     */
    @XStreamImplicit(itemFieldName = "orderLossAudit")
    private List<String> orderLossAudit;

    /**
     * 订单优惠后亏损金额大于成本5%审核权限角色
     * {@link com.wanhutong.backend.modules.enums.RoleEnNameEnum}
     */
    @XStreamImplicit(itemFieldName = "orderLowestAudit")
    private List<String> orderLowestAudit;


    @Override
    public SystemConfig parse(String content) throws Exception {
        SystemConfig systemConfig = XmlUtils.fromXml(content);

        return systemConfig;
    }

    public List<String> getServiceChargeAudit() {
        return serviceChargeAudit;
    }

    public List<String> getOrderLossAudit() {
        return orderLossAudit;
    }

    public List<String> getOrderLowestAudit() {
        return orderLowestAudit;
    }
}
