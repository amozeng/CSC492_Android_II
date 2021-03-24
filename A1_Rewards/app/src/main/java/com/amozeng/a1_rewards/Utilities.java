package com.amozeng.a1_rewards;


import androidx.appcompat.app.ActionBar;

// Home/Up Nav
class Utilities {

    static void setupHomeIndicator(ActionBar actionBar) {
        if (actionBar != null) {
            // Comment out the below line to show the default home indicator
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_with_logo);

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }}}

