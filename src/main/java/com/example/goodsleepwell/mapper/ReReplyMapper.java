package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.model.sleepBoardRereply;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ReReplyMapper {
    @Select("select * from sleepBoardRereply where rid = #{rid}")
    List<sleepBoardRereply> findAll(@Param("rid") final int rid);

    @Select("select count(*) from sleepBoardRereply where boardIp = #{boardIp}")
    int checkPostorNot(@Param("boardIp") final String boardIp);

    @Select("select now()-(select a.registerTime from sleepBoardRereply as a \n" +
            "where boardIp = #{boardIp} order by a.registerTime desc limit 1)>=10")
    int checkPostorNot2(@Param("boardIp") final String boardIp);

    @Select("select count(*) from sleepLikeReReplyCheck where boardIp = #{boardIp} and rrid = #{rrid}")
    int checkLike(@Param("boardIp") final String boardIp, @Param("rrid") final int rrid);

    @Insert("insert into sleepLikeReReplyCheck(likeTime,rrid,boardIp) values(now(),#{rrid},#{boardIp})")
    void likeSave(@Param("boardIp") final String boardIp, @Param("rrid") final int rrid);

    @Update("update sleepBoardRereply set likeCount = likeCount+1 where rrid = #{rrid}")
    void likeUpdate(@Param("rrid") final int rrid);

    @Select("select count(*) from sleepFireReReplyCheck where boardIp = #{boardIp} and rrid = #{rrid}")
    int checkFire(@Param("boardIp") final String boardIp,@Param("rrid") final int rrid);

    @Insert("insert into sleepFireReReplyCheck(fireTime,rrid,boardIp) values(now(),#{rrid},#{boardIp})")
    void fireSave(@Param("boardIp") final String boardIp, @Param("rrid") final int rrid);

    @Update("update sleepBoardRereply set fireCount = fireCount+1 where rrid = #{rrid}")
    void fireUpdate(@Param("rrid") final int rrid);

    @Insert("insert into sleepBoardRereply(rid,writer,rereplyContent,password,likeCount,fireCount,boardIp,id) " +
            "VALUES(#{reReply.rid}, #{reReply.writer},#{reReply.rereplyContent},#{reReply.password}," +
            "#{reReply.likeCount},#{reReply.fireCount},#{reReply.boardIp},#{reReply.id})")
    void save(@Param("reReply") final sleepBoardRereply reReply);

    @Select("select count(*) from sleepBoardRereply where rrid = #{rrid} and password = #{password}")
    int checkDelete(@Param("password") final String password, @Param("rrid") final int rrid);

    @Delete("delete from sleepBoardRereply where rrid = #{rrid} and password = #{password}")
    void delete(@Param("password") final String password,@Param("rrid") final int rrid);

}
