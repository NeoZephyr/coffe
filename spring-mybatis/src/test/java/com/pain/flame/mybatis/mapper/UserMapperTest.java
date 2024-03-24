package com.pain.flame.mybatis.mapper;

import com.pain.flame.mybatis.entity.*;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Created by Administrator on 2018/9/9.
 */
public class UserMapperTest extends BaseMapperTest {

    /********************* 简单查询 *********************/
    @Test
    public void testSelectByIdWithType() {
        SqlSession sqlSession = getSqlSession();
        try {
            User user = sqlSession.selectOne("com.pain.flame.mybatis.mapper.UserMapper.selectByIdWithType", 1);
            System.out.println(user);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByIdWithMap() {
        SqlSession sqlSession = getSqlSession();
        try {
            User user = sqlSession.selectOne("com.pain.flame.mybatis.mapper.UserMapper.selectByIdWithMap", 1);
            System.out.println(user);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByUsernameAndEmail() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = userMapper.selectByUsernameAndEmail("李师师", "shi.li@qq.com");
            System.out.println(users);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByIdWithProxy() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByIdWithMap(1);
        System.out.println(user);
        user.setUsername("赞数据");
        User user1 = userMapper.selectByIdWithMap(1);
        System.out.println(user1);
        sqlSession.close();
    }

    /********************* 数据插入 *********************/
    @Test
    public void testInsertUser() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = new User();
            user.setUsername("李道宗");
            user.setPassword("blala");
            user.setEmail("li.dao@qq.com");
            user.setBirth(new Date());
            int line = userMapper.insertUser(user);
            System.out.println("line: " + line);
            System.out.println(user);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsertUserCand() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = new User();
            user.setUsername("李师师");
            user.setPassword("99876uiugfa9uej");
            user.setEmail("shi.li@qq.com");
            user.setBirth(new Date());
            int line = userMapper.insertUserCand(user);
            System.out.println("line: " + line);
            System.out.println(user);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    /********************* 动态 sql *********************/
    @Test
    public void testSelectByUser() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User queryUser = new User();
        queryUser.setUsername("仁");
        queryUser.setEmail("renwei.liu@163.com");
        List<User> users = userMapper.selectByUser(queryUser);
        System.out.println(users);
    }

    @Test
    public void testSelectByIdOrUsername() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User queryUser = new User();
        queryUser.setId(1);
        queryUser.setUsername("page");
        List<User> users = userMapper.selectByIdOrUsername(queryUser);
        System.out.println(users);
    }

    @Test
    public void testUpdateByIdSelective() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User updateUser = new User();
        updateUser.setId(1);
        updateUser.setUsername("page");
        updateUser.setEmail("blibli@u.com");
        int num = userMapper.updateByIdSelective(updateUser);
        System.out.println("update record num: " + num);
        sqlSession.commit();
    }

    @Test
    public void testSelectByIdList() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<Integer> ids = new ArrayList<>();
        ids.addAll(Arrays.asList(1, 5, 7));
        List<User> users = userMapper.selectByIdList(ids);
        System.out.println(users);
    }

    @Test
    public void testBatchInsert() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            User user = new User();
            user.setUsername("李时" + i);
            user.setPassword("123456");
            user.setEmail(user.getUsername() + "@163.com");
            user.setBirth(new Date());
            users.add(user);
        }

        int result = userMapper.batchInsert(users);
        System.out.println("result: " + result);
        System.out.println(users);
        sqlSession.commit();
    }

    @Test
    public void testUpdateByMap() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("id", 1);
        updateUser.put("username", "李世民");
        updateUser.put("email", "lishiming@gmail.oom");

        int num = userMapper.updateByMap(updateUser);
        System.out.println("update record num: " + num);
        sqlSession.commit();
    }

    @Test
    public void testSelectByUsername() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = userMapper.selectByUsername("pa");
            System.out.println(users);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByUsernameCand() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = userMapper.selectByUsernameCand("pa");
            System.out.println(users);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByUsernameWithProxy() {
        SqlSession sqlSession = getSqlSession();
        List<User> users = sqlSession.selectList("selectByUsername", "pa");
//        List<User> users = sqlSession.selectList("com.pain.mapper.UserMapper.selectByUsername", "pa");
        System.out.println(users);
        sqlSession.close();
    }

    /********************* 一对多 *********************/
    @Test
    public void testSelectUserWithRoleByUserId1() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        UserExt userExt = userMapper.selectUserWithRoleByUserId1(5);
        System.out.println(userExt);
    }

    @Test
    public void testSelectUserWithRoleByUserId2() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        UserExt userExt = userMapper.selectUserWithRoleByUserId2(5);
        System.out.println(userExt);
    }

    @Test
    public void testSelectUserWithRoleByUserId3() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        UserExt userExt = userMapper.selectUserWithRoleByUserId3(11);
        System.out.println(userExt);
    }

    @Test
    public void testSelectOrderById() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        Order order = userMapper.selectOrderById(1);
        System.out.println(order);
    }

    @Test
    public void testSelectOrderByIdCand1() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        Order order = userMapper.selectOrderByIdCand1(4);
        System.out.println(order);
    }

    @Test
    public void testSelectOrderByIdCand2() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        Order order = userMapper.selectOrderByIdCand2(4);
        System.out.println(order);
    }

//    延迟加载
    @Test
    public void testSelectUserById() {
        SqlSession sqlSession = getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        UserExt user = userMapper.selectUserById(11);

        System.out.println("用户名：" + user.getUsername());
        for (Role role : user.getRoles()) {
            RoleExt re = (RoleExt) role;
            System.out.println("角色名：" + re.getName());
            for (Privilege privilege : re.getPrivileges()) {
                System.out.println("权限名：" + privilege.getName());
            }
        }
    }
}