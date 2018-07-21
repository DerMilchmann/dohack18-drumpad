package de.dohack.dohack18.drumpad.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.*;

import com.example.jakob.drumpad.R;

public class MainActivity extends AppCompatActivity {

    Takt takt;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setContentView(R.layout.activity_main);
        takt = new Takt(4,4);
    }
}
