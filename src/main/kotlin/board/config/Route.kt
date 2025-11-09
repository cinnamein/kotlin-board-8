package board.config

import com.sun.net.httpserver.HttpExchange

data class Route(
    val method: String,
    val path: String,
    val handler: (HttpExchange) -> Unit,
)
