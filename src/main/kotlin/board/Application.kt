package board

import di.context.BeanFactory
import web.server.WebServer

class Application

fun main() {
    BeanFactory.init(Application::class.java)
    WebServer().start()
}