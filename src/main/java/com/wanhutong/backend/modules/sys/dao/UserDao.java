/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.wx.SysWxPersonalUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {
	
	/**
	 * 根据登录名称查询用户
	 * @param loginName
	 * @return
	 */
	public User getByLoginName(User user);

	/**
	 * 根据OfficeId获取当前节点下的所有字节点
	 * @param office
	 * @return
	 */
	public List<User> findYzUser(Office office);
	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);

	/**
	 * 通过OfficeId获取用户列表，查询采购中心关联客户专员使用
	 *
	 * @param user
	 * @return
	 */
	public default List<User> selectUserByOfficeId(User user) {
		return null;
	}

	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);
	
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);

	/**
	 * 会员搜索
	 */
	public List<User> contact(User user);

	/**
	 * 会员搜索
	 */
	public List<User> findUserByCompany(Integer type);

	public List<User> systemCompany();

	void recovery(User user);

	/**
	 * 根据角色取所有用户
	 * @param roleId
	 * @return
	 */
	List<User> findUserByRole(int roleId);

	/**
	 * 根据角色enname取所有用户
	 * @param roleEnName
	 * @return
	 */
	List<User> findUserByRoleEnName(String roleEnName);


	/**
	 * 查询C端注册用户
	 * */
	public List<User> findPersonalUser();

	/**
	 * 沟通记录查询 客户专员和品类主管
	 * */
	public List<User> userSelectCompany(User user);

	/**
	 * 查询供应商和主负责人信息
	 * @return
	 */
	User findVendUser(@Param("orderId") Integer orderId);

	/**
	 * 查询供应商和主负责人信息
	 * @return
	 */
	List<User> findVendUserV2(@Param("orderId") Integer orderId);

	/**
	 * 品类主管 管理
	 * */
	List<User> findSeleList(User user);

	/**
	 * 根据user的officeType获取用户列表
	 * @param companyIdTypeList
	 * @return
	 */
	List<User> findListByOfficeType(@Param("companyIdTypeList") List<Integer> companyIdTypeList);

}
