/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service.wx;

import java.util.List;

import com.wanhutong.backend.common.utils.CacheUtils;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.wx.SysWxPersonalUser;
import com.wanhutong.backend.modules.sys.dao.wx.SysWxPersonalUserDao;

/**
 * 注册用户Service
 * @author Oy
 * @version 2018-04-10
 */
@Service
@Transactional(readOnly = true)
public class SysWxPersonalUserService extends CrudService<SysWxPersonalUserDao, SysWxPersonalUser> {

	@Autowired
	private UserDao userDao;

	public SysWxPersonalUser get(Integer id) {
		return super.get(id);
	}
	
	public List<SysWxPersonalUser> findList(SysWxPersonalUser sysWxPersonalUser) {
		return super.findList(sysWxPersonalUser);
	}
	
	public Page<SysWxPersonalUser> findPage(Page<SysWxPersonalUser> page, SysWxPersonalUser sysWxPersonalUser) {
		return super.findPage(page, sysWxPersonalUser);
	}
	
	@Transactional(readOnly = false)
	public void save(SysWxPersonalUser sysWxPersonalUser) {
		super.save(sysWxPersonalUser);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysWxPersonalUser sysWxPersonalUser) {
		super.delete(sysWxPersonalUser);
	}

	/**
	 * 查询C端注册用户
	 * */
	public List<User> findUserByOffice() {
		List<User> list = (List<User>) CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ );
			if (list == null){
				list = userDao.findPersonalUser();
				CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ , list);
			}
		return list;
	}
}