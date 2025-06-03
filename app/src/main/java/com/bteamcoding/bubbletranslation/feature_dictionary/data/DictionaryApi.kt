package com.bteamcoding.bubbletranslation.feature_dictionary.data

import retrofit2.http.GET
import retrofit2.http.Query

interface DictionaryApi {
    @GET("ajax/gets?from=eng&to=vie")
    suspend fun lookup(@Query("query") word: String): String
}

