package web.http

import web.http.converter.JsonConverter
import kotlin.reflect.KClass

data class HttpRequest(
    val method: String,
    val path: String,
    val headers: Map<String, String>,
    val body: ByteArray,
    val pathVariables: Map<String, String> = emptyMap(),
    private val jsonConverter: JsonConverter
) {
    fun <T : Any> readBody(type: KClass<T>): T {
        return jsonConverter.deserialize(body, type)
    }

    fun getBodyAsString(): String {
        return body.toString(Charsets.UTF_8)
    }

    fun getHeader(name: String): String? {
        return headers[name]
    }

    fun getPathVariable(name: String): String? {
        return pathVariables[name]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HttpRequest
        return method == other.method &&
                path == other.path &&
                headers == other.headers &&
                body.contentEquals(other.body) &&
                pathVariables == other.pathVariables
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        result = 31 * result + pathVariables.hashCode()
        return result
    }
}

inline fun <reified T : Any> HttpRequest.readBody(): T {
    return this.readBody(T::class)
}