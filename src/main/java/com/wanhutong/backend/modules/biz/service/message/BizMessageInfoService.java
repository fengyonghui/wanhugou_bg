/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.message;

import java.util.ArrayList;
import java.util.List;

import com.wanhutong.backend.modules.biz.dao.message.BizMessageUserDao;
import com.wanhutong.backend.modules.enums.BizMessageCompanyTypeEnum;
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
//        if (bizMessageInfo.getCompanyId() == null) {
//            LOGGER.error("save message has no companyId");
//            return Pair.of(Boolean.FALSE, "参数错误!");
//        }
        User currentUser = UserUtils.getUser();
        bizMessageInfo.setCreateBy(currentUser);
        bizMessageInfo.setUpdateBy(currentUser);
        super.save(bizMessageInfo);

        List<Integer> companyIdTypeListOriginal = bizMessageInfo.getCompanyIdTypeList();
        List<Integer> companyIdTypeList = new ArrayList<Integer>();
        Integer companyId = bizMessageInfo.getCompanyId();
        List<User> resultUserList = new ArrayList<User>();
        if (companyId != null) {
            for (Integer companyIdType : companyIdTypeListOriginal) {
                if (companyIdType == BizMessageCompanyTypeEnum.OTHER_TYPE.getType()) {
                    continue;
                }
                companyIdTypeList.add(companyIdType);
            }

            if (CollectionUtils.isNotEmpty(companyIdTypeList)) {
                resultUserList = userDao.findListByOfficeType(companyIdTypeList);
            }

            User user = new User();
            user.setCompany(new Office(bizMessageInfo.getCompanyId()));
            List<User> list = userDao.findList(user);
            resultUserList.addAll(list);
            if(CollectionUtils.isEmpty(resultUserList)) {
                return Pair.of(Boolean.FALSE, "当前选择的发送用户无有效人员!");
            }
        } else {
            resultUserList = userDao.findListByOfficeType(companyIdTypeListOriginal);
        }

//        User user = new User();
//        user.setCompany(new Office(bizMessageInfo.getCompanyId()));
//        List<User> list = userDao.findList(user);
//        if(CollectionUtils.isEmpty(list)) {
//            return Pair.of(Boolean.FALSE, "当前公司下无用户!");
//        }
//        int i = bizMessageUserDao.insertBatch(list, bizMessageInfo.getId(), DEFAULT_BIZ_STATUS);
        int i = bizMessageUserDao.insertBatch(resultUserList, bizMessageInfo.getId(), DEFAULT_BIZ_STATUS);
        LOGGER.info("save message userCount:[{}], companyId:[{}], messageId[{}]", i, bizMessageInfo.getCompanyId(), bizMessageInfo.getId());
        return Pair.of(Boolean.TRUE, "保存成功");
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(BizMessageInfo bizMessageInfo) {
        super.delete(bizMessageInfo);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void autoSendMessageInfo(String title, String content, Integer userId, String option) {
        BizMessageInfo bizMessageInfo = new BizMessageInfo();
        bizMessageInfo.setTitle(title);
        bizMessageInfo.setContent(content);
        bizMessageInfo.setBizStatus(BizMessageInfo.BizStatus.SEND_COMPLETE.getStatus());

        User user = new User();
        if ("requestHeader".equals(option)) {
            user.setCompany(new Office(userId));
        } else {
            user.setId(userId);
        }

        List<User> list = userDao.findList(user);

        if (CollectionUtils.isNotEmpty(list) && list.get(0) != null) {
            if (list.get(0).getCompany() != null) {
                bizMessageInfo.setCompanyId(list.get(0).getCompany().getId());
            }
            User currentUser = UserUtils.getUser();
            bizMessageInfo.setCreateBy(currentUser);
            bizMessageInfo.setUpdateBy(currentUser);
            super.save(bizMessageInfo);

            int i = bizMessageUserDao.insertBatch(list, bizMessageInfo.getId(), DEFAULT_BIZ_STATUS);
            LOGGER.info("save message userCount:[{}], companyId:[{}], messageId[{}]", i, bizMessageInfo.getCompanyId(), bizMessageInfo.getId());
        }
    }

}