package com.wanhutong.backend.common.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.web.po.BizPoHeaderController;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件相关controller
 * @author Ma.Qiang
 * @date 20180717
 */
@Controller
@RequestMapping(value = "${adminPath}/file")
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizPoHeaderController.class);

    @RequestMapping(value = "upload")
    public void uploadFile(MultipartHttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件名
        List<MultipartFile> files = multipartRequest.getFiles("file");
        List<String> fileList = Lists.newArrayList();
        for (MultipartFile file : files) {
            if (file != null) {
                String originalFilename = file.getOriginalFilename();
                String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
                try {
                    String result = AliOssClientUtil.uploadObject2OSS(file.getInputStream(), fullName, file.getSize(), AliOssClientUtil.getOssUploadPath());
                    if (StringUtils.isNotBlank(result)) {
                        fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                        fileList.add(fullName);
                    }
                } catch (Exception e) {
                    LOGGER.error("upload file error:", e);
                }
            }
        }

        resultMap.put("fileList", fileList);
        try {
            response.getWriter().write(JsonUtil.generateData(resultMap, null));
        } catch (IOException e) {
            LOGGER.error("upload file response writer error:", e);
        }
    }

    @RequestMapping(value = "page")
    public String page() {
        return "modules/biz/uploadFileTest";
    }

    @RequestMapping(value = "video")
    public String video(HttpServletRequest request, String src) {
        request.setAttribute("src", src);
        return "modules/biz/common/video";
    }
}
