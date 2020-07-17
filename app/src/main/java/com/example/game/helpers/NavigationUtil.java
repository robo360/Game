package com.example.game.helpers;

import android.app.Activity;
import android.content.Intent;

/*
This class contains helper functions.
 */
public class NavigationUtil {
    public static void goToActivity(Activity activity, Class aClass) {
        Intent i = new Intent(activity, aClass);
        activity.startActivity(i);
        activity.finish();
    }
}
