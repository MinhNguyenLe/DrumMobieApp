package com.example.drumapp;//Package


//Libraries

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//Main Class
public class MainActivity extends AppCompatActivity {

    //Declare Checkboxes
    CheckBox s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16;
    CheckBox hh1, hh2, hh3, hh4, hh5, hh6, hh7, hh8, hh9, hh10, hh11, hh12, hh13, hh14, hh15, hh16;
    CheckBox ht1, ht2, ht3, ht4, ht5, ht6, ht7, ht8, ht9, ht10, ht11, ht12, ht13, ht14, ht15, ht16;
    CheckBox lt1, lt2, lt3, lt4, lt5, lt6, lt7, lt8, lt9, lt10, lt11, lt12, lt13, lt14, lt15, lt16;
    CheckBox k1, k2, k3, k4, k5, k6, k7, k8, k9, k10, k11, k12, k13, k14, k15, k16;
    CheckBox c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16;
    CheckBox oh1, oh2, oh3, oh4, oh5, oh6, oh7, oh8, oh9, oh10, oh11, oh12, oh13, oh14, oh15, oh16;
    CheckBox ri1, ri2, ri3, ri4, ri5, ri6, ri7, ri8, ri9, ri10, ri11, ri12, ri13, ri14, ri15, ri16;
    CheckBox rb1, rb2, rb3, rb4, rb5, rb6, rb7, rb8, rb9, rb10, rb11, rb12, rb13, rb14, rb15, rb16;

    //Declare Sounds

    SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    int snare;
    int crash;
    int hi;
    int lo;
    int kick;
    int closed;
    int open;
    int rbell;
    int ride;

    //Declare UI Elements

    Button add;
    Button back;
    Button next;
    Button middle;
    Button save;
    Button dup;
    Button play;
    Button setting;
    ImageView playhead;
    TextView land;
    SeekBar speedbar;

    //Declare Status Booleans

    Boolean playing;
    Boolean saving;
    public static boolean isRendering = false;

    //Declare Other Status Variables

    String saveString = "";
    String finalSave = "";

    int currentpage;
    int totalpages;
    int barx;
    int speed;
    int ids;

    final List<String> saveList = new ArrayList<String>();

    final int defaultx = 95;
    final int speedmult = 10;

    RenderThread render = new RenderThread();
    private Button record;

    //Define Control Bar Functions

    public void add() {
        if (totalpages != currentpage) {
            save();
            saveList.set(currentpage - 1, saveString);
            loadpage(totalpages);
            currentpage = totalpages;
        }
        save();
        saveList.set(currentpage - 1, saveString);
        totalpages++;
        currentpage = totalpages;
        speed = (speedbar.getProgress() + 1);
        speed = (int) (Math.round(speed / 10) * 10);
        if (speed <= 9) {
            saveList.add("01020304050607080910111213141516$s0" + speed);
        }
        else if (speed == 100) {
            saveList.add("01020304050607080910111213141516$s" + speed);
        }
        else {
            saveList.add("01020304050607080910111213141516$s90");
        }
        clearAll();
        setUI();
        speedbar.setProgress(speed);
        speed = (speedbar.getProgress() + 1) * speedmult;

    }

    public void next() {
        if (currentpage < saveList.size()) {
            save();
            saveList.set(currentpage - 1, saveString);
            currentpage++;
            loadpage(currentpage);
        } else if (currentpage == saveList.size()) {
            save();
            saveList.set(currentpage - 1, saveString);
            currentpage = 1;
            loadpage(currentpage);
        }
    }

    public void back() {
        if (currentpage != 1) {
            save();
            saveList.set(currentpage - 1, saveString);
            currentpage--;
            loadpage(currentpage);
        } else if (currentpage == 1) {
            save();
            saveList.set(currentpage - 1, saveString);
            currentpage = saveList.size();
            loadpage(currentpage);
        }
    }

    public void dup() {
        save();
        saveList.set(currentpage - 1, saveString);
        saveList.add(saveString);
        clearAll();
        setUI();
        speed = (speedbar.getProgress() + 1);
        speed = (int) (Math.round(speed / 10) * 10);
        speedbar.setProgress(speed);
        speed = (speedbar.getProgress() + 1) * speedmult;
        totalpages++;
        currentpage = totalpages;
        //System.out.println(saveList.get(currentpage - 1));
        loadpage(currentpage);
    }

    //Define Recording/Saving Functions

    public void loadpage(int page) {
        clearAll();
        setUI();
        String pageSave;
        pageSave = saveList.get(page - 1);
        int currentColumn = 0;
        String currentRead = "";
        String speedstring;
        for (int v = 0; v < (pageSave.length() - 1); v = v + 2) {
            currentRead = String.valueOf(pageSave.charAt(v)) + String.valueOf(pageSave.charAt(v + 1));
            switch (currentRead) {
                case "$s":
                    speedstring = pageSave.substring(v + 2);
                    speed = Integer.parseInt(speedstring);
                    speed = (int) (Math.round(speed / 10) * 10);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            speedbar.setProgress(speed);
                        }
                    });
                    speed = speed * speedmult;

