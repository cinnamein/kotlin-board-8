package di.context

import di.stereotype.Bean
import di.stereotype.Component
import di.stereotype.Configuration
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

class ComponentScanner {

    private val logger: Logger = LoggerFactory.getLogger(ComponentScanner::class.java)

    /**
     * 지정된 패키지에서 컴포넌트를 스캔합니다.
     *
     * @param baseClass 스캔 기준 클래스
     * @return 스캔 결과
     */
    fun scan(baseClass: Class<*>): ScanResult {
        logger.info("Scanning package: ${baseClass.packageName}")

        val reflections = createReflections(baseClass)
        val componentClasses = scanComponents(reflections)
        val beanMethods = scanBeanMethods(reflections, componentClasses)
        logger.info("Found ${componentClasses.size} @Component and ${beanMethods.size} @Bean definitions")
        return ScanResult(
            beanMethods = beanMethods,
            componentClasses = componentClasses,
            reflections = reflections
        )
    }

    /**
     * Reflections 객체를 생성합니다.
     *
     * @param baseClass 스캔 기준 클래스
     * @return 생성된 Reflections 객체
     */
    private fun createReflections(baseClass: Class<*>): Reflections {
        val configBuilder = ConfigurationBuilder()
            .forPackages("board")
            .addScanners(Scanners.TypesAnnotated)
            .addClassLoaders(baseClass.classLoader)
        return Reflections(configBuilder)
    }

    /**
     * @Component 애노테이션이 붙은 클래스를 스캔합니다.
     *
     * @param reflections Reflections 객체
     * @return 스캔된 컴포넌트 클래스 집합
     */
    private fun scanComponents(reflections: Reflections): Set<Class<*>> {
        val components = mutableSetOf<Class<*>>()
        components.addAll(
            reflections.getTypesAnnotatedWith(Component::class.java, true)
                .filter { !it.isInterface && !it.isAnnotation }
        )

        val metaAnnotations = reflections.getTypesAnnotatedWith(Component::class.java, true)
            .filter { it.isAnnotation }
        for (metaAnnotation in metaAnnotations) {
            @Suppress("UNCHECKED_CAST")
            val annotationClass = metaAnnotation as Class<out Annotation>
            components.addAll(
                reflections.getTypesAnnotatedWith(metaAnnotation, true)
                    .filter { !it.isInterface && !it.isAnnotation }
            )
            logger.debug("Found meta-annotation: ${metaAnnotation.simpleName}")
        }
        return components
    }

    /**
     * @Bean 애노테이션이 붙은 메서드를 스캔합니다.
     *
     * @param reflections Reflections 객체
     * @param componentClasses 컴포넌트 클래스 집합
     * @return 빈 타입과 메서드의 맵
     */
    private fun scanBeanMethods(
        reflections: Reflections,
        componentClasses: Set<Class<*>>
    ): Map<Class<*>, Method> {
        val beanMethods = mutableMapOf<Class<*>, Method>()
        val configClasses = componentClasses.filter { it.isAnnotationPresent(Configuration::class.java) }

        for (configClass in configClasses) {
            configClass.declaredMethods
                .filter { it.isAnnotationPresent(Bean::class.java) }
                .forEach { method ->
                    val beanType = method.returnType
                    if (beanMethods.containsKey(beanType)) {
                        logger.warn("Duplicate @Bean definition for type: ${beanType.simpleName}")
                    }
                    beanMethods[beanType] = method
                }
        }
        return beanMethods
    }
}