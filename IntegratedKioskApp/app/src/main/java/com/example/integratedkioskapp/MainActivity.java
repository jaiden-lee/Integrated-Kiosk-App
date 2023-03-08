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

        Numpad.createClickListeners(binding);
        Camera camTest = new Camera(binding);
    }




}