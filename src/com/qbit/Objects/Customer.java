package com.qbit.Objects;

/**
 * User: cbates
 */
public class Customer {
    private int id;
    private String name;
    private String email;
    private boolean activated;

    public Customer(String name, String email, boolean activated) {
        this.name = name;
        this.email = email;
        this.activated = activated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
