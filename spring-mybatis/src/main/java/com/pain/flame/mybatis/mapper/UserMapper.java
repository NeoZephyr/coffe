package com.pain.flame.mybatis.mapper;

import com.pain.flame.mybatis.entity.Order;
import com.pain.flame.mybatis.entity.User;
import com.pain.flame.mybatis.entity.UserExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/9.
 */
public interface UserMapper {

    public User selectByIdWithType(Integer id);
    public User selectByIdWithMap(Integer id);
    public List<User> selectByUsernameAndEmail(@Param("username") String username,
                                               @Param("email") String email);

    public List<User> selectByUser(User user);
    public List<User> selectByUsername(String username);
    public List<User> selectByUsernameCand(@Param("username") String username);
    public List<User> selectByIdOrUsername(User user);
    public int updateByIdSelective(User user);
    public List<User> selectByIdList(List<Integer> ids);
    public int batchInsert(List<User> users);
    public int updateByMap(@Param("user") Map<String, Object> user);


    public int insertUser(User user);

    public int insertUserCand(User user);

    public UserExt selectUserWithRoleByUserId1(Integer userId);

    public UserExt selectUserWithRoleByUserId2(Integer userId);

    public UserExt selectUserWithRoleByUserId3(Integer userId);

    public Order selectOrderById(Integer orderId);

    public Order selectOrderByIdCand1(Integer orderId);

    public Order selectOrderByIdCand2(Integer orderId);

    public UserExt selectUserById(Integer userId);
}
