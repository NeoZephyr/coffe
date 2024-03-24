package com.pain.green.bean.factory;

import com.pain.green.ioc.domain.User;

public interface UserFactory {
    default User createUser() {
        User user = new User();
        user.setId(5L);
        user.setName("侯景");
        return user;
    }
}
