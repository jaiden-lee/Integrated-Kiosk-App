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
    public static int butNum;
    public static void createClickListeners (ActivityMainBinding binding) {

        Button buttons[] = {
                binding.zero,
                binding.one,
                binding.two,
                binding.three,
                binding.four,
                binding.five,
                binding.six,
                binding.seven,
                binding.eight,
                binding.nine
        };

        // NUM BUTTONS
        for (int buttonNum = 0; buttonNum<buttons.length; buttonNum++) {
            butNum = buttonNum;
            buttons[buttonNum].setOnClickListener(new View.OnClickListener() {
                int num = butNum;
                @Override
                public void onClick (View view) {
                    if (MainActivity.currentId.length() < 5){
                        MainActivity.currentId+=""+num;
                        MainActivity.displayStudentId.setText(MainActivity.currentId);
                    }
                }
            });
        }
        // DELETE BUTTON
        binding.delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick (View view) {
               if (MainActivity.currentId.length()>=1) {
                   MainActivity.currentId = MainActivity.currentId.substring(0, MainActivity.currentId.length()-1);
                   MainActivity.displayStudentId.setText(MainActivity.currentId);
               }
               if (MainActivity.currentId.length()==0) {
                   MainActivity.displayStudentId.setText("CLICK FOR FACE OR ENTER ID");
               }
           }
        });
        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                MainActivity.cameraEnabled = false;
                if (MainActivity.currentId.length()>=1 && MainActivity.currentId.length()<=5 && MainActivity.checkStringIsNumber(MainActivity.currentId)) {
//                    ServerCommunication.postStudentID(MainActivity.currentId);
                    ServerCommunication.uploadStudentID(MainActivity.currentId);
                    MainActivity.currentId = "";
//                    MainActivity.displayStudentId.setText("CLICK FOR FACE OR ENTER ID");
                    // re-covers the camera
                }
                MainActivity.camCover.setVisibility(View.VISIBLE);
                MainActivity.disableCameraButton.setVisibility(View.INVISIBLE);


            }
        });
    }
}
