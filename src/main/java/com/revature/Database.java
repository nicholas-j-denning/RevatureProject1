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
import java.sql.PreparedStatement;

abstract class Database {

	private static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(
			System.getenv("url"), 
			System.getenv("db_username"), 
			System.getenv("db_password")
		);
	}

	// Return true if a username is already in the db
    public static boolean usernameExists(String username){
        boolean result = true;

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = selectUsername(conn,username);
			ResultSet set = stmt.executeQuery()
		) {

            result = set.next();
	
		} catch(SQLException e) {
			e.printStackTrace();
		}
        return result;
    }

	// Creates prepared statement for usernameExists method
	private static PreparedStatement selectUsername(Connection connection, String username) throws SQLException{
        String SQL = "SELECT account_username FROM account WHERE account_username=?";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1, username);
		return stmt;
	}

    // return true if role is NOT in user_role table
    public static boolean isRoleInvalid(String role){
        boolean result = true;
        String SQL = "SELECT user_role_type FROM user_role";

		try(
			Connection conn = getConnection(); 
			Statement stmt = conn.createStatement(); 
			ResultSet set = stmt.executeQuery(SQL)
		) {

            Set<String> roles = new HashSet<>();
            while(set.next()) roles.add(set.getString(1));
            result = !roles.contains(role);
	
		}catch(SQLException e) {
			e.printStackTrace();
		}
        return result;
    }

    // add a record to the account table, make sure to validate before calling this
    public static boolean createAccount(Account account){
        boolean result = false;

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = insertAccount(conn, account);
		) {

			stmt.execute();
            result = true;
	
		}catch(SQLException e) {
			e.printStackTrace();
		}    
        return result;
	
	}
 
	// Creates prepared statement for createAccount method
	private static PreparedStatement insertAccount(Connection connection, Account account) throws SQLException{
        String SQL = "INSERT INTO account VALUES(?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1, account.getUsername());
		stmt.setInt(2, account.getPasswordHash());
		stmt.setString(3, account.getLegalName());
		stmt.setString(4, account.getRole());
		return stmt;
 	}
   
	public static boolean isLoginValid(String username, String password){
        boolean result = false;

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = selectPassword(conn, username);
			ResultSet set = stmt.executeQuery()
		) {

            if (set.next()) {
				int passwordHash = set.getInt("account_password_hash");
				int inputHash = password.hashCode();
				result = (passwordHash == inputHash);
			}

	
		}catch(SQLException e) {
			e.printStackTrace();
		} 
        return result;
    }

	// Creates prepared statement for isLoginValid method
	private static PreparedStatement selectPassword(Connection connection, String username) throws SQLException{
        String SQL = "SELECT account_password_hash FROM account WHERE account_username=?";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1, username);
		return stmt;
	}
	
    // add a record to the ticket table
    public static boolean createTicket(Ticket ticket){
        boolean result = false;

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = insertTicket(conn, ticket);
		) {

			stmt.executeUpdate();
            result = true;
	
		}catch(SQLException e) {
			e.printStackTrace();
		}     
        return result;
    }

	// Creates prepared statement for createTicket method
	private static PreparedStatement insertTicket(Connection connection, Ticket ticket) throws SQLException{
        String SQL = "INSERT INTO ticket VALUES(DEFAULT,?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1,ticket.getUsername());
		stmt.setFloat(2,ticket.getAmount());
		stmt.setString(3,ticket.getDescription());
		stmt.setString(4,ticket.getStatus());
		return stmt;
	}
	
    // return true if role is NOT in user_role table
    public static boolean isManager(String username){
        boolean result = false;

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = selectRole(conn, username);
			ResultSet set = stmt.executeQuery()
		) {

			set.next();
			result = set.getString("account_role").equals("Finance Manager");
				
		}catch(SQLException e) {
			e.printStackTrace();
		} 
        return result;
    }

	// Creates prepared statement for isManager method
	private static PreparedStatement selectRole(Connection connection, String username) throws SQLException{
        String SQL = "SELECT account_role FROM account WHERE account_username=?";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1, username);
		return stmt;
	}

    // return a List of all pending tickets
    public static List<Ticket> listPendingTickets(){
        String SQL = "SELECT * FROM ticket WHERE ticket_status='Pending'";
		List<Ticket> tickets = new ArrayList<>();

		try(
			Connection conn = getConnection(); 
			Statement stmt = conn.createStatement(); 
			ResultSet set = stmt.executeQuery(SQL)
		) {
			while(set.next()){
				int id = set.getInt("ticket_id");
				String username = set.getString("ticket_username");
				float amount = set.getFloat("ticket_amount");
				String description = set.getString("ticket_description");
				String status = set.getString("ticket_status");
				tickets.add(new Ticket(id, username, amount, description, status));
			}
	
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return tickets;
    }

	// change the status of a pending ticket, validate status before calling
	public static boolean updateTicketStatus(int id, String status){
        boolean result = false;

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = updateStatus(conn, id, status);
		) {

			result = stmt.executeUpdate() == 1;
	
		}catch(SQLException e) {
			e.printStackTrace();
		}    
        return result;
    }

	// Creates prepared statement for updateTicketStatus method
	private static PreparedStatement updateStatus(Connection connection, int id, String status) throws SQLException{
        String SQL= "UPDATE ticket SET ticket_status=? WHERE ticket_status='Pending' AND ticket_id=?";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1, status);
		stmt.setInt(2, id);
		return stmt;
	}

    // return true if username is already in account table
    public static List<Ticket> listEmployeeTickets(String employee){
		List<Ticket> tickets = new ArrayList<>();

		try(
			Connection conn = getConnection(); 
			PreparedStatement stmt = selectTickets(conn, employee);
			ResultSet set = stmt.executeQuery()
		) {

			while(set.next()){
				int id = set.getInt("ticket_id");
				String username = set.getString("ticket_username");
				float amount = set.getFloat("ticket_amount");
				String description = set.getString("ticket_description");
				String status = set.getString("ticket_status");
				tickets.add(new Ticket(id, username, amount, description, status));
			}
	
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return tickets;
    }

	// Creates prepared statement for listEmployeeTickets method
	private static PreparedStatement selectTickets(Connection connection, String username) throws SQLException{
        String SQL = "SELECT * FROM ticket WHERE ticket_username=?";
		PreparedStatement stmt = connection.prepareStatement(SQL); 
		stmt.setString(1, username);
		return stmt;
	}

}
