SimpleTwitterClient
===================

A simple twitter android app. Users can view their timeline, compose tweets, view tweet in detail and reply to a tweet.

Time spent: 40 hrs

Completed User Stories:

Required:
* User can sign in to Twitter using OAuth login
* User can view the tweets from their home timeline
  - User should be displayed the username, name, and body for each tweet
  - User should be displayed the relative timestamp for each tweet "8m", "7h"
  - User can view more tweets as they scroll with infinite pagination
  - Optional: Links in tweets are clickable and will launch the web browser (see autolink)
* User can compose a new tweet
  - User can click a “Compose” icon in the Action Bar on the top right
  - User can then enter a new tweet and post this to twitter
  - User is taken back to home timeline with new tweet visible in timeline
  - Optional: User can see a counter with total number of characters left for tweet

Optional:
* Advanced: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
* Advanced: User can open the twitter app offline and see last loaded tweets
  - Tweets are persisted into sqlite and can be displayed from the local DB (Images not saved!)
* Advanced: User can tap a tweet to display a "detailed" view of that tweet
* Advanced: User can select "reply" from detail view to respond to a tweet
* Advanced: Improve the user interface and theme the app to feel "twitter branded"

Notes:
Spent quite some time in getting the scrolling, playing with the db, branding and layout of the app.
Had fun configuring the action bar for number of chars left in the tweet.

Walkthrough of all user stories:

![Video Walkthrough](twitter_client_1.gif)
