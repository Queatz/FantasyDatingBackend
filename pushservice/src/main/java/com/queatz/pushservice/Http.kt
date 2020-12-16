package com.queatz.pushservice

import com.queatz.on.On
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.nio.charset.Charset


class Http constructor(private val on: On) {

    companion object {
        private const val FCM_KEY = "AAAAMB2tK6o:APA91bFlmDFWc8YDOxhvoRUmfvFTnHWQUzPIkvJDjiCB8ZBm2jA7ObdKVEo_vL91ZacLIWJ4KQdqHN01V7XZ834fd3KDEYV_QjwS9ICUl3lIqLX7k9fqYTgjGwswCkGceWDxpW9kLNOz"
    }

    private val client = HttpClient(CIO) {
        install(DefaultRequest) {
            headers.append(HttpHeaders.Authorization, "key=$FCM_KEY")
        }
    }

    suspend fun post(
        url: String,
        payload: String,
        callback: (Response) -> Unit
    ) {
        client.post<HttpResponse>(url) {
            this.body = TextContent(
                payload, ContentType.Application.Json.withCharset(
                    Charset.forName(
                        "UTF-8"
                    )
                )
            )
        }.let {
            callback(Response(it.status, it.readText(Charset.forName("UTF-8"))))
        }
    }
}

data class Response(
    val status: HttpStatusCode,
    val content: String
)
