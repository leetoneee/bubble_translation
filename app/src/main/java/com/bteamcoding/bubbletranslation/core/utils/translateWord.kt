package com.bteamcoding.bubbletranslation.core.utils

import android.provider.Settings.Global.getString
import com.bteamcoding.bubbletranslation.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import com.bteamcoding.bubbletranslation.BuildConfig


// Hàm gọi API để dịch văn bản
fun callApiForTranslation(parsedText: String): String? {
    val apiUrl = "https://api.openai.com/v1/responses" // API endpoint
    var text: String = ""
    val apiKey = BuildConfig.API_KEY// Replace with your actual API key


    // Prepare JSON data
    val json = JSONObject().apply {
//        put("max_output_tokens", 2048)
        put("model", "gpt-4.1-nano")
//        put("temperature", 0.3)
//        put("type", "json_schema")
        put("input",
            "Translate the following English words into Vietnamese and return the result as JSON array format. This JSON string must include the following components for each word:\n" +
                    "\n" +
                    "english: The original English word.\n" +
                    "phonetic: The English phonetic transcription (IPA).\n" +
                    "part_of_speech: The part of speech (noun, verb, adjective, etc.).\n" +
                    "meanings: An array of objects, each object representing a different meaning of the word. Each meaning object must have:\n" +
                    "+ meaning: The English definition of that meaning.\n" +
                    "+ vietnamese: The corresponding Vietnamese meaning.\n" +
                    "+ example_sentence: An English example sentence using that meaning.\n" +
                    "+ vietnamese_translation: A Vietnamese translation of that example sentence.\n" +
                    "\n" +
                    "For each word, provide at least three meanings, along with the corresponding part of speech and an example sentence for each meaning. If a word has meanings belonging to different parts of speech, include all of them.\n" +
                    "\n" +
                    "The word to be translated is: $parsedText"
        ) // Use parsedText instead of hardcoded "hello"
    }

    try {
        val url: URL = URI.create(apiUrl).toURL()
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

        // Set request method to POST
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $apiKey")

        // Enable input and output streams
        connection.doOutput = true
        connection.doInput = true

        // Write JSON data to the request body
        val outputStream: OutputStream = connection.outputStream
        outputStream.write(json.toString().toByteArray())
        outputStream.flush()
        outputStream.close()

        // Get the response code
        val responseCode: Int = connection.responseCode
        println("Response Code: $responseCode")

        // If successful, read the response data
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            // Process JSON response
            val jsonResponse = JSONObject(response) // Ensure you're parsing the String response here
            text = jsonResponse
                .getJSONArray("output")
                .getJSONObject(0)
                .getJSONArray("content")
                .getJSONObject(0)
                .getString("text")
        } else {
            println("Error: Unable to fetch data from the API")
        }

        // Close the connection
        connection.disconnect()
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
    text = cleanJsonString(text)
    // val jsonText = JSONObject(text)
    return text
}

fun cleanJsonString(input: String): String {
    val trimmed = input
        .removePrefix("```json")
        .removeSuffix("```")
        .replace("\n", "")
        .replace("\r", "")
        .trim()

    return if (trimmed.trimStart().startsWith("[")) {
        JSONArray(trimmed).toString()
    } else {
        JSONObject(trimmed).toString()
    }
}

