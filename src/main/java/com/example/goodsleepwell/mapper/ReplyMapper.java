package com.example.goodsleepwell.mapper;

import com.example.goodsleepwell.Model.sleepBoardReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReplyMapper {
    @Select("select * from sleepBoardreply where id = #{id}")
    List<sleepBoardReply> findAll(@Param("id") final int id);


}
