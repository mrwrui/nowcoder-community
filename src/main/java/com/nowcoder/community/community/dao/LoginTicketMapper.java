package com.nowcoder.community.community.dao;

import com.nowcoder.community.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    // 这里演示在注解里面写sql，优点是很方便，不用新建一个html文件，缺点是不便于阅读
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired}) "
    })

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_Id,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //这里也是能写sql判断的语句的，和html中差不多，不过要写if语句必须要套上<script></script>
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
