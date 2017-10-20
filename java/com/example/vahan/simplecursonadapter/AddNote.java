package com.example.vahan.simplecursonadapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;

public class AddNote extends BaseActivity implements  View.OnClickListener {

    EditText e, e1;
    Calendar calSet;
    DB db;
    ToggleButton tg, alarm;
    int image, alarmImage;
    int cnt = 0;
    int i=0;
    View view;
    final static int RQS_1 = 1;
    AlertDialog.Builder adb;
    ToggleButton toggleButton;
    AlertDialog dialog;
    LinearLayout view1;
    String str;
    final int DIALOG1 = 2;
    RelativeLayout relativeLayout;
    EditText pass,old;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Cursor cursor;
    String title,note;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        e = (EditText) findViewById(R.id.editText);
        e1 = (EditText) findViewById(R.id.editText2);

        tg = (ToggleButton) findViewById(R.id.toggleButton9);
        tg.setOnClickListener(this);

        alarm = (ToggleButton) findViewById(R.id.toggleButton4);
        alarm.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.add_note);
        setSupportActionBar(toolbar);

        db = new DB(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggleButton4:
                if ((alarm.isChecked())) {
                    alarmImage = R.drawable.ic_alarm_black_24d;
                        showDialog(1);
                } else {
                    image = R.drawable.blank;
                }
                break;
            case R.id.toggleButton9:
                if ((tg.isChecked())) {
                    Cursor c = db.getPassData();
                    startManagingCursor(c);
                    str = "";
                    if (c.moveToFirst()) {
                        str = c.getString(c.getColumnIndex("password"));
                    }
                    if(str.equals("")) {
                        Toast.makeText(getApplicationContext(), "You don't have password,please create", Toast.LENGTH_LONG).show();
                        showDialog(DIALOG1);
                    }
                    else{
                        image = R.drawable.ic_lock_outline_black_24d;
                        Snackbar.make(v,"Password set",Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    image = R.drawable.blank;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.button:
                if (e.getText().toString().equals("") && e1.getText().toString().equals("") || (e.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(), R.string.Nothing_to_save, Toast.LENGTH_LONG).show();
                } else {
                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                    String date = getString(R.string.Created) + String.valueOf(df.format("dd.MM.yyyy,hh:mm ", new Date()));
                    Intent i1 = new Intent(this, EditNote.class);
                    i1.putExtra("created", date);

                    db.addRec(e.getText().toString() + "\n" + e1.getText().toString(),
                            date, image, alarmImage);
                    title=e.getText().toString();
                    note=e1.getText().toString();

                    AddNote.this.finish();

                    Toast.makeText(getApplicationContext(), R.string.Note_added, Toast.LENGTH_LONG).show();
                    if (alarm.isChecked()) {
                        setAlarm(calSet);
                    }

                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", cnt);
        Log.d(" xc", "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cnt = savedInstanceState.getInt("count");
        Log.d(" xc", "onRestoreInstanceState");
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack,  calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            tpd.setButton(DialogInterface.BUTTON_NEGATIVE,
                   "CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                dialog.cancel();
                                alarm.setChecked(false);
                            }
                        }
                    });

            tpd.setCancelable(false);
            return tpd;
        }title=e.getText().toString();
        note=e1.getText().toString();
        if(id==DIALOG1){
            adb = new AlertDialog.Builder(this);
            view1 = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.activity_password, null);
            adb.setTitle(R.string.create_password);
            adb.setView(view1);
            dialog = adb.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            return dialog;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            calSet = (Calendar) calNow.clone();
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
        }
    };

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        relativeLayout = (RelativeLayout) dialog.findViewById(R.id.dgdg);
        toggleButton = (ToggleButton) dialog.findViewById(R.id.toggleButton0);
        pass = (EditText) dialog.findViewById(
                R.id.editPassword);
        old = (EditText) dialog.findViewById(R.id.editText5);
        if (id == DIALOG1) {
            relativeLayout.setVisibility(View.GONE);
            toggleButton.setVisibility(View.GONE);
            old.setVisibility(View.GONE);
            final ToggleButton tg1 = (ToggleButton) dialog.findViewById(R.id.toggleButton2);
            tg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((tg1.isChecked())) {
                        pass.setTransformationMethod(null);
                        pass.setSelection(pass.getText().length());
                    } else {
                        pass.setTransformationMethod(new PasswordTransformationMethod());
                        pass.setSelection(pass.getText().length());
                    }
                }
            });
            Button but = (Button) dialog.getWindow().findViewById(R.id.button6);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.editPas(pass.getText().toString(), "1");
                    image = R.drawable.ic_lock_outline_black_24d;
                    dialog.dismiss();
                }
            });
        }
    }

    private void setAlarm(Calendar targetCal) {
        Toast.makeText(this, "Alarm is set at " + targetCal.getTime(),
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), NotificationPublisher.class);
        intent.putExtra("state", title);
        intent.putExtra("state1",note);
        pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                pendingIntent);
    }
}

