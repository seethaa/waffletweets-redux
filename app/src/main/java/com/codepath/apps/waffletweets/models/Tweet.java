package com.codepath.apps.waffletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by seetha on 8/2/16.
 *
 *
 *"text": "just another test",
 "contributors": null,
 "id": 240558470661799936,
 "retweet_count": 0,
 "in_reply_to_status_id_str": null,
 "geo": null,
 "retweeted": false,
 "in_reply_to_user_id": null,
 "place": null,
 "source": "<a href="//realitytechnicians.com%5C%22" rel="\"nofollow\"">OAuth Dancer Reborn</a>",
 "user": {
 "name": "OAuth Dancer",
 "profile_sidebar_fill_color": "DDEEF6",
 "profile_background_tile": true,
 "profile_sidebar_border_color": "C0DEED",
 "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
 "created_at": "Wed Mar 03 19:37:35 +0000 2010",
 "location": "San Francisco, CA",
 "follow_request_sent": false,
 "id_str": "119476949",
 "is_translator": false,
 "profile_link_color": "0084B4",
 "entities": {
 "url": {
 "urls": [
 {
 "expanded_url": null,
 "url": "http://bit.ly/oauth-dancer",
 "indices": [
 0,
 26
 ],
 "display_url": null
 }
 ]
 },
 "description": null
 },
 "default_profile": false,
 "url": "http://bit.ly/oauth-dancer",
 "contributors_enabled": false,
 "favourites_count": 7,
 "utc_offset": null,
 "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
 "id": 119476949,
 "listed_count": 1,
 "profile_use_background_image": true,
 "profile_text_color": "333333",
 "followers_count": 28,
 "lang": "en",
 "protected": false,
 "geo_enabled": true,
 "notifications": false,
 "description": "",
 "profile_background_color": "C0DEED",
 "verified": false,
 "time_zone": null,
 "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
 "statuses_count": 166,
 "profile_background_image_url": "http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
 "default_profile_image": false,
 "friends_count": 14,
 "following": false,
 "show_all_inline_media": false,
 "screen_name": "oauth_dancer"
 },
 "in_reply_to_screen_name": null,
 "in_reply_to_status_id": null
 },
 {
 "coordinates": {
 "coordinates": [
 -122.25831,
 37.871609
 ],
 "type": "Point"
 },
 "truncated": false,
 "created_at": "Tue Aug 28 21:08:15 +0000 2012",
 "favorited": false,
 "id_str": "240556426106372096",
 "in_reply_to_user_id_str": null,
 "entities": {
 "urls": [
 {
 "expanded_url": "http://blogs.ischool.berkeley.edu/i290-abdt-s12/",
 "url": "http://t.co/bfj7zkDJ",
 "indices": [
 79,
 99
 ],
 "display_url": "blogs.ischool.berkeley.edu/i290-abdt-s12/"
 }
 ],
 "hashtags": [

 ],
 "user_mentions": [
 {
 "name": "Cal",
 "id_str": "17445752",
 "id": 17445752,
 "indices": [
 60,
 64
 ],
 "screen_name": "Cal"
 },
 {
 "name": "Othman Laraki",
 "id_str": "20495814",
 "id": 20495814,
 "indices": [
 70,
 77
 ],
 "screen_name": "othman"
 }
 ]
 },
 *
 *
 *
 *
 *
 *
 */

//parse JSON and store he datta, encapsulate state logic or display logic
public class Tweet {
    private String body;
    private long uid; //unique id for tweet
    private User user;
    private String createdAt;

    public User getUser() {
        return user;
    }


    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }




    //deserialize json coming in, and turn into json object
    //tweet.fromJSON("(...)") --> <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        //extract values from the json, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //return tweet
        return tweet;
    }

    /**
     * Pass in array of items and out list of tweets
     * @param jsonArrayResponse JSONArray of tweets
     * @return
     */
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArrayResponse) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        //iterate json array and create tweets:
        for (int i=0; i<jsonArrayResponse.length(); i++){
            try {
                JSONObject tweetJSONObject = jsonArrayResponse.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJSONObject);
                if (tweet!=null){
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;//even if one tweet doesn't work, parse the rest
            }

        }
        //return finished list
        return tweets;
    }
}
