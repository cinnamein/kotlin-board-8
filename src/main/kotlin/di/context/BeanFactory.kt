package di.context

import org.slf4j.LoggerFactory

object BeanFactory {

    private val scanner = ComponentScanner()
    private val registry = BeanRegistry()
    private val creator = BeanCreator(registry)
    private val logger = LoggerFactory.getLogger(BeanFactory::class.java)

    /**
     * ApplicationContext를 초기화합니다.
     *
     * @param clazz 스캔 기준 클래스
     */
    fun init(clazz: Class<*>) {
        logger.info("Initializing ApplicationContext")
        registry.clear()
        val scanResult = scanner.scan(clazz)
        registry.registerScanResult(scanResult)
        instantiateAllComponents()
        logger.info("ApplicationContext initialized. Total ${registry.getSingletonCount()} components")
    }

    /**
     * 컴포넌트를 조회합니다.
     *
     * @param clazz 조회할 컴포넌트 타입
     * @return 컴포넌트 인스턴스
     */
    fun <T : Any> getComponent(clazz: Class<T>): T {
        return creator.getOrCreate(clazz)
    }

    /**
     * 특정 애노테이션이 붙은 컴포넌트를 조회합니다.
     *
     * @param annotation 조회할 애노테이션 타입
     * @return 컴포넌트 인스턴스 리스트
     */
    fun getComponentsWithAnnotation(annotation: Class<out Annotation>): List<Any> {
        val reflections = registry.getReflections()
            ?: throw IllegalStateException("Context not initialized. Call init() first")
        val componentClasses = reflections.getTypesAnnotatedWith(annotation, true)
            .filter { !it.isInterface && !it.isAnnotation }
        return componentClasses.map { getComponent(it) }
    }

    /**
     * 모든 컴포넌트를 인스턴스화합니다.
     */
    private fun instantiateAllComponents() {
        logger.info("Instantiating all components")
        registry.getAllComponents().forEach { clazz ->
            creator.getOrCreate(clazz)
        }
    }
}