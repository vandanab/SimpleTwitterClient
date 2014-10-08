package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.NetworkMonitor;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetsListFragment {
	private TwitterClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		populateTimeline(1L, 0, false);
	}

	public void populateTimeline(long sinceId, long maxId, final boolean isNewData) {
		if (NetworkMonitor.isNetworkAvailable(getActivity())) {
			client.getHomeTimeline(sinceId, maxId, new JsonHttpResponseHandler() {
	
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s.toString());
				}
	
				@Override
				public void onSuccess(JSONArray jsonResponse) {
					if (isNewData) {
						addAll(0, Tweet.fromJSONArray(jsonResponse, Tweet.Source.HOME));
					} else {
						addAll(Tweet.fromJSONArray(jsonResponse, Tweet.Source.HOME));
					}
				}
	
			});
		} else {
			ArrayList<Tweet> tweets = new ArrayList<Tweet>(Tweet.getAll(Tweet.Source.HOME, sinceId, maxId, 0));
			if (isNewData) {
				addAll(0, tweets);
			} else {
				addAll(tweets);
			}
		}
	}
	
	public void loadMoreDataFromApi(long maxId) {
		populateTimeline(0, maxId, false);
	}

	public void loadNewDataFromApi(long sinceId) {
		populateTimeline(sinceId, 0, true);
	}
}
