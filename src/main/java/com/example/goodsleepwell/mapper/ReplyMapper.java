package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepBoardReply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReplyMapper {
    @Select("select * from sleepBoardReply where id = #{id}")
    List<sleepBoardReply> findAll(@Param("id") final int id);

    @Select("select count(*) from sleepBoardReply where boardIp = #{boardIp}")
    int checkPostorNot(@Param("boardIp") final String boardIp);

    @Select("select now()-(select a.registerTime from sleepBoardReply as a \n" +
            "where boardIp = #{boardIp} order by a.registerTime desc limit 1)>=300")
    int checkPostorNot2(@Param("boardIp") final String boardIp);

    @Insert("insert into sleepBoardReply(id,writer,replyContent,password,likeCount,fireCount,boardIp) " +
            "VALUES(#{reply.id}, #{reply.writer},#{reply.replyContent},#{reply.password}," +
            "#{reply.likeCount},#{reply.fireCount},#{reply.boardIp})")
    void save(@Param("reply") final sleepBoardReply reply);


}
