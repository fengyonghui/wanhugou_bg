package com.wanhutong.backend.common.utils;

import java.util.Set;

public class RoleUtils {




    public static Boolean hasRole (Set<String> roleSet, String roleStr) {
        if (StringUtils.isBlank(roleStr)) {
            return false;
        }
        String[] roleArr = ",".equalsIgnoreCase(roleStr) ? new String[]{roleStr} : roleStr.split(",");
        for (String role : roleArr) {
            if (roleSet.contains(role)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
