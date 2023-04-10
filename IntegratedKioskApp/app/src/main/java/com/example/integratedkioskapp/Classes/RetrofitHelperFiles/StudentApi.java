package com.example.integratedkioskapp.Classes.RetrofitHelperFiles;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface StudentApi {
    @GET("kiosk/login")
    public Call<JsonElement> sendStudentID (@Query("id") String studentId, @Query("kiosk") String kioskNum);
}
