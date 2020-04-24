package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepWellBoardContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from sleepBoardContent")
    List<sleepWellBoardContent> findAll();

    @Insert("insert into sleepBoardContent(writer,writerTitle,linkUrl,linkTitle,linkChannel,likeCount,dislikeCount,fireCount,thumbnailUrl) " +
            "VALUES(#{swBoard.writer}, #{swBoard.writerTitle},#{swBoard.linkUrl},#{swBoard.linkTitle}," +
            "#{swBoard.linkChannel},#{swBoard.likeCount},#{swBoard.dislikeCount},#{swBoard.fireCount},#{swBoard.thumbnailUrl})")
    void save(@Param("swBoard") final sleepWellBoardContent swBoard);

    @Update("update sleepBoardContent set likeCount = likeCount+1 where id = #{id}")
    void likeupdate(@Param("id") final int id);

    @Update("update sleepBoardContent set dislikeCount = dislikeCount+1 where id = #{id}")
    void dislikeupdate(@Param("id") final int id);


}
