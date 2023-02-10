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
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.interfaces.Detector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.FaceDetector;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
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
import android.widget.TextView;
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
    private ImageAnalysis imageAnalysisBarcode;
    private ImageAnalysis imageAnalysisFace;
    private TextView displayText;

    private Executor captureExecutor;
    private Executor analysisExecutor;
    private Executor faceExecutor;

    private Calendar curdate = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();

    private Calendar flushcurdate = Calendar.getInstance();
    private Calendar flushcalendar = Calendar.getInstance();

    public Camera(ActivityMainBinding binding){
        previewView = binding.previewView;
        displayText = binding.displayStudentId;

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

        //barcode use case
        imageAnalysisBarcode = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280 , 720))
                .setTargetRotation(Surface.ROTATION_180)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build();
        //find out the type of barcode on our id cards to optimize
        BarcodeScannerOptions barcodeOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39
                )
                .build();
        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(barcodeOptions);
        imageAnalysisBarcode.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
           @Override
           public void analyze(@NonNull ImageProxy imageProxy) {
               processImageProxy(barcodeScanner, imageProxy);
           }
        });

        //face analysis use case
        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.15f)
                .enableTracking()
                .build();
        FaceDetector faceDetector = (FaceDetector) FaceDetection.getClient(faceDetectorOptions);
        imageAnalysisFace = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280 , 720))
                .setTargetRotation(Surface.ROTATION_180)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build();
        imageAnalysisFace.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
           @Override
           public void analyze(@NonNull ImageProxy imageProxy) {
                processFaceDetection(faceDetector, imageProxy);
           }
        });

        Log.d("CAMERAXTHING", "AFTER");
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //CREATING THE CAMERA AND BINDING USE CASES TO ITS LIFE CYCLE
        cam = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageCapture, imageAnalysisBarcode, imageAnalysisFace);
    }

    @ExperimentalGetImage
    public void processFaceDetection (FaceDetector faceDetector, ImageProxy imageProxy) {
        if (imageProxy==null) return;
        Image image = imageProxy.getImage();
        if (image==null) return;

        InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());
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

//                     try{
//                         Thread.sleep(1000);
//                     }
//                     catch(InterruptedException e){
//                     }

                     flushcalendar = Calendar.getInstance();
                     Calendar flushcompare = (Calendar)flushcurdate.clone();
                     flushcompare.add(Calendar.MILLISECOND, 10000);
                     if(flushcalendar.compareTo(flushcompare) > 0){
                         try {
                             Flush();
                             Log.d("CAMERAXTHING", "Flushed");
                         } catch (FileNotFoundException e) {
                             e.printStackTrace();
                         }
                         flushcurdate = Calendar.getInstance();
                     }

                     if (barcodes.size()>0) {
                         calendar = Calendar.getInstance();
                         Calendar compare = (Calendar)curdate.clone();
                         compare.add(Calendar.MILLISECOND, 1000);
                         if (calendar.compareTo(compare) > 0) {
                             Log.d("CAMERAXTHING", "CALENDAR CUFF");
                             curdate = Calendar.getInstance();
                            if(barcodes.get(0).getRawValue().length()==5) {
                                ///where the barcode text is set
                                displayText.setText(barcodes.get(0).getRawValue());
                            }
                             Log.d("CAMERAXTHING", "SIZE: " + barcodes.size() + " ID: " + barcodes.get(0).getDriverLicense());


                             try {
                                 takePicture();
                             } catch (FileNotFoundException e) {
                                 e.printStackTrace();
                             }
                         }
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

    public void Flush() throws FileNotFoundException {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/");
        for(File file: dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
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

