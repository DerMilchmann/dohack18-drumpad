package de.dohack.dohack18.drumpad.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.media.MediaRecorder.AudioSource;
import android.widget.TextView;

import com.example.jakob.drumpad.R;
import com.skyfishjy.library.RippleBackground;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private TextView bpmText;
    private TextView taktText;
    private ImageView image;
    private Point centre = new Point();

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
                image.performClick();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        AsyncTask reader = new AudioStreamTask().execute();

        RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        /*
        float centreX = rippleBackground.getX() + rippleBackground.getWidth()  / 2;
        float centreY = rippleBackground.getY() + rippleBackground.getHeight() / 2;
        centre = new Point();
        centre.set((int)centreX, (int)centreY); */

        this.getWindowManager().getDefaultDisplay().getRealSize(centre);
        centre.x = centre.x/2;
        centre.y = centre.y/2;

        createNewTarget();

        //TODO: bei setText(takt.getBpm()) wird eine Exception geworfen
        /*Takt takt = new Takt(4,4);
        bpmText = (TextView) findViewById(R.id.bpmText);
        bpmText.setText(takt.getBpm());
        taktText = (TextView) findViewById(R.id.taktText);
        taktText.setText(takt.getAnzahlGrundschlaege() + "/" + takt.getNotenlaenge());*/

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

    private void createNewTarget() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.mainView);

        image = new ImageView(this);
        image.setImageDrawable(getDrawable(R.drawable.ic_blur_circular_black_24dp));
        setRushToCenterAnimation(image);

        image.setOnClickListener( view -> {
            layout.removeView(view);

            createNewTarget();
        });

        layout.addView(image);
    }

    private void setRushToCenterAnimation(ImageView view) {
        //Point randomEntrancePoint = generateRandomEntrancePoint();
        view.setX(900);
        view.setY(900);

        ObjectAnimator objectAnimatorXTranslation = ObjectAnimator.ofFloat(view, "translationX", centre.x);
        objectAnimatorXTranslation.setDuration(3000);
        objectAnimatorXTranslation.start();
        ObjectAnimator objectAnimatorYTranslation = ObjectAnimator.ofFloat(view, "translationY", 900);
        objectAnimatorYTranslation.setDuration(3000);
        objectAnimatorYTranslation.start();
    }

    //TODO(Fabian): Random-Function überarbeiten
    private Point generateRandomEntrancePoint() {
        Random random = new Random();
        int side = random.nextInt(4);
        Point randomPoint = new Point();

        switch (side) {
            case 0: randomPoint.x = 0;
                    randomPoint.y = random.nextInt(centre.y);
                    break;
            case 1: randomPoint.x = centre.x;
                    randomPoint.y = random.nextInt(centre.y);
                    break;
            case 2: randomPoint.y = 0;
                    randomPoint.x = random.nextInt(centre.x);
                    break;
            case 3: randomPoint.y = centre.y;
                    randomPoint.x = random.nextInt(centre.x);
                    break;
        }

        return randomPoint;
    }
}
