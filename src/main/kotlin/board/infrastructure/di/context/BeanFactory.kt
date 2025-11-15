package board.infrastructure.di.context

import org.slf4j.LoggerFactory

object BeanFactory {

    private val scanner = ComponentScanner()
    private val registry = BeanRegistry()
    private val creator = BeanCreator(registry)
    private val logger = LoggerFactory.getLogger(BeanFactory::class.java)

    fun init(clazz: Class<*>) {
        logger.info("Initializing ApplicationContext")
        registry.clear()
        val scanResult = scanner.scan(clazz)
        registry.registerScanResult(scanResult)
        instantiateAllComponents()
        logger.info("ApplicationContext initialized. Total ${registry.getSingletonCount()} components")
    }

    fun <T : Any> getComponent(clazz: Class<T>): T {
        return creator.getOrCreate(clazz)
    }

    fun getComponentsWithAnnotation(annotation: Class<out Annotation>): List<Any> {
        val reflections = registry.getReflections()
            ?: throw IllegalStateException("Context not initialized. Call init() first")
        val componentClasses = reflections.getTypesAnnotatedWith(annotation, true)
            .filter { !it.isInterface && !it.isAnnotation }
        return componentClasses.map { getComponent(it) }
    }

    private fun instantiateAllComponents() {
        logger.info("Instantiating all components")
        registry.getAllComponents().forEach { clazz ->
            creator.getOrCreate(clazz)
        }
    }
}