package com.example.ibscapstone

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ErrorLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: Exception) {
            Log.e("ApiServiceError", "Error during API call: ${chain.request().url}", e)
            throw e // Re-throw the exception to maintain original behavior
        }
    }
}