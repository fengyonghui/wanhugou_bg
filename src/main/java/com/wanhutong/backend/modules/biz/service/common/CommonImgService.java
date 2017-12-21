/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.common;

import java.util.List;

import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.enums.ImgEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.dao.common.CommonImgDao;

/**
 * 通用图片Service
 * @author zx
 * @version 2017-12-18
 */
@Service
@Transactional(readOnly = true)
public class CommonImgService extends CrudService<CommonImgDao, CommonImg> {
	@Autowired
	private CommonImgDao commonImgDao;

	public CommonImg get(Integer id) {
		return super.get(id);
	}
	
	public List<CommonImg> findList(CommonImg commonImg) {
		return super.findList(commonImg);
	}
	
	public Page<CommonImg> findPage(Page<CommonImg> page, CommonImg commonImg) {
		return super.findPage(page, commonImg);
	}
	
	@Transactional(readOnly = false)
	public void save(CommonImg commonImg) {
		super.save(commonImg);
	}
	
	@Transactional(readOnly = false)
	public void delete(CommonImg commonImg) {
		super.delete(commonImg);
	}

	@Transactional(readOnly = false)
	public void saveCommonImg(BizProductInfo bizProductInfo) {
		String photos=bizProductInfo.getPhotos();
		if(photos!=null){
			CommonImg commonImg=new CommonImg();
			photos=photos.substring(1);
			String[]photoArr=photos.split("\\|");
			if(photoArr.length>=1){
				commonImg.setImgType(ImgEnum.MAIN_PRODUCT_TYPE.getCode());
				commonImg.setObjectId(bizProductInfo.getId());
				commonImg.setObjectName("biz_product_info");
				commonImgDao.deleteCommonImg(commonImg);
				for (int i=0;i<photoArr.length;i++){
					commonImg.setImgPath(photoArr[i]);
					commonImg.setImgSort(i);
					commonImg.setImgServer(DsConfig.getImgServer());
					super.save(commonImg);
				}

			}


		}
	}
	
}