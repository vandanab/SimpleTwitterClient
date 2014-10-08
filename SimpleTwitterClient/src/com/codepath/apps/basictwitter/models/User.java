package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Users")
public class User extends Model implements Serializable {
	private static final long serialVersionUID = 2367391970349940530L;

	@Column(name = "name")
	private String name;
	@Column(name = "uid", unique = true, onUniqueConflict=Column.ConflictAction.IGNORE)
	private long uid;
	@Column(name = "screen_name", unique = true, onUniqueConflict=Column.ConflictAction.IGNORE)
	private String screenName;
	@Column(name = "profile_image_url")
	private String profileImageUrl;
	@Column(name = "background_image_url")
	private String backgroundImageUrl;
	@Column(name = "background_color")
	private String backgroundColor;
	@Column(name = "tweets_count")
	private long tweetsCount;
	@Column(name = "followers_count")
	private long followersCount;
	@Column(name = "following_count")
	private long followingCount;
	@Column(name = "tag_line")
	private String tagLine;
	@Column(name = "profile_banner_url")
	private String profileBannerUrl;

	public String getProfileBannerUrl() {
		return profileBannerUrl;
	}

	public void setProfileBannerUrl(String profileBannerUrl) {
		this.profileBannerUrl = profileBannerUrl;
	}

	public String getTagLine() {
		return tagLine;
	}

	public long getTweetsCount() {
		return tweetsCount;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public long getFollowingCount() {
		return followingCount;
	}

	public static User fromSharedPreferences(SharedPreferences prefs) {
		User user = new User();
		user.screenName = prefs.getString("screen_name", null);
		user.name = prefs.getString("name", null);
		user.profileImageUrl = prefs.getString("profile_image_url", null);
		user.uid = prefs.getLong("uid", 0);
		return user;
	}

	public static User fromJSON(JSONObject jsonObject) {
		try {
			long id = jsonObject.getLong("id");
			/*User user = getUserById(id);
			if (user != null) {
				return user;
			}*/
			User user = new User();
			user.name = jsonObject.getString("name");
			user.uid = id;
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
			user.backgroundImageUrl = jsonObject.getString("profile_background_image_url");
			user.backgroundColor = jsonObject.getString("profile_background_color");
			user.followersCount = jsonObject.getLong("followers_count");
			user.followingCount = jsonObject.getLong("friends_count");
			user.tweetsCount = jsonObject.getLong("statuses_count");
			user.tagLine = jsonObject.getString("description");
			//user.save();
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
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

	public String getBackgroundImageUrl() {
		return backgroundImageUrl;
	}

	public static User getUserById(long uid) {
		List<User> user = new Select()
			.from(User.class)
			.where("uid=" + uid)
			.execute();
		return user.get(0);
	}
}
