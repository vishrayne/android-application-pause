package com.vishrayne.protectedapp;

import android.app.Application;
import android.util.Log;

/**
 * Created by vishnu on 25/8/14.
 */
public class LockableApplication extends Application {
    private OnApplicationStateChangedListener mListener;

    /**
     * The application state listener, to which resume and pause events are published to. Any interested client should implement the required methods.
     */
    public interface OnApplicationStateChangedListener {
        public void onAppPaused();

        public void onAppResumed();
    }

    /**
     * Sets OnApplicationStateChangedListener.
     *
     * @param listener The listener to be set.
     */
    public void setOnApplicationStateChangedListener(OnApplicationStateChangedListener listener) {
        mListener = listener;
    }

    /*
     * No synchronization required since bind() and unbind() are always called from
     * the same thread (the "main thread" or the "UI thread")
     *
     * This is because bind() is always called from an Activity's onStart(),
     * and unbind() always from an Activity's onStop().
     */
    private int mBoundCount = 0;

    protected void bind() {
        if (mListener == null) {
            debugLog("OnApplicationStateChangedListener not set. Skipping...");
            return;
        }

        if (mBoundCount == 0) {
            mListener.onAppResumed();
        }

        mBoundCount++;
        debugLog("mBoundCount incremented to " + mBoundCount);
    }

    protected void unbind() {
        if (mListener == null) {
            debugLog("OnApplicationStateChangedListener not set. Skipping...");
            return;
        }

        mBoundCount--;
        debugLog("mBoundCount decremented to " + mBoundCount);

        if (mBoundCount == 0) {
            mListener.onAppPaused();
        }
    }

    /**
     * Debug logger
     *
     * @param message The message to be logged.
     */
    private void debugLog(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("android-app-pause", message);
        }
    }
}
