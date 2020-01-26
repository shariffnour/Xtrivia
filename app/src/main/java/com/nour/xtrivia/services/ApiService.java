package com.nour.xtrivia.services;

import com.nour.xtrivia.Questions;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api.php")
    Call<Questions> getQuestions(@Query("amount") int amount,
                                 @Query("category") int category,
                                 @Query("type") String type);
}
