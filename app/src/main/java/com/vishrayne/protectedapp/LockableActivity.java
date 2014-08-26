package com.vishrayne.protectedapp;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class LockableActivity extends FragmentActivity implements LockableApplication.OnApplicationStateChangedListener {
    private boolean mRotated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set OnApplicationStateChangedListener
        getLocakableApplication().setOnApplicationStateChangedListener(this);

        Boolean nonConfigState =
                (Boolean) getLastCustomNonConfigurationInstance();
        mRotated = nonConfigState == null ? false : nonConfigState;
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
         * We check to see if onStart() is being called as a result of device
         * orientation change. If NOT, only then we bind to the application. If
         * mRotated is true, we are already bound to the application.
         */
        if (!mRotated) {
            getLocakableApplication().bind();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRotated = false;

        /*
         * The following code is only required to be executed if device
         * rotations should NOT be treated as app pauses (this is the default behaviour).
         *
         * It checks to see if the onStop() is being called because of an orientation change.
         * If so, it does NOT perform the unbind.
         */
        mRotated = isBeingRotated();

        if (!mRotated) {
            getLocakableApplication().unbind();
        }
    }

    @Override
    public void onAppPaused() {
        debugLog("Application Paused");
    }

    @Override
    public void onAppResumed() {
        debugLog("Application Resumed");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean isBeingRotated() {
        /*
         * Fine-tuning of the logic to detect if we are being rotated.
         * >= HC, we have an isChangingConfigurations() which we can check before actual getChangingConfigurations()
         *
         * This is not available pre-HC. In those cases, we directly call getChangingConfigurations()
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (!isChangingConfigurations()) {
                return false;
            }
        }

        int changingConfig = getChangingConfigurations();
        debugLog(String.format("Changing Config: %d", changingConfig));

        if ((changingConfig & ActivityInfo.CONFIG_ORIENTATION) == ActivityInfo.CONFIG_ORIENTATION) {
            return true;
        }

        return false;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mRotated ? Boolean.TRUE : Boolean.FALSE;
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

    /**
     * Points to LockableApplication.
     *
     * @return lockableApplication
     */
    private LockableApplication getLocakableApplication() {
        return (LockableApplication) getApplication();
    }
}
