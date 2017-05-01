package com.cashkaro.utils;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Utils {
    private static final String BOLD_FONT_PATH = "Montserrat-Bold.ttf";
    private static final String REGULAR_FONT_PATH = "Montserrat-Regular.ttf";
    private static Typeface regularFont, boldFont;

    static void loadFonts() {
        regularFont = Typeface.createFromAsset(MyApp.getContext().getAssets(),
                Utils.REGULAR_FONT_PATH);
        boldFont = Typeface.createFromAsset(MyApp.getContext().getAssets(),
                Utils.BOLD_FONT_PATH);
    }

    public static void setFontAllView(ViewGroup vg) {

        for (int i = 0; i < vg.getChildCount(); ++i) {

            View child = vg.getChildAt(i);

            if (child instanceof ViewGroup) {
                setFontAllView((ViewGroup) child);
            } else if (child != null) {
                Typeface face;
                if (child.getTag() != null
                        && child.getTag().toString().toLowerCase()
                        .equals("bold")) {
                    face = boldFont;
                } else {
                    face = regularFont;
                }
                if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    textView.setTypeface(face);
                }
            }
        }
    }
}
