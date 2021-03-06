package com.wanhutong.backend.common.utils;

import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;

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

    public static Boolean hasRoleByProcess(Set<String> roleSet, com.wanhutong.backend.modules.config.parse.Process roleStr) {
        if (roleStr == null) {
            return false;
        }
        List<String> roleEnNameEnum = roleStr.getRoleEnNameEnum();
        if (CollectionUtils.isEmpty(roleEnNameEnum)) {
            return Boolean.FALSE;
        }
        for (String role : roleEnNameEnum) {
            if (roleSet.contains(role.trim())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }



    public static Boolean hasRole (User user, List<String> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return Boolean.FALSE;
        }

        List<Role> userRoleList = user.getRoleList();
        for (String s : roleList) {
            RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
            Role role = new Role();
            role.setEnname(roleEnNameEnum.getState());
            if (userRoleList.contains(role)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static Boolean hasPermission(String marking) {
        Boolean flag = false;
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            flag = true;
        } else {
            List<String> permissionAllList = UserUtils.getPermissionAllList();
            for (String permission:permissionAllList) {
                if (permission.equals(marking)){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
