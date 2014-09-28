package com.codepath.apps.basictwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

public class User {
	private String name;
	private long uid;
	private String screenName;
	private String profileImageUrl;

	public static User fromSharedPreferences(SharedPreferences prefs) {
		User user = new User();
		user.screenName = prefs.getString("screen_name", null);
		user.name = prefs.getString("name", null);
		user.profileImageUrl = prefs.getString("profile_image_url", null);
		user.uid = prefs.getLong("uid", 0);
		return user;
	}

	public static User fromJSON(JSONObject jsonObject) {
		User user = new User();
		try {
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
}
