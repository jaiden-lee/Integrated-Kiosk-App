package com.example.integratedkioskapp.Classes;

import com.example.integratedkioskapp.Classes.RetrofitHelperFiles.ImageApi;
import com.example.integratedkioskapp.Classes.RetrofitHelperFiles.StudentApi;
import com.example.integratedkioskapp.Classes.RetrofitHelperFiles.StudentID;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

public class ServerCommunication {
    public static final String ai_base_url = "http://192.168.86.59:8000/";
    public static final String id_base_url = "http://10.56.9.186:8000/";
    // kiosk/login
    // this URL is temporary, change this to the actual server link later

    public static void uploadImageFilesForBarCode (ArrayList<String> barcodeFilePaths) {
        MultipartBody.Part[] barcodes = new MultipartBody.Part[barcodeFilePaths.size()];
        for (int index = 0; index<barcodeFilePaths.size(); index++) {
            File imageFile = new File(barcodeFilePaths.get(index));
            if (imageFile.exists()) {
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                        "kiosk1-" + String.valueOf((System.currentTimeMillis() / 1000)) +
                                "-" + index, imageFile.getName(), RequestBody.create(MediaType.parse("image/*"),
                                imageFile));
                barcodes[index] = imagePart;
            }
        }

        // CREATING RETROFIT CLASSES
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ai_base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageApi imageApi =retrofit.create(ImageApi.class);
        Call<ResponseBody> call = imageApi.postImagesForBarcode(barcodes);

        // SENDING AN ASYNC REQUEST
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    //INPUT: Accepts an array of the image file paths
    //OUTPUT: None
    public static void uploadImageFilesForFaceRec (ArrayList<String> imageFilePaths) {
        // PREPARING IMAGES TO BE SENT
        MultipartBody.Part[] images = new MultipartBody.Part[imageFilePaths.size()];
        for (int index = 0; index<imageFilePaths.size(); index++) {
            File imageFile = new File(imageFilePaths.get(index));
            if (imageFile.exists()) {
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                        "kiosk1-" + String.valueOf((System.currentTimeMillis() / 1000)) +
                                "-" + index, imageFile.getName(), RequestBody.create(MediaType.parse("image/*"),
                                imageFile));
                images[index] = imagePart;
            }
        }

        // CREATING RETROFIT CLASSES
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ai_base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageApi imageApi =retrofit.create(ImageApi.class);
        Call<ResponseBody> call = imageApi.postImagesForFaceRec(images);

        // SENDING AN ASYNC REQUEST
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public static void uploadStudentID (String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(id_base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StudentApi studentApi = retrofit.create(StudentApi.class);
        Call<StudentID> call = studentApi.sendStudentID(id, "1");
        call.enqueue(new Callback<StudentID>() {
            @Override
            public void onResponse(Call<StudentID> call, Response<StudentID> response) {

            }

            @Override
            public void onFailure(Call<StudentID> call, Throwable t) {

            }
        });
    }
}
