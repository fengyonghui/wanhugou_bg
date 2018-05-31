package com.wanhutong.backend.common.utils;

import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;

import java.util.List;
import java.util.Set;

public class RoleUtils {




    public static Boolean hasRole (Set<String> roleSet, String roleStr) {
        if (StringUtils.isBlank(roleStr)) {
            return false;
        }
        roleStr = roleStr.replaceAll("\\[", StringUtils.EMPTY).replaceAll("\\]", StringUtils.EMPTY);
        String[] roleArr = ",".equalsIgnoreCase(roleStr) ? new String[]{roleStr} : roleStr.split(",");
        for (String role : roleArr) {
            if (roleSet.contains(role.trim())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }



    public static Boolean hasRole (User user, List<String> roleList) {
        for (String s : roleList) {
            RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
            Role role = new Role();
            role.setEnname(roleEnNameEnum.getState());
            if (user.getRoleList().contains(role)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
