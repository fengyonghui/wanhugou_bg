/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.custom;

import java.util.List;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
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
////		String officeIds = bizCustomCenterConsultant.getOfficeIds();
//		String[] officeIdStr= StringUtils.split(officeIds,",");
//		for(int i=0;i<officeIdStr.length;i++){
//			Office office = officeService.get(Integer.parseInt(officeIdStr[i]));
//			BizCustomCenterConsultant bcc = super.get(office.getId());
//			if(bcc!=null){
////				System.out.println("  数据库已有相同采购商—不做操作 ");
//			}else{
//				bizCustomCenterConsultant.setCustoms(office);//采购商
//				bizCustomCenterConsultant.setCenters(bizCustomCenterConsultant.getCenters());//采购中心
//				bizCustomCenterConsultant.setConsultants(bizCustomCenterConsultant.getConsultants());//采购顾问
				super.save(bizCustomCenterConsultant);
//			}
//		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCustomCenterConsultant bizCustomCenterConsultant) {
		super.delete(bizCustomCenterConsultant);
	}

}