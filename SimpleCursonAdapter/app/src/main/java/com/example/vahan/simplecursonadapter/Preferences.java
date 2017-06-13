package com.example.vahan.simplecursonadapter;

/**
 * Created by vahan on 6/9/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.view.ContextThemeWrapper;

import java.util.Map;

public class Preferences {

    private static final BoolToStringPref[] PREF_MIGRATION = new BoolToStringPref[]{
            new BoolToStringPref(R.string.pref_dark_theme, false,
                    R.string.pref_theme, R.string.pref_theme_value_blue),
    };

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

    public static void migrate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        for (BoolToStringPref pref : PREF_MIGRATION) {
            if (pref.isChanged(context, sp)) {
                editor.putString(context.getString(pref.newKey), context.getString(pref.newValue));
            }

            if (pref.hasOldValue(context, sp)) {
                editor.remove(context.getString(pref.oldKey));
            }
        }

        editor.apply();
    }

    public static void applyTheme(ContextThemeWrapper contextThemeWrapper) {
        if (Preferences.BlueThemeEnabled(contextThemeWrapper)) {
            contextThemeWrapper.setTheme(R.style.AppTheme_Blue);
        }
        if (Preferences.GreenThemeEnabled(contextThemeWrapper)) {
            contextThemeWrapper.setTheme(R.style.AppTheme_Green);
        }
        if (Preferences.BrownThemeEnabled(contextThemeWrapper)) {
            contextThemeWrapper.setTheme(R.style.AppTheme_Leather);
        }

    }

    private static boolean BlueThemeEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_theme),
                        context.getString(R.string.pref_theme_value_black))
                .equals(context.getString(R.string.pref_theme_value_blue));
    }

    private static boolean GreenThemeEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_theme),
                        context.getString(R.string.pref_theme_value_black))
                .equals(context.getString(R.string.pref_theme_value_green));
    }

    private static boolean BrownThemeEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_theme),
                        context.getString(R.string.pref_theme_value_black))
                .equals(context.getString(R.string.pref_theme_value_brown));
    }



    private static class BoolToStringPref {
        private final int oldKey;
        private final boolean oldDefault;
        private final int newKey;
        private final int newValue;

        private BoolToStringPref(@StringRes int oldKey, boolean oldDefault,
                                 @StringRes int newKey, @StringRes int newValue) {
            this.oldKey = oldKey;
            this.oldDefault = oldDefault;
            this.newKey = newKey;
            this.newValue = newValue;
        }

        private boolean isChanged(Context context, SharedPreferences sp) {
            return hasOldValue(context, sp) &&
                    sp.getBoolean(context.getString(oldKey), oldDefault) != oldDefault;
        }

        private boolean hasOldValue(Context context, SharedPreferences sp) {
            return sp.contains(context.getString(oldKey));
        }
    }
}