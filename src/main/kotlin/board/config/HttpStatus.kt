package board.config

enum class HttpStatus(
    val statusCode: Int,
    val message: String,
) {
    // 200 Success
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    // 400 Client Error
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    // 500 Server Error
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
}