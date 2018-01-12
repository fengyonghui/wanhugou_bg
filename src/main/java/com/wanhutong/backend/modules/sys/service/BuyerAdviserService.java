package com.wanhutong.backend.modules.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.dao.BuyerAdviserDao;
import com.wanhutong.backend.modules.sys.entity.BuyerAdviser;

@Service
@Transactional(readOnly = true)
public class BuyerAdviserService extends CrudService<BuyerAdviserDao, BuyerAdviser> {

}
