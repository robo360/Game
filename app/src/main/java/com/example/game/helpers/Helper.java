package com.example.game.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/*
This class contains helper functions.
 */
public class Helper {
    public static void goToActivity(Activity activity, Class aClass) {
        Context context = activity;
        Intent i = new Intent(context, aClass);
        context.startActivity(i);
        activity.finish();
    }
}
