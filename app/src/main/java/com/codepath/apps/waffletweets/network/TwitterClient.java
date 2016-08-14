package com.codepath.apps.waffletweets.network;

import android.content.Context;

import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

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
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "fZvXe4HRYAWnHAMNySFXwcDGF";
    public static final String REST_CONSUMER_SECRET = "5O8N8AsIzAqp3N25XeFrO1XXTOW72AVa7uEzfc8tuB8b1GBaL8";
    public static final String REST_CALLBACK_URL = "oauth://cpwaffletweets"; // Change this (here and in manifest)

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }


    /**
     * Retrieves tweets for the user's home timeline
     *
     * @param max_id
     * @param handler
     */
    public void getHomeTimeline(Long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        //Specify params
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (max_id != null) {
            params.put("max_id", max_id);
        }

        // Execute the request
        getClient().get(apiUrl, params, handler);
    }

    /**
     * Retrieves user info including name, screenname, profile pic
     *
     * @param handler
     */
    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        //Specify params
//        RequestParams params = new RequestParams();

        // Execute the request
        getClient().get(apiUrl, null, handler);
    }

    /**
     * Post a new tweet to timeline
     *
     * @param tweet
     * @param handler
     */
    public void postTweet(Tweet tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        //Specify params
        RequestParams params = new RequestParams();
        params.put("status", tweet.getBody());
        getClient().post(apiUrl, params, handler);
    }

    public void getMentionsTimeline( JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        //Specify params
        RequestParams params = new RequestParams();
        params.put("count", 25);
//        if (max_id != null) {
//            params.put("max_id", max_id);
//        }

        // Execute the request
        getClient().get(apiUrl, params, jsonHttpResponseHandler);
    }


    public void getUserTimeline(String screenName, AsyncHttpResponseHandler jsonHttpResponseHandler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        //Specify params
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        // Execute the request
        getClient().get(apiUrl, params, jsonHttpResponseHandler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}