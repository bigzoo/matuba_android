package com.tatusafety.matuba.rest;

/**
 * Created by incentro on 4/7/2018.
 */


import com.tatusafety.matuba.model.Journey;
import com.tatusafety.matuba.model.JourneyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("api/journeys")
    Call<JourneyResponse> getJourneys(@Query("from_lat") String From_A,
                                      @Query("from_long") String To_A,
                                      @Query("to_lat") String From_B,
                                      @Query("to_long") String To_B);


}