package com.wanhutong.backend.modules.config.parse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("process")
public class Process {
    /**
     * 状态码
     */
    @XStreamAlias("name")
    private String name;

    /**
     * 状态码
     */
    @XStreamAlias("code")
    private int code;

    /**
     * 处理角色
     */
    @XStreamImplicit(itemFieldName = "roleEnNameEnum")
    private List<String> roleEnNameEnum;

    /**
     * 通过之后的状态
     */
    @XStreamAlias("passCode")
    private int passCode;

    /**
     * 拒绝之后的状态
     */
    @XStreamAlias("rejectCode")
    private int rejectCode;

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public List<String> getRoleEnNameEnum() {
        return roleEnNameEnum;
    }

    public int getPassCode() {
        return passCode;
    }

    public int getRejectCode() {
        return rejectCode;
    }

}