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

    fun scanAndRegister(router: Router) {
        val controllers = BeanFactory.getComponentsWithAnnotation(Controller::class.java)
        logger.info("Found ${controllers.size} controller(s)")

        val handlerMethods = controllers.flatMap { controller ->
            scanHandlerMethods(controller)
        }

        handlerMethods.forEach { handlerMethod ->
            registerHandlerMethod(router, handlerMethod)
            logger.info("Mapped: $handlerMethod")
        }
        logger.info("Total ${handlerMethods.size} route(s) registered")
    }

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