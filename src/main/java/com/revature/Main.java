package com.revature;

import com.revature.Models.Account;

import com.revature.Database;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class Main{
    public static void main(String[] args) {

        // Start Server on port 8000
        Javalin app = Javalin.create().start(8000);

        // Create account from form parameters
        app.post("/create-account", (Context ctx) -> {
            
            // Get form parameters
            String username = ctx.formParam("username");
            String password= ctx.formParam("password");
            String legalName = ctx.formParam("legalName");
            String role = ctx.formParam("role");

            // Basic validity checks on input
            if (username.equals("")){
                ctx.result("ERROR: Username cannot be blank.");
            } else if (Database.usernameExists(username)){
                ctx.result("ERROR: Username is already taken.");
            } else if (password.equals("")){
                ctx.result("ERROR: Password cannot be blank.");
            } else if (legalName.equals("")){
                ctx.result("ERROR: Legal Name cannot be blank.");
            } else if (Database.isRoleInvalid(role)){
                ctx.result("ERROR: Invalid role.");
            } else {
                // If input is vald, add account the the database
                // TODO: 
                Account account = new Account(username, password.hashCode(), legalName, role);
                Boolean b = Database.createAccount(account);
                ctx.result(b.toString());
            };
        });


    }
}

