package com.example.integratedkioskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.integratedkioskapp.Classes.Numpad;
import com.example.integratedkioskapp.Classes.Camera;
import com.example.integratedkioskapp.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public static String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        androidx.constraintlayout.widget.ConstraintLayout view = binding.getRoot();
        setContentView(view);

        currentId = "";
        TextView displayStudentId = binding.displayStudentId;
        displayStudentId.setText(currentId);

        
        Button camCover = binding.cover;

        camCover.setElevation(10);
        Log.d("COVER", "cover's elevation: " + camCover.getElevation());
        camCover.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //stuff
                camCover.setElevation(1);
                Log.d("COVER", "YAYYYYYY");
                Log.d("COVER", "cover's elevation: " + camCover.getElevation());
            }
        });

        Numpad.createClickListeners(binding);
        Camera camTest = new Camera(binding);
    }




}