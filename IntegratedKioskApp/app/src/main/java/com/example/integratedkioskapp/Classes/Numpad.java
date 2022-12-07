package com.example.integratedkioskapp.Classes;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Numpad {
    public static String currentId = "";
    public static int butNum;
    public static void createClickListeners (Button[] buttons, TextView idLabel) {
        for (int buttonNum = 0; buttonNum<buttons.length; buttonNum++) {
            butNum = buttonNum+1;
            buttons[buttonNum].setOnClickListener (new View.OnClickListener() {
                int num = butNum;
                @Override
                public void onClick(View view) {
                    currentId+=num;
                    idLabel.setText(currentId);
                }
            });
        }
    }
}
