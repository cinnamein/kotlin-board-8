package board.presentation

data class Route(
    val method: String,
    val path: String,
    val handler: (HttpRequest) -> HttpResponse,
)
