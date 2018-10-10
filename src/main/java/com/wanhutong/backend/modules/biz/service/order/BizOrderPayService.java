package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.DoOrderHeaderProcessFifthConfig;
import com.wanhutong.backend.modules.config.parse.JointOperationOrderProcessLocalConfig;
import com.wanhutong.backend.modules.config.parse.JointOperationOrderProcessOriginConfig;
import com.wanhutong.backend.modules.config.parse.Process;
import com.wanhutong.backend.modules.enums.OrderPayProportionStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BizOrderPayService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {

    @Autowired
    private CommonProcessService commonProcessService;

    public static final String DATABASE_TABLE_NAME = "biz_order_header";
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> orderPayHandler(String orderNum, String orderType) {
        // 取订单
        BizOrderHeader bizOrderHeader = dao.getByOrderNum(orderNum);

        if (bizOrderHeader == null) {
            return Pair.of(Boolean.TRUE, "bizOrderHeader is null");
        }
        // 取当前支付比例
        OrderPayProportionStatusEnum proportionStatus = OrderPayProportionStatusEnum.parse(bizOrderHeader);
        if (proportionStatus == OrderPayProportionStatusEnum.ZERO) {
            return Pair.of(Boolean.TRUE, "proportionStatus is zero");
        }
        //订单类型为普通订单时
        if (OrderTypeEnum.SO.getOrderType().equals(orderType)) {
            // 取当前审核状态
            CommonProcessEntity tempEntity = new CommonProcessEntity();
            tempEntity.setCurrent(1);

            int suplys = 1;
            String orderTableName = JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME;
            if (bizOrderHeader.getSuplys() == null || bizOrderHeader.getSuplys() == 0 || bizOrderHeader.getSuplys() == 721) {
                suplys = 0;
                orderTableName = JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME;
            }
            tempEntity.setObjectName(orderTableName);
            tempEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
            List<CommonProcessEntity> list = commonProcessService.findList(tempEntity);

            JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
            JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();

            if (CollectionUtils.isNotEmpty(list)) {
                if (list.size() > 1) {
                    return Pair.of(Boolean.FALSE, "操作失败, 当前流程数量大于1");
                }
                // 取审核状态payCode
                // 如果有, 更新当前状态
                Process process = null;
                Integer payCode = null;
                CommonProcessEntity cureentProcessEntity = list.get(0);
                if (suplys == 0) {
                    process = originConfig.getProcessMap().get(Integer.valueOf(cureentProcessEntity.getType()));
                } else {
                    process = localConfig.getProcessMap().get(Integer.valueOf(cureentProcessEntity.getType()));
                }

                switch (proportionStatus) {
                    case ALL:
                        payCode = process.getAllPayCode();
                        break;
                    case FIFTH:
                        payCode = process.getFifthPayCode();
                        break;
                    default:
                        break;
                }
                if (payCode == null || payCode == 0) {
                    return Pair.of(Boolean.TRUE, "payCode is null");
                }
                if(process.getCode() == payCode) {
                    return Pair.of(Boolean.TRUE, "payCode == currentCode");
                }

                // 前一状态备注:订单支付比例更新自动通过
                cureentProcessEntity.setBizStatus(CommonProcessEntity.AuditType.PASS.getCode());
                cureentProcessEntity.setProcessor("0");
                cureentProcessEntity.setDescription("订单支付比例更新自动通过");
                cureentProcessEntity.setCurrent(0);
                commonProcessService.save(cureentProcessEntity);

                CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
                nextProcessEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
                nextProcessEntity.setObjectName(orderTableName);
                nextProcessEntity.setType(String.valueOf(payCode));
                nextProcessEntity.setPrevId(cureentProcessEntity.getId());
                nextProcessEntity.setCurrent(1);
                commonProcessService.save(nextProcessEntity);
            }
        } else if (OrderTypeEnum.DO.getOrderType().equals(orderType)) {
            // 取当前审核状态
            CommonProcessEntity tempEntity = new CommonProcessEntity();
            tempEntity.setCurrent(1);
            tempEntity.setObjectName(DATABASE_TABLE_NAME);
            tempEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));

            List<CommonProcessEntity> list = commonProcessService.findList(tempEntity);
            DoOrderHeaderProcessFifthConfig doAuditConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

            if (CollectionUtils.isNotEmpty(list)) {
                if (list.size() > 1) {
                    return Pair.of(Boolean.FALSE, "操作失败, 当前流程数量大于1");
                }
                // 取审核状态payCode
                // 如果有, 更新当前状态
                DoOrderHeaderProcessFifthConfig.OrderHeaderProcess process = null;
                Integer payCode = null;
                CommonProcessEntity cureentProcessEntity = list.get(0);
                process = doAuditConfig.getProcessMap().get(Integer.valueOf(cureentProcessEntity.getType()));

                switch (proportionStatus) {
                    case ALL:
                        payCode = process.getAllPayCode();
                        break;
                    default:
                        break;
                }
                if (payCode == null || payCode == 0) {
                    return Pair.of(Boolean.TRUE, "payCode is null");
                }
                if(process.getCode().equals(payCode)) {
                    return Pair.of(Boolean.TRUE, "payCode == currentCode");
                }

                // 前一状态备注:订单支付比例更新自动通过
                cureentProcessEntity.setBizStatus(CommonProcessEntity.AuditType.PASS.getCode());
                cureentProcessEntity.setProcessor("0");
                cureentProcessEntity.setDescription("订单支付比例更新自动通过");
                cureentProcessEntity.setCurrent(0);
                commonProcessService.save(cureentProcessEntity);

                CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
                nextProcessEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
                nextProcessEntity.setObjectName(DATABASE_TABLE_NAME);
                nextProcessEntity.setType(String.valueOf(payCode));
                nextProcessEntity.setPrevId(cureentProcessEntity.getId());
                nextProcessEntity.setCurrent(1);
                commonProcessService.save(nextProcessEntity);
            }
        }
        return Pair.of(Boolean.TRUE, "success");
    }
}
