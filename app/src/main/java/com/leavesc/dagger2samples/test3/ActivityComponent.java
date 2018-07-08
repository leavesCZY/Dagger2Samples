package com.leavesc.dagger2samples.test3;

import com.leavesc.dagger2samples.Main3Activity;
import com.leavesc.dagger2samples.MainActivity;

import dagger.Component;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:33
 * 描述：
 */
@Component(dependencies = {ApplicationComponent.class}, modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(Main3Activity mainActivity);

}