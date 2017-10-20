package com.example.vahan.simplecursonadapter;

/**
 * Created by vahan on 6/9/17.
 */

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;

import java.util.Map;

public class Preferences {


    public static void sync(PreferenceManager preferenceManager) {
        Map<String, ?> map = preferenceManager.getSharedPreferences().getAll();
        for (String key : map.keySet()) {
            sync(preferenceManager, key);
        }
    }

    public static void sync(PreferenceManager preferenceManager, String key) {
        Preference pref = preferenceManager.findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }

    public static void applyTheme(ContextThemeWrapper contextThemeWrapper) {
        if (Preferences.GreenThemeEnabled(contextThemeWrapper)) {
            contextThemeWrapper.setTheme(R.style.AppTheme_Green);
        }
        if (Preferences.BrownThemeEnabled(contextThemeWrapper)) {
            contextThemeWrapper.setTheme(R.style.AppTheme_Leather);
        }
        if (Preferences.BlueThemeEnabled(contextThemeWrapper)) {
            contextThemeWrapper.setTheme(R.style.AppTheme_Blue);
        }

    }


    private static boolean BlueThemeEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_theme),
                        context.getString(R.string.pref_theme_value_blue))
                .equals(context.getString(R.string.pref_theme_value_blue));
    }


    private static boolean GreenThemeEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_theme),
                        context.getString(R.string.pref_theme_value_blue))
                .equals(context.getString(R.string.pref_theme_value_green));
    }

    private static boolean BrownThemeEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_theme),
                        context.getString(R.string.pref_theme_value_blue))
                .equals(context.getString(R.string.pref_theme_value_brown));
    }
}