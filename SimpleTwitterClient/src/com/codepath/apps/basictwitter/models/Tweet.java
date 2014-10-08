package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	private static final long serialVersionUID = -4196204485284047120L;

	private static final String TWITTER_DATE_FORMAT =
			"EEE MMM dd HH:mm:ss ZZZZZ yyyy";

	@Column(name = "body")
	private String body;
	@Column(name = "tid")
	private long tid;
	@Column(name = "theid", unique = true, onUniqueConflict=Column.ConflictAction.IGNORE)
	private String theid;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private User user;
	@Column(name = "is_retweet")
	private boolean isRetweet;
	@Column(name = "retweetedBy")
	private String retweetedBy;
	@Column(name = "retweet_count")
	private long retweetCount;
	@Column(name = "favorites_count")
	private long favoritesCount;
	@Column(name = "source")
	private String source;
	@Column(name = "media_url")
	private String mediaUrl;

	public String getMediaUrl() {
		return mediaUrl;
	}

	public enum Source {
		HOME, MENTIONS, USER, COMPOSE
	}

	public long getRetweetCount() {
		return retweetCount;
	}

	public long getFavoritesCount() {
		return favoritesCount;
	}

	public static Tweet fromJSON(JSONObject jsonObject, Source source) {
		try {
			String id = String.valueOf(jsonObject.getLong("id")) + "_" + source.toString();
			Tweet tweet = getTweetByTheId(id);
			if (tweet != null) {
				return tweet;
			}
			
			tweet = new Tweet();
			// Extract values from the json to populate the member variables.
			if (jsonObject.has("retweeted_status")) {
				tweet.isRetweet = true;
				tweet.retweetedBy = jsonObject.getJSONObject("user").getString("name");
				jsonObject = jsonObject.getJSONObject("retweeted_status");
			}
			tweet.body = jsonObject.getString("text");
			tweet.tid = jsonObject.getLong("id");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.createdAt = getDateFromTwitterDate(jsonObject.getString("created_at"));
			tweet.retweetCount = jsonObject.getLong("retweet_count");
			tweet.favoritesCount = jsonObject.getLong("favorite_count");
			tweet.source = source.toString();
			tweet.theid = String.valueOf(jsonObject.getLong("id")) + "_" + source.toString();
			if (jsonObject.has("entities") && jsonObject.getJSONObject("entities").has("media")) {
				JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
				if (media.length() > 0) {
					tweet.mediaUrl = ((JSONObject) media.get(0)).getString("media_url");
				}
			}
			tweet.save();
			return tweet;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
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

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray, Source source) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJSON = null;
			try {
				tweetJSON = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJSON(tweetJSON, source);
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

	public long getTid() {
		return tid;
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

	public static List<Tweet> getAll(Source source, long sinceId, long maxId, long userId) {
		User user = User.getUserById(userId);
		if (user != null) {
			if (maxId > 0) {
				return new Select()
					.from(Tweet.class)
					.where("source='" + source.toString() + "' and tid<" + maxId + " and user=" + user.getId())
					.orderBy("tid DESC")
					.execute();
			} else if (sinceId > 0) {
				return new Select()
					.from(Tweet.class)
					.where("source='" + source.toString() + "' and tid>" + sinceId + " and user=" + user.getId())
					.orderBy("tid DESC")
					.execute();
			}
		} else {
			if (maxId > 0) {
				return new Select()
					.from(Tweet.class)
					.where("source='" + source.toString() + "' and tid<" + maxId)
					.orderBy("tid DESC")
					.execute();
			} else if (sinceId > 0) {
				return new Select()
					.from(Tweet.class)
					.where("source='" + source.toString() + "' and tid>" + sinceId)
					.orderBy("tid DESC")
					.execute();
			}
		}
		return null;
	}

	public static Tweet getTweetByTheId(String id) {
		List<Tweet> tweet = new Select()
			.from(Tweet.class)
			.where("theid = '" + id + "'")
			.execute();
		if (tweet != null && !tweet.isEmpty()) {
			return tweet.get(0);
		} else {
			return null;
		}
	}
}
