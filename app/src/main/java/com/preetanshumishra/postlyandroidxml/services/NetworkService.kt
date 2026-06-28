package com.preetanshumishra.postlyandroidxml.services

import com.preetanshumishra.postlyandroidxml.models.LoginResponse
import com.preetanshumishra.postlyandroidxml.models.PostModel
import com.preetanshumishra.postlyandroidxml.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

enum class APIError {
    invalidEndpoint,
    invalidResponse,
    requestFailed,
    decodeFailure
}

class NetworkException(
    val apiError: APIError,
    val statusCode: Int? = null
) : Exception(
    when (apiError) {
        APIError.invalidEndpoint -> "The app could not reach the requested API endpoint."
        APIError.invalidResponse -> "The server returned an invalid response."
        APIError.requestFailed -> "The request failed with status code ${statusCode ?: "unknown"}."
        APIError.decodeFailure -> "The app could not read the server response."
    }
)

enum class APIEndpoint(private val path: String) {
    login("login"),
    users("users"),
    posts("posts");

    fun getUrl(): URL? {
        val baseUrl =
            "http://192.168.2.56:3005/"
        return runCatching { URL("$baseUrl$path") }.getOrNull()
    }
}

interface NetworkService {
    suspend fun fetchUserToken(username: String, password: String): String
    suspend fun fetchPosts(token: String): List<PostModel>
    suspend fun fetchUsers(token: String): List<UserModel>
}

class NetworkServiceImplementation : NetworkService {

    private val json = Json {
        this.ignoreUnknownKeys = true
        this.coerceInputValues = true
    }

    private suspend inline fun <reified T> request(url: URL, headers: Map<String, String> = emptyMap()): T = withContext(Dispatchers.IO) {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            headers.forEach { (key, value) ->
                setRequestProperty(key, value)
            }
        }

        try {
            val statusCode = connection.responseCode

            if (statusCode !in 200..299) {
                throw NetworkException(APIError.requestFailed, statusCode)
            }

            val body = connection.inputStream.bufferedReader().use(BufferedReader::readText)

            try {
                json.decodeFromString<T>(body)
            } catch (_: SerializationException) {
                throw NetworkException(APIError.decodeFailure)
            }
        } catch (e: NetworkException) {
            throw e
        } catch (_: Exception) {
            throw NetworkException(APIError.invalidResponse)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildBasicAuthHeader(username: String, password: String): String {
        val credentials = "$username:$password"
        val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
        return "Basic $encoded"
    }

    override suspend fun fetchUserToken(username: String, password: String): String {
        val url = APIEndpoint.login.getUrl()
            ?: throw NetworkException(APIError.invalidEndpoint)

        val response: LoginResponse = request(
            url = url,
            headers = mapOf(
                "Authorization" to buildBasicAuthHeader(username, password)
            )
        )

        return response.apiKey
    }

    override suspend fun fetchPosts(token: String): List<PostModel> {
        val url = APIEndpoint.posts.getUrl()
            ?: throw NetworkException(APIError.invalidEndpoint)

        return request(
            url = url,
            headers = mapOf("x-access-token" to token)
        )
    }

    override suspend fun fetchUsers(token: String): List<UserModel> {
        val url = APIEndpoint.users.getUrl()
            ?: throw NetworkException(APIError.invalidEndpoint)

        return request(
            url = url,
            headers = mapOf("x-access-token" to token)
        )
    }
}
