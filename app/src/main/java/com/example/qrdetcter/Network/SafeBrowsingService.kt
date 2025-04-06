package com.example.qrdetcter.Network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Define API request and response models
data class ThreatEntry(val url: String)

data class ThreatInfo(
    val threatTypes: List<String> = listOf("MALWARE", "SOCIAL_ENGINEERING"),
    val platformTypes: List<String> = listOf("ANY_PLATFORM"),
    val threatEntryTypes: List<String> = listOf("URL"),
    val threatEntries: List<ThreatEntry>
)

data class SafeBrowsingRequest(val client: Map<String, String>, val threatInfo: ThreatInfo)

data class SafeBrowsingResponse(val matches: List<Map<String, Any>>?)

interface SafeBrowsingService {
    @POST("v4/threatMatches:find?key=AIzaSyAT9tlqw8v_QyT6r0zs7uixD-u-tWDlvUc")
    suspend fun checkUrl(@Body request: SafeBrowsingRequest): SafeBrowsingResponse
}

// Retrofit instance
object SafeBrowsingApi {
    private const val BASE_URL = "https://safebrowsing.googleapis.com/"

    val service: SafeBrowsingService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SafeBrowsingService::class.java)
    }
}
