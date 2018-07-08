package com.leavesc.dagger2samples.test2;

import com.leavesc.dagger2samples.Main2Activity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:14
 * 描述：
 */
@Component(modules = {UserModule.class})
@Singleton
public interface UserComponent {

    void inject(Main2Activity mainActivity);

}
