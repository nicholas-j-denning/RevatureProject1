package com.revature;

import com.revature.Models.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    

    // return true if username is already in account table
    public static boolean usernameExists(String username){
        boolean result = true;
        Statement stmt = null;
		ResultSet set = null;
        String SQL = "SELECT account_username FROM account WHERE account_username='" + username +"'";

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			set = stmt.executeQuery(SQL);
            result = set.next();
	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				set.close();
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
        return result;
    }

    // return true if role is NOT in user_role table
    public static boolean isRoleInvalid(String role){
        boolean result = true;
        Statement stmt = null;
		ResultSet set = null;
        String SQL = "SELECT user_role_type FROM user_role";

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			set = stmt.executeQuery(SQL);
            Set<String> roles = new HashSet<>();
            while(set.next()) roles.add(set.getString(1));
            result = !roles.contains(role);
	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				set.close();
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
        return result;
    }

    // add a record to the account table, make sure to validate before calling this
    public static boolean createAccount(Account account){
        boolean result = false;
        Statement stmt = null;
        String SQL = "INSERT INTO account VALUES('" 
            + account.getUsername() + "', "
            + account.getPasswordHash() + ", '"
            + account.getLegalName() + "', '"
            + account.getRole() + "');";

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			stmt.execute(SQL);
            result = true;
	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}    
        return result;
    }
   
	public static boolean isLoginValid(String username, String password){
        boolean result = false;
        Statement stmt = null;
		ResultSet set = null;
        String SQL = "SELECT account_password_hash FROM account WHERE account_username='" + username +"'";

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			set = stmt.executeQuery(SQL);
            if (set.next()) {
				int passwordHash = set.getInt("account_password_hash");
				int inputHash = password.hashCode();
				result = (passwordHash == inputHash);
			}

	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				set.close();
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
        return result;
    }

    // add a record to the ticket table
    public static boolean createTicket(Ticket ticket){
        boolean result = false;
        Statement stmt = null;
        String SQL = "INSERT INTO ticket VALUES(" 
            + "DEFAULT, '" // Auto increments SQL SERIAL type
            + ticket.getUsername() + "', "
            + ticket.getAmount() + ", '"
            + ticket.getDescription() + "', '"
            + ticket.getStatus() + "');";

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			stmt.execute(SQL);
            result = true;
	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}    
        return result;
    }

    // return true if role is NOT in user_role table
    public static boolean isManager(String username){
        boolean result = false;
        Statement stmt = null;
		ResultSet set = null;
        String SQL = "SELECT account_role FROM account WHERE account_username='" +username+ "'";

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			set = stmt.executeQuery(SQL);
			set.next();
			result = set.getString("account_role").equals("Finance Manager");
				
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				set.close();
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
        return result;
    }

    // return true if username is already in account table
    public static List<Ticket> listPendingTickets(){
        Statement stmt = null;
		ResultSet set = null;
        String SQL = "SELECT * FROM ticket WHERE ticket_status='Pending'";
		List<Ticket> tickets = new ArrayList<>();

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			set = stmt.executeQuery(SQL);
			while(set.next()){
				int id = set.getInt("ticket_id");
				String username = set.getString("ticket_username");
				float amount = set.getInt("ticket_amount");
				String description = set.getString("ticket_description");
				String status = set.getString("ticket_status");
				tickets.add(new Ticket(id, username, amount, description, status));
			}
	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				set.close();
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return tickets;
    }

	// change the status of a pending ticket, validate status before calling
	public static boolean updateTicketStatus(int id, String status){
        boolean result = false;
        Statement stmt = null;
		ResultSet set = null;
        String SQL_UPDATE = "UPDATE ticket SET ticket_status='"+status+"' WHERE ticket_status='Pending' AND ticket_id="+id;
        String SQL_SELECT = "SELECT ticket_status FROM ticket WHERE ticket_id="+id;

		try(Connection conn = DriverManager.getConnection(
				System.getenv("url"), 
				System.getenv("db_username"), 
				System.getenv("db_password")
			)) {

			stmt = conn.createStatement();
			set = stmt.executeQuery(SQL_SELECT);
			set.next();
			String oldStatus = set.getString("ticket_status");
			if (oldStatus.equals("Pending")){
				stmt.execute(SQL_UPDATE);
				result = true;
			}
	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				stmt.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}    
        return result;
    }

}
