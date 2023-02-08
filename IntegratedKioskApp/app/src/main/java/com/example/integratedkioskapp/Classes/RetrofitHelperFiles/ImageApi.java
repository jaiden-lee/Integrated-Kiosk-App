package com.example.integratedkioskapp.Classes.RetrofitHelperFiles;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageApi {
    @Multipart
    @POST("post_images/") // this is the part of the URL after the base URL
    public Call<ResponseBody> postImagesForFaceRec (@Part MultipartBody.Part[] images);

    @Multipart
    @POST("post_barcodes/")
    public Call<ResponseBody> postImagesForBarcode (@Part MultipartBody.Part[] images);
}
