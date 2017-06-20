package com.droidmentor.remindme;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String TAG="RemindMe";
    LocalData localData;

    SwitchCompat reminderSwitch;
    TextView tvTime;

    LinearLayout ll_set_time;

    int hour,min;

    public static final int DAILY_REMINDER_REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localData = new LocalData(getApplicationContext());

        ll_set_time=(LinearLayout)findViewById(R.id.ll_set_time);
        tvTime=(TextView)findViewById(R.id.tv_reminder_time_desc);

        reminderSwitch=(SwitchCompat)findViewById(R.id.timerSwitch);

        hour=localData.get_hour();
        min=localData.get_min();

        tvTime.setText(getFormatedTime(hour,min));
        reminderSwitch.setChecked(localData.getReminderStatus());

        if(!localData.getReminderStatus())
            ll_set_time.setAlpha(0.4f);

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                localData.setReminderStatus(isChecked);
                if(isChecked)
                {
                    Log.d(TAG, "onCheckedChanged: true");
                    NotificationScheduler.setReminder(MainActivity.this,AlarmReceiver.class,localData.get_hour(),localData.get_min());
                    ll_set_time.setAlpha(1f);
                }

                else
                {
                    Log.d(TAG, "onCheckedChanged: false");
                    NotificationScheduler.cancelReminder(MainActivity.this,AlarmReceiver.class);
                    ll_set_time.setAlpha(0.4f);
                }

            }
        });

        ll_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(localData.getReminderStatus())
                   showTimePickerDialog(localData.get_hour(),localData.get_min());
            }
        });

    }

    private void showTimePickerDialog(int h, int m) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.timepicker_header, null);

        TimePickerDialog builder = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        Log.d(TAG, "onTimeSet: hour " + hour);
                        Log.d(TAG, "onTimeSet: min " + min);
                        localData.set_hour(hour);
                        localData.set_min(min);
                        tvTime.setText(getFormatedTime(hour,min));
                        NotificationScheduler.setReminder(MainActivity.this,AlarmReceiver.class,localData.get_hour(),localData.get_min());


                    }
                }, h, m, false);

        builder.setCustomTitle(view);
        builder.show();

    }

    public String getFormatedTime(int h,int m)
    {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h+":"+m;
        String newDateString="";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT,getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return newDateString;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }
}
