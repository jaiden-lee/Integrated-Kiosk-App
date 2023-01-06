package com.example.integratedkioskapp.Classes;


import com.example.integratedkioskapp.MainActivity;
import com.example.integratedkioskapp.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.impl.utils.ContextUtil;
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

public class Camera{
//for the camera to start, you must implement 2/3 of the camerax uses cases: Preview and ImageCapture
    private androidx.camera.core.Camera cam;
    private PreviewView previewView;
    private Context context;
    private ImageCapture imageCapture;
    private ArrayList<File> imageFiles;
    private File imageFile;
    private ImageAnalysis imageAnalysis;

    private Executor executor;

    public Camera(ActivityMainBinding binding){
        previewView = binding.previewView;

        context = previewView.getContext();
        imageFiles = new ArrayList<File>();
        executor = Executors.newSingleThreadExecutor();

        if (cameraPermissionGranted()){
            Log.d("CAMERA", "Permission Granted");
            //capture use case
            imageCapture = new ImageCapture.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .build();
            //analysis use case
            imageAnalysis = new ImageAnalysis.Builder()
                    .setTargetResolution(new Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

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
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                //This should never be reached because no errors need to be handled for this Future
            }
        }, ContextCompat.getMainExecutor(context));
    }
    
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().setTargetRotation(context.getDisplay().getRotation()).build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();


        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cam = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, imageCapture, preview);
    }

    // talk to veer about how to return an image so we can send into the server

    private File takePicture() throws FileNotFoundException {

        String fileName = Calendar.getInstance().getTime().toString().replaceAll(":", ".");
        String filePath = Environment.getExternalStorageState() + "/Dowload/" + fileName + ".jgp";
        imageFile = new File (filePath);
//
//        FileOutputStream outputStream = new FileOutputStream(filePath);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(imageFile).build();
        imageCapture.takePicture(outputFileOptions, executor,
                new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults){
                //we did it!
            }
            public void onError(ImageCaptureException error){
                //oh no!
            }
        });
        imageFiles.add(imageFile);
        return imageFile;
    }

    public ArrayList<File> getImageFiles(){
        return imageFiles;
    }


}

