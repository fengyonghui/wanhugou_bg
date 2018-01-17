package com.wanhutong.backend.modules.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.dao.BizCustCreditDao;
import com.wanhutong.backend.modules.sys.entity.BizCustCredit;

@Service
@Transactional(readOnly = true)
public class BizCustCreditService extends CrudService<BizCustCreditDao, BizCustCredit> {

}
