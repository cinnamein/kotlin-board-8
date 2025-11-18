package web.method

import org.slf4j.LoggerFactory
import web.http.HttpRequest
import web.http.HttpResponse

class HandlerMethodInvoker {

    private val logger = LoggerFactory.getLogger(HandlerMethodInvoker::class.java)

    fun invoke(handlerMethod: HandlerMethod, request: HttpRequest): HttpResponse {
        val parameterCount = handlerMethod.method.parameterCount
        try {
            if (parameterCount == 0) {
                logger.info("Invoking handler method without parameters: ${request::class.simpleName}")
                return handlerMethod.method.invoke(handlerMethod.bean) as HttpResponse
            }
            if (parameterCount == 1) {
                logger.info("Invoking handler method with single parameter: ${request::class.simpleName}")
                return handlerMethod.method.invoke(handlerMethod.bean, request) as HttpResponse
            }
            logger.info("Handler method has unsupported parameter count: $parameterCount. ")
            throw IllegalArgumentException("Handler method has unsupported parameter count: $parameterCount. ")
        } catch (e: Exception) {
            logger.error("Failed to invoke handler method: $handlerMethod", e)
            throw IllegalArgumentException("Handler method invocation failed")
        }
    }
}