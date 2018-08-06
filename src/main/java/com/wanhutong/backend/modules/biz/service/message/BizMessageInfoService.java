/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.message;

import java.util.List;

import com.wanhutong.backend.modules.biz.dao.message.BizMessageUserDao;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageInfo;
import com.wanhutong.backend.modules.biz.dao.message.BizMessageInfoDao;

import javax.annotation.Resource;

/**
 * 站内信Service
 *
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@Service
@Transactional(readOnly = true)
public class BizMessageInfoService extends CrudService<BizMessageInfoDao, BizMessageInfo> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BizMessageInfoService.class);

    private static final Integer DEFAULT_BIZ_STATUS = 0;

    @Resource
    private BizMessageUserDao bizMessageUserDao;
    @Resource
    private UserDao userDao;


    @Override
    public BizMessageInfo get(Integer id) {
        return super.get(id);
    }

    @Override
    public List<BizMessageInfo> findList(BizMessageInfo bizMessageInfo) {
        return super.findList(bizMessageInfo);
    }

    @Override
    public Page<BizMessageInfo> findPage(Page<BizMessageInfo> page, BizMessageInfo bizMessageInfo) {
        return super.findPage(page, bizMessageInfo);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> saveMessage(BizMessageInfo bizMessageInfo) {
        if (bizMessageInfo.getCompanyId() == null) {
            LOGGER.error("save message has no companyId");
            return Pair.of(Boolean.FALSE, "参数错误!");
        }
        User currentUser = UserUtils.getUser();
        bizMessageInfo.setCreateBy(currentUser);
        bizMessageInfo.setUpdateBy(currentUser);
        super.save(bizMessageInfo);
        User user = new User();
        user.setCompany(new Office(bizMessageInfo.getCompanyId()));
        List<User> list = userDao.findList(user);
        if(CollectionUtils.isEmpty(list)) {
            return Pair.of(Boolean.FALSE, "当前公司下无用户!");
        }
        int i = bizMessageUserDao.insertBatch(list, bizMessageInfo.getId(), DEFAULT_BIZ_STATUS);
        LOGGER.info("save message userCount:[{}], companyId:[{}], messageId[{}]", i, bizMessageInfo.getCompanyId(), bizMessageInfo.getId());
        return Pair.of(Boolean.TRUE, "保存成功");
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(BizMessageInfo bizMessageInfo) {
        super.delete(bizMessageInfo);
    }

}