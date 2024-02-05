package com.passwordnotes.dao;

public class Account {
    private int id;
    private String tag;
    private String name;
    private String password;
    private String remark;
    private int weight;
    private long time;
    private int isDelete;
    private int priority;

    public Account() {
    }

    public Account(String tag, String name, String password, String remark, int weight, long time, int isDelete, int priority) {
        this.tag = tag;
        this.name = name;
        this.password = password;
        this.remark = remark;
        this.weight = weight;
        this.time = time;
        this.isDelete = isDelete;
        this.priority = priority;
    }

    public Account(int id, String tag, String name, String password, String remark, int weight, long time, int isDelete, int priority) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.password = password;
        this.remark = remark;
        this.weight = weight;
        this.time = time;
        this.isDelete = isDelete;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", remark='" + remark + '\'' +
                ", weight=" + weight +
                ", time=" + time +
                ", isDelete=" + isDelete +
                ", priority=" + priority +
                '}';
    }
}
