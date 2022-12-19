package com.example.integratedkioskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.integratedkioskapp.Classes.Numpad;
import com.example.integratedkioskapp.Classes.Camera;
import com.example.integratedkioskapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        androidx.constraintlayout.widget.ConstraintLayout view = binding.getRoot();
        setContentView(view);



//        Button[] numpadButtons = {
//                findViewById(R.id.zero),
//                findViewById(R.id.one),
//                findViewById(R.id.two),
//                findViewById(R.id.three),
//                findViewById(R.id.four),
//                findViewById(R.id.five),
//                findViewById(R.id.six),
//                findViewById(R.id.seven),
//                findViewById(R.id.eight),
//                findViewById(R.id.nine)
//        };
//        TextView idLabel = findViewById(R.id.display_student_id);

        Numpad.createClickListeners(binding);
    }
}