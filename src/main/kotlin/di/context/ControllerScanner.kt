package di.context

import di.stereotype.Controller
import org.slf4j.LoggerFactory
import web.method.HandlerMethod
import web.method.HandlerMethodInvoker
import web.method.annotation.*
import web.router.Router

class ControllerScanner {

    private val logger = LoggerFactory.getLogger(ControllerScanner::class.java)
    private val invoker = HandlerMethodInvoker()

    /**
     * 컨트롤러를 스캔하고 라우터에 등록합니다.
     *
     * @param router 라우트를 등록할 Router 객체
     */
    fun scanAndRegister(router: Router) {
        val controllers = BeanFactory.getComponentsWithAnnotation(Controller::class.java)
        logger.info("Found ${controllers.size} controller(s)")

        val handlerMethods = scanAllHandlerMethods(controllers)
        registerAllHandlerMethods(router, handlerMethods)

        logger.info("Total ${handlerMethods.size} route(s) registered")
    }

    /**
     * 모든 컨트롤러에서 핸들러 메서드를 스캔합니다.
     *
     * @param controllers 스캔할 컨트롤러 리스트
     * @return 스캔된 핸들러 메서드 리스트
     */
    private fun scanAllHandlerMethods(controllers: List<Any>): List<HandlerMethod> {
        return controllers.flatMap { controller ->
            scanHandlerMethods(controller)
        }
    }

    /**
     * 단일 컨트롤러에서 핸들러 메서드를 스캔합니다.
     *
     * @param controller 스캔할 컨트롤러 객체
     * @return 스캔된 핸들러 메서드 리스트
     */
    private fun scanHandlerMethods(controller: Any): List<HandlerMethod> {
        val handlerMethods = mutableListOf<HandlerMethod>()
        val controllerClass = controller::class.java
        for (method in controllerClass.declaredMethods) {
            method.getAnnotation(GetMapping::class.java)?.let { mapping ->
                handlerMethods.add(HandlerMethod(controller, method, "GET", mapping.path))
            }

            method.getAnnotation(PostMapping::class.java)?.let { mapping ->
                handlerMethods.add(HandlerMethod(controller, method, "POST", mapping.path))
            }

            method.getAnnotation(PutMapping::class.java)?.let { mapping ->
                handlerMethods.add(HandlerMethod(controller, method, "PUT", mapping.path))
            }

            method.getAnnotation(DeleteMapping::class.java)?.let { mapping ->
                handlerMethods.add(HandlerMethod(controller, method, "DELETE", mapping.path))
            }
        }
        return handlerMethods
    }

    /**
     * 모든 핸들러 메서드를 라우터에 등록합니다.
     *
     * @param router 라우트를 등록할 Router 객체
     * @param handlerMethods 등록할 핸들러 메서드 리스트
     */
    private fun registerAllHandlerMethods(router: Router, handlerMethods: List<HandlerMethod>) {
        handlerMethods.forEach { handlerMethod ->
            registerHandlerMethod(router, handlerMethod)
            logger.info("Mapped: $handlerMethod")
        }
    }

    /**
     * 핸들러 메서드를 라우터에 등록합니다.
     *
     * @param router 라우트를 등록할 Router 객체
     * @param handlerMethod 등록할 핸들러 메서드
     */
    private fun registerHandlerMethod(router: Router, handlerMethod: HandlerMethod) {
        when (handlerMethod.httpMethod) {
            "GET" -> router.get(handlerMethod.path) { request ->
                invoker.invoke(handlerMethod, request)
            }

            "POST" -> router.post(handlerMethod.path) { request ->
                invoker.invoke(handlerMethod, request)
            }

            "PUT" -> router.put(handlerMethod.path) { request ->
                invoker.invoke(handlerMethod, request)
            }

            "DELETE" -> router.delete(handlerMethod.path) { request ->
                invoker.invoke(handlerMethod, request)
            }
        }
    }
}