package com.example.drumapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    Spinner[] spinners;
    Button[] plays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        spinners = new Spinner[6];
        plays = new Button[6];
        for(int i = 0; i < 6; i ++)
        {
            String name = "";
            switch (i)
            {
                case 0: name = "hh"; break;
                case 1: name = "ki"; break;
                case 2: name = "sn"; break;
                case 3: name = "pi"; break;
                case 4: name = "ba"; break;
                case 5: name = "bo"; break;
            }
            String sname = name + "spinner";
            String pname = name + "play";
            spinners[i] = (Spinner) findViewById(getResources().getIdentifier(sname, "id", getPackageName()));
            plays[i] = (Button) findViewById(getResources().getIdentifier(pname, "id", getPackageName()));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }
}