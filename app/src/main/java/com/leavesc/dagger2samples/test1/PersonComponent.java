package com.leavesc.dagger2samples.test1;

import com.leavesc.dagger2samples.MainActivity;

import dagger.Component;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
@Component
public interface PersonComponent {

    void inject(MainActivity mainActivity);

}