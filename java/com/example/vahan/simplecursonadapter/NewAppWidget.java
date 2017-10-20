package com.example.vahan.simplecursonadapter;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;

public class NewAppWidget extends AppWidgetProvider {

    SimpleCursorAdapter scAdapter;
    DB db;
    Cursor cursor;
    static RemoteViews views;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, NoteActivity.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv);
        views.setRemoteAdapter(R.id.lvData,
                new Intent(context, NoteActivity.class));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        db = new DB(context);
        db.open();
        cursor = db.getAllData();
        final String[] from = new String[]{DB.COLUMN_IMG,DB.COLUMN_IMG1,DB.COLUMN_TXT, DB.COLUMN_DATE};
        int[] to = new int[]{R.id.ivImg,R.id.imageView2, R.id.tvText, R.id.textView6};
        scAdapter = new SimpleCursorAdapter(context, R.layout.item, cursor, from, to);
//        views.setRemoteAdapter(R.id.lv,scAdapter);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }


    @Override
    public void onDisabled(Context context) {
    }
}

