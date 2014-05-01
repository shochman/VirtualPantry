package com.thundersnacks.virtualpantry;

import com.thundersnacks.database.DbAdapter;
import com.thundersnacks.virtualpantrymodel.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	// private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	
	// Value for registration attempt
	private User registration;

	//DB
	private DbAdapter dbAdapter;
	
	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		// Set up the login form.
		Context context = LoginActivity.this.getApplicationContext(); 
		this.dbAdapter = new DbAdapter(context);
		//DUMMY_CREDENTIALS.add("bar@example.com:" + hashPW("world"));
		//DUMMY_CREDENTIALS.add("foo@example.com:" + hashPW("hello"));
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		/*if (mAuthTask != null) {
			return;
		}*/

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = hashPW(mPasswordView.getText().toString());

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			User user = dbAdapter.validateUserCredentials(LoginActivity.this.mEmail, LoginActivity.this.mPassword);
			boolean success = user != null;
			showProgress(false);
			if (success) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		    	startActivity(intent);
				finish();
			} else {
				mEmailView.setError("Credentials are invalid");
			}
		}
	}

	public void attemptRegister() {
		
		/*if (mAuthTask != null) {
			return;
		}*/
		
		final Dialog registerDialog = new Dialog(LoginActivity.this);
		registerDialog.setContentView(R.layout.register_popup);
    	registerDialog.setTitle("Registration");
    	registerDialog.show();
    	EditText emailText = (EditText) registerDialog.findViewById(R.id.registerEmailEdit);
    	emailText.setText(mEmail);
    	Button registerButton = (Button) registerDialog.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {					
				if (registerUser(registerDialog, registration)) 
					registerDialog.dismiss();
			}
		});
        
		//mPasswordView
		//	.setError(getString(R.string.error_incorrect_password));
		mPasswordView.requestFocus();
	}
	
	private boolean registerUser(Dialog registerDialog, User r) {
		
		EditText usernameText = (EditText) registerDialog.findViewById(R.id.registerUsernameEdit);
		EditText emailText = (EditText) registerDialog.findViewById(R.id.registerEmailEdit);
		EditText passwordText = (EditText) registerDialog.findViewById(R.id.registerPasswordEdit);
		EditText confirmText = (EditText) registerDialog.findViewById(R.id.registerPassword2Edit);
		
		// reset errors
		usernameText.setError(null);
		emailText.setError(null);
		passwordText.setError(null);
		confirmText.setError(null);
		
		String username = usernameText.getText().toString();
		String email = emailText.getText().toString();
		String password = passwordText.getText().toString();
		String confirm = confirmText.getText().toString();
		
		r = new User(username, email, password, confirm);
		boolean valid = true;
		
		if(r.getUsername().isEmpty()) {
			usernameText.setError(getString(R.string.error_field_required));
			valid = false;
		}
		
		if(r.getEmail().isEmpty()) {
			emailText.setError(getString(R.string.error_field_required));
			valid = false;
		} else if(!r.validEmail()) {
			emailText.setError(getString(R.string.error_invalid_email));
			valid = false;
		}
		
		if(r.getPassword().isEmpty()) {
			passwordText.setError(getString(R.string.error_field_required));
			valid = false;
		} else if(!r.validPassword()) {
			passwordText.setError(getString(R.string.error_invalid_password));
			valid = false;
		}
		
		if(r.getConfirmPassword().isEmpty()) {
			confirmText.setError(getString(R.string.error_field_required));
			valid = false;
		} else if(!r.matchingPasswords()) {
			confirmText.setError("Passwords do not match");
			valid = false;
		}
		
		if(valid)
		{
			// DUMMY_CREDENTIALS.add(email+":"+hashPW(password));
			dbAdapter.insertUser(username, hashPW(password), email);
		}
		return valid;
		
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public static String hashPW(String password) {
		String hashtext = "";
		try{
			String plaintext = password;
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
				hashtext = "0"+hashtext;
			} 
			
		} catch(Exception e) {
				e.printStackTrace();
		}
		
		return hashtext;
	}

}
