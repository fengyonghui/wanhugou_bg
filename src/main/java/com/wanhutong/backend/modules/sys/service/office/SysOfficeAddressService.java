/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service.office;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.dao.office.SysOfficeAddressDao;

import javax.annotation.Resource;

/**
 * 地址管理(公司+详细地址)Service
 * @author OuyangXiutian
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class SysOfficeAddressService extends CrudService<SysOfficeAddressDao, SysOfficeAddress> {
	@Autowired
	private CommonLocationService commonLocationService;
	@Resource
	private SysOfficeAddressDao sysOfficeAddressDao;

	public SysOfficeAddress get(Integer id) {
		return super.get(id);
	}
	
	public List<SysOfficeAddress> findList(SysOfficeAddress sysOfficeAddress) {
		return super.findList(sysOfficeAddress);
	}
	
	public Page<SysOfficeAddress> findPage(Page<SysOfficeAddress> page, SysOfficeAddress sysOfficeAddress) {

			User user=UserUtils.getUser();
		boolean flag=false;
		boolean roleFlag = false;
		if(user.getRoleList()!=null){
			for(Role role:user.getRoleList()){
				if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname()) || RoleEnNameEnum.WAREHOUSESPECIALIST.getState().equals(role.getEnname())){
					flag=true;
					break;
				}
				if (RoleEnNameEnum.BUYER.getState().equals(role.getEnname())){
					roleFlag = true;
				}
			}
		}
		if(user.isAdmin()){
			return super.findPage(page, sysOfficeAddress);
		}else {
			if(sysOfficeAddress.getOffice()!=null && StringUtils.isNotBlank(sysOfficeAddress.getOffice().getType())&&sysOfficeAddress.getOffice().getType().equals(OfficeTypeEnum.VENDOR.getType())){
				return super.findPage(page, sysOfficeAddress);
			}else {
				if(flag) {
					sysOfficeAddress.getSqlMap().put("officeAddress", BaseService.dataScopeFilter(user, "s", "su"));
				}else if(roleFlag){
					sysOfficeAddress.setCon(user);
				}
				return super.findPage(page, sysOfficeAddress);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void save(SysOfficeAddress sysOfficeAddress) {
		if(sysOfficeAddress.getBizLocation()!=null){
			commonLocationService.updateCommonLocation(sysOfficeAddress.getBizLocation());
		}
		Integer statu=sysOfficeAddress.getDeFaultStatus();
		if(statu == OrderHeaderBizStatusEnum.ORDER_DEFAULTSTATUS.getState()){//1
			SysOfficeAddress address = new SysOfficeAddress();
//			BeanUtils.copyProperties(sysOfficeAddress,address);
			address.setType(null);
			address.setOffice(sysOfficeAddress.getOffice());
			List<SysOfficeAddress> list = super.findList(address);
			if(list.size()!=0){
				for (SysOfficeAddress add : list) {
					address = super.get(add);
					address.setDeFaultStatus(0);
					super.save(address);
				}
			}
		}
		super.save(sysOfficeAddress);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysOfficeAddress sysOfficeAddress) {
		super.delete(sysOfficeAddress);
	}


	public List<SysOfficeAddress> orderHeaderfindList(SysOfficeAddress sysOfficeAddress) {
		return sysOfficeAddressDao.orderHeaderfindList(sysOfficeAddress);
	}

	public List<SysOfficeAddress> findListByTypes(SysOfficeAddress sysOfficeAddress){
		return sysOfficeAddressDao.findListByTypes(sysOfficeAddress);
	}
}