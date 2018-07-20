package com.example.naville.kotlinmap.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

public class ApplyFont {

    public static void nunitoFont(Activity activity, TextView textView){
        Typeface tf = Typeface.createFromAsset(activity.getAssets(),
                "fonts/nunito_light.ttf");
        textView.setTypeface(tf);
    }

}
