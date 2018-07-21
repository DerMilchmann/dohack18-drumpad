package de.dohack.dohack18.drumpad.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

public class AudioStreamReader extends AsyncTask<Void, short[], Void> {
    int blockSize = 2048;// = 256;
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2;

    @Override
    protected Void doInBackground(Void... params) {

        try {
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
            if (audioRecord == null) {
                return null;
            }

            final short[] buffer = new short[blockSize];
            final double[] toTransform = new double[blockSize];
            audioRecord.startRecording();
            while (audioRecord.getState() == AudioRecord.RECORDSTATE_RECORDING) {
                Thread.sleep(100);
                final int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                publishProgress(buffer);
            }
            audioRecord.stop();
            audioRecord.release();
        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
        return null;
    }



    @Override
    protected void onProgressUpdate(short[]... buffer) {
        super.onProgressUpdate(buffer);
        float freq = calculate(buffer[0]);
    }


    public static float calculate(short [] buffer) {
        /*double sum = 0;
        for (int i = 0; i < readSize; i++) {
            sum += buffer [i] * buffer [i];
        }
        if (readSize > 0) {
            final double amplitude = sum / readSize;
            volumeBar.setProgress((int) Math.sqrt(amplitude));
        }*/
        return 0.0f;
    }
}
