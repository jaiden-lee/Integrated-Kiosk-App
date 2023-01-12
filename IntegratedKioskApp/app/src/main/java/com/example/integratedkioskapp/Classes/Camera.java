package com.example.integratedkioskapp.Classes;


import static androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED;

import com.example.integratedkioskapp.MainActivity;
import com.example.integratedkioskapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.interfaces.Detector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.impl.utils.ContextUtil;
import androidx.camera.mlkit.vision.MlKitAnalyzer;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.core.Preview;
import androidx.camera.core.CameraSelector;
import android.util.Log;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.LifecycleOwner;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Camera {
//for the camera to start, you must implement 2/3 of the camerax uses cases: Preview and ImageCapture
    private androidx.camera.core.Camera cam;
    private PreviewView previewView;
    private Context context;
    private ImageCapture imageCapture;
    private ArrayList<File> imageFiles;
    private File imageFile;
    public boolean isBinded = false;
    private ImageAnalysis imageAnalysis;

    private Executor captureExecutor;
    private Executor analysisExecutor;

    public Camera(ActivityMainBinding binding){
        previewView = binding.previewView;

        context = previewView.getContext();

        imageFiles = new ArrayList<File>();

        captureExecutor = Executors.newSingleThreadExecutor();
        analysisExecutor = Executors.newSingleThreadExecutor();

        if (cameraPermissionGranted()){
            Log.d("CAMERA", "Permission Granted");
            
            startCamera();
        }
        else{
            Toast.makeText(context, "Camera permissions not granted by user.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean cameraPermissionGranted(){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera(){
        // Request a CameraProvider
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(context);
        // verify that the CameraProvider initialized
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindUseCases(cameraProvider);
                takePicture();
            } catch (ExecutionException | InterruptedException | FileNotFoundException e) {
//                bindUseCases(cameraProvider);
                // should never be reached
            }
        }, ContextCompat.getMainExecutor(context));
        Log.d("CAMERAXTHING", "Camera Started");
    }
    
    @SuppressLint("UnsafeOptInUsageError")
    private void bindUseCases(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .setTargetRotation(context.getDisplay().getRotation())
                //.setTargetRotation(Surface.ROTATION_180)
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
//could be an issue with rotation int value

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //capture use case
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_90)
                .build();
        //analysis use case
        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setTargetRotation(Surface.ROTATION_180)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        //find out the type of barcode on our id cards to optimize
        BarcodeScannerOptions barcodeOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39)
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(barcodeOptions);

//        @SuppressLint("UnsafeOptInUsageError") MlKitAnalyzer analyzer = new MlKitAnalyzer(List.of(scanner), COORDINATE_SYSTEM_VIEW_REFERENCED,
//                analysisExecutor, new ImageAnalysis.Analyzer imageProxy = processImageProxy(scanner, imageProxy);
//            // The value of result.getResult(barcodeScanner) can be used directly for drawing UI overlay.
//            // Need to test this on actual android device
////            @ExperimentalGetImage
//             });
        Log.d("CAMERAXTHING", "FIRST");
//        imageAnalysis.setAnalyzer(analysisExecutor, new MlKitAnalyzer(List.of(scanner), COORDINATE_SYSTEM_VIEW_REFERENCED,
//                analysisExecutor, (result) -> {
//            Log.d("CAMERAXTHING", "TEST");
//           processImageProxy(scanner, (ImageProxy)result.getValue(scanner)); }));

        imageAnalysis.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
           @Override
           public void analyze(@NonNull ImageProxy imageProxy) {
               processImageProxy(scanner, imageProxy);
           }
        });
        Log.d("CAMERAXTHING", "AFTER");
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cam = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageCapture, imageAnalysis);
    }

    @ExperimentalGetImage
    public void processImageProxy (BarcodeScanner scanner, ImageProxy imageProxy) {
        if (imageProxy==null) return;
        Image image = imageProxy.getImage();

        if (image == null) return;

        InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());
        scanner.process(inputImage).addOnSuccessListener(
            new OnSuccessListener<List<Barcode>>() {
                 @Override
                 public void onSuccess(List<Barcode> barcodes) {
                     if (barcodes.size()>0) {
                         Log.d("CAMERAXTHING", "ID: "+ barcodes.get(0).getRawValue());
                     }
                 }
             }
        ).addOnFailureListener(
        new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CAMERAXTHING", "nlg u kinda suck");
            }
        }
        ).addOnCompleteListener(
                new OnCompleteListener<List<Barcode>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Barcode>> task) {
                        imageProxy.close();
                    }
                }
        );




    }
    public File takePicture() throws FileNotFoundException {
        Log.d("CAMERAXTHING", "PICTURE TAKEN");
        String fileName = Calendar.getInstance().getTime().toString().replaceAll(":", "-");
        //idea: what if it's Downloads not Download
        //- Maddy
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + fileName + ".jpeg";
        Log.d("CAMERAXTHING", filePath);
        imageFile = new File (filePath);
//
//        FileOutputStream outputStream = new FileOutputStream(filePath);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(imageFile).build();
        imageCapture.takePicture(outputFileOptions, captureExecutor,
                new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults){
                //we did it!
                Log.d("CAMERAXTHING", "IMAGE SAVED: "+ filePath);
            }
            public void onError(ImageCaptureException error){
                //oh no!
                Log.d("CAMERAXTHING", "ERROR: "+error.toString());
            }
        });
        imageFiles.add(imageFile);
        return imageFile;
    }

    public ArrayList<File> getImageFiles(){
        return imageFiles;
    }


}

