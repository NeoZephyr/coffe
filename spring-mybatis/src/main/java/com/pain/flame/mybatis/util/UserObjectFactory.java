package com.pain.flame.mybatis.util;

import com.pain.flame.mybatis.entity.User;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

public class UserObjectFactory extends DefaultObjectFactory {
    @Override
    public <T> T create(Class<T> type) {
        if (type == User.class) {
            User user = (User) super.create(type);
            user.setPassword("******");
            return (T) user;
        } else {
            return super.create(type);
        }
    }
}