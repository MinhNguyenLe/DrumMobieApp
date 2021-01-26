package com.example.drumapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    Spinner[] spinners;
    ImageButton[] plays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        spinners = new Spinner[6];
        plays = new ImageButton[6];
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
            plays[i] = (ImageButton) findViewById(getResources().getIdentifier(pname, "id", getPackageName()));
            ArrayAdapter<String> adapter=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            getItems(name)
                    );

            adapter.setDropDownViewResource
                    (android.R.layout.simple_list_item_single_choice);
            spinners[i].setAdapter(adapter);
            final int finalI = i;
            spinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    change(finalI);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            plays[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resourceName = v.getResources().getResourceName(v.getId());
                play(resourceName);
            }
        });
        }
    }

    private void change(int i) {
        //Log.i("[SET]", i + "");
        String name = "";
        long level = 0;
        String last = "";
        switch (i)
        {
            case 0: name = "hh"; break;
            case 1: name = "ki"; break;
            case 2: name = "sn"; break;
            case 3: name = "pi"; break;
            case 4: name = "ba"; break;
            case 5: name = "bo"; break;
        }
        level = spinners[i].getSelectedItemId();
        MainActivity._instance.setSounds(name, (int) (level + 1));
    }

    private void play(String name) {
        String soundname = name.substring(23,25);
        switch (soundname)
        {
            case "hh": MainActivity._instance.sp.play(MainActivity._instance.hihat, 1, 1, 0, 0, 1); break;
            case "ki": MainActivity._instance.sp.play(MainActivity._instance.kick, 1, 1, 0, 0, 1); break;
            case "sn": MainActivity._instance.sp.play(MainActivity._instance.snare, 1, 1, 0, 0, 1); break;
            case "pi": MainActivity._instance.sp.play(MainActivity._instance.piano, 1, 1, 0, 0, 1); break;
            case "ba": MainActivity._instance.sp.play(MainActivity._instance.bass, 1, 1, 0, 0, 1); break;
            case "bo": MainActivity._instance.sp.play(MainActivity._instance.bongo, 1, 1, 0, 0, 1); break;
        }

    }

    public static String[] getItems(String name)
    {

        String high = "High ";
        String medium = "Medium ";
        String low = "Low ";
        String last = "Hat";
        switch (name)
        {
            case "ki": last = "Kick"; break;
            case "sn": last = "Snare"; break;
            case "pi": last = "KeysBell"; break;
            case "ba": last = "Bass"; break;
            case "bo": last = "Bongo";; break;
        }
        high += last;
        medium += last;
        low += last;
        return new String[] {high,medium,low};
    }
}