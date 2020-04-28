package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepLikeCheck;
import com.example.goodsleepwell.Model.sleepWellBoardContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from sleepBoardContent")
    List<sleepWellBoardContent> findAll();

    @Insert("insert into sleepBoardContent(writer,writerTitle,linkUrl,linkTitle,linkChannel,likeCount,dislikeCount,fireCount,thumbnailUrl,boardIp) " +
            "VALUES(#{swBoard.writer}, #{swBoard.writerTitle},#{swBoard.linkUrl},#{swBoard.linkTitle}," +
            "#{swBoard.linkChannel},#{swBoard.likeCount},#{swBoard.dislikeCount},#{swBoard.fireCount},#{swBoard.thumbnailUrl},#{swBoard.boardIp})")
    void save(@Param("swBoard") final sleepWellBoardContent swBoard);

    @Update("update sleepBoardContent set likeCount = likeCount+1 where id = #{id}")
    void likeupdate(@Param("id") final int id);

    @Update("update sleepBoardContent set dislikeCount = dislikeCount+1 where id = #{id}")
    void dislikeupdate(@Param("id") final int id);

    @Select("select count(*) from sleepBoardContent where boardIp = #{boardIp}")
    int checkPostorNot(@Param("boardIp") final String boardIp);

    @Select("select now()-(select a.registerTime from sleepBoardContent as a \n" +
            "where boardIp = #{boardIp} order by a.registerTime desc limit 1)>=300")
    int checkPostorNot2(@Param("boardIp") final String boardIp);

    @Select("select count(*) from sleepLikeCheck where boardIp = #{boardIp} and id = #{id}")
    int checkLike(@Param("boardIp") final String boardIp, @Param("id") final int id);

    @Insert("insert into sleepLikeCheck(id,boardIp) values(#{id},#{boardIp})")
    void likesave(@Param("boardIp") final String boardIp, @Param("id") final int id);
}
