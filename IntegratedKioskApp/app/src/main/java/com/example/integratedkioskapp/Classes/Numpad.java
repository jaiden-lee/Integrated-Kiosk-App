package com.example.integratedkioskapp.Classes;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.*;

import com.example.integratedkioskapp.MainActivity;
import com.example.integratedkioskapp.databinding.ActivityMainBinding;

public class Numpad {
    public static String currentId = "";
    public static int butNum;
    public static void createClickListeners (ActivityMainBinding binding) {

        Button button1 = binding.one;
        button1.setOnClickListener (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentId+="1";
                    Log.d("NUMPAD", "onClick: "+currentId);
                }
            });


//        for (int buttonNum = 0; buttonNum<buttons.length; buttonNum++) {
//            butNum = buttonNum+1;
//            buttons[buttonNum].setOnClickListener (new View.OnClickListener() {
//                int num = butNum;
//                @Override
//                public void onClick(View view) {
//                    currentId+=num;
//                    idLabel.setText(currentId);
//                }
//            });
//        }
    }
}
