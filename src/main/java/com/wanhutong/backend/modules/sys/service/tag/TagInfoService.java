package com.wanhutong.backend.modules.sys.service.tag;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.tag.TagInfo;
import com.wanhutong.backend.modules.sys.dao.tag.TagInfoDao;

/**
 * 标签属性表Service
 * @author zx
 * @version 2018-03-19
 */
@Service
@Transactional(readOnly = true)
public class TagInfoService extends CrudService<TagInfoDao, TagInfo> {
    @Override
    public TagInfo get(Integer id) {
        return super.get(id);
    }
    @Override
    public List<TagInfo> findList(TagInfo tagInfo) {
        return super.findList(tagInfo);
    }
    @Override
    public Page<TagInfo> findPage(Page<TagInfo> page, TagInfo tagInfo) {
        return super.findPage(page, tagInfo);
    }

    @Transactional(readOnly = false)
    @Override
    public void save(TagInfo tagInfo) {
        super.save(tagInfo);
    }
    @Override
    @Transactional(readOnly = false)
    public void delete(TagInfo tagInfo) {
        super.delete(tagInfo);
    }

}
