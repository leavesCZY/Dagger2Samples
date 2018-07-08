package com.leavesc.dagger2samples.test2;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:11
 * 描述：
 */
public class User {

    private String name;

    public User() {
        name = "user default name";
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
