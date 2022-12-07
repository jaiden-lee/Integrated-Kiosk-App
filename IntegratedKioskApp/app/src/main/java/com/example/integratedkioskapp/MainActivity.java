package com.example.integratedkioskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.integratedkioskapp.Classes.Numpad;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button[] numpadButtons = {findViewById(R.id.one), findViewById(R.id.two)};
        TextView idLabel = findViewById(R.id.display_student_id);

        Numpad.createClickListeners(numpadButtons, idLabel);
    }
}