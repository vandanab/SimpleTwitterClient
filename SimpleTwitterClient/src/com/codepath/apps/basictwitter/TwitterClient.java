package com.codepath.apps.basictwitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "l1rhRTTzbhu5RCguYwutp2SdN";
	public static final String REST_CONSUMER_SECRET = "mQIjKMp5TE8UL6X8PJteUxrJp3jMFBtPnfNUM9WVouvczAlbyd";
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if (sinceId > 0L) {
			params.put("since_id", String.valueOf(sinceId));
		}
		if (maxId > 0L) {
			params.put("max_id", String.valueOf(maxId));
		}
		client.get(apiUrl, params, handler);
		// apis which are http get requests, for post requests we will need to call client.post()
	}

	public void getMentionsTimeline(long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if (sinceId > 0L) {
			params.put("since_id", String.valueOf(sinceId));
		}
		if (maxId > 0L) {
			params.put("max_id", String.valueOf(maxId));
		}
		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(long userId, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		if (userId > 0L) {
			RequestParams params = new RequestParams();
			params.put("user_id", String.valueOf(userId));
			if (sinceId > 0L) {
				params.put("since_id", String.valueOf(sinceId));
			}
			if (maxId > 0L) {
				params.put("max_id", String.valueOf(maxId));
			}
			client.get(apiUrl, params, handler);
		} else {
			client.get(apiUrl, null, handler);
		}
	}

	public void getProfileBanner(long userId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/profile_banner.json");
		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(userId));
		client.get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}

	public void postUpdate(String status, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", String.valueOf(status));
		client.post(apiUrl, params, handler);
	}

	public void postReply(String status, long replyToId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", String.valueOf(status));
		params.put("in_reply_to_status_id", String.valueOf(replyToId));
		client.post(apiUrl, params, handler);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	/*public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}*/

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}