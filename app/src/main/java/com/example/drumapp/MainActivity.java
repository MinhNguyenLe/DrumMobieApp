package com.example.drumapp;//Package


//Libraries

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

//Main Class
public class MainActivity extends AppCompatActivity {
    //Declare Checkboxes
    Button[][] sounds;
    boolean[][] checkeds;

    //Declare Sounds
    SoundPool sp;
    int hihat;
    int kick;
    int snare;
    int piano;
    int bass;
    int bongo;
    int closed;
    int open;

    //Declare UI Elements

    ImageButton add;
    ImageButton back;
    ImageButton next;
    Button middle;
    ImageButton save;
    ImageButton dup;
    ImageButton play;
    ImageButton setting;
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

    int currentpage;
    int totalpages;
    int barx;
    int speed;
    int volume;
    int ids;

    final List<String> saveList = new ArrayList<String>();

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
                        sp.stop(open);
                        sp.play(closed, 1, 1, 0, 0, 1);
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
            //Log.i("[ONCLICK]", soundname);
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
        ids = 1;
        currentpage = 1;

        hihat = sp.load(this.getApplicationContext(), R.raw.hihat, 1);
        snare = sp.load(this.getApplicationContext(), R.raw.snare, 1);
        kick = sp.load(this.getApplicationContext(), R.raw.newkick, 1);
        piano = sp.load(this.getApplicationContext(), R.raw.cowbell, 1);
        bass = sp.load(this.getApplicationContext(), R.raw.bass, 1);
        bongo = sp.load(this.getApplicationContext(), R.raw.bongo, 1);
        closed = sp.load(this.getApplicationContext(), R.raw.closed, 1);
        open = sp.load(this.getApplicationContext(), R.raw.open, 1);


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
                Log.i("[ONSET]", tmpName);
                sounds[i][j] = (Button) findViewById(getResources().getIdentifier(tmpName, "id", getPackageName()));
                sounds[i][j].setOnClickListener(PadBtnOnClick);
                if((j + 1) % 4 == 0)
                    sounds[i][j].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_break, null));
                else
                    sounds[i][j].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_normal, null));
            }
        }


        play = (ImageButton) findViewById(R.id.playid);
        save = (ImageButton) findViewById(R.id.saveid1);
        add = (ImageButton) findViewById(R.id.addid);
        back = (ImageButton) findViewById(R.id.backid);
        next = (ImageButton) findViewById(R.id.nextid);
        middle = (Button) findViewById(R.id.middleid);
        dup = (ImageButton) findViewById(R.id.dupid);
        setting = (ImageButton) findViewById(R.id.settingid);
        saveList.add("01020304050607080910111213141516$s20");

        record = (Button) findViewById(R.id.recordid);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
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
/*        finalSave = "";
        for (int p = 1; p <= totalpages; p++) {
            currentpage = p;
            loadpage(currentpage);
            save();
            saveList.set(p - 1, saveString);
        }
        for (String page : saveList) {
            finalSave = finalSave + page + "||";
        }
        Intent intent = new Intent(MainActivity.this, nameScreen.class);
        intent.putExtra("content", finalSave);
        startActivity(intent);*/
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
        playing = false;
        play.setImageResource(R.drawable.ic_baseline_pause_32);
        isRendering = false;
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
            sp.stop(open);
            sp.play(closed, volumex, volumex, 0, 0, 1);
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

    private void record() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

}