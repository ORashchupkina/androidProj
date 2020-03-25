package ru.infoenergo.android.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;


@SuppressLint("Registered")
public class App extends Application {
    public static App instance;
    static Activity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Required initialization logic here!
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            String tagInfo = "information_MyCustomApplication";

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivity = activity;
                Log.d(tagInfo, "{1DBB9132-4E0F-4CD7-910C-236AE22538C3} onActivityResumed:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static App getInstance() {
        return instance;
    }

    public static Activity getmActivity() {
        return mActivity;
    }
}
