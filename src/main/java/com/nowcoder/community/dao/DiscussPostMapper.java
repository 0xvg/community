package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Discusspost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<Discusspost> selectDiscussPosts(int userId,int offset, int limit);

    //@Param注解 给参数取别名
    //如果只有一个参数，并且在if句里使用必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

}
