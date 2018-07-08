package com.leavesc.dagger2samples.test3;

import android.content.Context;

import dagger.Component;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:26
 * 描述：
 */
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context getContext();

}