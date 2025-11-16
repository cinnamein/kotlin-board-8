package web.router

import web.http.HttpRequest
import web.http.HttpResponse

data class Route(
    val method: String,
    val path: String,
    val handler: (HttpRequest) -> HttpResponse,
)
