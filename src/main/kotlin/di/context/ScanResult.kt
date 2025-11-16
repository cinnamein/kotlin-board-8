package di.context

import org.reflections.Reflections
import java.lang.reflect.Method

data class ScanResult(
    val beanMethods: Map<Class<*>, Method>,
    val componentClasses: Set<Class<*>>,
    val reflections: Reflections
)
