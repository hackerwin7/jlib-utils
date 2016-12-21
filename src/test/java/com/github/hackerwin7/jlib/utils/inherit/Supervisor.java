package com.github.hackerwin7.jlib.utils.inherit;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/21
 * Time: 4:42 PM
 * Desc:
 * Tips:
 */
public abstract class Supervisor {

    public final int value;
    protected final String code;

    public Supervisor() {
        value = 0;
        code = "ddd";
        System.out.println(name());
    }

    private String name(String name) {
        return "supervisor_" + name;
    }

//    private String name() {
//        return "priva_" + this.getClass().getName();
//    }

    protected String name() {
        return "priva_" + this.getClass().getName();
    }
}
