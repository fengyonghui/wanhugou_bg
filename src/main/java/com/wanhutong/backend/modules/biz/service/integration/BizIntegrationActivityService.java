/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.integration;

import java.util.*;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import com.wanhutong.backend.modules.config.CronUtils;
import com.wanhutong.backend.modules.config.web.QuartzManager;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.dao.integration.BizIntegrationActivityDao;

import javax.annotation.Resource;

/**
 * 积分活动Service
 * @author LX
 * @version 2018-09-16
 */
@Service
@Transactional(readOnly = true)
public class BizIntegrationActivityService extends CrudService<BizIntegrationActivityDao, BizIntegrationActivity>{
    @Resource
	private BizIntegrationActivityDao bizIntegrationActivityDao;
    @Resource
    private QuartzManager quartzManager;

    @Resource
	private OfficeService officeService;

	public BizIntegrationActivity get(Integer id) {
		return super.get(id);
	}
	
	public List<BizIntegrationActivity> findList(BizIntegrationActivity bizIntegrationActivity) {
		return super.findList(bizIntegrationActivity);
	}
	
	public Page<BizIntegrationActivity> findPage(Page<BizIntegrationActivity> page, BizIntegrationActivity bizIntegrationActivity) {
		return super.findPage(page, bizIntegrationActivity);
	}
	
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void save(BizIntegrationActivity bizIntegrationActivity) {
		User user = UserUtils.getUser();
		if(Objects.isNull(bizIntegrationActivity.getStatus()))
		{
			bizIntegrationActivity.setStatus(1);
		}

		bizIntegrationActivity.setSendStatus(0);
		bizIntegrationActivity.setActivityTools("万户币");
        String activityName  = bizIntegrationActivity.getActivityName();
		if(StringUtils.isNotBlank(bizIntegrationActivity.getActivityName()))
		{
			String vFullName = getFirstLetters(activityName, HanyuPinyinCaseType.UPPERCASE);
			bizIntegrationActivity.setActivityCode(vFullName);
		}
		if(bizIntegrationActivity.getActivityCode().equals("ZCS")||bizIntegrationActivity.getActivityCode().equals("ZFS")||bizIntegrationActivity.getActivityCode().equals("XDK"))
		{
			super.save(bizIntegrationActivity);
			return;
		}
		String officeIds = bizIntegrationActivity.getOfficeIds();
		List<BizIntegrationActivity> list = Lists.newArrayList();
		Integer sendScope = bizIntegrationActivity.getSendScope();
		Integer id = bizIntegrationActivity.getId();
		BizIntegrationActivity bizIntegrationActivity1 = null;
		//获取指定用户的id集合
		if(StringUtils.isNotBlank(officeIds)&&sendScope==-3)
		{
			String[] strings = officeIds.split(",");
			for(String s:strings)
			{
				  bizIntegrationActivity1 = new BizIntegrationActivity();
                  bizIntegrationActivity1.setBizStatus(0);
                  bizIntegrationActivity1.setStatus(1);
                  bizIntegrationActivity1.setUserId(Integer.valueOf(s));
                  list.add(bizIntegrationActivity1);
			}
		}
		if(!Objects.isNull(id))
		{
			//修改积分活动
			if(!Objects.isNull(user))
			{
				bizIntegrationActivity.setUpdateBy(user);
			}
			bizIntegrationActivity.setId(id);
			if(sendScope == -3)
			{
				//删除活动用户表数据
				bizIntegrationActivityDao.updateMiddleStatusByActivityId(id);

				//保存活动表
				super.save(bizIntegrationActivity);

				//添加活动用户表数据
				for (BizIntegrationActivity bizIntegrationActivity2:list)
				{
					bizIntegrationActivity2.setId(id);
				}
				bizIntegrationActivityDao.insertMiddle(list);
			}
			else
			{
				BizIntegrationActivity localIntegrationActivity = this.get(id);
				if(!localIntegrationActivity.getSendScope().equals(sendScope))
				{
					//删除活动用户表数据
					bizIntegrationActivityDao.updateMiddleStatusByActivityId(id);
					//保存活动表
					super.save(bizIntegrationActivity);
					//添加活动用户表数据
					this.insertMiddleTotal(sendScope,bizIntegrationActivity);

				}
			}
			super.save(bizIntegrationActivity);
			//修改定时任务
			quartzManager.modifyJobTime(id.toString(),id.toString(),id.toString(),id.toString(),CronUtils.getCron(bizIntegrationActivity.getSendTime()));
		}
		else
		{
			//保存积分活动
			if(!Objects.isNull(user))
			{
				bizIntegrationActivity.setCreateBy(user);
			}
			super.save(bizIntegrationActivity);
			Integer sid = bizIntegrationActivity.getId();
			for (BizIntegrationActivity bizIntegrationActivity2:list)
			{
				bizIntegrationActivity2.setId(bizIntegrationActivity.getId());
			}
			if(sendScope == -3)
			{
				//添加活动用户表数据
				bizIntegrationActivityDao.insertMiddle(list);
			}
			else
			{
				//添加活动用户表数据
				this.insertMiddleTotal(sendScope,bizIntegrationActivity);
			}
			//添加定时任务
            quartzManager.addJob(sid.toString(),sid.toString(),sid.toString(),sid.toString(),BizIntegrationTimeService.class,CronUtils.getCron(bizIntegrationActivity.getSendTime()));
		}
	}

