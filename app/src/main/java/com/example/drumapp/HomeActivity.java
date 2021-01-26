package com.example.drumapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    Button record;
    Button beat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        record = (Button) findViewById(R.id.recordid);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });

        beat = (Button) findViewById(R.id.beatid);
        beat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beat();
            }
        });
    }

    private void beat() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void record() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
}
