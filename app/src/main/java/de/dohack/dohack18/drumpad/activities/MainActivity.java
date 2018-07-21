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
    private TextView volumeBarText;
    private TextView bpmText;
    private TextView taktText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        progressBar = (ProgressBar) findViewById(R.id.volumeBar);
        //progressBar.setMin(30000);
        progressBar.setMax(33000);
        volumeBarText = (TextView) findViewById(R.id.volumeBarText);

        AsyncTask reader = new AudioStreamReader().execute();

        Takt takt = new Takt(4,4);
        bpmText = (TextView) findViewById(R.id.bpmText);
        bpmText.setText(takt.getAnzahlGrundschlaege() + "/" + takt.getNotenlaenge());
        taktText = (TextView) findViewById(R.id.taktText);
        taktText.setText(takt.getBpm());
    }

    public class AudioStreamReader extends AsyncTask<Void, short[], Void> {
        private int blockSize = 2048;// = 256;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                AudioRecord audioRecord = findAudioRecord();

                final short[] buffer = new short[blockSize];
                final double[] toTransform = new double[blockSize];
                if(audioRecord != null) {
                    if(audioRecord.getState() == AudioRecord.STATE_INITIALIZED) audioRecord.startRecording();
                    else;

                    boolean test = audioRecord.getState() == AudioRecord.STATE_INITIALIZED;
                    System.out.println(test);
                    boolean test2 = audioRecord.getState() == AudioRecord.RECORDSTATE_RECORDING;
                    System.out.println(test2);

                    while (true) {
                        Thread.sleep(100);
                        final int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                        short[] readSize = {(short)bufferReadResult};
                        publishProgress(buffer, readSize);
                    }
                    //audioRecord.stop();
                    //audioRecord.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onProgressUpdate(short[]... buffers) {
            super.onProgressUpdate(buffers);
            progressBar.setProgress(calculate(buffers[0], buffers[1][0]));
            //textView.setText(progressBar.getProgress());
        }


        public int calculate(short [] buffer, int readSize) {
            double sum = 0;
            for (int i = 0; i < readSize; i++) {
                sum += buffer [i] * buffer [i];
            }
            if (readSize > 0) {
                final double amplitude = sum / readSize;
                System.out.println(Math.sqrt(amplitude));
                return (int) Math.sqrt(amplitude);
            }
            return 0;
        }
    }

    public AudioRecord findAudioRecord() {
        int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        System.out.println("Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        System.out.println(rate + "Exception, keep trying.");
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
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
