/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 确认排产表DAO接口
 * @author 王冰洋
 * @version 2018-07-24
 */
@MyBatisDao
public interface BizCompletePalnDao extends CrudDao<BizCompletePaln> {

    /**
     * 确认排产后更改排产状态
     * @param bizCompletePaln
     */
    void updateCompleteStatus(BizCompletePaln bizCompletePaln);

    /**
     * 批量确认排产后更改排产状态
     * @param paramList
     */
    void batchUpdateCompleteStatus(@Param("paramList")List<String> paramList);
}