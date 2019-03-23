package com.nour.xtrivia.services;

import com.nour.xtrivia.Questions;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api.php?amount=1&type=multiple")
    Call<Questions> getQuestions();
}
