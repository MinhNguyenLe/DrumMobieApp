package com.example.drumapp;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioListFragment extends Fragment implements AudioListApdater.onItemListClick {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView audioList;

    //list of audio:
    private File[] allFiles;

    //list adapter to show audios
    private AudioListApdater audioListApdater;

    //media player to play audio
    private MediaPlayer mediaPlayer = null;
    private boolean isplaying =false;
    private boolean player_ended = false;

    private File fileToPlay;

    //UI Elements:
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;


    public AudioListFragment() {
        // Required empty public constructor
    }


    public static AudioListFragment newInstance(String param1, String param2) {
        AudioListFragment fragment = new AudioListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.audio_list_view);

        //path of all audio files
        String path = requireActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles=directory.listFiles(); // get list of audios

        audioListApdater= new AudioListApdater(allFiles, this);

        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListApdater);



        //UI Elements:
        playBtn=view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);
        playerSeekbar = view.findViewById(R.id.player_seekbar);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //button:
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isplaying)
                    pauseAudio();
                else
                {
                    if(fileToPlay != null)
                    {
                        if(!player_ended)
                            resumeAudio();
                        else
                            playAudio(fileToPlay);
                    }
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null)
                {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null)
                {
                    int progress=seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });

    }


    @Override
    public void onClickListener(File file, int position) {
        fileToPlay=file;
        if(isplaying)
        {
            stopAudio();
            playAudio(fileToPlay);
        }
        else
        {
            playAudio(fileToPlay);
            Log.e("PLAYLOG", file.getAbsolutePath());
        }
    }

    private void stopAudio() {
        isplaying=false;
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        playerHeader.setText("Stop");
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void pauseAudio(){
        isplaying=false;
        mediaPlayer.pause();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));

        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio(){
        mediaPlayer.start();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        isplaying=true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void playAudio(File fileToPlay) {
        player_ended=false;
        mediaPlayer= new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        isplaying=true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("Finished");
                player_ended=true;
            }
        });
        //manage seekbar :
        playerSeekbar.setMax(mediaPlayer.getDuration());
        seekbarHandler= new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 100);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isplaying)
            stopAudio();
    }
}