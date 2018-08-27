package com.wanhutong.backend.modules.config.parse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
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

    /**
     * 商品上架金额小于成本审核权限角色
     * {@link com.wanhutong.backend.modules.enums.RoleEnNameEnum}
     */
    @XStreamImplicit(itemFieldName = "putawayUnderCostAudit")
    private List<String> putawayUnderCostAudit;

    //=====================================临时活动 start====================================
    /**
     * 可以设置低于成本20%的货架
     *
     */
    @XStreamAlias("activityShelfId")
    private Integer activityShelfId;

    /**
     * 可以设置低于成本20%的日期 在此日期以前
     *
     */
    @XStreamAlias("activityDate")
    private String activityDate;
    //=====================================临时活动 end====================================
    /**
     * 拍照订单的服务费系数
     *
     */
    @XStreamAlias("photoOrderRatio")
    private BigDecimal photoOrderRatio;

    /**
     * PO查询过滤时间节点（小于此节点）
     */
    @XStreamAlias("filteringDate")
    private String filteringDate;





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

    public List<String> getPutawayUnderCostAudit() {
        return putawayUnderCostAudit;
    }

    public Integer getActivityShelfId() {
        return activityShelfId;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public BigDecimal getPhotoOrderRatio() {
        return photoOrderRatio;
    }

    public String getFilteringDate() {
        return filteringDate;
    }
}
