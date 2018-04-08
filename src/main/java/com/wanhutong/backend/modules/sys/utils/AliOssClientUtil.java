package com.wanhutong.backend.modules.sys.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.FileUtils;
import com.wanhutong.backend.common.utils.IdGen;
import com.wanhutong.backend.common.utils.PropertiesLoader;
import com.wanhutong.backend.modules.sys.entity.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * 使用阿里云OSS存储对象上传图片
 *
 * @author DreamerCK
 * @date 2018-01-12 9:55
 **/
@Component
public class AliOssClientUtil {
    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = Maps.newHashMap();

    private final static String PAHT_PREFIX = "corp";
    private final static int IMG_BASE64_SPILT_SIZE = 2;
    private final static int UPLOAD_LIMIT_SIZE = 1024 * 1024 * 10;
    /**
     * 属性文件加载对象
     */
    private static PropertiesLoader loader = new PropertiesLoader("oss.properties");

    private static OSSClient ossClient = null;

    protected static Logger log = LoggerFactory.getLogger(AliOssClientUtil.class);

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    private static OSSClient getOSSClient() {
        if (ossClient == null) {
            String endpoint = getConfig("endpoint");
            ossClient = new OSSClient(endpoint, getAccessKeyId(), getAccessKeySecret());
        }
        return ossClient;
    }

    /**
     * 创建存储空间
     *
     * @param bucketName 存储空间
     * @return String
     */
    public static String createBucketName(String bucketName) {
        //存储空间
        OSSClient ossClient = getOSSClient();
        if (!ossClient.doesBucketExist(bucketName)) {
            //创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            log.info("创建存储空间成功");
            return bucket.getName();
        }
        return bucketName;
    }

