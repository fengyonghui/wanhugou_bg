/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.config.web;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.config.dao.DynamicConfigDao;
import com.wanhutong.backend.modules.config.entity.CommonDynamicConfig;
import com.wanhutong.backend.modules.config.entity.DynamicConfigBean;
import com.wanhutong.backend.modules.config.service.CommonDynamicConfigService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * 动态配置文件Controller
 *
 * @author Ma.Qiang
 * @version 2018-04-28
 */
@Controller
@RequestMapping(value = "${adminPath}/config/commonDynamicConfig")
public class CommonDynamicConfigController extends BaseController {
    @Resource
    private DynamicConfigDao dynamicConfigDaoImpl;

    @Autowired
    private CommonDynamicConfigService commonDynamicConfigService;

    @ModelAttribute
    public CommonDynamicConfig get(@RequestParam(required = false) Integer id) {
        CommonDynamicConfig entity = null;
        if (id != null) {
            entity = commonDynamicConfigService.get(id);
        }
        if (entity == null) {
            entity = new CommonDynamicConfig();
        }
        return entity;
    }

    @RequiresPermissions("config:commonDynamicConfig:view")
    @RequestMapping(value = {"list", ""})
    public String list(CommonDynamicConfig commonDynamicConfig, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<CommonDynamicConfig> page = commonDynamicConfigService.findPage(new Page<CommonDynamicConfig>(request, response), commonDynamicConfig);
        model.addAttribute("page", page);
        return "modules/config/commonDynamicConfigList";
    }

    /**
     * 上传热部署文件
     *
     * @param file 输入名称
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String upload(HttpServletRequest request, HttpServletResponse response) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            // 获取上传文件名
            MultipartFile file = multipartRequest.getFile("file");
            String filename = file.getOriginalFilename();
            String content = new String(file.getBytes());
            // 判断上传文件是否为空
            if (file == null || file.isEmpty()) {
                return generateData(Boolean.FALSE, "上传文件不能为空!");

            }
            if (!filename.toLowerCase().endsWith(".xml")) {
                return generateData(Boolean.FALSE, "请上传以xml结尾的文件!");
            }
            // 上传文件
            dynamicConfigDaoImpl.insertConfig(filename, content);
            // 根据输入名称模糊查询在配置文件
            return generateData(Boolean.TRUE, "上传成功!");
        } catch (Exception e) {
        } finally {
        }
        return generateData(Boolean.FALSE, "查询失败!");
    }

    private String generateData(Boolean result, String msg) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        return JSONObject.fromObject(resultMap).toString();
    }

    /**
     * 文件下载
     *
     * @return
     */
    @RequestMapping(value = "/download")
    public ResponseEntity<byte[]> download(@RequestParam("fileName") String fileName,
                                           @RequestParam("version") String version,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        long start = System.currentTimeMillis();
        File tempFile = null;
        OutputStream out = null;

        try {
            out = response.getOutputStream();
            DynamicConfigBean dynamicConfigBean = dynamicConfigDaoImpl.getDynamicConfigBeanByVersion(fileName, version);
            // 把上传的数据写到临时文件中
            InputStream in = new ByteArrayInputStream(dynamicConfigBean.getContent().getBytes());

            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentLength(in.available());
            //第三步：老套路，开始copy
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
        } finally {
        }
        return null;
    }

}