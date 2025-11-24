package di.context

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class BeanCreator(
    private val registry: BeanRegistry
) {

    private val logger = LoggerFactory.getLogger(BeanCreator::class.java)

    /**
     * 빈을 조회하거나 생성합니다.
     *
     * @param clazz 조회할 빈 타입
     * @return 빈 인스턴스
     */
    fun <T : Any> getOrCreate(clazz: Class<T>): T {
        registry.getSingleton(clazz)?.let {
            @Suppress("UNCHECKED_CAST")
            return it as T
        }
        if (!registry.hasDefinition(clazz)) {
            throw RuntimeException("No bean definition found for type ${clazz.name}")
        }
        registry.markInCreation(clazz)

        try {
            logger.debug("Creating bean: [${clazz.simpleName}]")
            val instance = create(clazz)
            registry.saveSingleton(clazz, instance)
            logger.info("Created bean: [${clazz.simpleName}]")
            return instance
        } finally {
            registry.unmarkInCreation(clazz)
        }
    }

    /**
     * 빈을 생성합니다.
     *
     * @param clazz 생성할 빈 타입
     * @return 생성된 빈 인스턴스
     */
    private fun <T : Any> create(clazz: Class<T>): T {
        return when {
            registry.getBeanMethod(clazz) != null -> createFromBeanMethod(clazz)
            registry.isComponent(clazz) -> createFromConstructor(clazz)
            else -> throw IllegalStateException("No creation strategy for ${clazz.name}")
        }
    }

    /**
     * @Bean 메서드로부터 빈을 생성합니다.
     *
     * @param clazz 생성할 빈 타입
     * @return 생성된 빈 인스턴스
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> createFromBeanMethod(clazz: Class<T>): T {
        val method = registry.getBeanMethod(clazz)
            ?: throw IllegalStateException("No @Bean method for ${clazz.name}")
        logger.debug("Building [${clazz.simpleName}] from @Bean method [${method.name}]")
        val configInstance = getOrCreate(method.declaringClass)
        val dependencies = method.parameterTypes.map { paramType ->
            getOrCreate(paramType)
        }
        return method.invoke(configInstance, *dependencies.toTypedArray()) as T
    }

    /**
     * 생성자로부터 빈을 생성합니다.
     *
     * @param clazz 생성할 빈 타입
     * @return 생성된 빈 인스턴스
     */
    private fun <T : Any> createFromConstructor(clazz: Class<T>): T {
        val kClass: KClass<T> = clazz.kotlin
        val constructor: KFunction<T> = kClass.primaryConstructor
            ?: throw RuntimeException("No primary constructor for @Component ${clazz.name}")
        logger.debug("Building [${clazz.simpleName}] from constructor")

        val dependencies = constructor.parameters.map { param ->
            val paramType = param.type.jvmErasure.java
            getOrCreate(paramType)
        }
        return constructor.call(*dependencies.toTypedArray())
    }
}