package com.user.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.usercenter.mapper.TagMapper;
import com.user.usercenter.model.Tag;
import com.user.usercenter.service.TagService;
import org.springframework.stereotype.Service;

/**
 * @author zmj
 * @description 针对表【tag】的数据库操作Service实现
 * @createDate 2023-12-01 15:30:34
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

}




