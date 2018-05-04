/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.vend;

import java.util.List;

import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.dao.vend.BizVendInfoDao;

/**
 * 供应商拓展表Service
 * @author liuying
 * @version 2018-02-24
 */
@Service
@Transactional(readOnly = true)
public class BizVendInfoService extends CrudService<BizVendInfoDao, BizVendInfo> {

	@Autowired
	private BizVendInfoDao bizVendInfoDao;
	@Autowired
	private CommonImgService commonImgService;

	@Override
	public BizVendInfo get(Integer id) {
		BizVendInfo bizVendInfo = super.get(id);
		List<CommonImg> compactImgList = getImgList(id, ImgEnum.VEND_COMPACT);
		List<CommonImg> idCardImgList = getImgList(id, ImgEnum.VEND_IDENTITY_CARD);
		StringBuilder compactSb = new StringBuilder();
		StringBuilder idCardSb = new StringBuilder();
		compactImgList.forEach(o -> compactSb.append(o.getImgServer().concat(o.getImgPath())).append("|"));
		idCardImgList.forEach(o -> idCardSb.append(o.getImgServer().concat(o.getImgPath())).append("|"));
		if (bizVendInfo == null) {
			return null;
		}
		bizVendInfo.setCompactPhotos(compactSb.toString());
		bizVendInfo.setIdCardPhotos(idCardSb.toString());
		return bizVendInfo;
	}

	public List<CommonImg> getImgList(Integer id, ImgEnum imgEnum) {
		CommonImg commonImg = new CommonImg();
		commonImg.setObjectId(id);
		commonImg.setObjectName(imgEnum.getTableName());
		commonImg.setImgType(imgEnum.getCode());
		return commonImgService.findList(commonImg);
	}

	@Override
	public List<BizVendInfo> findList(BizVendInfo bizVendInfo) {
		return super.findList(bizVendInfo);
	}

	@Override
	public Page<BizVendInfo> findPage(Page<BizVendInfo> page, BizVendInfo bizVendInfo) {
		return super.findPage(page, bizVendInfo);
	}

	@Override
	@Transactional(readOnly = false)
	public void save(BizVendInfo bizVendInfo) {
		super.save(bizVendInfo);
	}
	
	@Transactional(readOnly = false)
	@Override
	public void delete(BizVendInfo bizVendInfo) {
		super.delete(bizVendInfo);
	}

	@Transactional(readOnly = false)
	public void recover(BizVendInfo bizVendInfo) {
		bizVendInfoDao.recover(bizVendInfo);
	}

	/**
	 * 供应商审核状态修改
	 * @param id 供应商ID
	 * @param status 审核状态
	 * @return 操作结果
	 */
	@Transactional(readOnly = false)
	public Pair<Boolean, String> auditSupplier(int id, int status) {
		int i = bizVendInfoDao.auditSupplier(id, status);
		boolean result = i == 1;
		String msg = result ? "操作成功" : "操作失败";
		return Pair.of(result, msg);
	}
}