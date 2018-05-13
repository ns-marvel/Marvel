package com.springwoodcomputers.marvel.utility;

import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

public class SnackbarUtils {

    private SnackbarUtils() {
    }

    private static TextView getActionTextView(Snackbar snackbar) {
        return snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
    }

    public static void setFontToDefault(Snackbar snackbar) {
        TextView snackbarAction = getActionTextView(snackbar);
        TextView snackbarText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        snackbarAction.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
    }

    public static void setBackgroundColour(Snackbar snackbar, @ColorRes int colourRes) {
        int colour = ContextCompat.getColor(snackbar.getContext(), colourRes);
        snackbar.getView().setBackgroundColor(colour);
    }

    public static void setActionTextColour(Snackbar snackbar, @ColorRes int colourRes) {
        int colour = ContextCompat.getColor(snackbar.getContext(), colourRes);
        getActionTextView(snackbar).setTextColor(colour);
    }

    public static Snackbar makeErrorSnackbar(View view, @StringRes int messageRes, int length) {
        Snackbar errorSnackbar = Snackbar.make(view, messageRes, length);
        setBackgroundColour(errorSnackbar, android.R.color.holo_red_dark);
        setFontToDefault(errorSnackbar);
        setActionTextColour(errorSnackbar, android.R.color.holo_orange_light);
        return errorSnackbar;
    }
}