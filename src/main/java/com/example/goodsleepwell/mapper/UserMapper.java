package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepWellBoardContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select a.id,a.writer,a.writerTitle,a.linkUrl,a.linkTitle,a.linkChannel,a.likeCount,a.dislikeCount,a.fireCount," +
            "a.thumbnailUrl,a.boardIp from sleepBoardContent as a")
    List<sleepWellBoardContent> findAll();

    @Insert("insert into sleepBoardContent(writer,writerTitle,linkUrl,linkTitle,linkChannel,likeCount,dislikeCount,fireCount,thumbnailUrl,boardIp,password) " +
            "VALUES(#{swBoard.writer}, #{swBoard.writerTitle},#{swBoard.linkUrl},#{swBoard.linkTitle}," +
            "#{swBoard.linkChannel},#{swBoard.likeCount},#{swBoard.dislikeCount},#{swBoard.fireCount},#{swBoard.thumbnailUrl},#{swBoard.boardIp},#{swBoard.password})")
    void save(@Param("swBoard") final sleepWellBoardContent swBoard);

    @Update("update sleepBoardContent set likeCount = likeCount+1 where id = #{id}")
    void likeUpdate(@Param("id") final int id);

    @Update("update sleepBoardContent set dislikeCount = dislikeCount+1 where id = #{id}")
    void dislikeUpdate(@Param("id") final int id);

    @Select("select count(*) from sleepBoardContent where boardIp = #{boardIp}")
    int checkPostorNot(@Param("boardIp") final String boardIp);

    @Select("select now()-(select a.registerTime from sleepBoardContent as a \n" +
            "where boardIp = #{boardIp} order by a.registerTime desc limit 1)>=300")
    int checkPostorNot2(@Param("boardIp") final String boardIp);

    @Select("select count(*) from sleepLikeCheck where boardIp = #{boardIp} and id = #{id}")
    int checkLike(@Param("boardIp") final String boardIp, @Param("id") final int id);

    @Insert("insert into sleepLikeCheck(likeTime,id,boardIp) values(now(),#{id},#{boardIp})")
    void likeSave(@Param("boardIp") final String boardIp, @Param("id") final int id);

    @Select("select count(*) from sleepBoardContent where id = #{id} and password = #{password}")
    int checkDelete(@Param("password") final String password, @Param("id") final int id);

    @Delete("delete from sleepBoardContent where id = #{id} and password = #{password}")
    void delete(@Param("password") final String password,@Param("id") final int id);

    @Delete("delete from sleepLikeCheck where id = #{id}")
    void likeBoardDelete(@Param("id") final int id);
}
