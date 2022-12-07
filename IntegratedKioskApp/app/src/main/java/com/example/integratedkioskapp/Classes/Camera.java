package com.example.integratedkioskapp.Classes;
import android.view.SurfaceHolder;

import com.google.mlkit.vision.camera.CameraSourceConfig;
import com.google.mlkit.vision.camera.CameraXSource;

public class Camera {

    private static CameraXSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    public static void buildCamera(){

        cameraSource = new CameraXSource(new CameraSourceConfig.Builder())
                .setRequestedPreviewSize(1920, 1080)
                //1 for facing front and 0 for facing back
                .setFacing(0)
                .setAutoFocusEnabled(true)
                //.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE) read the documentation about this
                .build();

    }


}
