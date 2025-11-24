package web.method

import org.slf4j.LoggerFactory
import web.http.HttpRequest
import web.http.HttpResponse

class HandlerMethodInvoker {

    private val logger = LoggerFactory.getLogger(HandlerMethodInvoker::class.java)

    /**
     * 핸들러 메서드를 실행합니다.
     *
     * @param handlerMethod 실행할 핸들러 메서드
     * @param request HTTP 요청 객체
     * @return HTTP 응답 객체
     */
    fun invoke(handlerMethod: HandlerMethod, request: HttpRequest): HttpResponse {
        val parameterCount = handlerMethod.method.parameterCount

        return try {
            when (parameterCount) {
                0 -> invokeWithoutParameters(handlerMethod, request)
                1 -> invokeWithSingleParameter(handlerMethod, request)
                else -> throw IllegalArgumentException("Handler method has unsupported parameter count: $parameterCount")
            }
        } catch (e: Exception) {
            logger.error("Failed to invoke handler method: $handlerMethod", e)
            throw IllegalArgumentException("Handler method invocation failed")
        }
    }

    /**
     * 파라미터 없이 핸들러 메서드를 실행합니다.
     *
     * @param handlerMethod 실행할 핸들러 메서드
     * @param request HTTP 요청 객체
     * @return HTTP 응답 객체
     */
    private fun invokeWithoutParameters(handlerMethod: HandlerMethod, request: HttpRequest): HttpResponse {
        logger.info("Invoking handler method without parameters: ${request::class.simpleName}")
        return handlerMethod.method.invoke(handlerMethod.bean) as HttpResponse
    }

    /**
     * 단일 파라미터로 핸들러 메서드를 실행합니다.
     *
     * @param handlerMethod 실행할 핸들러 메서드
     * @param request HTTP 요청 객체
     * @return HTTP 응답 객체
     */
    private fun invokeWithSingleParameter(handlerMethod: HandlerMethod, request: HttpRequest): HttpResponse {
        logger.info("Invoking handler method with single parameter: ${request::class.simpleName}")
        return handlerMethod.method.invoke(handlerMethod.bean, request) as HttpResponse
    }
}