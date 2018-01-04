package com.wanhutong.backend.modules.sys.utils;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.CacheUtils;
import com.wanhutong.backend.common.utils.SpringContextHolder;
import com.wanhutong.backend.modules.biz.dao.paltform.BizPlatformInfoDao;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class PlatformUtils {

    private static BizPlatformInfoDao bizPlatformInfoDao = SpringContextHolder.getBean(BizPlatformInfoDao.class);

    public static final String CACHE_PLATFORMINFO_MAP = "platformInfoMap";

    public static String getPlatFormName(Integer value,  String defaultValue){
        if (StringUtils.isNotBlank(Integer.toString(value))){
            for (BizPlatformInfo dict : getPlatformInfoList()){
                if (value == dict.getId()){
                    return dict.getName();
                }
            }
        }
        return defaultValue;
    }

    public static List<BizPlatformInfo> getPlatformInfoList(){
        @SuppressWarnings("unchecked")
         List<BizPlatformInfo> dictMap = (List<BizPlatformInfo>)CacheUtils.get(CACHE_PLATFORMINFO_MAP);
        List<BizPlatformInfo> platformInfoList=null;
        if (dictMap==null){
            platformInfoList=bizPlatformInfoDao.findAllList(new BizPlatformInfo());
            CacheUtils.put(CACHE_PLATFORMINFO_MAP,platformInfoList );
        }
        if (platformInfoList == null){
            platformInfoList =dictMap;
        }
        return platformInfoList;
    }
}
