package com.example.apollinariia.litup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.apollinariia.litup.data.AlarmDbHelper;
import com.example.apollinariia.litup.sensors.AccelerometerDetector;
import com.example.apollinariia.litup.sensors.AccelerometerGraph;
import com.example.apollinariia.litup.sensors.AccelerometerProcessing;
import com.example.apollinariia.litup.sensors.OnStepCountChangeListener;

import org.achartengine.GraphicalView;


public class AlarmAlertActivity extends Activity {

    private static final String TAG = "AlarmAlertActivity";
    private Alarm alarm;
    private MediaPlayer mediaPlayer = null;
    private Vibrator vibrator;
    private boolean alarmActive;
    Ringtone ringtone;
    private AccelerometerGraph mAccelGraph;
    private AccelerometerDetector mAccelDetector;
    private TextView mStepCountTextView;
    private int mStepCount = 0;
    public TextView gyro;
    private final AccelerometerProcessing mAccelerometerProcessing = AccelerometerProcessing.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStepCountTextView = (TextView) findViewById(R.id.mStepCount);
        gyro = (TextView) findViewById(R.id.textGyro);

        mAccelGraph = new AccelerometerGraph(AccelerometerProcessing.THRESH_INIT_VALUE);

        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.alarm_alert);

        AlarmDbHelper.init(getBaseContext());

        final Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            alarm = Parcelables.toParcelableAlarm(bundle.getByteArray(Alarm.TAG));
        }

        if (alarm == null) {
            Log.d(TAG, "Alarm is null");
            return;
        }

        this.setTitle(alarm.getName());

        final TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);

        final CallStateListener callStateListener = new CallStateListener();

        telephonyManager.listen(callStateListener, CallStateListener.LISTEN_CALL_STATE);


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);

        //creates a graph of changes in accelerometer (the view is set to invisible mainly for testing purposes).
        GraphicalView graphicalView = mAccelGraph.getView(this);
        graphicalView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
        graphLayout.addView(graphicalView);

        //initializing sensors, step counter, and shaking (shaking with time interval).
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelDetector = new AccelerometerDetector(sensorManager, mAccelGraph);
        mAccelDetector.setStepCountChangeListener(new OnStepCountChangeListener() {
            @Override
            public void onStepCountChange(long eventMsecTime) {
                //show steps!
                mStepCountTextView = (TextView) findViewById(R.id.mStepCount);
                mStepCount++;
                mStepCountTextView.setText(getResources().getString(R.string.steps) + String.valueOf(mStepCount));

                if (mStepCount == 1) {
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            if (mStepCount >= 3) {
                                alarm.snooze(getApplicationContext());
                                stopAlarm();
                                Intent i = new Intent(AlarmAlertActivity.this, FaceTrackerActivity.class);
                                startActivity(i);
                                mStepCount = 0;
                            }
                        }
                    }.start();
                } else if (mStepCount == 25) {
                    stopAlarm();
                    finish();
                }
            }
        });
        initializeSeekBar();
        mAccelDetector.startDetector();
        startAlarm();
    }


    private void startAlarm() {
        if (alarm != null && !alarm.getTonePath().isEmpty()) {
            Log.d(TAG, "startAlarm(): " + alarm.getAlarmTimeStringParcelable());

            mediaPlayer = new MediaPlayer();

            if (alarm.shouldVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 200, 200, 200};
                vibrator.vibrate(pattern, 0);
            }

            try {

                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.setDataSource(this, Uri.parse(alarm.getTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();

                ringtone.play();

            } catch (Exception e) {
            } finally {
                mediaPlayer.release();
                alarmActive = false;
            }

        }
    }

    private void initializeSeekBar() {
        final SeekBar seekBar = (SeekBar) findViewById(R.id.offset_seekBar);
        seekBar.setMax(130 - 90);
        seekBar.setProgress((int) AccelerometerProcessing.getInstance().getThresholdValue());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double threshold = AccelerometerProcessing.THRESH_INIT_VALUE * (progress + 90) / 100;
                mAccelerometerProcessing.onThresholdChange(threshold);
                mAccelGraph.onThresholdChange(threshold);
                Log.d("ADebugTag", "Value: " + Double.toString(threshold));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void stopAlarm() {
        if (alarm != null) {
            Log.d(TAG, "stop alarm");
            alarm.setActive(false);
            AlarmDbHelper.update(alarm);
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelDetector = new AccelerometerDetector(sensorManager, mAccelGraph);
            mAccelDetector.setStepCountChangeListener(new OnStepCountChangeListener() {
                @Override
                public void onStepCountChange(long eventMsecTime) {
                    ++mStepCount;
                    mStepCountTextView.setText(String.valueOf(mStepCount));
                }
            });
            try {
                vibrator.cancel();
            } catch (Exception e) {
            }
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
            }
            try {
                mediaPlayer.release();
            } catch (Exception e) {
            }
            try {
                ringtone.stop();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Log.d(TAG, intent.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmActive = true;
    }

    @Override
    public void onBackPressed() {
        if (!alarmActive) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }

    private class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(getClass().getSimpleName(), "Incoming call: "
                            + incomingNumber);
                    try {
                        mediaPlayer.pause();
                    } catch (Exception e) {

                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(getClass().getSimpleName(), "Call State Idle");
                    try {
                        mediaPlayer.start();
                    } catch (Exception e) {
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}