package com.codepath.apps.waffletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Twitter User object
 * Created by seetha on 8/2/16.
 */
@Parcel
public class User {
    private String name;
    private long uid;
    private String screenName;
    private String profileImageURL;
    private String tagline;
    private int followersCount;
    private int followingsCount;

    /**
     * Empty constructor for Parceler
     */
    public User(){
        //empty constructor
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

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return followingsCount;
    }
    //deserialize user json -> <User>
    public static User fromJSON(JSONObject json){
        User user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageURL = json.getString("profile_image_url");
            user.tagline = json.getString("description");
            user.followersCount = json.getInt("followers_count");
            user.followingsCount = json.getInt("friends_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

}
