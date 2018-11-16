package com.cbw.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by cbw on 2018/11/15.
 */
@Entity
public class User {

    @Id(autoincrement = true)
    Long id;

    int studentNo;
    int age;
    String name;

    @Generated(hash = 1366721128)
    public User(Long id, int studentNo, int age, String name) {
        this.id = id;
        this.studentNo = studentNo;
        this.age = age;
        this.name = name;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getStudentNo() {
        return this.studentNo;
    }
    public void setStudentNo(int studentNo) {
        this.studentNo = studentNo;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", studentNo=" + studentNo +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
