package com.mike.signedin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends FragmentActivity {

	private EditText mUsernameField;
	private EditText mPasswordField;
	private TextView mErrorField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		init();

	}

	private void init() {

		mErrorField = (TextView) findViewById(R.id.error_messages);
		mUsernameField = (EditText) findViewById(R.id.register_username);
		mPasswordField = (EditText) findViewById(R.id.register_password);

	}

	public void register(final View v) {
		if (mUsernameField.getText().length() == 0
				|| mPasswordField.getText().length() == 0) {

			Toast.makeText(RegisterActivity.this,
					"Username/Password field missing!!", Toast.LENGTH_LONG)
					.show();

			return;
		}

		v.setEnabled(false);
		ParseUser user = new ParseUser();
		user.setUsername(mUsernameField.getText().toString());
		user.setPassword(mPasswordField.getText().toString());
		mErrorField.setText("");

		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Intent intent = new Intent(RegisterActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
					switch (e.getCode()) {
					case ParseException.USERNAME_TAKEN:
						mErrorField
								.setText("Sorry, this username has already been taken.");
						break;
					case ParseException.USERNAME_MISSING:
						mErrorField
								.setText("Sorry, you must supply a username to register.");
						break;
					case ParseException.PASSWORD_MISSING:
						mErrorField
								.setText("Sorry, you must supply a password to register.");
						break;
					default:
						mErrorField.setText(e.getLocalizedMessage());
					}
					v.setEnabled(true);
				}
			}
		});
	}

	public void showLogin(View v) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}