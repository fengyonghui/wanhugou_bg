/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 备货清单DAO接口
 * @author liuying
 * @version 2017-12-23
 */
@MyBatisDao
public interface BizRequestHeaderDao extends CrudDao<BizRequestHeader> {

    int updateProcessId(@Param("headerId")Integer headerId, @Param("processId") Integer processId);

    int findContByFromOffice(Integer fromOfficeId);

    List<BizRequestHeader> findListForPoHeader(BizRequestHeader bizRequestHeader);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    int updateREStatus(@Param("id")int id, @Param("status") Integer status);

}