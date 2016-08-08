package com.codepath.apps.waffletweets.activities;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.etComposeTweet) EditText etText;

    private static String mTweetBody;
    private static User mCurrentUser;

    @OnClick(R.id.btnCancel)
    public void cancelTweet() {
        this.dismiss();
    }

    @OnClick(R.id.btnTweet)
    public void saveTweet(Button button) {

        if (isNetworkAvailable() && isOnline()) {
            mTweetBody = etText.getText().toString();
            Tweet tweet = new Tweet();
            tweet.setBody(mTweetBody);
            tweet.setUser(mCurrentUser);

            String twitterFormat = "EEE MMM dd HH:mm:ss Z yyyy";
            String timeStamp = new SimpleDateFormat(twitterFormat).format(new java.util.Date());
            tweet.setCreatedAt(timeStamp);

            ComposeTweetDialogListener activity = (ComposeTweetDialogListener) getActivity();
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

        Glide.with(getContext()).load(mCurrentUser.getProfileImageURL()).centerCrop().placeholder(R.drawable.ic_launcher)
        .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 5))
        .into(ivProfileImage);

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
