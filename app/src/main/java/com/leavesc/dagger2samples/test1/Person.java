package com.leavesc.dagger2samples.test1;

import javax.inject.Inject;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:21
 * 描述：
 */
public class Person {

    private String name;

    @Inject
    public Person() {
        name = "person default name";
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}