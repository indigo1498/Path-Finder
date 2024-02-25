package com.example.pathfinder;

public class HelperClass {


    public HelperClass(String name, String number, String username, String password, boolean isMale) {
        this.name = name;
        this.number = number;
        this.username = username;
        this.password = password;
        this.isMale = isMale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    String name, number, username, password;
    boolean isMale;
    public HelperClass(String name, String number, String username, String password, Boolean isMale) {
        this.name = name;
        this.number = number;
        this.username = username;
        this.password = password;
        this.isMale = isMale;
    }

}