                    break;
                case "01":
                    currentColumn = 1;
                    break;
                case "02":
                    currentColumn = 2;
                    break;
                case "03":
                    currentColumn = 3;
                    break;
                case "04":
                    currentColumn = 4;
                    break;
                case "05":
                    currentColumn = 5;
                    break;
                case "06":
                    currentColumn = 6;
                    break;
                case "07":
                    currentColumn = 7;
                    break;
                case "08":
                    currentColumn = 8;
                    break;
                case "09":
                    currentColumn = 9;
                    break;
                case "10":
                    currentColumn = 10;
                    break;
                case "11":
                    currentColumn = 11;
                    break;
                case "12":
                    currentColumn = 12;
                    break;
                case "13":
                    currentColumn = 13;
                    break;
                case "14":
                    currentColumn = 14;
                    break;
                case "15":
                    currentColumn = 15;
                    break;
                case "16":
                    currentColumn = 16;
                    break;
                case "cr":
                    switch (currentColumn) {
                        case 1:
                            c1.setChecked(true);
                            break;
                        case 2:
                            c2.setChecked(true);
                            break;
                        case 3:
                            c3.setChecked(true);
                            break;
                        case 4:
                            c4.setChecked(true);
                            break;
                        case 5:
                            c5.setChecked(true);
                            break;
                        case 6:
                            c6.setChecked(true);
                            break;
                        case 7:
                            c7.setChecked(true);
                            break;
                        case 8:
                            c8.setChecked(true);
                            break;
                        case 9:
                            c9.setChecked(true);
                            break;
                        case 10:
                            c10.setChecked(true);
                            break;
                        case 11:
                            c11.setChecked(true);
                            break;
                        case 12:
                            c12.setChecked(true);
                            break;
                        case 13:
                            c13.setChecked(true);
                            break;
                        case 14:
                            c14.setChecked(true);
                            break;
                        case 15:
                            c15.setChecked(true);
                            break;
                        case 16:
                            c16.setChecked(true);
                            break;
                    }
                    break;
                case "hh":
                    switch (currentColumn) {
                        case 1:
                            hh1.setChecked(true);
                            break;
                        case 2:
                            hh2.setChecked(true);
                            break;
                        case 3:
                            hh3.setChecked(true);
                            break;
                        case 4:
                            hh4.setChecked(true);
                            break;
                        case 5:
                            hh5.setChecked(true);
                            break;
                        case 6:
                            hh6.setChecked(true);
                            break;
                        case 7:
                            hh7.setChecked(true);
                            break;
                        case 8:
                            hh8.setChecked(true);
                            break;
                        case 9:
                            hh9.setChecked(true);
                            break;
                        case 10:
                            hh10.setChecked(true);
                            break;
                        case 11:
                            hh11.setChecked(true);
                            break;
                        case 12:
                            hh12.setChecked(true);
                            break;
                        case 13:
                            hh13.setChecked(true);
                            break;
                        case 14:
                            hh14.setChecked(true);
                            break;
                        case 15:
                            hh15.setChecked(true);
                            break;
                        case 16:
                            hh16.setChecked(true);
                            break;
                    }
                    break;
                case "ht":
                    switch (currentColumn) {
                        case 1:
                            ht1.setChecked(true);
                            break;
                        case 2:
                            ht2.setChecked(true);
                            break;
                        case 3:
                            ht3.setChecked(true);
                            break;
                        case 4:
                            ht4.setChecked(true);
                            break;
                        case 5:
                            ht5.setChecked(true);
                            break;
                        case 6:
                            ht6.setChecked(true);
                            break;
                        case 7:
                            ht7.setChecked(true);
                            break;
                        case 8:
                            ht8.setChecked(true);
                            break;
                        case 9:
                            ht9.setChecked(true);
                            break;
                        case 10:
                            ht10.setChecked(true);
                            break;
                        case 11:
                            ht11.setChecked(true);
                            break;
                        case 12:
                            ht12.setChecked(true);
                            break;
                        case 13:
                            ht13.setChecked(true);
                            break;
                        case 14:
                            ht14.setChecked(true);
                            break;
                        case 15:
                            ht15.setChecked(true);
                            break;
                        case 16:
                            ht16.setChecked(true);
                            break;
                    }
                    break;
                case "lt":
                    switch (currentColumn) {
                        case 1:
                            lt1.setChecked(true);
                            break;
                        case 2:
                            lt2.setChecked(true);
                            break;
                        case 3:
                            lt3.setChecked(true);
                            break;
                        case 4:
                            lt4.setChecked(true);
                            break;
                        case 5:
                            lt5.setChecked(true);
                            break;
                        case 6:
                            lt6.setChecked(true);
                            break;
                        case 7:
                            lt7.setChecked(true);
                            break;
                        case 8:
                            lt8.setChecked(true);
                            break;
                        case 9:
                            lt9.setChecked(true);
                            break;
                        case 10:
                            lt10.setChecked(true);
                            break;
                        case 11:
                            lt11.setChecked(true);
                            break;
                        case 12:
                            lt12.setChecked(true);
                            break;
                        case 13:
                            lt13.setChecked(true);
                            break;
                        case 14:
                            lt14.setChecked(true);
                            break;
                        case 15:
                            lt15.setChecked(true);
                            break;
                        case 16:
                            lt16.setChecked(true);
                            break;
                    }
                    break;
                case "sn":
                    switch (currentColumn) {
                        case 1:
                            s1.setChecked(true);
                            break;
                        case 2:
                            s2.setChecked(true);
                            break;
                        case 3:
                            s3.setChecked(true);
                            break;
                        case 4:
                            s4.setChecked(true);
                            break;
                        case 5:
                            s5.setChecked(true);
                            break;
                        case 6:
                            s6.setChecked(true);
                            break;
                        case 7:
                            s7.setChecked(true);
                            break;
                        case 8:
                            s8.setChecked(true);
                            break;
                        case 9:
                            s9.setChecked(true);
                            break;
                        case 10:
                            s10.setChecked(true);
                            break;
                        case 11:
                            s11.setChecked(true);
                            break;
                        case 12:
                            s12.setChecked(true);
                            break;
                        case 13:
                            s13.setChecked(true);
                            break;
                        case 14:
                            s14.setChecked(true);
                            break;
                        case 15:
                            s15.setChecked(true);
                            break;
                        case 16:
                            s16.setChecked(true);
                            break;
                    }
                    break;
                case "kb":
                    switch (currentColumn) {
                        case 1:
                            k1.setChecked(true);
                            break;
                        case 2:
                            k2.setChecked(true);
                            break;
                        case 3:
                            k3.setChecked(true);
                            break;
                        case 4:
                            k4.setChecked(true);
                            break;
                        case 5:
                            k5.setChecked(true);
                            break;
                        case 6:
                            k6.setChecked(true);
                            break;
                        case 7:
                            k7.setChecked(true);
                            break;
                        case 8:
                            k8.setChecked(true);
                            break;
                        case 9:
                            k9.setChecked(true);
                            break;
                        case 10:
                            k10.setChecked(true);
                            break;
                        case 11:
                            k11.setChecked(true);
                            break;
                        case 12:
                            k12.setChecked(true);
                            break;
                        case 13:
                            k13.setChecked(true);
                            break;
                        case 14:
                            k14.setChecked(true);
                            break;
                        case 15:
                            k15.setChecked(true);
                            break;
                        case 16:
                            k16.setChecked(true);
                            break;
                    }
                    break;
                case "oh":
                    switch (currentColumn) {
                        case 1:
                            oh1.setChecked(true);
                            break;
                        case 2:
                            oh2.setChecked(true);
                            break;
                        case 3:
                            oh3.setChecked(true);
                            break;
                        case 4:
                            oh4.setChecked(true);
                            break;
                        case 5:
                            oh5.setChecked(true);
                            break;
                        case 6:
                            oh6.setChecked(true);
                            break;
                        case 7:
                            oh7.setChecked(true);
                            break;
                        case 8:
                            oh8.setChecked(true);
                            break;
                        case 9:
                            oh9.setChecked(true);
                            break;
                        case 10:
                            oh10.setChecked(true);
                            break;
                        case 11:
                            oh11.setChecked(true);
                            break;
                        case 12:
                            oh12.setChecked(true);
                            break;
                        case 13:
                            oh13.setChecked(true);
                            break;
                        case 14:
                            oh14.setChecked(true);
                            break;
                        case 15:
                            oh15.setChecked(true);
                            break;
                        case 16:
                            oh16.setChecked(true);
                            break;
                    }
                    break;
                case "rb":
                    switch (currentColumn) {
                        case 1:
                            rb1.setChecked(true);
                            break;
                        case 2:
                            rb2.setChecked(true);
                            break;
                        case 3:
                            rb3.setChecked(true);
                            break;
                        case 4:
                            rb4.setChecked(true);
                            break;
                        case 5:
                            rb5.setChecked(true);
                            break;
                        case 6:
                            rb6.setChecked(true);
                            break;
                        case 7:
                            rb7.setChecked(true);
                            break;
                        case 8:
                            rb8.setChecked(true);
                            break;
                        case 9:
                            rb9.setChecked(true);
                            break;
                        case 10:
                            rb10.setChecked(true);
                            break;
                        case 11:
                            rb11.setChecked(true);
                            break;
                        case 12:
                            rb12.setChecked(true);
                            break;
                        case 13:
                            rb13.setChecked(true);
                            break;
                        case 14:
                            rb14.setChecked(true);
                            break;
                        case 15:
                            rb15.setChecked(true);
                            break;
                        case 16:
                            rb16.setChecked(true);
                            break;
                    }
                    break;
                case "ri":
                    switch (currentColumn) {
                        case 1:
                            ri1.setChecked(true);
                            break;
                        case 2:
                            ri2.setChecked(true);
                            break;
                        case 3:
                            ri3.setChecked(true);
                            break;
                        case 4:
                            ri4.setChecked(true);
                            break;
                        case 5:
                            ri5.setChecked(true);
                            break;
                        case 6:
                            ri6.setChecked(true);
                            break;
                        case 7:
                            ri7.setChecked(true);
                            break;
                        case 8:
                            ri8.setChecked(true);
                            break;
                        case 9:
                            ri9.setChecked(true);
                            break;
                        case 10:
                            ri10.setChecked(true);
                            break;
                        case 11:
                            ri11.setChecked(true);
                            break;
                        case 12:
                            ri12.setChecked(true);
                            break;
                        case 13:
                            ri13.setChecked(true);
                            break;
                        case 14:
                            ri14.setChecked(true);
                            break;
                        case 15:
                            ri15.setChecked(true);
                            break;
                        case 16:
                            ri16.setChecked(true);
                            break;
                    }
                    break;
            }
        }
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speedbar.setProgress(speed / speedmult);
            }
        });
        */
    }

    public void save() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                save.setText("X");
            }
        });
        saveString = "";
        if (!saving) {
            ids = 1;
            saving = true;
            saveAction();
            speed = (speedbar.getProgress() + 1);
            speed = (int) (Math.round(speed / 10) * 10);
            if (speed <= 9) {
                saveString = saveString + "$s0" + String.valueOf(speed);
            }
            else if (speed == 100) {
                saveString = saveString + "$s99";
            }
            else {
                saveString = saveString + "$s" + String.valueOf(speed);
            }
            speed = speed * speedmult;
        }
    }

    public void soundSave(CheckBox hh, CheckBox s, CheckBox ht, CheckBox lt, CheckBox k, CheckBox c, CheckBox oh, CheckBox ri, CheckBox rb) {

        if (c.isChecked()) {
            saveString = saveString + "cr";
        }
        if (hh.isChecked()) {
            saveString = saveString + "hh";
        }
        if (s.isChecked()) {
            saveString = saveString + "sn";
        }
        if (ht.isChecked()) {
            saveString = saveString + "ht";
        }
        if (lt.isChecked()) {
            saveString = saveString + "lt";
        }
        if (k.isChecked()) {
            saveString = saveString + "kb";
        }
        if (oh.isChecked()) {
            saveString = saveString + "oh";
        }
        if (rb.isChecked()) {
            saveString = saveString + "rb";
        }
        if (ri.isChecked()) {
            saveString = saveString + "ri";
        }
    }

    public void saveAction() {
        while (saving) {
            if (ids <= 9) {
                saveString = saveString + "0" + String.valueOf(ids);
            } else {
                saveString = saveString + String.valueOf(ids);
            }

            switch (ids) {
                case 1:
                    soundSave(hh1, s1, ht1, lt1, k1, c1, oh1, ri1, rb1);
                    break;
                case 2:
                    soundSave(hh2, s2, ht2, lt2, k2, c2, oh2, ri2, rb2);
                    break;
                case 3:
                    soundSave(hh3, s3, ht3, lt3, k3, c3, oh3, ri3, rb3);
                    break;
                case 4:
                    soundSave(hh4, s4, ht4, lt4, k4, c4, oh4, ri4, rb4);
                    break;
                case 5:
                    soundSave(hh5, s5, ht5, lt5, k5, c5, oh5, ri5, rb5);
                    break;
                case 6:
                    soundSave(hh6, s6, ht6, lt6, k6, c6, oh6, ri6, rb6);
                    break;
                case 7:
                    soundSave(hh7, s7, ht7, lt7, k7, c7, oh7, ri7, rb7);
                    break;
                case 8:
                    soundSave(hh8, s8, ht8, lt8, k8, c8, oh8, ri8, rb8);
                    break;
                case 9:
                    soundSave(hh9, s9, ht9, lt9, k9, c9, oh9, ri9, rb9);
                    break;
                case 10:
                    soundSave(hh10, s10, ht10, lt10, k10, c10, oh10, ri10, rb10);
                    break;
                case 11:
                    soundSave(hh11, s11, ht11, lt11, k11, c11, oh11, ri11, rb11);
                    break;
                case 12:
                    soundSave(hh12, s12, ht12, lt12, k12, c12, oh12, ri12, rb12);
                    break;
                case 13:
                    soundSave(hh13, s13, ht13, lt13, k13, c13, oh13, ri13, rb13);
                    break;
                case 14:
                    soundSave(hh14, s14, ht14, lt14, k14, c14, oh14, ri14, rb14);
                    break;
                case 15:
                    soundSave(hh15, s15, ht15, lt15, k15, c15, oh15, ri15, rb15);
                    break;
                case 16:
                    soundSave(hh16, s16, ht16, lt16, k16, c16, oh16, ri16, rb16);
                    break;
            }
            if (ids < 16) {
                ids++;
            } else if (ids == 16) {
                saving = false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        save.setText("SAVE");
                    }
                });
            }
        }
    }

    public void saveAll() {
//        finalSave = "";
//        for (int p = 1; p <= totalpages; p++) {
//            currentpage = p;
//            loadpage(currentpage);
//            save();
//            saveList.set(p - 1, saveString);
//        }
//        for (String page : saveList) {
//            finalSave = finalSave + page + "||";
//        }
//        Intent intent = new Intent(MainActivity.this, nameScreen.class);
//        intent.putExtra("content", finalSave);
//        startActivity(intent);
    }

    public void loadAll(String loadString) {
        //System.out.println(myPrefs.getString("1", template));
        String currentRead2;
        saveList.clear();
        saveString = "";
        for (int j = 0; j < (loadString.length() - 1); j = j + 2) {
            currentRead2 = String.valueOf(loadString.charAt(j)) + String.valueOf(loadString.charAt(j + 1));
            if (currentRead2.equals("||")) {
                saveList.add(saveString);
                saveString = "";
            }
            else {
                saveString = saveString + currentRead2;
            }
        }
    }

    //Define Playing/Stopping Functions

    public void nextPlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                next();
                play();
            }
        });
    }

    private void setting() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void play() {
        speed = (speedbar.getProgress() + 1) * speedmult;
        speed = (int) (Math.round(speed / 10) * 10);
        if (!playing) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playhead.setX(defaultx);
                    play.setText("STOP");
                    playhead.setVisibility(View.VISIBLE);
                }
            });

            ids = 1;
            playing = true;

            if (render.getStatus().equals(AsyncTask.Status.RUNNING)) {
                render.cancel(true);
            }

            render = new RenderThread();
            render.execute();
            isRendering = true;

        } else {
            playing = false;
            render.cancel(true);
            isRendering = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    play.setText("PLAY");
                    playhead.setX(defaultx);
                    playhead.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void soundCheck(CheckBox hh, CheckBox s, CheckBox ht, CheckBox lt, CheckBox k, CheckBox c, CheckBox oh, CheckBox ri, CheckBox rb) {

        if (c.isChecked()) {
            sp.play(crash, 1, 1, 0, 0, 1);
            saveString = saveString + "cr";
        }
        if (hh.isChecked()) {
            sp.stop(open);
            sp.play(closed, 1, 1, 0, 0, 1);
            saveString = saveString + "hh";
        }
        if (s.isChecked()) {
            sp.play(snare, 1, 1, 0, 0, 1);
            saveString = saveString + "sn";
        }
        if (ht.isChecked()) {
            sp.play(hi, 1, 1, 0, 0, 1);
            saveString = saveString + "ht";
        }
        if (lt.isChecked()) {
            sp.play(lo, 1, 1, 0, 0, 1);
            saveString = saveString + "ht";
        }
        if (k.isChecked()) {
            sp.play(kick, 1, 1, 0, 0, 1);
            saveString = saveString + "kb";
        }
        if (oh.isChecked()) {
            sp.stop(open);
            sp.play(open, 1, 1, 0, 0, 1);
            saveString = saveString + "oh";
        }
        if (rb.isChecked()) {
            sp.play(rbell, 1, 1, 0, 0, 1);
            saveString = saveString + "rb";
        }
        if (ri.isChecked()) {
            sp.play(ride, 1, 1, 0, 0, 1);
            saveString = saveString + "ri";
        }
        if (ids != 16) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playhead.setVisibility(View.VISIBLE);
                    playhead.setX(playhead.getX() + 96);
                }
            });
        }
    }

    class RenderThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            action();
            isRendering = false;
            render.cancel(true);
            return null;
        }

        public void action() {
            ids = 1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playhead.setVisibility(View.VISIBLE);
                }
            });
            while (playing) {
                if (ids <= 9) {
                    saveString = saveString + "0" + String.valueOf(ids);
                } else {
                    saveString = saveString + String.valueOf(ids);
                }

                switch (ids) {
                    case 1:
                        soundCheck(hh1, s1, ht1, lt1, k1, c1, oh1, ri1, rb1);
                        break;
                    case 2:
                        soundCheck(hh2, s2, ht2, lt2, k2, c2, oh2, ri2, rb2);
                        break;
                    case 3:
                        soundCheck(hh3, s3, ht3, lt3, k3, c3, oh3, ri3, rb3);
                        break;
                    case 4:
                        soundCheck(hh4, s4, ht4, lt4, k4, c4, oh4, ri4, rb4);
                        break;
                    case 5:
                        soundCheck(hh5, s5, ht5, lt5, k5, c5, oh5, ri5, rb5);
                        break;
                    case 6:
                        soundCheck(hh6, s6, ht6, lt6, k6, c6, oh6, ri6, rb6);
                        break;
                    case 7:
                        soundCheck(hh7, s7, ht7, lt7, k7, c7, oh7, ri7, rb7);
                        break;
                    case 8:
                        soundCheck(hh8, s8, ht8, lt8, k8, c8, oh8, ri8, rb8);
                        break;
                    case 9:
                        soundCheck(hh9, s9, ht9, lt9, k9, c9, oh9, ri9, rb9);
                        break;
                    case 10:
                        soundCheck(hh10, s10, ht10, lt10, k10, c10, oh10, ri10, rb10);
                        break;
                    case 11:
                        soundCheck(hh11, s11, ht11, lt11, k11, c11, oh11, ri11, rb11);
                        break;
                    case 12:
                        soundCheck(hh12, s12, ht12, lt12, k12, c12, oh12, ri12, rb12);
                        break;
                    case 13:
                        soundCheck(hh13, s13, ht13, lt13, k13, c13, oh13, ri13, rb13);
                        break;
                    case 14:
                        soundCheck(hh14, s14, ht14, lt14, k14, c14, oh14, ri14, rb14);
                        break;
                    case 15:
                        soundCheck(hh15, s15, ht15, lt15, k15, c15, oh15, ri15, rb15);
                        break;
                    case 16:
                        soundCheck(hh16, s16, ht16, lt16, k16, c16, oh16, ri16, rb16);
                        break;
                }
                if (ids < 16) {
                    ids++;
                } else if (ids == 16) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playhead.setX(defaultx);
                            playing = false;
                            nextPlay();
                        }
                    });
                    ids = 1;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speed = (speedbar.getProgress() + 1) * speedmult;
                        speed = (int) (Math.round(speed / 10) * 10);

                    }
                });

                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // UI and Game Initialization Functions

    public void setUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                add.setText("+");
                back.setText("<");
                next.setText(">");
                save.setText("SAVE");
                play.setText("PLAY");
                playhead.setX(defaultx);
                speedbar.setProgress(speed);
                playhead.setVisibility(View.INVISIBLE);
                middle.setText(String.valueOf(currentpage));
            }
        });
    }

    public void clearAll() {
        c1.setChecked(false);
        c2.setChecked(false);
        c3.setChecked(false);
        c4.setChecked(false);
        c5.setChecked(false);
        c6.setChecked(false);
        c7.setChecked(false);
        c8.setChecked(false);
        c9.setChecked(false);
        c10.setChecked(false);
        c11.setChecked(false);
        c12.setChecked(false);
        c13.setChecked(false);
        c14.setChecked(false);
        c15.setChecked(false);
        c16.setChecked(false);
        hh1.setChecked(false);
        hh2.setChecked(false);
        hh3.setChecked(false);
        hh4.setChecked(false);
        hh5.setChecked(false);
        hh6.setChecked(false);
        hh7.setChecked(false);
        hh8.setChecked(false);
        hh9.setChecked(false);
        hh10.setChecked(false);
        hh11.setChecked(false);
        hh12.setChecked(false);
        hh13.setChecked(false);
        hh14.setChecked(false);
        hh15.setChecked(false);
        hh16.setChecked(false);
        oh1.setChecked(false);
        oh2.setChecked(false);
        oh3.setChecked(false);
        oh4.setChecked(false);
        oh5.setChecked(false);
        oh6.setChecked(false);
        oh7.setChecked(false);
        oh8.setChecked(false);
        oh9.setChecked(false);
        oh10.setChecked(false);
        oh11.setChecked(false);
        oh12.setChecked(false);
        oh13.setChecked(false);
        oh14.setChecked(false);
        oh15.setChecked(false);
        oh16.setChecked(false);
        ht1.setChecked(false);
        ht2.setChecked(false);
        ht3.setChecked(false);
        ht4.setChecked(false);
        ht5.setChecked(false);
        ht6.setChecked(false);
        ht7.setChecked(false);
        ht8.setChecked(false);
        ht9.setChecked(false);
        ht10.setChecked(false);
        ht11.setChecked(false);
        ht12.setChecked(false);
        ht13.setChecked(false);
        ht14.setChecked(false);
        ht15.setChecked(false);
        ht16.setChecked(false);
        lt1.setChecked(false);
        lt2.setChecked(false);
        lt3.setChecked(false);
        lt4.setChecked(false);
        lt5.setChecked(false);
        lt6.setChecked(false);
        lt7.setChecked(false);
        lt8.setChecked(false);
        lt9.setChecked(false);
        lt10.setChecked(false);
        lt11.setChecked(false);
        lt12.setChecked(false);
        lt13.setChecked(false);
        lt14.setChecked(false);
        lt15.setChecked(false);
        lt16.setChecked(false);
        rb1.setChecked(false);
        rb2.setChecked(false);
        rb3.setChecked(false);
        rb4.setChecked(false);
        rb5.setChecked(false);
        rb6.setChecked(false);
        rb7.setChecked(false);
        rb8.setChecked(false);
        rb9.setChecked(false);
        rb10.setChecked(false);
        rb11.setChecked(false);
        rb12.setChecked(false);
        rb13.setChecked(false);
        rb14.setChecked(false);
        rb15.setChecked(false);
        rb16.setChecked(false);
        ri1.setChecked(false);
        ri2.setChecked(false);
        ri3.setChecked(false);
        ri4.setChecked(false);
        ri5.setChecked(false);
        ri6.setChecked(false);
        ri7.setChecked(false);
        ri8.setChecked(false);
        ri9.setChecked(false);
        ri10.setChecked(false);
        ri11.setChecked(false);
        ri12.setChecked(false);
        ri13.setChecked(false);
        ri14.setChecked(false);
        ri15.setChecked(false);
        ri16.setChecked(false);
        k1.setChecked(false);
        k2.setChecked(false);
        k3.setChecked(false);
        k4.setChecked(false);
        k5.setChecked(false);
        k6.setChecked(false);
        k7.setChecked(false);
        k8.setChecked(false);
        k9.setChecked(false);
        k10.setChecked(false);
        k11.setChecked(false);
        k12.setChecked(false);
        k13.setChecked(false);
        k14.setChecked(false);
        k15.setChecked(false);
        k16.setChecked(false);
        s1.setChecked(false);
        s2.setChecked(false);
        s3.setChecked(false);
        s4.setChecked(false);
        s5.setChecked(false);
        s6.setChecked(false);
        s7.setChecked(false);
        s8.setChecked(false);
        s9.setChecked(false);
        s10.setChecked(false);
        s11.setChecked(false);
        s12.setChecked(false);
        s13.setChecked(false);
        s14.setChecked(false);
        s15.setChecked(false);
        s16.setChecked(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
        getSupportActionBar().hide();

        land = (TextView) findViewById(R.id.textView8);

        ids = 1;
        currentpage = 1;

        snare = sp.load(this.getApplicationContext(), R.raw.snare, 1);
        crash = sp.load(this.getApplicationContext(), R.raw.crash, 1);
        closed = sp.load(this.getApplicationContext(), R.raw.closed, 1);
        hi = sp.load(this.getApplicationContext(), R.raw.hi, 1);
        lo = sp.load(this.getApplicationContext(), R.raw.lo, 1);
        kick = sp.load(this.getApplicationContext(), R.raw.newkick, 1);
        open = sp.load(this.getApplicationContext(), R.raw.open, 1);
        ride = sp.load(this.getApplicationContext(), R.raw.ride, 1);
        rbell = sp.load(this.getApplicationContext(), R.raw.rbell, 1);

        hh1 = (CheckBox) findViewById(R.id.hh1id);
        hh2 = (CheckBox) findViewById(R.id.hh2id);
        hh3 = (CheckBox) findViewById(R.id.hh3id);
        hh4 = (CheckBox) findViewById(R.id.hh4id);
        hh5 = (CheckBox) findViewById(R.id.hh5id);
        hh6 = (CheckBox) findViewById(R.id.hh6id);
        hh7 = (CheckBox) findViewById(R.id.hh7id);
        hh8 = (CheckBox) findViewById(R.id.hh8id);
        hh9 = (CheckBox) findViewById(R.id.hh9id);
        hh10 = (CheckBox) findViewById(R.id.hh10id);
        hh11 = (CheckBox) findViewById(R.id.hh11id);
        hh12 = (CheckBox) findViewById(R.id.hh12id);
        hh13 = (CheckBox) findViewById(R.id.hh13id);
        hh14 = (CheckBox) findViewById(R.id.hh14id);
        hh15 = (CheckBox) findViewById(R.id.hh15id);
        hh16 = (CheckBox) findViewById(R.id.hh16id);

        s1 = (CheckBox) findViewById(R.id.sid1);
        s2 = (CheckBox) findViewById(R.id.sid2);
        s3 = (CheckBox) findViewById(R.id.sid3);
        s4 = (CheckBox) findViewById(R.id.sid4);
        s5 = (CheckBox) findViewById(R.id.sid5);
        s6 = (CheckBox) findViewById(R.id.sid6);
        s7 = (CheckBox) findViewById(R.id.sid7);
        s8 = (CheckBox) findViewById(R.id.sid8);
        s9 = (CheckBox) findViewById(R.id.sid9);
        s10 = (CheckBox) findViewById(R.id.sid10);
        s11 = (CheckBox) findViewById(R.id.sid11);
        s12 = (CheckBox) findViewById(R.id.sid12);
        s13 = (CheckBox) findViewById(R.id.sid13);
        s14 = (CheckBox) findViewById(R.id.sid14);
        s15 = (CheckBox) findViewById(R.id.sid15);
        s16 = (CheckBox) findViewById(R.id.sid16);

        ht1 = (CheckBox) findViewById(R.id.hoid1);
        ht2 = (CheckBox) findViewById(R.id.hoid2);
        ht3 = (CheckBox) findViewById(R.id.hoid3);
        ht4 = (CheckBox) findViewById(R.id.hoid4);
        ht5 = (CheckBox) findViewById(R.id.hoid5);
        ht6 = (CheckBox) findViewById(R.id.hoid6);
        ht7 = (CheckBox) findViewById(R.id.hoid7);
        ht8 = (CheckBox) findViewById(R.id.hoid8);
        ht9 = (CheckBox) findViewById(R.id.hoid9);
        ht10 = (CheckBox) findViewById(R.id.hoid10);
        ht11 = (CheckBox) findViewById(R.id.hoid11);
        ht12 = (CheckBox) findViewById(R.id.hoid12);
        ht13 = (CheckBox) findViewById(R.id.hoid13);
        ht14 = (CheckBox) findViewById(R.id.hoid14);
        ht15 = (CheckBox) findViewById(R.id.hoid15);
        ht16 = (CheckBox) findViewById(R.id.hoid16);

        lt1 = (CheckBox) findViewById(R.id.lt1);
        lt2 = (CheckBox) findViewById(R.id.lt2);
        lt3 = (CheckBox) findViewById(R.id.lt3);
        lt4 = (CheckBox) findViewById(R.id.lt4);
        lt5 = (CheckBox) findViewById(R.id.lt5);
        lt6 = (CheckBox) findViewById(R.id.lt6);
        lt7 = (CheckBox) findViewById(R.id.lt7);
        lt8 = (CheckBox) findViewById(R.id.lt8);
        lt9 = (CheckBox) findViewById(R.id.lt9);
        lt10 = (CheckBox) findViewById(R.id.lt10);
        lt11 = (CheckBox) findViewById(R.id.lt11);
        lt12 = (CheckBox) findViewById(R.id.lt12);
        lt13 = (CheckBox) findViewById(R.id.lt13);
        lt14 = (CheckBox) findViewById(R.id.lt14);
        lt15 = (CheckBox) findViewById(R.id.lt15);
        lt16 = (CheckBox) findViewById(R.id.lt16);

        oh1 = (CheckBox) findViewById(R.id.ohh1);
        oh2 = (CheckBox) findViewById(R.id.ohh2);
        oh3 = (CheckBox) findViewById(R.id.ohh3);
        oh4 = (CheckBox) findViewById(R.id.ohh4);
        oh5 = (CheckBox) findViewById(R.id.ohh5);
        oh6 = (CheckBox) findViewById(R.id.ohh6);
        oh7 = (CheckBox) findViewById(R.id.ohh7);
        oh8 = (CheckBox) findViewById(R.id.ohh8);
        oh9 = (CheckBox) findViewById(R.id.ohh9);
        oh10 = (CheckBox) findViewById(R.id.ohh10);
        oh11 = (CheckBox) findViewById(R.id.ohh11);
        oh12 = (CheckBox) findViewById(R.id.ohh12);
        oh13 = (CheckBox) findViewById(R.id.ohh13);
        oh14 = (CheckBox) findViewById(R.id.ohh14);
        oh15 = (CheckBox) findViewById(R.id.ohh15);
        oh16 = (CheckBox) findViewById(R.id.ohh16);

        k1 = (CheckBox) findViewById(R.id.kid1);
        k2 = (CheckBox) findViewById(R.id.kid2);
        k3 = (CheckBox) findViewById(R.id.kid3);
        k4 = (CheckBox) findViewById(R.id.kid4);
        k5 = (CheckBox) findViewById(R.id.kid5);
        k6 = (CheckBox) findViewById(R.id.kid6);
        k7 = (CheckBox) findViewById(R.id.kid7);
        k8 = (CheckBox) findViewById(R.id.kid8);
        k9 = (CheckBox) findViewById(R.id.kid9);
        k10 = (CheckBox) findViewById(R.id.kid10);
        k11 = (CheckBox) findViewById(R.id.kid11);
        k12 = (CheckBox) findViewById(R.id.kid12);
        k13 = (CheckBox) findViewById(R.id.kid13);
        k14 = (CheckBox) findViewById(R.id.kid14);
        k15 = (CheckBox) findViewById(R.id.kid15);
        k16 = (CheckBox) findViewById(R.id.kid16);

        c1 = (CheckBox) findViewById(R.id.cid1);
        c2 = (CheckBox) findViewById(R.id.cid2);
        c3 = (CheckBox) findViewById(R.id.cid3);
        c4 = (CheckBox) findViewById(R.id.cid4);
        c5 = (CheckBox) findViewById(R.id.cid5);
        c6 = (CheckBox) findViewById(R.id.cid6);
        c7 = (CheckBox) findViewById(R.id.cid7);
        c8 = (CheckBox) findViewById(R.id.cid8);
        c9 = (CheckBox) findViewById(R.id.cid9);
        c10 = (CheckBox) findViewById(R.id.cid10);
        c11 = (CheckBox) findViewById(R.id.cid11);
        c12 = (CheckBox) findViewById(R.id.cid12);
        c13 = (CheckBox) findViewById(R.id.cid13);
        c14 = (CheckBox) findViewById(R.id.cid14);
        c15 = (CheckBox) findViewById(R.id.cid15);
        c16 = (CheckBox) findViewById(R.id.cid16);

        ri1 = (CheckBox) findViewById(R.id.rid1);
        ri2 = (CheckBox) findViewById(R.id.rid2);
        ri3 = (CheckBox) findViewById(R.id.rid3);
        ri4 = (CheckBox) findViewById(R.id.rid4);
        ri5 = (CheckBox) findViewById(R.id.rid5);
        ri6 = (CheckBox) findViewById(R.id.rid6);
        ri7 = (CheckBox) findViewById(R.id.rid7);
        ri8 = (CheckBox) findViewById(R.id.rid8);
        ri9 = (CheckBox) findViewById(R.id.rid9);
        ri10 = (CheckBox) findViewById(R.id.rid10);
        ri11 = (CheckBox) findViewById(R.id.rid11);
        ri12 = (CheckBox) findViewById(R.id.rid12);
        ri13 = (CheckBox) findViewById(R.id.rid13);
        ri14 = (CheckBox) findViewById(R.id.rid14);
        ri15 = (CheckBox) findViewById(R.id.rid15);
        ri16 = (CheckBox) findViewById(R.id.rid16);

        rb1 = (CheckBox) findViewById(R.id.bellid1);
        rb2 = (CheckBox) findViewById(R.id.bellid2);
        rb3 = (CheckBox) findViewById(R.id.bellid3);
        rb4 = (CheckBox) findViewById(R.id.bellid4);
        rb5 = (CheckBox) findViewById(R.id.bellid5);
        rb6 = (CheckBox) findViewById(R.id.bellid6);
        rb7 = (CheckBox) findViewById(R.id.bellid7);
        rb8 = (CheckBox) findViewById(R.id.bellid8);
        rb9 = (CheckBox) findViewById(R.id.bellid9);
        rb10 = (CheckBox) findViewById(R.id.bellid10);
        rb11 = (CheckBox) findViewById(R.id.bellid11);
        rb12 = (CheckBox) findViewById(R.id.bellid12);
        rb13 = (CheckBox) findViewById(R.id.bellid13);
        rb14 = (CheckBox) findViewById(R.id.bellid14);
        rb15 = (CheckBox) findViewById(R.id.bellid15);
        rb16 = (CheckBox) findViewById(R.id.bellid16);

        play = (Button) findViewById(R.id.playid);
        save = (Button) findViewById(R.id.saveid1);
        add = (Button) findViewById(R.id.addid);
        back = (Button) findViewById(R.id.backid);
        next = (Button) findViewById(R.id.nextid);
        middle = (Button) findViewById(R.id.middleid);
        dup = (Button) findViewById(R.id.dupid);
        setting = (Button) findViewById(R.id.settingid);
        record = (Button) findViewById(R.id.recordid);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
        saveList.add("01020304050607080910111213141516$s20");

        totalpages = 1;

        playing = false;
        saving = false;


        playhead = (ImageView) findViewById(R.id.imageView);
        playhead.setImageResource(R.drawable.playheadimg);

        barx = -10;
        speedbar = (SeekBar) findViewById(R.id.speedbarid);
        speed = 50;

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAll();
                //loadAll(template);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        dup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dup();
            }
        });
        speedbar.setKeyProgressIncrement(10);
        speedbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = (speedbar.getProgress() + 1);
                speed = (int) (Math.round(speed / 10) * 10);
                speedbar.setProgress(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setUI();
    }

    private void record() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }


}