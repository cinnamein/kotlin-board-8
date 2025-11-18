package web.method

import java.lang.reflect.Method

data class HandlerMethod(
    val bean: Any,
    val method: Method,
    val httpMethod: String,
    val path: String,
) {
    val beanType: Class<*> = bean::class.java
    val methodName: String = method.name

    override fun toString(): String {
        return "$httpMethod $path -> ${beanType.simpleName}.$methodName()"
    }
}