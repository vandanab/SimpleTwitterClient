package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.User;
import com.codepath.oauth.OAuthLoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {
	public static final String USER_PREFS = "LoggedInUserPrefsFile";
	private SharedPreferences userPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userPrefs = getSharedPreferences(USER_PREFS, 0);
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		String screenName = userPrefs.getString("screen_name", null);
		if (screenName == null) {
			// make a call to twitter to populate info.
			TwitterClient client = TwitterApplication.getRestClient();
			client.getUserInfo(new JsonHttpResponseHandler() {
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s.toString());
				}

				@Override
				public void onSuccess(JSONObject jsonResponse) {
					User user = User.fromJSON(jsonResponse);
					SharedPreferences.Editor editor = userPrefs.edit();
				    editor.putString("screen_name", user.getScreenName());
				    editor.putString("name", user.getName());
				    editor.putLong("uid", user.getUid());
				    editor.putString("profile_image_url", user.getProfileImageUrl());
				    editor.commit();
				}
			});
		}
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
		//Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
