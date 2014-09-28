package com.codepath.apps.basictwitter.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;

public class Tweet {
	private static final String TWITTER_DATE_FORMAT =
			"EEE MMM dd HH:mm:ss ZZZZZ yyyy";

	private String body;
	private long id;
	private Date createdAt;
	private User user;
	private boolean isRetweet;
	private String retweetedBy;
	private long retweetCount;
	private long favoritesCount;

	public long getRetweetCount() {
		return retweetCount;
	}

	public long getFavoritesCount() {
		return favoritesCount;
	}

	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		// Extract values from the json to populate the member variables.
		try {
			if (jsonObject.has("retweeted_status")) {
				tweet.isRetweet = true;
				tweet.retweetedBy = jsonObject.getJSONObject("user").getString("name");
				jsonObject = jsonObject.getJSONObject("retweeted_status");
			}
			tweet.body = jsonObject.getString("text");
			tweet.id = jsonObject.getLong("id");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.createdAt = getDateFromTwitterDate(jsonObject.getString("created_at"));
			tweet.retweetCount = jsonObject.getLong("retweet_count");
			tweet.favoritesCount = jsonObject.getLong("favorite_count");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public String getRetweetedBy() {
		return retweetedBy;
	}

	private static Date getDateFromTwitterDate(String dateString) {
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.US);
		sf.setLenient(true);
		try {
			return sf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJSON = null;
			try {
				tweetJSON = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJSON(tweetJSON);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}
		return tweets;
	}

	@Override
	public String toString() {
		return getBody() + " - " + getUser().getScreenName();
	}

	public String getBody() {
		return body;
	}

	public long getId() {
		return id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public String getRelativeTimeString() {
		long now = System.currentTimeMillis();
		long creationTimestamp = this.createdAt.getTime();
		String relativeTimeString = (String) DateUtils.getRelativeTimeSpanString(
				creationTimestamp, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
		relativeTimeString = relativeTimeString.replace(" ago", "");
		relativeTimeString = relativeTimeString.replace(" hours", "h");
		relativeTimeString = relativeTimeString.replace(" mins", "m");
		relativeTimeString = relativeTimeString.replace(" days", "d");
		return relativeTimeString;
	}
}
