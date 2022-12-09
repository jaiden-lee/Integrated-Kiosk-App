package com.example.integratedkioskapp.Classes.RetrofitHelperFiles;

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
    public Call<StudentID> sendStudentID (@Query("id") String studentId, @Query("kiosk") String kioskNum);
}
