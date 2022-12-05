package com.example.integratedkioskapp.Classes;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Numpad {
    public static Button button1;
    public static void createClickListeners (Activity context) {
        button1 = (Button)context.findViewById(R.id.one);
    }
}
