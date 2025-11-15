package board.infrastructure.di.context

import org.reflections.Reflections
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

class BeanRegistry {

    private val singletons: MutableMap<Class<*>, Any> = ConcurrentHashMap()
    private val inCreation: MutableSet<Class<*>> = ConcurrentHashMap.newKeySet()
    private val beanMethodDefinitions: MutableMap<Class<*>, Method> = ConcurrentHashMap()
    private val componentDefinitions: MutableSet<Class<*>> = ConcurrentHashMap.newKeySet()
    private var reflections: Reflections? = null

    fun registerScanResult(scanResult: ScanResult) {
        beanMethodDefinitions.clear()
        componentDefinitions.clear()

        beanMethodDefinitions.putAll(scanResult.beanMethods)
        componentDefinitions.addAll(scanResult.componentClasses)
        reflections = scanResult.reflections
    }

    fun saveSingleton(clazz: Class<*>, instance: Any) {
        singletons[clazz] = instance
    }

    fun getSingleton(clazz: Class<*>): Any? {
        return singletons[clazz]
    }

    fun getBeanMethod(clazz: Class<*>): Method? {
        return beanMethodDefinitions[clazz]
    }

    fun isComponent(clazz: Class<*>): Boolean {
        return componentDefinitions.contains(clazz)
    }

    fun hasDefinition(clazz: Class<*>): Boolean {
        return componentDefinitions.contains(clazz) || beanMethodDefinitions.contains(clazz)
    }

    fun getAllComponents(): Set<Class<*>> {
        return componentDefinitions.toSet()
    }

    fun markInCreation(clazz: Class<*>) {
        if (!inCreation.add(clazz)) {
            throw RuntimeException("Circular dependency detected for class: ${clazz.name}")
        }
    }

    fun unmarkInCreation(clazz: Class<*>) {
        inCreation.remove(clazz)
    }

    fun getReflections(): Reflections? {
        return reflections
    }

    fun getSingletonCount(): Int {
        return singletons.size
    }

    fun clear() {
        singletons.clear()
        inCreation.clear()
        componentDefinitions.clear()
        beanMethodDefinitions.clear()
        reflections = null
    }
}