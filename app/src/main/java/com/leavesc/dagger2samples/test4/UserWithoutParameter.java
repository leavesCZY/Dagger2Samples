package com.leavesc.dagger2samples.test4;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 20:34
 * 描述：
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface UserWithoutParameter {

}