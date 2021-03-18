package com.swjcook.vocabularyapplication.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swjcook.vocabularyapplication.BuildConfig
import com.swjcook.vocabularyapplication.network.entities.NetworkVocabularyList
import com.swjcook.vocabularyapplication.network.entities.NetworkWord
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

const val ENDPOINT_URL = BuildConfig.API_ENDPOINT_URL

interface VocabularyService {

    @GET("/all_lists.json")
    suspend fun getAllLists(): List<NetworkVocabularyList>

    @GET("/lists/{uuid}.json")
    suspend fun getListByUUID(@Path("uuid") uuid: String): List<NetworkWord>

}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    private val retrofit = Retrofit.Builder()
        .baseUrl(ENDPOINT_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val vocabularyService = retrofit.create(VocabularyService::class.java)
}