package com.example.testapp;

//태형
//도어락 정보를 가진 JSON객체
public class DoorLock {
    private String password;
    private int status; //아두이노의 servo_ 와 통신하기 위한 변수
    private int longPress; //비밀번호 변경 플래그
    private int attempt;
    private long lockTime;
    private long appLock;

    public DoorLock(){}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLongPress() {
        return longPress;
    }

    public void setLongPress(int longPress) {
        this.longPress = longPress;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public long getAppLock() {
        return appLock;
    }

    public void setAppLock(long appLock) {
        this.appLock = appLock;
    }
}
