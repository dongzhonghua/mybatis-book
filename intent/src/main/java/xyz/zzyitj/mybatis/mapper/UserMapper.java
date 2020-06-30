package xyz.zzyitj.mybatis.mapper;

import org.apache.ibatis.annotations.Select;
import xyz.zzyitj.mybatis.entity.UserEntity;
import xyz.zzyitj.mybatis.entity.UserQuery;

import java.util.List;

public interface UserMapper {

    @Select("select * from user")
    List<UserEntity> getUserPageable(UserQuery query);

}