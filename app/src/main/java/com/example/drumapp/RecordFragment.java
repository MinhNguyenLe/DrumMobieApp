package com.example.drumapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaDataSource;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    //flag check to record
    private boolean isRecording=false;
    private String recordpermissions = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    //file path to save
    private String recordFile;

    private MediaRecorder mediaRecorder;

    private Chronometer timer ;
    //text for file name after recorded
    private TextView filenametext;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        listBtn=view.findViewById(R.id.record_list_btn);
        timer=view.findViewById(R.id.record_timer);
        filenametext=view.findViewById(R.id.record_filename);

        listBtn.setOnClickListener(this);
        recordBtn=view.findViewById(R.id.record_btn);
        recordBtn.setOnClickListener(this);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.record_list_btn:
                if(isRecording)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording=false;
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", null);
                    alertDialog.setTitle("Audio is still recording");
                    alertDialog.setMessage("You sure want to stop recording ?");
                    alertDialog.create().show();
                }
                else
                {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;
            case R.id.record_btn:
                if(isRecording)
                {
                    //stop recording
                    stopRecording();
                    isRecording=false;
                }
                else
                {
                    //start recording
                    if(checkPermissions())
                    {
                        startRecording();
                        isRecording=true;
                    }
                }
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startRecording() {
        //this for reset time clock
        timer.setBase(SystemClock.elapsedRealtime());
        //start count time
        timer.start();
        //set up record :
        //here set name include date time with it:
        SimpleDateFormat formatter= new SimpleDateFormat("dd_MM_YYYY_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        recordFile="BanThuAm_" + formatter.format(now) + ".3gp";
        //set file path
        String recordPath = requireActivity().getExternalFilesDir("/").getAbsolutePath();
        mediaRecorder= new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        Log.e("Output", recordPath);

        //change imgae of button
        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.off_record_btn_icon, null));
        //change the text "Press to record -> name of the file"
        filenametext.setText("Recording File :" + recordFile);
        // actually record:
        try {
            mediaRecorder.prepare();
            Log.e("test prepare","prepare");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        Log.e("test start","start");
    }


    private void stopRecording() {
        //stop count time
        timer.stop();
        mediaRecorder.stop();
        Log.e("test stop","stop");
        mediaRecorder.release();
        Log.e("test release","release");
        mediaRecorder=null; //if user click record again mediarecorder init again and start over
        //change button imgae back to red
        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_3, null));
        //change the text to "file saved""
        filenametext.setText("Recorded, File Saved :" + recordFile);
    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(getContext(), recordpermissions) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordpermissions}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording)
        stopRecording();
    }
}