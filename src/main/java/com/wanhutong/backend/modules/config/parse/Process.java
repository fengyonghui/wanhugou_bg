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
     * 列表筛选过滤条件是否显示
     */
    @XStreamAlias("showFilter")
    private String showFilter;

    /**
     * 通过之后的状态
     */
    @XStreamAlias("passCode")
    private int passCode;

    /**
     * 通过之后的状态 全部支付
     */
    @XStreamAlias("allPassCode")
    private int allPassCode;

    /**
     * 通过之后的状态 20%支付
     */
    @XStreamAlias("fifthPassCode")
    private int fifthPassCode;

    /**
     * 通过之后的状态 0%支付
     */
    @XStreamAlias("zeroPassCode")
    private int zeroPassCode;


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

    public String getShowFilter() {
        return showFilter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setRoleEnNameEnum(List<String> roleEnNameEnum) {
        this.roleEnNameEnum = roleEnNameEnum;
    }

    public void setShowFilter(String showFilter) {
        this.showFilter = showFilter;
    }

    public void setPassCode(int passCode) {
        this.passCode = passCode;
    }

    public void setRejectCode(int rejectCode) {
        this.rejectCode = rejectCode;
    }

    public int getAllPassCode() {
        return allPassCode;
    }

    public void setAllPassCode(int allPassCode) {
        this.allPassCode = allPassCode;
    }

    public int getFifthPassCode() {
        return fifthPassCode;
    }

    public void setFifthPassCode(int fifthPassCode) {
        this.fifthPassCode = fifthPassCode;
    }

    public int getZeroPassCode() {
        return zeroPassCode;
    }

    public void setZeroPassCode(int zeroPassCode) {
        this.zeroPassCode = zeroPassCode;
    }
}