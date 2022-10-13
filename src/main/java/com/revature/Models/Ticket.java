package com.revature.Models;

import java.util.Objects;

public class Ticket {
    private Integer id; // should be null if the ticket is not yet in the database 
    private String username;
    private float amount;
    private String description;
    private String status;

    public Ticket() {
    }

    public Ticket(Integer id, String username, float amount, String description, String status) {
        this.id = id;
        this.username = username;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Ticket id(Integer id) {
        setId(id);
        return this;
    }

    public Ticket username(String username) {
        setUsername(username);
        return this;
    }

    public Ticket amount(float amount) {
        setAmount(amount);
        return this;
    }

    public Ticket description(String description) {
        setDescription(description);
        return this;
    }

    public Ticket status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Ticket)) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) && Objects.equals(username, ticket.username) && amount == ticket.amount && Objects.equals(description, ticket.description) && Objects.equals(status, ticket.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, amount, description, status);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", username='" + getUsername() + "'" +
            ", amount='" + getAmount() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

}
