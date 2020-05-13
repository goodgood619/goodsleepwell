package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepBoardReply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReplyMapper {
    @Select("select * from sleepBoardReply where id = #{id}")
    List<sleepBoardReply> findAll(@Param("id") final int id);

    @Select("select count(*) from sleepBoardReply where boardIp = #{boardIp}")
    int checkPostorNot(@Param("boardIp") final String boardIp);

    @Select("select now()-(select a.registerTime from sleepBoardReply as a \n" +
            "where boardIp = #{boardIp} order by a.registerTime desc limit 1)>=10")
    int checkPostorNot2(@Param("boardIp") final String boardIp);

    @Select("select count(*) from sleepLikeReplyCheck where boardIp = #{boardIp} and rid = #{rid}")
    int checkLike(@Param("boardIp") final String boardIp, @Param("rid") final int rid);

    @Insert("insert into sleepLikeReplyCheck(likeTime,rid,boardIp) values(now(),#{rid},#{boardIp})")
    void likeSave(@Param("boardIp") final String boardIp, @Param("rid") final int rid);

    @Update("update sleepBoardReply set likeCount = likeCount+1 where rid = #{rid}")
    void likeUpdate(@Param("rid") final int rid);

    @Insert("insert into sleepBoardReply(id,writer,replyContent,password,likeCount,fireCount,boardIp) " +
            "VALUES(#{reply.id}, #{reply.writer},#{reply.replyContent},#{reply.password}," +
            "#{reply.likeCount},#{reply.fireCount},#{reply.boardIp})")
    void save(@Param("reply") final sleepBoardReply reply);


    @Select("select count(*) from sleepBoardReply where rid = #{rid} and password = #{password}")
    int checkDelete(@Param("password") final String password, @Param("rid") final int rid);

    @Delete("delete from sleepBoardReply where rid = #{rid} and password = #{password}")
    void delete(@Param("password") final String password,@Param("rid") final int rid);

    @Delete("delete from sleepLikeReplyCheck where rid = #{rid}")
    void likeBoardDelete(@Param("rid") final int rid);
}
