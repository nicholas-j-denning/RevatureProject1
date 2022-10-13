package com.revature;

import com.revature.Models.*;
import java.util.HashSet;
import java.util.Set;
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
            Set<String> roles = new HashSet();
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
            result = stmt.execute(SQL);
	
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
