/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.*;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.excel.fieldtype.OfficeType;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.modules.sys.dao.BizCustCreditDao;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.entity.BizCustCredit;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;

import static com.wanhutong.backend.common.persistence.BaseEntity.DEL_FLAG_NORMAL;

/**
 * 机构Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {
	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private BizCustCreditDao bizCustCreditDao;

	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll){
		if (isAll != null && isAll){
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList();
		}
	}
	
	@Transactional(readOnly = true)
	public List<Office> findList(Office office){
		if(office != null){
			office.setParentIds(office.getParentIds()+"%");
			return dao.findByParentIdsLike(office);
		}
		return  new ArrayList<Office>();
	}

	public List<Office> filerOffice(List<Office> offices, OfficeTypeEnum officeType){
		Office office = new Office();
			User user = UserUtils.getUser();
			if (officeType.getType().equals(OfficeTypeEnum.CUSTOMER.getType())) {
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "so", "pp"));

			} else {
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
			}

		office.setType(String.valueOf(officeType.ordinal()));

		office.setDelFlag(DEL_FLAG_NORMAL);

		List<Office> list = queryList(office);
		//get all parents
		Set<Integer> parentSet = new HashSet<>();
		for (Office office1 : list){
			String[] parentIds = office1.getParentIds().split(",");
			for (String id : parentIds){
				parentSet.add(Integer.valueOf(id));
			}
		}

		if(offices == null || offices.size() == 0){
			office.setType(null);
			offices = queryList(office);
		}

		Iterator<Office> iterator = offices.iterator();

			while(iterator.hasNext()){
				Office office1 = iterator.next();
				Integer id = office1.getId();
				if(!parentSet.contains(id) && !String.valueOf(officeType.ordinal()).equals(office1.getType()))
					iterator.remove();   //注意这个地方
			}



		return offices;

	}

	public List<Office> queryList(Office office){
		return officeDao.queryList(office);
	}

	public List<Office> queryCenterList(Office office) {
		User user = UserUtils.getUser();
		office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
		return officeDao.queryList(office);
	}


	@Transactional(readOnly = false)
	public void save(Office office) {
		super.save(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	//创建采购商同时创建钱包
	@Transactional(readOnly = false)
	public void save(Office office,BizCustCredit bizCustCredit) {
		super.save(office);
		if(office.getIsNewRecord() || bizCustCredit.getOfficeId()== null){
			if(bizCustCredit != null ){
				bizCustCredit.setCreateBy(office.getCreateBy());
				bizCustCredit.setOfficeId(office.getId());
				bizCustCredit.setPayPwd(SystemService.entryptPassword(DictUtils.getDictValue("密码", "payment_password", "")));
				bizCustCredit.setuVersion(1);
				bizCustCreditDao.insert(bizCustCredit);
			}
		}else{ 
			bizCustCredit.setUpdateBy(office.getUpdateBy());
			bizCustCreditDao.update(bizCustCredit);
		}
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
}
