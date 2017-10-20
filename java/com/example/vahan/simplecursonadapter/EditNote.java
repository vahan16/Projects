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
import android.text.format.DateFormat;
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

public class EditNote extends BaseActivity implements View.OnClickListener {

    EditText e, e1;
    DB db;
    int image, image1, alarmImage;
    ToggleButton tg, alarm;
    String id, created;
    int cnt = 0;
    Calendar calSet;
    Cursor cursor;
    AlertDialog.Builder adb;
    ToggleButton toggleButton;
    AlertDialog dialog;
    LinearLayout view1;
    String str;
    final int DIALOG1 = 2;
    RelativeLayout relativeLayout;
    EditText pass, old;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.edit_note);
        setSupportActionBar(toolbar);

        tg = (ToggleButton) findViewById(R.id.toggleButton);
        tg.setOnClickListener(this);

        alarm = (ToggleButton) findViewById(R.id.toggleButton5);
        alarm.setOnClickListener(this);

        e = (EditText) findViewById(R.id.editText3);
        e1 = (EditText) findViewById(R.id.editText4);

        Intent intent = getIntent();
        String text = intent.getStringExtra("key3");
        String text1 = intent.getStringExtra("key4");
        e.setText(text);
        e1.setText(text1);
        id = intent.getStringExtra("key0");
        created = intent.getStringExtra("key5");
        image1 = intent.getIntExtra("key6", 0);

        db = new DB(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);

        if ((tg.isChecked())) {
            tg.setChecked(true);
            image1 = R.drawable.ic_lock_outline_black_24d;
        } else if (image1 == R.drawable.ic_lock_outline_black_24d) {
            tg.setChecked(true);
            image = R.drawable.ic_lock_outline_black_24dp;
        } else if (image == R.drawable.ic_lock_outline_black_24dp) {
            tg.setChecked(true);
            image1 = R.drawable.ic_lock_outline_black_24d;
        } else if (!tg.isChecked()) {
            image1 = R.drawable.blank;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggleButton5:
                if ((alarm.isChecked())) {
                    alarmImage = R.drawable.ic_alarm_black_24d;
                    showDialog(1);
                } else {
                    image = R.drawable.blank;
                }

            case R.id.toggleButton:
                if ((tg.isChecked())) {
                    Cursor c = db.getPassData();
                    startManagingCursor(c);
                    str = "";
                    if (c.moveToFirst()) {
                        str = c.getString(c.getColumnIndex("password"));
                    }
                    if (str.equals("")) {
                        Toast.makeText(getApplicationContext(), "You don't have password,please create", Toast.LENGTH_LONG).show();
                        showDialog(DIALOG1);
                    } else {
                        image1 = R.drawable.ic_lock_outline_black_24d;
                        Snackbar.make(v, R.string.Password_set, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    image1 = R.drawable.blank;
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.button4:

                if (e.getText().toString().equals("") && e1.getText().toString().equals("") || e.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.Nothing_to_edit, Toast.LENGTH_LONG).show();
                } else {

                    String[] arr = created.split("/");
                    String firstWord = arr[0];
                    String date = getString(R.string.Edited) + String.valueOf(DateFormat.format("dd.MM.yyyy,hh:mm", new Date()));

                    db.editRec(e.getText().toString() + "\n" + e1.getText().toString(), firstWord + date, id, image1, alarmImage);

                    Toast.makeText(getApplicationContext(), R.string.Note_edited, Toast.LENGTH_LONG).show();
                    if ((tg.isChecked())) {
                        tg.setChecked(true);
                    }

                    Intent i = new Intent(this, NoteActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                    if ((tg.isChecked())) {
                        tg.setChecked(true);
                        image = R.drawable.ic_lock_outline_black_24dp;
                        image1 = R.drawable.ic_lock_outline_black_24d;
                    }
                    if (alarm.isChecked()) {
                        setAlarm(calSet);
                    }

                }
                break;
            case R.id.button2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete this note?")
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id1) {
                                db.delRec(Long.parseLong(id));
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "I send you one of my notes\n'" + e.getText().toString() + "\n" + e1.getText().toString() + "'");
                startActivity(Intent.createChooser(i, "Choose one"));
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
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
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
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
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
        }
        if (id == DIALOG1) {
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
                    image1 = R.drawable.ic_lock_outline_black_24d;
                    dialog.dismiss();

                }
            });
        }
    }


    private void setAlarm(Calendar targetCal) {
        Toast.makeText(this, "Alarm is set at " + targetCal.getTime(),
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), NotificationPublisher.class);
        intent.putExtra("state", e.getText().toString());
        intent.putExtra("state1", e1.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                pendingIntent);
    }

}
