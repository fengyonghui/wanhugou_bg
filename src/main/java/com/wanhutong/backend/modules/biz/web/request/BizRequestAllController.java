package com.wanhutong.backend.modules.biz.web.request;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestAll")
public class BizRequestAllController {

    @RequestMapping(value = {"list", ""})
    public String list(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader){
        model.addAttribute(source);
        model.addAttribute("bizRequestHeader",bizRequestHeader);
        model.addAttribute("bizOrderHeader",bizOrderHeader);
        return "modules/biz/request/bizRequestAllList";
    }
}
