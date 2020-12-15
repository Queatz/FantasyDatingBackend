package com.queatz.pushservice

import com.queatz.on.On
import io.ktor.client.HttpClient
import io.ktor.client.features.DefaultRequest
import io.ktor.client.request.post
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import java.nio.charset.Charset


class Http constructor(private val on: On) {

    companion object {
        private const val FCM_KEY = "AAAAMB2tK6o:APA91bFlmDFWc8YDOxhvoRUmfvFTnHWQUzPIkvJDjiCB8ZBm2jA7ObdKVEo_vL91ZacLIWJ4KQdqHN01V7XZ834fd3KDEYV_QjwS9ICUl3lIqLX7k9fqYTgjGwswCkGceWDxpW9kLNOz"
    }

    private val client = HttpClient {
        install(DefaultRequest) {
            headers.append(HttpHeaders.Authorization,  "key=$FCM_KEY")
        }
    }

    suspend fun post(
        url: String,
        payload: String,
        callback: (Response) -> Unit
    ) {
        client.post<HttpResponse>(url) {
            this.body = TextContent(payload, ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
        }.let {
            callback(Response(it.status, it.readText(Charset.forName("UTF-8"))))
        }
    }
}

data class Response(
    val status: HttpStatusCode,
    val content: String
)
