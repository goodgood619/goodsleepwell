package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepBoardRereply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReReplyMapper {
    @Select("select * from sleepBoardRereply where rid = #{rid}")
    List<sleepBoardRereply> findAll(@Param("rid") final int rid);

    @Select("select count(*) from sleepBoardRereply where boardIp = #{boardIp}")
    int checkPostorNot(@Param("boardIp") final String boardIp);

    @Select("select now()-(select a.registerTime from sleepBoardRereply as a \n" +
            "where boardIp = #{boardIp} order by a.registerTime desc limit 1)>=300")
    int checkPostorNot2(@Param("boardIp") final String boardIp);

    @Insert("insert into sleepBoardRereply(rid,writer,rereplyContent,password,likeCount,fireCount,boardIp) " +
            "VALUES(#{reReply.rid}, #{reReply.writer},#{reReply.rereplyContent},#{reReply.password}," +
            "#{reReply.likeCount},#{reReply.fireCount},#{reReply.boardIp})")
    void save(@Param("reReply") final sleepBoardRereply reReply);
}
