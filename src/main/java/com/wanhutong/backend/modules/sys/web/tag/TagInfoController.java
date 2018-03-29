package com.wanhutong.backend.modules.sys.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.tag.TagInfo;
import com.wanhutong.backend.modules.sys.service.tag.TagInfoService;

/**
 * 标签属性表Controller
 * @author zx
 * @version 2018-03-19
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/tag/tagInfo")
public class TagInfoController extends BaseController {

    @Autowired
    private TagInfoService tagInfoService;

    @ModelAttribute
    public TagInfo get(@RequestParam(required=false) Integer id) {
        TagInfo entity = null;
        if (id!=null){
            entity = tagInfoService.get(id);
        }
        if (entity == null){
            entity = new TagInfo();
        }
        return entity;
    }

    @RequiresPermissions("sys:tag:tagInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(TagInfo tagInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<TagInfo> page = tagInfoService.findPage(new Page<TagInfo>(request, response), tagInfo);
        model.addAttribute("page", page);
        return "modules/sys/tag/tagInfoList";
    }

    @RequiresPermissions("sys:tag:tagInfo:view")
    @RequestMapping(value = "form")
    public String form(TagInfo tagInfo, Model model) {
        model.addAttribute("tagInfo", tagInfo);
        return "modules/sys/tag/tagInfoForm";
    }

    @RequiresPermissions("sys:tag:tagInfo:edit")
    @RequestMapping(value = "save")
    public String save(TagInfo tagInfo, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, tagInfo)){
            return form(tagInfo, model);
        }
        tagInfoService.save(tagInfo);
        addMessage(redirectAttributes, "保存标签属性成功");
        return "redirect:"+Global.getAdminPath()+"/sys/tag/tagInfo/?repage";
    }

    @RequiresPermissions("sys:tag:tagInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(TagInfo tagInfo, RedirectAttributes redirectAttributes) {
        tagInfoService.delete(tagInfo);
        addMessage(redirectAttributes, "删除标签属性成功");
        return "redirect:"+Global.getAdminPath()+"/sys/tag/tagInfo/?repage";
    }

}