    /**
     * 删除存储空间buckName
     *
     * @param bucketName 存储空间
     */
    public static void deleteBucket(String bucketName) {
        OSSClient ossClient = getOSSClient();
        ossClient.deleteBucket(bucketName);
        log.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 创建模拟文件夹
     *
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名如"qj_nanjing/"
     * @return 文件夹名
     */
    public static String createFolder(String bucketName, String folder) {
        //判断文件夹是否存在，不存在则创建
        OSSClient ossClient = getOSSClient();
        if (!ossClient.doesObjectExist(bucketName, folder)) {
            //创建文件夹
            ossClient.putObject(bucketName, folder, new ByteArrayInputStream(new byte[0]));
            log.info("创建文件夹成功");
            //得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, folder);
            return object.getKey();
        }
        return folder;
    }

    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param ossClient  oss连接
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名 如"qj_nanjing/"
     * @param key        Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public static void deleteFile(OSSClient ossClient, String bucketName, String folder, String key) {
        ossClient.deleteObject(getBucketName(), getFolder() + key);
        log.info("删除" + getBucketName() + "下的文件" + getFolder() + key + "成功");
    }

    /**
     * 上传图片至OSS
     *
     * @param is 输入流
     * @return String 返回的唯一MD5数字签名
     */
    public static String uploadObject2OSS(InputStream is, String fileName, Long fileSize, String path) {
                /*// 图片格式
        String suffix = "";*/
        String resultStr = null;
        try {
            //以输入流的形式上传文件
            //文件名
            /*String uuid = UUID.randomUUID().toString().replace("-", "");
            String newFileName = new StringBuffer(uuid).append(".").append(suffix).toString();*/
            //文件大小
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(is.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filefileName/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件   (上传文件流的形式)
            OSSClient ossClient = getOSSClient();
//            String bucketName = bucketName;
//            String folder = folder;
            PutObjectResult putResult = null;
            if (path.indexOf(".") > 0){
                putResult = ossClient.putObject(getBucketName(), path, is, metadata);
            }else {
                putResult = ossClient.putObject(getBucketName(), path + fileName, is, metadata);
            }
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;

    }
    /**
     * 上传图片至OSS
     *
     * @param file 上传文件（文件全路径如：D:\\image\\cake.jpg）
     * @return String 返回的唯一MD5数字签名
     */
    public static String uploadObject2OSS(File file, String path) {
        /*// 图片格式
        String suffix = "";*/
        String resultStr = null;
        try {
            //以输入流的形式上传文件
            InputStream is = new FileInputStream(file);

            //文件名
            String fileName = file.getName();
            /*String uuid = UUID.randomUUID().toString().replace("-", "");
            String newFileName = new StringBuffer(uuid).append(".").append(suffix).toString();*/
            //文件大小
            Long fileSize = file.length();
            resultStr = uploadObject2OSS(is, fileName, fileSize, path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * 上传base64图片至OSS
     *
     * @param imgPath    上传路径
     * @param base64Data base64图片数据
     * @return String 返回的唯一MD5数字签名
     */
    public static String uploadBase64Object2OSS(String imgPath, String base64Data) {
        // 图片格式
        String dataSuffix = "";
        // 图片内容
        String data = "";
        String resultStr = null;
        try {
            if (StringUtils.isBlank(base64Data)) {

                throw new Exception();
            } else {
                String[] d = base64Data.split("base64,");

                if (d.length == IMG_BASE64_SPILT_SIZE) {
                    dataSuffix = d[0];
                    data = d[1];
                } else {
                    throw new Exception();
                }
            }
            String suffix = getContentTypeByBase64(dataSuffix);
            // 因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
            int index = data.indexOf("=");
            if (index > 0) {
                data = data.substring(0, index);
            }
            byte[] bs = Base64.decodeBase64(data);
            int length = data.length();
            int fileLength = length - (length / 8) * 2;
            //限制文件大小为4m以内
            if (fileLength > UPLOAD_LIMIT_SIZE) {
                throw new Exception();
            }
            //以输入流的形式上传文件
            InputStream is = new ByteArrayInputStream(bs);
            //文件名
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String newFileName = new StringBuffer(uuid).append(".").append(suffix).toString();
            //文件大小
            Integer fileSize = fileLength;
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(is.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(suffix);
            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filefileName/filesize=" + newFileName + "/" + fileSize + "Byte.");
            //上传文件   (上传文件流的形式)
//            String bucketName = bucketName;
//            String folder = folder;
            String finalImgPath = new StringBuilder().append(getFolder()).append(imgPath).append(newFileName).toString();
            OSSClient ossClient = getOSSClient();
            PutObjectResult putResult = ossClient.putObject(getBucketName(), finalImgPath, is, metadata);
            //解析结果
            putResult.getETag();
            resultStr = finalImgPath;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        log.info("resultStr" + resultStr);
        return resultStr;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    private static String getContentType(String fileName) {
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        //默认返回类型
        return "image/jpeg";
    }

    private static String getContentTypeByBase64(String dataSuffix) throws Exception {
        //文件的后缀名
        String suffix = "";
        // data:image/jpeg;base64,base64编码的jpeg图片数据
        if ("data:image/jpeg;".equalsIgnoreCase(dataSuffix) || "data:image/jpg;".equalsIgnoreCase(dataSuffix)) {
            suffix = ".jpg";
            // data:image/x-icon;base64,base64编码的icon图片数据
        } else if ("data:image/x-icon;".equalsIgnoreCase(dataSuffix)) {

            suffix = ".ico";
            // data:image/gif;base64,base64编码的gif图片数据
        } else if ("data:image/gif;".equalsIgnoreCase(dataSuffix)) {

            suffix = ".gif";
            // data:image/png;base64,base64编码的png图片数据
        } else if ("data:image/png;".equalsIgnoreCase(dataSuffix)) {

            suffix = ".png";
        } else {
            throw new Exception("上传图片格式不合法");
        }

        return suffix;
    }

    public static String getPahtPrefix() {
        return PAHT_PREFIX;
    }

    public static int getImgBase64SpiltSize() {
        return IMG_BASE64_SPILT_SIZE;
    }

    public static int getUploadLimitSize() {
        return UPLOAD_LIMIT_SIZE;
    }

    public static String getEndpoint() {
        String endpoint = getConfig("endpoint");
        return endpoint;
    }

    public static String getAccessKeyId() {
        String accessKeyId = getConfig("accessKeyId");
        return accessKeyId;
    }

    public static String getAccessKeySecret() {
        String accessKeySecret = getConfig("accessKeySecret");
        return accessKeySecret;
    }

    public static String getBucketName() {
        String bucketName = getConfig("bucketName");
        return bucketName;
    }

    public static String getFolder() {
        String folder = getConfig("folder");
        return folder;
    }

    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            value = loader.getProperty(key);
            map.put(key, value != null ? value : com.wanhutong.backend.common.utils.StringUtils.EMPTY);
        }
        return value;
    }

    /**
     * 获取当前用户的OSS 文件上传路径
     * @return OSS 文件上传路径
     */
    public static String getOssUploadPath(){
        User user = UserUtils.getUser();
        String pathPrefix = AliOssClientUtil.getPahtPrefix();
        String datePart = DateUtils.formatDate(new Date()).replaceAll("-", "");
        String ossFolder = AliOssClientUtil.getFolder();
        String ossPath =  ossFolder + "/"+ pathPrefix + user.getCompany().getId() +"/" + user.getId() + "/" + datePart + "/";

        return ossPath;
    }

    public static String getRandomFileName(String type){
        return IdGen.uuid()+"."+type;
    }

    /**
     * 将文件上传至阿里云OSS服务器
     * @param pathFile 需上传的文件，全路径
     * @param delAfterUpload 上传后，是否删除当前文件
     * @return
     */
    public static String uploadFile(String pathFile, boolean delAfterUpload){
        String ossPath = getOssUploadPath();
        String fileType = FileUtils.getFileExtension(pathFile);
        String newFileName = getRandomFileName(fileType);
        String uploadFullPathWithName = ossPath + newFileName;
        File file = new File(pathFile);
        AliOssClientUtil.uploadObject2OSS(file, uploadFullPathWithName);

        if (delAfterUpload && file.exists()) {
            file.delete();
        }
        return uploadFullPathWithName;
    }
}
