package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.NetworkMonitor;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {
	private TwitterClient client;
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
	}

	public void populateTimeline(User user, long sinceId, long maxId, final boolean isNewData) {
		this.user = user;
		long userId = 0L;
		if (user != null) {
			userId = user.getUid();
		}
		if (NetworkMonitor.isNetworkAvailable(getActivity())) {
			client.getUserTimeline(userId, sinceId, maxId, new JsonHttpResponseHandler() {
	
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s.toString());
				}
	
				@Override
				public void onSuccess(JSONArray jsonResponse) {
					if (isNewData) {
						addAll(0, Tweet.fromJSONArray(jsonResponse, Tweet.Source.USER));
					} else {
						addAll(Tweet.fromJSONArray(jsonResponse, Tweet.Source.USER));
					}
				}
	
			});
		} else {
			ArrayList<Tweet> tweets = new ArrayList<Tweet>(Tweet.getAll(Tweet.Source.USER, sinceId, maxId, user.getUid()));
			if (isNewData) {
				addAll(0, tweets);
			} else {
				addAll(tweets);
			}
		}
	}

	public void loadMoreDataFromApi(long maxId) {
		populateTimeline(user, 0, maxId, false);
	}

	public void loadNewDataFromApi(long sinceId) {
		populateTimeline(user, sinceId, 0, true);
	}
}
