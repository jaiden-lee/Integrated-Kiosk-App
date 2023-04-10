package com.example.integratedkioskapp.Classes.RetrofitHelperFiles;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ImageApi {
    @Multipart
    @POST("post_images/") // this is the part of the URL after the base URL
    public Call<String> postImagesForFaceRec (@Part MultipartBody.Part[] images);

    @Multipart
    @POST("post_barcodes/")
    public Call<ResponseBody> postImagesForBarcode (@Part MultipartBody.Part[] images);

    @POST("post_ID/")
    public Call<StudentID> postStudentID (@Body StudentIDBody studentId);
}
