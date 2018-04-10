/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.*;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.custom.BizCustomCenterConsultantDao;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
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
	private BizCustCreditService bizCustCreditService;
	@Autowired
	private BizCustomCenterConsultantDao bizCustomCenterConsultantDao;
	@Autowired
	private CommonLocationService commonLocationService;
	@Autowired
	private SysOfficeAddressService sysOfficeAddressService;
	@Autowired
	private SystemService systemService;


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
			User user = UserUtils.getUser();
			if(!user.isAdmin()&&!OfficeTypeEnum.VENDOR.getType().equals(office.getType())&&!OfficeTypeEnum.CUSTOMER.getType().equals(office.getType())){
				office.setDataStatus("filter");
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
			}
			else if(!user.isAdmin()&&OfficeTypeEnum.CUSTOMER.getType().equals(office.getType())){
				boolean flag=false;
				boolean flagb=false;
				if(user.getRoleList()!=null){
					for(Role role:user.getRoleList()){
						if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
							flag=true;
							break;
						}else if(RoleEnNameEnum.BUYER.getState().equals(role.getName())){
							flagb=true;
							break;
						}
					}
				}
				BizCustomCenterConsultant customCenterConsultant=new BizCustomCenterConsultant();
				if(flag){
					customCenterConsultant.setCenters(user.getCompany());
				}else if(flagb){
					customCenterConsultant.setConsultants(user);
				}
				customCenterConsultant.setParentIds(office.getParentIds());

				List<Office> officeList=officeDao.findOfficeByIdToParent(customCenterConsultant);

				return  officeList;
			}
			return dao.findByParentIdsLike(office);
		}
		return  new ArrayList<Office>();
	}

	public List<Office> filerOffice(List<Office> offices,String source, OfficeTypeEnum officeType){
		Office office = new Office();
		User user = UserUtils.getUser();
		if(!user.isAdmin()&& !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) &&!OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())){
			office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
		}
		else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))){

		}
		else if(!user.isAdmin()&&OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())){
			boolean flag=false;
			boolean flagb=false;
			if(user.getRoleList()!=null){
				for(Role role:user.getRoleList()){
					if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
						flag=true;

					}else if(RoleEnNameEnum.BUYER.getState().equals(role.getEnname())){
						flagb=true;

					}
				}
			}
			BizCustomCenterConsultant customCenterConsultant=new BizCustomCenterConsultant();
			if(flag){
				customCenterConsultant.setCenters(user.getCompany());

				List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

				return officeList;
			}else if(flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
				customCenterConsultant.setCenters(user.getCompany());
				if(StringUtils.isNotBlank(source) && source.equals("purchaser")){
					customCenterConsultant.setConsultants(user);
				}
				List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

				return officeList;
			}
			else if(flagb || (flag&& StringUtils.isNotBlank(source) && source.equals("con"))){
				office.setType(String.valueOf(officeType.ordinal()));

				office.setDelFlag(DEL_FLAG_NORMAL);
				List<Office> officeList =	officeDao.findOfficeCustByIdToParent(office);
				return officeList;
			}
		}

		office.setType(String.valueOf(officeType.ordinal()));

		office.setDelFlag(DEL_FLAG_NORMAL);

		List<Office> list = queryList(office);
		//get all parents
		Set<Integer> parentSet = new HashSet<>();
		for (Office office1 : list) {
			String[] parentIds = office1.getParentIds().split(",");
			for (String id : parentIds) {
				parentSet.add(Integer.valueOf(id));
			}
		}

		if (offices == null || offices.size() == 0) {
			office.setType(null);
			//	office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "so", ""));
			offices = queryList(office);
		}

		Iterator<Office> iterator = offices.iterator();
		while (iterator.hasNext()) {
			Office office1 = iterator.next();
			Integer id = office1.getId();
			if (!parentSet.contains(id) && !String.valueOf(officeType.ordinal()).equals(office1.getType()))
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
		CommonLocation commonLocation =null;
		if(office.getBizLocation()!=null && !office.getBizLocation().getAddress().equals("")){
			if(office.getAddress()!=null && office.getBizLocation().getProvince()==null && office.getBizLocation().getCity()==null){
				CommonLocation commonLocation1 = commonLocationService.get(Integer.parseInt(office.getAddress()));
				office.getBizLocation().setAddress(office.getBizLocation().getAddress());
				office.getBizLocation().setId(Integer.parseInt(office.getAddress()));
				office.getBizLocation().setProvince(commonLocation1.getProvince());
				office.getBizLocation().setCity(commonLocation1.getCity());
				office.getBizLocation().setRegion(commonLocation1.getRegion());
			}
			commonLocation = commonLocationService.updateCommonLocation(office.getBizLocation());
		}
		if(commonLocation!=null){
			/**
			 * 用于保存地址
			 * */
			SysOfficeAddress officeAddress = new SysOfficeAddress();
			if(office.getAddress()!=null){
				officeAddress.setId(Integer.parseInt(office.getAddress()));
			}
			officeAddress.setOffice(office);
			officeAddress.setBizLocation(commonLocation);
			officeAddress.setReceiver(String.valueOf(office.getMaster()));
			officeAddress.setPhone(String.valueOf(office.getPhone()));
			String offType = DictUtils.getDictValue("公司地址", "office_type", "");
			String sysDefau = DictUtils.getDictValue("默认", "sysadd_deFault", "");
			if(offType!=null){
				officeAddress.setType(Integer.parseInt(offType));
			}else {
				officeAddress.setType(2);
			}
			if(sysDefau!=null){
				officeAddress.setDeFaultStatus(Integer.parseInt(sysDefau));
			}else{
				officeAddress.setDeFaultStatus(1);
			}
			sysOfficeAddressService.save(officeAddress);
			office.setAddress(String.valueOf(officeAddress.getId()));
		}
		super.save(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	//创建采购商同时创建钱包
	@Transactional(readOnly = false)
	public void save(Office office,BizCustCredit bizCustCredit) {
		super.save(office);
		if(bizCustCredit.getId()== null){
			if(bizCustCredit != null ){
//				bizCustCredit.setId(office.getId());
				bizCustCredit.setCustomer(office);
				bizCustCredit.setPayPwd(SystemService.entryptPassword(DictUtils.getDictValue("密码", "payment_password", "")));
				bizCustCredit.setuVersion(1);
				bizCustCredit.setCustFalg("officeCust");
				bizCustCreditService.save(bizCustCredit);
			}
		}else{
			bizCustCreditService.save(bizCustCredit);
		}
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	public Page<Office> findPage(Page<Office> page, Office office) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, office);
		}else {
			boolean flag=false;
			boolean flagb=false;
			if(user.getRoleList()!=null){
				for(Role role:user.getRoleList()){
					if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
						flag=true;

					}else if(RoleEnNameEnum.BUYER.getState().equals(role.getEnname())){
						flagb=true;

					}
				}
			}
			if(flag){
				office.setCenterId(user.getCompany().getId());
			}else if(flagb){
				office.setConsultantId(user.getId());
			}
			//office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
			return super.findPage(page, office);
		}
	}

	/**
	 * 用于客户专员查询采购中心
	 * */
	public List<Office> CustomerfilerOffice(List<Office> offices,String source, OfficeTypeEnum officeType){
		Office office = new Office();
		User user = UserUtils.getUser();
		if(!user.isAdmin()&& !OfficeTypeEnum.VENDOR.getType().equals(officeType.getType()) &&!OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType()) && user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType())
				|| user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) || user.getCompany().getType().equals(OfficeTypeEnum.NETWORKSUPPLY.getType())){
			office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
		}
		else if (StringUtils.isNotBlank(source) && (source.equals("ghs") || source.equals("gys") || source.equals("cgs"))){

		}
		else if(!user.isAdmin()&&OfficeTypeEnum.CUSTOMER.getType().equals(officeType.getType())){
			boolean flag=false;
			boolean flagb=false;
			if(user.getRoleList()!=null){
				for(Role role:user.getRoleList()){
					if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
						flag=true;
						break;
					}else if(RoleEnNameEnum.BUYER.getState().equals(role.getEnname())){
						flagb=true;
						break;
					}
				}
			}
			BizCustomCenterConsultant customCenterConsultant=new BizCustomCenterConsultant();
			if(flag && StringUtils.isBlank(source)){
				customCenterConsultant.setCenters(user.getCompany());

				List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

				return officeList;
			}else if(flagb && StringUtils.isNotBlank(source) && source.equals("purchaser")) {
				customCenterConsultant.setCenters(user.getCompany());
				if(StringUtils.isNotBlank(source) && source.equals("purchaser")){
					customCenterConsultant.setConsultants(user);
				}
				List<Office> officeList = officeDao.findOfficeByIdToParent(customCenterConsultant);

				return officeList;
			}
			else if(flagb || (flag&& StringUtils.isNotBlank(source) && source.equals("con"))){
				office.setType(String.valueOf(officeType.ordinal()));

				office.setDelFlag(DEL_FLAG_NORMAL);
				List<Office> officeList =	officeDao.findOfficeCustByIdToParent(office);
				return officeList;
			}
		}

		office.setType(String.valueOf(officeType.ordinal()));
		office.setCustomerTypeTen(String.valueOf(officeType.WITHCAPITAL.getType()));
		office.setCustomerTypeEleven(String.valueOf(officeType.NETWORKSUPPLY.getType()));
		office.setDelFlag(DEL_FLAG_NORMAL);

		List<Office> list = queryList(office);
		//get all parents
		Set<Integer> parentSet = new HashSet<>();
		for (Office office1 : list) {
			String[] parentIds = office1.getParentIds().split(",");
			for (String id : parentIds) {
				parentSet.add(Integer.valueOf(id));
			}
		}

		if (offices == null || offices.size() == 0) {
			office.setType(null);
			//	office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "so", ""));
			offices = queryList(office);
		}

		Iterator<Office> iterator = offices.iterator();
		while (iterator.hasNext()) {
			Office office1 = iterator.next();
			Integer id = office1.getId();
			if (!parentSet.contains(id) && !String.valueOf(officeType.ordinal()).equals(office1.getType()) &&
					!String.valueOf(10).equals(office1.getType()) && !String.valueOf(11).equals(office1.getType()))
				iterator.remove();   //注意这个地方
		}

		return offices;

	}

    /**
     *取所有的供应商
     * @param id
     * @return
     */
	public List<Office> findVendor(String id){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(",").append(id).append(",");
        String parentIds = stringBuffer.toString();
        return  officeDao.findVent(parentIds);
	}

	/**
	 * 取多个类型的office
	 * @param typeList
	 * @return
	 */
	public List<Office> findListByTypeList(List<String> typeList) {
		return officeDao.findListByTypeList(typeList);
	}

	/**
	 * 用于查询配资下边的采购商
	 * @param cust
	 * @return
	 */
	public List<Office> findCapitalList(Office cust){
		return officeDao.findList(cust);
	}
}
