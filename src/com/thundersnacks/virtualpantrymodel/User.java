package com.thundersnacks.virtualpantrymodel;

public class User {
	
	private int databaseId;
	private String username;
	private String email;
	private String password;
	private String confirmPassword;
	
	public User (String username, String email, String pswd, String cfmPswd) {
		this.username = username;
		this.email = email;
		this.password = pswd;
		this.confirmPassword = cfmPswd;
	}
	
	public User (String username, String email, String pswd, String cfmPswd, int databaseId) {
		this.databaseId = databaseId;
		this.username = username;
		this.email = email;
		this.password = pswd;
		this.confirmPassword = cfmPswd;
	}

	public int getDatabaseId() {
		return databaseId;
	}
	
	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}
	

	public boolean validEmail() {
		return email.contains("@");
	}
	
	public boolean matchingPasswords() {
		return password.equals(confirmPassword);
	}
	
	public boolean validPassword() {
		return password.length() >= 4;
			
	}
	
}
