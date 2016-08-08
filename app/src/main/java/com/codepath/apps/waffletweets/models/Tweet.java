package com.codepath.apps.waffletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by seetha on 8/2/16.
 * Parse data into Tweet object
 */

@Parcel
public class Tweet {
     String body;
     long uid; //unique id for tweet
     User user;
     String createdAt;

    /**
     * Empty constructor for Parceler
     */
    public Tweet(){
        //empty constructor
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBody(String body) {
        this.body = body;
    }

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


    /**
     * Deserialize JSONObject coming in and create Tweet object
     * @param jsonObject
     * @return
     */
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
