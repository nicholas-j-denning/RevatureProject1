// TODO: clean up db, Hash.hash(), hash username in cookie, tests
package com.revature;

import com.revature.Models.Account;
import com.revature.Models.Ticket;
import java.util.List;
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
            if (username==null||password==null||legalName==null||role==null) {
                ctx.result("ERROR: Missing form parameters.");
            } else if (username.equals("")){
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
                Account account = new Account(username, password.hashCode(), legalName, role);
                if(Database.createAccount(account)) ctx.result("Account created.");
            };
        });

        // Login to an account from form parameters, return a cookie to a user
        app.post("/login", (Context ctx) -> {
                
            // Get form parameters
            String username = ctx.formParam("username");
            String password= ctx.formParam("password");
            
            // Check username and password and return a cookie
            if (username==null||password==null) {
                ctx.result("ERROR: Missing form parameters.");
            } else if (Database.isLoginValid(username, password)){
                ctx.cookieStore().set("username", username);
                ctx.result("Login Successful");
            } else {
                ctx.result("Invalid Credentials");
            }

        });

        // clear cookieStore to logout
        app.post("/logout", (Context ctx) -> {
            String username = ctx.cookieStore().get("username");
            ctx.cookieStore().clear();
            if (username == null) ctx.result("You're not logged in");
            else ctx.result("User "+username+" has been logged out");
        });

        // Sumbit a new ticket
        app.post("/submit-ticket", (Context ctx) -> {

            // try/catch needed to validate amount input
            try {
                // Get username from cookie
                String username = ctx.cookieStore().get("username");
                // Get form parameters
                String amountString = ctx.formParam("amount");
                String description = ctx.formParam("description");

                if (username == null) {
                    ctx.result("ERROR: Please login before submitting a ticket.");
                } else if (description==null||amountString==null) {
                ctx.result("ERROR: Missing form parameters.");
                } else if (description.equals("")) {
                    ctx.result("ERROR: Description cannot be blank.");
                } else {
                    float amount = Float.parseFloat(amountString);
                    Ticket ticket = new Ticket(null, username, amount, description, "Pending");
                    if (Database.createTicket(ticket)) ctx.result("Ticket submitted successfully.");
                }
            }
            catch (NumberFormatException e){
                ctx.result("ERROR: Invalid amount");
            }
        });

        // view pending tickets, only available to account type Finance Manager
        app.get("/tickets", (Context ctx) -> {
            String username = ctx.cookieStore().get("username");
            if (username == null) {
                ctx.result("ERROR: Please login to view tickets.");
            } else if (Database.isManager(username)) {
                List<Ticket> tickets = Database.listPendingTickets();
                ctx.json(tickets);
            } else {
                ctx.result("ERROR: You don't have permission to view tickets.");
            }
        });

        // approve or deny a pending ticket, only available to account type Finance Manager
        app.post("/tickets", (Context ctx) -> {
            try{
                String username = ctx.cookieStore().get("username");
                String idString = ctx.formParam("id");
                String status = ctx.formParam("status");
                if (username == null) {
                    ctx.result("ERROR: Please login to process tickets.");
                } else if (idString==null||status==null) {
                ctx.result("ERROR: Missing form parameters.");
                } else if (!status.equals("Approved")&&!status.equals("Denied")){
                    ctx.result("ERROR: Ticked status must be 'Approved' or 'Denied'.");
                } else if (Database.isManager(username)) {
                    int id = Integer.parseInt(idString);
                    if (Database.updateTicketStatus(id,status))
                        ctx.result("Ticket "+id+" "+status+".");
                    else ctx.result("Ticket "+id+" is not Pending.");
                } else {
                    ctx.result("ERROR: You don't have permission to process tickets.");
                }
            } catch (NumberFormatException e) {
                ctx.result("ERROR: Invalid id");
            }
        });
        
        // view tickets beloning to an employee
        app.get("/my-tickets", (Context ctx) -> {
            String username = ctx.cookieStore().get("username");
            if (username == null) {
                ctx.result("ERROR: Please login to view tickets.");
            } else {
                List<Ticket> tickets = Database.listEmployeeTickets(username);
                ctx.json(tickets);
            }  
        });
    }
}

