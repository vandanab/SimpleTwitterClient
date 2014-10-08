package com.codepath.apps.basictwitter.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	private ImageLoader imageLoader;

	private static class ViewHolder {
		ImageView ivProfileImage;
		ImageView ivDetailsMedia;
		TextView tvBody;
		TextView tvName;
		TextView tvScreenName;
		TextView tvCreatedAt;
		TextView tvRetweetedBy;
		TextView tvRetweets;
		TextView tvFavorites;
		TextView tvReply;
	}

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.tweet_item, tweets);
		imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
					cacheInMemory().cacheOnDisc().build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
					.defaultDisplayImageOptions(defaultOptions)
					.build();
			imageLoader.init(config);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);

		ViewHolder viewHolder;

		if (convertView == null) {  // No recycled view.
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.tweet_item, parent, false);
			initializeViews(viewHolder, convertView);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
		viewHolder.ivProfileImage.setTag(tweet.getUser());
		viewHolder.tvReply.setTag(tweet);

		viewHolder.tvBody.setText(tweet.getBody());
		viewHolder.tvName.setText(Html.fromHtml("<b>" + tweet.getUser().getName() + "</b>"));
		viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
		viewHolder.tvCreatedAt.setText(tweet.getRelativeTimeString());
		if (tweet.getRetweetCount() > 0) {
			viewHolder.tvRetweets.setText(String.valueOf(tweet.getRetweetCount()));
		} else {
			viewHolder.tvRetweets.setText("");
		}
		if (tweet.getFavoritesCount() > 0) {
			viewHolder.tvFavorites.setText(String.valueOf(tweet.getFavoritesCount()));
		} else {
			viewHolder.tvFavorites.setText("");
		}
		if (tweet.isRetweet()) {
			viewHolder.tvRetweetedBy.setText(tweet.getRetweetedBy() + " retweeted");
			viewHolder.tvRetweetedBy.setVisibility(View.VISIBLE);
			viewHolder.tvRetweetedBy.setLayoutParams(getLayoutParams(View.VISIBLE));
		} else {
			viewHolder.tvRetweetedBy.setVisibility(View.INVISIBLE);
			viewHolder.tvRetweetedBy.setLayoutParams(getLayoutParams(View.INVISIBLE));
		}
		if (tweet.getMediaUrl() != null) {
			viewHolder.ivDetailsMedia.setImageResource(android.R.color.transparent);
			imageLoader.displayImage(tweet.getMediaUrl(), viewHolder.ivDetailsMedia);
			viewHolder.ivDetailsMedia.setVisibility(View.VISIBLE);
			viewHolder.ivDetailsMedia.setLayoutParams(getLayoutParamsImage(View.VISIBLE));
		} else {
			viewHolder.ivDetailsMedia.setVisibility(View.INVISIBLE);
			viewHolder.ivDetailsMedia.setLayoutParams(getLayoutParamsImage(View.INVISIBLE));
		}
		return convertView;
	}

	private RelativeLayout.LayoutParams getLayoutParamsImage(int visible) {
		if (visible == View.VISIBLE) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 480);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			params.addRule(RelativeLayout.BELOW, R.id.tvBody);
			params.setMargins(0, 0, 30, 10);
			return params;
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			params.addRule(RelativeLayout.BELOW, R.id.tvBody);
			params.setMargins(0, 0, 5, 5);
			return params;
		}
	}

	private RelativeLayout.LayoutParams getLayoutParams(int visibility) {
		if (visibility == View.VISIBLE) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			params.setMargins(0, 5, 0, 0);
			return params;
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			return params;
		}
	}

	private void initializeViews(ViewHolder viewHolder, View convertView) {
		viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
		viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
		viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
		viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
		viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
		viewHolder.tvRetweetedBy = (TextView) convertView.findViewById(R.id.tvRetweetedBy);
		viewHolder.tvRetweets = (TextView) convertView.findViewById(R.id.tvRetweets);
		viewHolder.tvFavorites = (TextView) convertView.findViewById(R.id.tvFavorites);
		viewHolder.tvReply = (TextView) convertView.findViewById(R.id.tvReply);
		viewHolder.ivDetailsMedia = (ImageView) convertView.findViewById(R.id.ivDetailsMedia);
		convertView.setTag(viewHolder);
	}
}
