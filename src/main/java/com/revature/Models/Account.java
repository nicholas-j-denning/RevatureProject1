package com.revature.Models;

import java.util.Objects;

public class Account {
    private String username;
    private String passwordHash;
    private String legalName;
    private String role;


    public Account() {
    }

    public Account(String username, String passwordHash, String legalName, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.legalName = legalName;
        this.role = role;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getLegalName() {
        return this.legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Account username(String username) {
        setUsername(username);
        return this;
    }

    public Account passwordHash(String passwordHash) {
        setPasswordHash(passwordHash);
        return this;
    }

    public Account legalName(String legalName) {
        setLegalName(legalName);
        return this;
    }

    public Account role(String role) {
        setRole(role);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Account)) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(username, account.username) && Objects.equals(passwordHash, account.passwordHash) && Objects.equals(legalName, account.legalName) && Objects.equals(role, account.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwordHash, legalName, role);
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", passwordHash='" + getPasswordHash() + "'" +
            ", legalName='" + getLegalName() + "'" +
            ", role='" + getRole() + "'" +
            "}";
    }

}