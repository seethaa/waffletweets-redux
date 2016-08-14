package com.codepath.apps.waffletweets.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Dialog Fragment for compose tweet functionality.
 * Created by seetha on 8/5/16.
 */
public class ComposeTweetDialogFragment extends DialogFragment {
    private Unbinder unbinder;

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.btnCancel)
    ImageButton btnCancel;
    @BindView(R.id.btnTweet) ImageButton btnTweet;
    @BindView(R.id.etComposeTweet) EditText etText;
    @BindView(R.id.tvCharRemaining) TextView tvCharRemaining;


    private static String mTweetBody;
    private static User mCurrentUser;

    @OnClick(R.id.btnCancel)
    public void cancelTweet() {
        this.dismiss();
    }

    @OnClick(R.id.btnTweet)
    public void saveTweet(ImageButton button) {

        if (isNetworkAvailable() && isOnline()) {
            mTweetBody = etText.getText().toString();
            Tweet tweet = new Tweet();
            tweet.setBody(mTweetBody);
            tweet.setUser(mCurrentUser);

            String twitterFormat = "EEE MMM dd HH:mm:ss Z yyyy";
            String timeStamp = new SimpleDateFormat(twitterFormat).format(new java.util.Date());
            tweet.setCreatedAt(timeStamp);

            //pass data back
            ComposeTweetDialogListener activity = (ComposeTweetDialogListener) getParentFragment();
            activity.onFinishComposeTweetDialog(tweet);
            this.dismiss();
        } else {
            callNetworkDialog();

        }

    }

    /**
     * Checks if an active network is available
     *
     * @return true if network is available, false otherwise
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Checks if device is connected to the internet
     *
     * @return true if device is connected, false otherwise
     */
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Alert user to connect to network
     */
    protected void callNetworkDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("No Connection Available");
        alert.setMessage("Please check your network connection, and try again!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.dialog_compose, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_compose, null);
        unbinder = ButterKnife.bind(this, layout);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCurrentUser = Parcels.unwrap(getArguments().getParcelable("currUser"));

        }

        //count characters
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // count character and enable/disable button
                int currChars = etText.getText().toString().length();
                int remaining = 140 - currChars;
                tvCharRemaining.setText(remaining+"");
//                System.out.println("DEBUGGY text change: " + etText.getText().toString() + " " + remaining);

                if (remaining <0){//if negative, disable buttona and make text red
                    tvCharRemaining.setTextColor(Color.RED);
                    btnTweet.setClickable(false);
                }
                else{
                    tvCharRemaining.setTextColor(Color.GRAY);
                    btnTweet.setClickable(true);

                }

            }
        });


        if (mCurrentUser!=null) {
            String profile_img_url = mCurrentUser.getProfileImageURL();
            Glide.with(getContext()).load(profile_img_url).centerCrop().placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 5))
                    .into(ivProfileImage);
        }

        return layout;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Interface used as a listener for TimelineActivity when fragment is dismissed.
     */
    public interface ComposeTweetDialogListener {
        void onFinishComposeTweetDialog(Tweet tweet);
    }
}
