package com.example.drumapp;//Package


//Libraries

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


//Main Class
public class MainActivity extends AppCompatActivity {
    private static final String SAVE_FILE_NAME = "listbeats.txt";
    //Declare Checkboxes
    Button[][] sounds;
    boolean[][] checkeds;
    public static MainActivity _instance;
    //Declare Sounds
    SoundPool sp;
    int hihat;
    int kick;
    int snare;
    int piano;
    int bass;
    int bongo;

    //Declare UI Elements

    ImageButton add;
    ImageButton back;
    ImageButton next;
    ImageButton delete;
    ImageButton clear;
    Button load;
    Button middle;
    ImageButton save;
    ImageButton dup;
    ImageButton play;
    Button setting;
    Button home;
    ImageView playhead;
    SeekBar speedbar;
    SeekBar volumebar;

    //Declare Status Booleans

    Boolean playing;
    Boolean saving;
    public static boolean isRendering = false;

    //Declare Other Status Variables

    String saveString = "";
    String finalSave = "";
    String fileName = "";
    String listBeats = "";

    int currentpage;
    int totalpages;
    int barx;
    int speed;
    int volume;
    int ids;

    List<String> saveList = new ArrayList<String>();

    final int defaultx = 35;
    final int speedmult = 10;

    RenderThread render = new RenderThread();
    private Button record;
    //On Pad Button Click
    View.OnClickListener PadBtnOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String resourceName = view.getResources().getResourceName(view.getId());
            String soundname = String.valueOf(resourceName.charAt(23)) + String.valueOf(resourceName.charAt(24));
            int soundIndex;
            if(resourceName.length() == 29)
                soundIndex = Integer.parseInt((String.valueOf(resourceName.charAt(25)) + String.valueOf(resourceName.charAt(26)))) -1;
            else
                soundIndex = Integer.parseInt(String.valueOf(resourceName.charAt(25))) - 1;
            int i = 0;
            switch (soundname)
            {
                case "ki": i = 1; break;
                case "sn": i = 2; break;
                case "pi": i = 3; break;
                case "ba": i = 4; break;
                case "bo": i = 5; break;
            }
            if(checkeds[i][soundIndex] == true)
            {
                checkeds[i][soundIndex] = false;
                if((soundIndex + 1) % 4 == 0)
                    sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_break, null));
                else
                    sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_normal, null));
            }
            else
            {
                checkeds[i][soundIndex] = true;
                switch (soundname)
                {
                    case "hh":
                        sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_hh, null));
                        sp.play(hihat, 1, 1, 0, 0, 1);
                        break;
                    case "ki":
                        sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_ki, null));
                        sp.play(kick, 1, 1, 0, 0, 1);
                        break;
                    case "sn":
                        sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_sn, null));
                        sp.play(snare, 1, 1, 0, 0, 1);
                        break;
                    case "pi":
                        sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_pi, null));
                        sp.play(piano, 1, 1, 0, 0, 1);
                        break;
                    case "ba":
                        sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_ba, null));
                        sp.play(bass, 1, 1, 0, 0, 1);
                        break;
                    case "bo":
                        sounds[i][soundIndex].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_bo, null));
                        sp.play(bongo, 1, 1, 0, 0, 1);
                        break;
                }
            }
        }
    };

    //Define Control Bar Functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .build();
        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 1);
        }
        _instance = this;
        ids = 1;
        currentpage = 1;

        hihat = sp.load(this.getApplicationContext(), R.raw.hihat, 1);
        snare = sp.load(this.getApplicationContext(), R.raw.snare, 1);
        kick = sp.load(this.getApplicationContext(), R.raw.newkick, 1);
        piano = sp.load(this.getApplicationContext(), R.raw.cowbell, 1);
        bass = sp.load(this.getApplicationContext(), R.raw.bass, 1);
        bongo = sp.load(this.getApplicationContext(), R.raw.bongo, 1);


        sounds = new Button[6][16];
        checkeds = new boolean[6][16];

        for(int i = 0; i < 6; i ++)
        {
            String name = null;
            switch (i)
            {
                case 0: name = "hh"; break;
                case 1: name = "ki"; break;
                case 2: name = "sn"; break;
                case 3: name = "pi"; break;
                case 4: name = "ba"; break;
                case 5: name = "bo"; break;
            }
            for(int j = 0; j < 16; j++)
            {
                String tmpName = name;
                tmpName += (j + 1) + "id";
                sounds[i][j] = (Button) findViewById(getResources().getIdentifier(tmpName, "id", getPackageName()));
                sounds[i][j].setOnClickListener(PadBtnOnClick);
                if((j + 1) % 4 == 0)
                    sounds[i][j].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_break, null));
                else
                    sounds[i][j].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_normal, null));
            }
        }


        play = (ImageButton) findViewById(R.id.playid);
        save = (ImageButton) findViewById(R.id.saveid);
        delete = (ImageButton) findViewById(R.id.deleteid);
        clear = (ImageButton) findViewById(R.id.clearid);
        add = (ImageButton) findViewById(R.id.addid);
        back = (ImageButton) findViewById(R.id.backid);
        next = (ImageButton) findViewById(R.id.nextid);
        middle = (Button) findViewById(R.id.middleid);
        dup = (ImageButton) findViewById(R.id.dupid);
        setting = (Button) findViewById(R.id.settingid);
        load = (Button) findViewById(R.id.loadid);
        saveList.add("01020304050607080910111213141516$s20");

        record = (Button) findViewById(R.id.recordid);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
        home = (Button) findViewById(R.id.homeid);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });

        totalpages = 1;
        playing = false;
        saving = false;


        playhead = (ImageView) findViewById(R.id.imageView);
        playhead.setImageResource(R.drawable.playheading);

        barx = -10;
        speedbar = (SeekBar) findViewById(R.id.speedbarid);
        speed = 50;

        volumebar = (SeekBar) findViewById(R.id.volumebarid);
        volume = 50;

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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
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
        volumebar.setKeyProgressIncrement(10);
        volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = (volumebar.getProgress());
                volume = (int)Math.round(volume * 10) / 10;
                volumebar.setProgress(volume);
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

    public void setSounds(String soundname, int level)
    {
        switch (soundname)
        {
            case "hh":
                if(level == 1)
                    hihat = sp.load(this.getApplicationContext(), R.raw.hihat, 1);
                if(level == 2)
                    hihat = sp.load(this.getApplicationContext(), R.raw.hihat2, 1);
                if(level == 3)
                    hihat = sp.load(this.getApplicationContext(), R.raw.hithat3, 1);
                break;
            case "ki":
                if(level == 1)
                    kick = sp.load(this.getApplicationContext(), R.raw.newkick, 1);
                if(level == 2)
                    kick = sp.load(this.getApplicationContext(), R.raw.kick2, 1);
                if(level == 3)
                    kick = sp.load(this.getApplicationContext(), R.raw.kick3, 1);
                break;
            case "sn":
                if(level == 1)
                    snare = sp.load(this.getApplicationContext(), R.raw.snare, 1);
                if(level == 2)
                    snare = sp.load(this.getApplicationContext(), R.raw.snare2, 1);
                if(level == 3)
                    snare = sp.load(this.getApplicationContext(), R.raw.snare3, 1);
                break;
            case "pi":
                if(level == 1)
                    piano = sp.load(this.getApplicationContext(), R.raw.cowbell, 1);
                if(level == 2)
                    piano = sp.load(this.getApplicationContext(), R.raw.cowbell2, 1);
                if(level == 3)
                    piano = sp.load(this.getApplicationContext(), R.raw.cowbell3, 1);
                break;
            case "ba":
                if(level == 1)
                    bass = sp.load(this.getApplicationContext(), R.raw.bass, 1);
                if(level == 2)
                    bass = sp.load(this.getApplicationContext(), R.raw.bass2wav, 1);
                if(level == 3)
                    bass = sp.load(this.getApplicationContext(), R.raw.bass3, 1);
                break;
            case "bo":
                if(level == 1)
                    bongo = sp.load(this.getApplicationContext(), R.raw.bongo, 1);
                if(level == 2)
                    bongo = sp.load(this.getApplicationContext(), R.raw.bongo2, 1);
                if(level == 3)
                    bongo = sp.load(this.getApplicationContext(), R.raw.bongo3, 1);
                break;
        }
    }
    private void load() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String[] filebeats = loaddata(SAVE_FILE_NAME).split("-");
        alert.setTitle("Choose The Beat");
        // Set an EditText view to get user input
        final EditText editText = new EditText(this);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(100,0,100,0);
        for(int i = 0; i <filebeats.length; i++)
        {
            if(filebeats[i].length() > 4)
            {
                final TextView listbeat = new TextView(this);
                listbeat.setText(i + 1 + ".\t" + filebeats[i].substring(0,filebeats[i].length()-4));
                layout.addView(listbeat);
            }
        }

        layout.addView(editText);
        alert.setView(layout);

        alert.setPositiveButton("Load", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String namefile = editText.getText() + ".txt";
                LoadBeat(namefile);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    private void clear() {
        clearAll();
        saveList = new ArrayList<String>();
        currentpage = 1;
        totalpages = 1;
        ids = 1;
        playing = false;
        saving = false;
        saveString = "";
        setUI();
        speed = (speedbar.getProgress() + 1);
        speed = (int) (Math.round(speed / 10) * 10);
        saveList.add(0,saveString);
    }

    private void delete() {
        if(saveList.size() == 1 && currentpage == 1)
            clearAll();
        else
        {
            saveList.remove(currentpage-1);
            totalpages --;
            if(currentpage > 1)
                currentpage --;
            speed = (speedbar.getProgress() + 1);
            speed = (int) (Math.round(speed / 10) * 10);
            setUI();
            loadpage(currentpage);
        }
    }

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
        if (speed <= 9)
            saveList.add("01020304050607080910111213141516$s0" + speed);
        else if (speed == 100)
            saveList.add("01020304050607080910111213141516$s" + speed);
        else
            saveList.add("01020304050607080910111213141516$s90");
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
                case "hh":
                    checkeds[0][currentColumn-1] = true ;
                    sounds[0][currentColumn-1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_hh, null));
                    break;
                case "ki":
                    checkeds[1][currentColumn-1] = true ;
                    sounds[1][currentColumn-1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_ki, null));
                    break;
                case "sn":
                    checkeds[2][currentColumn-1] = true ;
                    sounds[2][currentColumn-1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_sn, null));
                    break;
                case "pi":
                    checkeds[3][currentColumn-1] = true ;
                    sounds[3][currentColumn-1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_pi, null));
                    break;
                case "ba":
                    checkeds[4][currentColumn-1] = true ;
                    sounds[4][currentColumn-1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_ba, null));
                    break;
                case "bo":
                    checkeds[5][currentColumn-1] = true ;
                    sounds[5][currentColumn-1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_choosen_bo, null));
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

    public void soundSave(boolean hh, boolean ki, boolean s, boolean pi, boolean ba, boolean bo) {

        if (hh)
            saveString = saveString + "hh";
        if (s)
            saveString = saveString + "sn";
        if (ki)
            saveString = saveString + "ki";
        if (pi)
            saveString = saveString + "pi";
        if (ba)
            saveString = saveString + "ba";
        if (bo)
            saveString = saveString + "bo";
    }

    public void saveAction() {
        while (saving) {
            if (ids <= 9) {
                saveString = saveString + "0" + String.valueOf(ids);
            } else {
                saveString = saveString + String.valueOf(ids);
            }
            soundSave(checkeds[0][ids - 1], checkeds[1][ids - 1],checkeds[2][ids - 1],checkeds[3][ids - 1],checkeds[4][ids - 1],checkeds[5][ids - 1]);
            if (ids < 16) {
                ids++;
            } else if (ids == 16) {
                saving = false;
            }
        }
    }

    public void saveAll() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Insert Name For The Beat");
        alert.setMessage("Message");

        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                fileName = input.getText().toString();
                finalSave = "";
                for (int p = 1; p <= totalpages; p++) {
                    currentpage = p;
                    loadpage(currentpage);
                    save();
                    saveList.set(p - 1, saveString);
                }
                for (String page : saveList) {
                    finalSave = finalSave + page + "-";
                }
                fileName+=".txt";
                savedata(fileName, finalSave);
                listBeats += fileName + "-";
                savedata(SAVE_FILE_NAME,listBeats);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // Canceled.
            }
        });
        alert.show();

      //  writeToFile(finalSave);
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

    public void play() {

        speed = (speedbar.getProgress() + 1) * speedmult;
        speed = (int) (Math.round(speed / 10) * 10);
        if (!playing) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playhead.setX(defaultx);
                    playhead.setVisibility(View.VISIBLE);
                }
            });

            ids = 1;
            playing = true;
            play.setImageResource(R.drawable.ic_baseline_pause_32);

            if (render.getStatus().equals(AsyncTask.Status.RUNNING)) {
                render.cancel(true);
            }

            render = new RenderThread();
            render.execute();
            isRendering = true;

        } else {
            playing = false;
            render.cancel(true);
            play.setImageResource(R.drawable.ic_baseline_play_arrow_32);
            isRendering = false;
        }
    }

    public void soundCheck(boolean hh, boolean ki, boolean s, boolean pi, boolean ba, boolean bo)
    {
        float volumex = (float)volume/100;
        Log.i("[ONCLICK]", (float)volumex + "");
        if (hh) {
            sp.play(hihat, volumex, volumex, 0, 0, 1);
            saveString = saveString + "hh";
        }
        if (ki) {
            sp.play(kick, volumex, volumex,  0, 0, 1);
            saveString = saveString + "kb";
        }
        if (s) {
            sp.play(snare, volumex, volumex,  0, 0, 1);
            saveString = saveString + "sn";
        }
        if (pi) {
            sp.play(piano,  volumex, volumex,  0, 0, 1);
            saveString = saveString + "ht";
        }
        if (ba) {
            sp.play(bass,  volumex, volumex,  0, 0, 1);
            saveString = saveString + "ht";
        }
        if (bo) {
            sp.play(bongo,  volumex, volumex,  0, 0, 1);
            saveString = saveString + "rb";
        }
        if (ids != 16) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playhead.setVisibility(View.VISIBLE);
                    int playdiff = sounds[0][0].getWidth() + 5;
                    playhead.setX(playhead.getX() + playdiff);
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

                if (ids <= 9)
                    saveString = saveString + "0" + String.valueOf(ids);
                else
                    saveString = saveString + String.valueOf(ids);
                soundCheck(checkeds[0][ids-1],checkeds[1][ids-1],checkeds[2][ids-1],checkeds[3][ids-1],checkeds[4][ids-1],checkeds[5][ids-1]);
                if (ids < 16)
                    ids++;
                else
                {
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
                playhead.setX(defaultx);
                speedbar.setProgress(speed);
                playhead.setVisibility(View.INVISIBLE);
                middle.setText(String.valueOf(currentpage));
            }
        });
    }

    public void clearAll() {
        for(int i = 0; i < 6; i ++)
            for(int j = 0; j < 16; j ++)
            {
                checkeds[i][j] = false;
                if((j + 1) % 4 == 0)
                    sounds[i][j].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_break, null));
                else
                    sounds[i][j].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_normal, null));
            }
    }
    // save data
    public void savedata(String name, String save) {
        String text = save;
        FileOutputStream fos = null;
        try
        {
            fos = openFileOutput(name, MODE_PRIVATE);
            fos.write(text.getBytes());
            if(name != SAVE_FILE_NAME)
                Toast.makeText(this, "Saved to " + getFilesDir() + "/" + name.substring(0,name.length()-4), Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public String loaddata(String name) {
        FileInputStream fis = null;
        try
        {
            fis = openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null)
                sb.append(text).append("\n");
            return sb.toString();
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        finally {
            if (fis != null)
            {
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
    public void LoadBeat (String name)
    {
        String[] loadbeat = loaddata(name).split("-");
        saveList = new ArrayList<String>();
        for(int i = 0; i< loadbeat.length;i++)
            if(loadbeat[i].length() >= 10)
                saveList.add(loadbeat[i]);
        totalpages = saveList.size();
        currentpage = 1;
        clearAll();
        setUI();
        speed = (speedbar.getProgress() + 1);
        speed = (int) (Math.round(speed / 10) * 10);
        speedbar.setProgress(speed);
        speed = (speedbar.getProgress() + 1) * speedmult;
        loadpage(currentpage);
    }
    private void record() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
    private void home() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void setting(){
        playing = false;
        play.setImageResource(R.drawable.ic_baseline_play_arrow_32);
        isRendering = false;
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}