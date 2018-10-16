/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderUnlineDao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 线下支付订单(线下独有)Service
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderUnlineService extends CrudService<BizOrderHeaderUnlineDao, BizOrderHeaderUnline> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizOrderHeaderUnlineService.class);
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderStatusService bizOrderStatusService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private CommonImgService commonImgService;

	public BizOrderHeaderUnline get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderHeaderUnline> findList(BizOrderHeaderUnline bizOrderHeaderUnline) {
		return super.findList(bizOrderHeaderUnline);
	}
	
	public Page<BizOrderHeaderUnline> findPage(Page<BizOrderHeaderUnline> page, BizOrderHeaderUnline bizOrderHeaderUnline) {
		return super.findPage(page, bizOrderHeaderUnline);
	}

	/**
	 * 审核
	 * @param bizOrderHeaderUnline
	 */
	@Transactional(readOnly = false)
	public void save(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.save(bizOrderHeaderUnline);
		BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
        BigDecimal money = new BigDecimal(bizOrderHeader.getTotalDetail()+bizOrderHeader.getTotalExp()+bizOrderHeader.getFreight());
        if (bizOrderHeader.getBizStatus().equals(OrderHeaderBizStatusEnum.UNPAY.getState())) {
            if (money.compareTo(bizOrderHeaderUnline.getRealMoney())>=0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.INITIAL_PAY.getState());
            }
            if (money.compareTo(bizOrderHeaderUnline.getRealMoney())==0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
		}
		if (bizOrderHeader.getBizStatus().equals(OrderHeaderBizStatusEnum.INITIAL_PAY.getState())) {
            if (money.compareTo(bizOrderHeaderUnline.getRealMoney())==0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
        }
		bizOrderHeader.setReceiveTotal(bizOrderHeader.getReceiveTotal() + bizOrderHeaderUnline.getRealMoney().doubleValue());
		bizOrderHeaderService.saveOrderHeader(bizOrderHeader);

		/*用于 订单状态表 insert状态*/
		if (money.compareTo(bizOrderHeaderUnline.getRealMoney())>=0) {
			bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.INITIAL_PAY.getState());
		}
		if (money.compareTo(bizOrderHeaderUnline.getRealMoney())==0) {
			bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
		}
        bizOrderStatusService.saveOrderStatus(bizOrderHeader);
	}

	/**
	 * 保存
	 * @param bizOrderHeaderUnline
	 */
	@Transactional(readOnly = false)
	public void saveOffLine(BizOrderHeaderUnline bizOrderHeaderUnline) {
		BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
		bizOrderHeaderUnline.setSerialNum("0");
		super.save(bizOrderHeaderUnline);
		User user = systemService.getUser(orderHeader.getConsultantId());
		String offLineorderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.FO, orderHeader.getCustomer().getId(), user.getCompany().getId(), bizOrderHeaderUnline.getId());
		bizOrderHeaderUnline.setSerialNum(offLineorderNum);
		super.save(bizOrderHeaderUnline);
		saveCommonImg(bizOrderHeaderUnline);
	}

	/**
	 * 保存物流信息图片
	 *
	 * @param bizOrderHeaderUnline
	 */
	@Transactional(readOnly = false)
	public void saveCommonImg(BizOrderHeaderUnline bizOrderHeaderUnline) {
		String imgUrl = null;
		try {
			imgUrl = URLDecoder.decode(bizOrderHeaderUnline.getImgUrls(), "utf-8");//主图转换编码
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("物流信息图转换编码异常.", e);
		}
		if (imgUrl != null) {
			String[] photoArr = imgUrl.split("\\|");
			saveLogisticsImg(ImgEnum.UNlINE_PAYMENT_VOUCHER.getCode(), bizOrderHeaderUnline, photoArr);
		}
		List<CommonImg> commonImgs = getImgList(ImgEnum.UNlINE_PAYMENT_VOUCHER.getCode(), bizOrderHeaderUnline.getId());
		for (int i = 0; i < commonImgs.size(); i++) {
			CommonImg commonImg = commonImgs.get(i);
			commonImg.setImgSort(i);
			commonImgService.save(commonImg);
		}
	}

	private List<CommonImg> getImgList(Integer imgType, Integer bizInvoiceId) {
		CommonImg commonImg = new CommonImg();
		commonImg.setObjectId(bizInvoiceId);
		commonImg.setObjectName(ImgEnum.UNlINE_PAYMENT_VOUCHER.getTableName());
		commonImg.setImgType(imgType);
		return commonImgService.findList(commonImg);
	}

	public void saveLogisticsImg(Integer imgType, BizOrderHeaderUnline bizOrderHeaderUnline, String[] photoArr) {
		if (bizOrderHeaderUnline.getId() == null) {
			LOGGER.error("Can't save logistics image without bizOrderHeaderUnline ID!");
			return;
		}

		List<CommonImg> commonImgs = getImgList(imgType, bizOrderHeaderUnline.getId());

		Set<String> existSet = new HashSet<>();
		for (CommonImg commonImg1 : commonImgs) {
			existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
		}
		Set<String> newSet = new HashSet<>(Arrays.asList(photoArr));

		Set<String> result = new HashSet<String>();
		//差集，结果做删除操作
		result.clear();
		result.addAll(existSet);
		result.removeAll(newSet);
		for (String url : result) {
			for (CommonImg commonImg1 : commonImgs) {
				if (url.equals(commonImg1.getImgServer() + commonImg1.getImgPath())) {
					commonImg1.setDelFlag("0");
					commonImgService.delete(commonImg1);
				}
			}
		}
		//差集，结果做插入操作
		result.clear();
		result.addAll(newSet);
		result.removeAll(existSet);

		CommonImg commonImg = new CommonImg();
		commonImg.setObjectId(bizOrderHeaderUnline.getId());
		commonImg.setObjectName(ImgEnum.UNlINE_PAYMENT_VOUCHER.getTableName());
		commonImg.setImgType(imgType);
		commonImg.setImgSort(40);
		for (String name : result) {
			if (name.trim().length() == 0 || name.contains(DsConfig.getImgServer())) {
				continue;
			}
			String pathFile = Global.getUserfilesBaseDir() + name;
			String ossPath = AliOssClientUtil.uploadFile(pathFile, true);

			commonImg.setId(null);
			commonImg.setImgPath("/" + ossPath);
			commonImg.setImgServer(DsConfig.getImgServer());
			commonImgService.save(commonImg);
		}
	}

	@Transactional(readOnly = false)
	public void saveOrderOffline(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.save(bizOrderHeaderUnline);
	}

	@Transactional(readOnly = false)
	public void delete(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.delete(bizOrderHeaderUnline);
	}
	
}