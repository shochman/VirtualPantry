package com.thundersnacks.virtualpantrymodel;

public class Registration {
	
	private String username;
	
	private String email;
	
	private String password;
	
	private String confirmPassword;
	
	public Registration (String username, String email, String pswd, String cfmPswd) {
		this.username = username;
		this.email = email;
		this.password = pswd;
		this.confirmPassword = cfmPswd;
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
