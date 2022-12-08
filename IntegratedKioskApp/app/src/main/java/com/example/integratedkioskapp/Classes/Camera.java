package com.example.integratedkioskapp.Classes;
import android.view.SurfaceHolder;


import com.google.mlkit.vision.camera.CameraSourceConfig;
import com.google.mlkit.vision.camera.CameraXSource;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.common.Barcode;


public class Camera {

    private static CameraXSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static BarcodeScanner barcodeScanner;

    public static void buildCameraAndDetector(){

        barcodeScanner = new BarcodeScanning.getClient(new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraXSource(new CameraSourceConfig).Builder(com.example.integratedkioskapp.Classes, )
                .setRequestedPreviewSize(1920, 1080)
                //1 for facing front and 0 for facing back
                .setFacing(0)
                .setAutoFocusEnabled(true)
                //.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE) read the documentation about this
                .build();



    }



}
