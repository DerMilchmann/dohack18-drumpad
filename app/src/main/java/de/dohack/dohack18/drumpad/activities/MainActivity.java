package de.dohack.dohack18.drumpad.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.media.MediaRecorder.AudioSource;
import android.widget.TextView;

import com.example.jakob.drumpad.R;
import com.skyfishjy.library.RippleBackground;

import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private TextView bpmText;
    private TextView taktText;
    RippleBackground rippleBackground;
    private ImageView image;
    private LinkedList<ImageView> targets;
    Takt takt;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable(){
        @Override
        public void run(){
            createNewTarget();
            timerHandler.postDelayed(this, takt.getSchlaglaenge());
        }
    };

    public class AudioStreamTask extends AsyncTask<Void, Double, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SoundMeter soundMeter = new SoundMeter();
            soundMeter.start();

            while(true) {
                publishProgress(20 * Math.log(soundMeter.getAmplitude()) / Math.log(10));
            }
        }

        @Override
        protected void onProgressUpdate(Double... dbValues) {
            super.onProgressUpdate(dbValues);
            System.out.println(dbValues[0].intValue());
            if(dbValues[0].intValue() > 80) {
                System.out.println("tapped!");
                if(!targets.isEmpty()) {
                    targets.getFirst().performClick();
                    targets.removeFirst();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        AsyncTask reader = new AudioStreamTask().execute();

        rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        takt = new Takt(4,4);
        bpmText = (TextView) findViewById(R.id.bpmText);
        bpmText.setText(Integer.toString(takt.getBpm()));
        bpmText.setOnClickListener(v -> takt.incrementBpm());
        taktText = (TextView) findViewById(R.id.taktText);
        taktText.setText(takt.getAnzahlGrundschlaege() + "/" + takt.getNotenlaenge());
        
        targets = new LinkedList<>();
        timerHandler.postDelayed(timerRunnable,0);
    }

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    private void createNewTarget()  {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.mainView);

        image = new ImageView(this);
        image.setImageDrawable(getDrawable(R.drawable.ic_blur_circular_black_24dp));
        setRushToCenterAnimation(image);
        targets.addLast(image);
        image.setOnClickListener( view -> {
            layout.removeView(view);
        });

        layout.addView(image);
    }

    private void setRushToCenterAnimation(ImageView view) {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        int[] location = {0,0};
        rippleBackground.getLocationInWindow(location);
        int centerX = screenSize.x/2;
        int centerY = location[1]+rippleBackground.getHeight()/3;

        //Point randomEntrancePoint = generateRandomEntrancePoint(screenSize);
        Point entrancePoint = new Point(screenSize.x,centerY);

        view.setX(entrancePoint.x);
        view.setY(entrancePoint.y);

        ObjectAnimator objectAnimatorXTranslation = ObjectAnimator.ofFloat(view, "translationX", 0);
        objectAnimatorXTranslation.setDuration(2000);
        objectAnimatorXTranslation.setInterpolator(new LinearInterpolator());
        objectAnimatorXTranslation.start();
        /*ObjectAnimator objectAnimatorYTranslation = ObjectAnimator.ofFloat(view, "translationY", centerY);
        objectAnimatorYTranslation.setDuration(2000);
        objectAnimatorYTranslation.setInterpolator(new LinearInterpolator());
        objectAnimatorYTranslation.start();*/
    }
}