package com.example.javemovil20.model;

public class User {
    private String name;
    private String lastName;
    private int age;
    private String career;
    private String email;

    public User() {
    }

    public User(String name, String lastName, int age, String career, String email) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.career = career;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