	private void insertMiddleTotal(Integer sendScope,BizIntegrationActivity bizIntegrationActivity){
		List<Office> allOffices = Lists.newArrayList();
		List<BizIntegrationActivity> list = Lists.newArrayList();
		BizIntegrationActivity activity = null;
		if(sendScope==0)
		{
			allOffices = this.findAllOffice();
		}
		if(sendScope==-1)
		{
			allOffices = this.findOrderedOffice();
		}
		if(sendScope==-2)
		{
			allOffices = this.findUnOrderOffice();
		}
		for(Office office:allOffices)
		{
			activity = new BizIntegrationActivity();
			activity.setUserId(office.getId());
			activity.setId(bizIntegrationActivity.getId());
			activity.setBizStatus(0);
			activity.setStatus(1);
			list.add(activity);
		}
		bizIntegrationActivityDao.insertMiddle(list);
	}
	
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void delete(BizIntegrationActivity bizIntegrationActivity) {
		//判断是否为指定用户
		Integer id = bizIntegrationActivity.getId();
		bizIntegrationActivity = super.get(id);
		//删除活动用户表数据
		bizIntegrationActivityDao.updateMiddleStatusByActivityId(id);
		if(bizIntegrationActivity.getSendStatus()==0)
		{
			quartzManager.removeJob(id.toString(),id.toString(),id.toString(),id.toString());
		}
		super.delete(bizIntegrationActivity);
	}

	public BizIntegrationActivity getIntegrationByCode(String code){
         return bizIntegrationActivityDao.getIntegrationByCode(code);
	}


	public static String getFirstLetters(String chineseLanguage, HanyuPinyinCaseType caseType) {
		char[] cl_chars = chineseLanguage.trim().toCharArray();
		String hanyupinyin = "";
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(caseType);// 输出拼音全部大写
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
		try {
			for (int i = 0; i < cl_chars.length; i++) {
				String str = String.valueOf(cl_chars[i]);
				if (str.matches("[\u4e00-\u9fa5]+")) {
					// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
					hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0].substring(0, 1);
				} else if (str.matches("[0-9]+")) {
					// 如果字符是数字,取数字
					hanyupinyin += cl_chars[i];
				} else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母
					hanyupinyin += cl_chars[i];
				} else {// 否则不转换
					hanyupinyin += cl_chars[i];//如果是标点符号的话，带着
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			//log.info("字符不能转成汉语拼音");
		}
		return hanyupinyin;
	}

	//根据活动id查询对应的采购商
	 public String selectOfficeIdsByActivityId(Integer id){
	       return bizIntegrationActivityDao.selectOfficeIdsByActivityId(id);
	}

	//统计下单用户，未下单用户，全部用户数量
	public BizMoneyRecodeDetail countTotal(){
		Integer allOfficeTotal = bizIntegrationActivityDao.selectAllOfficeTotal();
		Integer orderOfficeTotal = bizIntegrationActivityDao.selectOrderOfficeTotal();
		Integer unOrderOfficeTotal = bizIntegrationActivityDao.selectUnOrderOfficeTotal();
		BizMoneyRecodeDetail bizMoneyRecodeDetail = new BizMoneyRecodeDetail();
		bizMoneyRecodeDetail.setUnOrderUser(unOrderOfficeTotal);
		bizMoneyRecodeDetail.setOrderUser(orderOfficeTotal);
		bizMoneyRecodeDetail.setTotalUser(allOfficeTotal);
		return bizMoneyRecodeDetail;
	}

	//下单的经销店列表
	public List<Office> findOrderedOffice(){
		  return bizIntegrationActivityDao.findOrderOfficeList();
	}

	//未下单的经销店列表
	public List<Office> findUnOrderOffice(){
		return bizIntegrationActivityDao.findUnOrderOfficeList();
	}

	//查询指定用户的经销店列表
	public List<Office> findCheckedOffice(String officeIds){
		List<Office> list = Lists.newArrayList();
		if(StringUtils.isNotBlank(officeIds))
		{
			String[] ss = officeIds.split(",");
			for(String s:ss)
			{
				Office office = officeService.get(Integer.valueOf(s));
				list.add(office);
			}
		}
		return list;
	}

	//全部的经销店列表
	public List<Office> findAllOffice(){
		List<Office> list = bizIntegrationActivityDao.findAllOffices();
		return list;
	}


	
}