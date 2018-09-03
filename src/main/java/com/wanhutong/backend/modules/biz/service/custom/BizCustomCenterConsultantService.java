/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.custom;

import java.util.List;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.dao.custom.BizCustomCenterConsultantDao;

/**
 * 客户专员管理Service
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
@Service
@Transactional(readOnly = true)
public class BizCustomCenterConsultantService extends CrudService<BizCustomCenterConsultantDao, BizCustomCenterConsultant> {

	@Autowired
	private OfficeService officeService;
	@Autowired
	private SystemService systemService;

	public BizCustomCenterConsultant get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCustomCenterConsultant> findList(BizCustomCenterConsultant bizCustomCenterConsultant) {
		return super.findList(bizCustomCenterConsultant);
	}
	
	public Page<BizCustomCenterConsultant> findPage(Page<BizCustomCenterConsultant> page, BizCustomCenterConsultant bizCustomCenterConsultant) {
		return super.findPage(page, bizCustomCenterConsultant);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCustomCenterConsultant bizCustomCenterConsultant) {
		Office centers = bizCustomCenterConsultant.getCenters();
		Office customs = bizCustomCenterConsultant.getCustoms();
		BizCustomCenterConsultant bcc = get(bizCustomCenterConsultant.getCustoms().getId());
		if(bcc !=null){
			if(("1").equals(bcc.getDelFlag())){
				bcc.setId(bizCustomCenterConsultant.getCustoms().getId());
				bcc.setConsultants(new User(bizCustomCenterConsultant.getConsultants().getId()));
				bcc.setCenters(new Office(centers.getId()));
				super.save(bcc);
			}else{
				bizCustomCenterConsultant.setId(customs.getId());
				bizCustomCenterConsultant.setCenters(officeService.get(centers));
				bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
				bizCustomCenterConsultant.setDelFlag("1");
				super.save(bizCustomCenterConsultant);
			}
		}else{
			bizCustomCenterConsultant.setCustoms(customs);
			bizCustomCenterConsultant.setCenters(officeService.get(centers));
			bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
			bizCustomCenterConsultant.setDelFlag("1");
			super.save(bizCustomCenterConsultant);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCustomCenterConsultant bizCustomCenterConsultant) {
		super.delete(bizCustomCenterConsultant);
	}

	@Transactional(readOnly = false)
	public List<BizCustomCenterConsultant> userFindList(BizCustomCenterConsultant bizCustomCenterConsultant) {
		return dao.userFindList(bizCustomCenterConsultant);
	}

	@Transactional(readOnly = false)
	public Page<BizCustomCenterConsultant> userFindListForPage(Page<BizCustomCenterConsultant> page, BizCustomCenterConsultant bizCustomCenterConsultant) {
		bizCustomCenterConsultant.setPage(page);
		page.setList(dao.userFindList(bizCustomCenterConsultant));
		return page;
	}

}