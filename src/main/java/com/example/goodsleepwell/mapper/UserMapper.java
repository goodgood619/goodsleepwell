package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepWellBoardContent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from sleepBoardContent")
    List<sleepWellBoardContent> findAll();

    @Insert("insert into sleepBoardContent(writer,writerTitle,linkUrl,linkTitle,linkChannel,likeCount,dislikeCount,fireCount) " +
            "VALUES(#{swBoard.writer}, #{swBoard.writerTitle},#{swBoard.linkUrl},#{swBoard.linkTitle}," +
            "#{swBoard.linkChannel},#{swBoard.likeCount},#{swBoard.dislikeCount},#{swBoard.fireCount})")
    void save(@Param("swBoard") final sleepWellBoardContent swBoard);
}
