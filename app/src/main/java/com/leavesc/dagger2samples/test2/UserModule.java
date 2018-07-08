package com.leavesc.dagger2samples.test2;

import com.leavesc.dagger2samples.test4.UserWithParameter;
import com.leavesc.dagger2samples.test4.UserWithoutParameter;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:12
 * 描述：
 */
@Module
public class UserModule {

    private String name;

    public UserModule(String name) {
        this.name = name;
    }

    @Provides
    public String provideName() {
        return name;
    }

    @Provides
    @Singleton
//    @Named("no empty")
    @UserWithParameter
    public User provideUser(String name) {
        return new User(name);
    }

    @Provides
    @Singleton
//    @Named("empty")
    @UserWithoutParameter
    public User provideUser2() {
        return new User();
    }

    @Provides
    public User provideUser3() {
        return new User("Hello");
    }

}
