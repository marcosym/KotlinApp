package com.example.naville.kotlinmap.util.fonts

import android.app.Activity
import android.graphics.Typeface
import android.support.design.widget.TextInputEditText
import android.widget.TextView

class Fonts {

    companion object {

        fun nunitoFontBold(activity: Activity, label: TextView?) {
            val tf = Typeface.createFromAsset(activity.assets,
                    "fonts/nunito_bold.ttf")
            if (label != null) {
                label.typeface = tf
            }

        }

        fun nunitoFontLight(activity: Activity, label: TextView?) {
            val tf = Typeface.createFromAsset(activity.assets,
                    "fonts/nunito_light.ttf")
            if (label != null) {
                label.typeface = tf
            }
        }

        fun nunitoFontRegular(activity: Activity, label: TextInputEditText?) {
            val tf = Typeface.createFromAsset(activity.assets,
                    "fonts/nunito_regular.ttf")
            if (label != null) {
                label.typeface = tf
            }
        }

    }
}
