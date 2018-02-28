package com.wanhutong.backend.modules.biz.web.prop;

import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropValueService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuPropValueService;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequestMapping(value = "${adminPath}/biz/prop")
public class PropValueCodeGen {
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PropValueService propValueService;
    @Autowired
    private BizCatePropValueService bizCatePropValueService;
    @Autowired
    private BizProdPropValueService bizProdPropValueService;
    @Autowired
    private BizSkuPropValueService bizSkuPropValueService;
    @Autowired
    private BizVendInfoService bizVendInfoService;

    @RequestMapping(value = "genSysPropCode")
    public String genSysPropCode() {
        PropValue propValue = new PropValue();
        propValue.setDelFlag("1");
        List<PropValue> sysPropValues = propValueService.findList(propValue);
        for (PropValue propValue1 : sysPropValues) {
            String code = HanyuPinyinHelper.getFirstLetters(propValue1.getValue(), HanyuPinyinCaseType.UPPERCASE);
            if (code.length() > 10){
                code = code.substring(0,10);
            }
            propValue1.setCode(code);
            log.info("-----:" + code);
            propValueService.save(propValue1);
        }

        return "";
    }

    @RequestMapping(value = "genCatePropCode")
    public String genCatePropCode() {
        BizCatePropValue propValue = new BizCatePropValue();
        propValue.setDelFlag("1");
        List<BizCatePropValue> propValues = bizCatePropValueService.findList(propValue);
        for (BizCatePropValue propValue1 : propValues) {
            String code = HanyuPinyinHelper.getFirstLetters(propValue1.getValue(), HanyuPinyinCaseType.UPPERCASE);
            if (code.length() > 10){
                code = code.substring(0,10);
            }
            propValue1.setCode(code);
            log.info("-----:" + code);
            bizCatePropValueService.save(propValue1);
        }

        return "";
    }

    @RequestMapping(value = "genProdPropCode")
    public String genProdPropCode() {
        BizProdPropValue propValue = new BizProdPropValue();
        propValue.setDelFlag("1");
        List<BizProdPropValue> propValues = bizProdPropValueService.findList(propValue);
        for (BizProdPropValue propValue1 : propValues) {
            String code = HanyuPinyinHelper.getFirstLetters(propValue1.getPropValue(), HanyuPinyinCaseType.UPPERCASE);
            if (code.length() > 10){
                code = code.substring(0,10);
            }
            propValue1.setCode(code);
            log.info("-----:" + code);
            bizProdPropValueService.save(propValue1);
        }

        return "";
    }

    @RequestMapping(value = "genSkuPropCode")
    public String genSkuPropCode() {
        BizSkuPropValue propValue = new BizSkuPropValue();
        propValue.setDelFlag("1");
        List<BizSkuPropValue> propValues = bizSkuPropValueService.findList(propValue);
        for (BizSkuPropValue propValue1 : propValues) {
            String code = HanyuPinyinHelper.getFirstLetters(propValue1.getPropValue(), HanyuPinyinCaseType.UPPERCASE);
            if (code.length() > 10){
                code = code.substring(0,10);
            }
            propValue1.setCode(code);
            log.info("-----:" + code);
            bizSkuPropValueService.save(propValue1);
        }

        return "";
    }

    @RequestMapping(value = "genVendorPropCode")
    @ResponseBody
    public String genVendorPropCode() {
        BizVendInfo propValue = new BizVendInfo();
        propValue.setDelFlag("1");
        List<BizVendInfo> propValues = bizVendInfoService.findList(propValue);
        for (BizVendInfo propValue1 : propValues) {
            String code = HanyuPinyinHelper.getFirstLetters(propValue1.getVendName(), HanyuPinyinCaseType.UPPERCASE);
            if (code.length() > 10){
                code = code.substring(0,10);
            }
            propValue1.setCode(code);
            log.info("-----:" + code);
            //set 0 to force update
            propValue1.setId(0);
            bizVendInfoService.save(propValue1);
        }

        return "success!";
    }
}
