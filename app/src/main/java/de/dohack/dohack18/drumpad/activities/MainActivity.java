package de.dohack.dohack18.drumpad.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.media.MediaRecorder.AudioSource;
import android.widget.TextView;

import com.example.jakob.drumpad.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private ProgressBar progressBar;
    private TextView bpmText;
    private TextView taktText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        progressBar = (ProgressBar) findViewById(R.id.volumeBar);
        progressBar.setMax(150);

        AsyncTask reader = new AudioStreamReader().execute();

        Takt takt = new Takt(4,4);
        bpmText = (TextView) findViewById(R.id.bpmText);
        bpmText.setText(takt.getBpm());
        taktText = (TextView) findViewById(R.id.taktText);
        taktText.setText(takt.getAnzahlGrundschlaege() + "/" + takt.getNotenlaenge());

    }

    public class AudioStreamReader extends AsyncTask<Void, Double, Void> {
        private int blockSize = 2048;// = 256;

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
            /*
             * TODO(Fabian): Die Dezibel müssen als relativer wert angegeben werden, es muss also
             *  vorab ein Pegel brechnet werden und dann die Abweichung davon berechnet werden
            */
            progressBar.setProgress(dbValues[0].intValue());
        }
    }

    // Requesting permission to RECORD_AUDIO
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
}
