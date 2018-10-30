package com.wanhutong.backend.common.web;

import com.wanhutong.backend.common.security.RSA;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomerInfo;
import com.wanhutong.backend.modules.biz.entity.dto.OfficeLevelApplyDto;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/app/office")
public class AppOfficeController {
    @Autowired
    private OfficeService officeService;


    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDaSp23PoO2EOEQnqZQNDYgMAk2\n" +
            "HOFpcoxFYEFKhBpdwlDhr75jwdjcSbYYdkFmi2awZvhGL1H5/PvBY4cs6rrVqhmx\n" +
            "xlIrgfMsEKHrA6DPxECxq45kFBenL9JaU7HcahSgEd+JpPyx8nC+W94Kne42r6U2\n" +
            "/tnpstf3V3YtFTVR3QIDAQAB";


    @RequestMapping(value = "/officeTypeApply")
    @ResponseBody
    public String officeTypeApply(@RequestBody OfficeLevelApplyDto officeLevelApplyDto) {
        if (officeLevelApplyDto == null || StringUtils.isBlank(officeLevelApplyDto.getSign())) {
            return JsonUtil.generateErrorData(HttpStatus.SC_BAD_REQUEST, "请求参数有误!", null);
        }

        try {
            boolean verify = RSA.verify(officeLevelApplyDto.buildData(), officeLevelApplyDto.getSign(), PUBLIC_KEY, "UTF-8");
            if (!verify) {
                return JsonUtil.generateErrorData(HttpStatus.SC_BAD_REQUEST, "签名验证失败!", null);
            }
        } catch (Exception e) {
            return JsonUtil.generateErrorData(HttpStatus.SC_BAD_REQUEST, "签名验证出现异常!", null);
        }

        Office office = officeService.get(officeLevelApplyDto.getOfficeId());
        if (!office.getType().equals(OfficeTypeEnum.SHOPKEEPER.getType()) && !office.getType().equals(OfficeTypeEnum.COMMISSION_MERCHANT.getType())) {
            return JsonUtil.generateErrorData(HttpStatus.SC_BAD_REQUEST, "操作失败, 当前机构类型不正确!", null);
        }

        if (office.getType().equals(officeLevelApplyDto.getApplyLevel().toString())) {
            return JsonUtil.generateErrorData(HttpStatus.SC_BAD_REQUEST, "操作失败, 申请等级有误!", null);
        }

        try {
            Pair<Boolean, String> booleanStringPair = officeService.officeTypeApply(office, officeLevelApplyDto);
            if (booleanStringPair.getLeft()) {
                return JsonUtil.generateData("操作成功!", null);
            }
            return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, booleanStringPair.getRight(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "操作失败!", null);
    }
}
