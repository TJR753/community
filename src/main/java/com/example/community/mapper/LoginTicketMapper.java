package com.example.community.mapper;

import com.example.community.domain.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated
public interface LoginTicketMapper {

    @Insert("insert into login_ticket(user_id,ticket,status,expired) values(#{userId},#{ticket},#{status},#{expired})")
    void insert(LoginTicket ticket);

    @Update("update login_ticket set status=1 where ticket=#{ticket}")
    void updateStatus(String ticket);

    @Select("select * from login_ticket where ticket=#{ticket}")
    LoginTicket findTicket(String ticket);
}
