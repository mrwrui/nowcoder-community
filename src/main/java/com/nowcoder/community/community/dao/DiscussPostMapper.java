package com.nowcoder.community.community.dao;

import com.nowcoder.community.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    // 首页上并不需要用户的Id，但是之后的功能开发用户的个人主页的功能，需要传入用户的个人Id。则这个方法有时需要传入用户Id有时不用，此时需要动态的拼一个条件。此时的sql是动态的sql。
    // offset是每一页起始的行号，limit是每一页最多显示多少数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param是一个注解用来给参数取一个别名
    // 如果只有一个参数，并且在<if>里使用，必须要加这个注解加别名
    int selectDiscussPostRows(@Param("userId") int userId);

}
