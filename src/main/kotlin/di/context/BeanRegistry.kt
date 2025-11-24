package di.context

import org.reflections.Reflections
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

class BeanRegistry {

    private val singletons: MutableMap<Class<*>, Any> = ConcurrentHashMap()
    private val inCreation: MutableSet<Class<*>> = ConcurrentHashMap.newKeySet()
    private val beanMethodDefinitions: MutableMap<Class<*>, Method> = ConcurrentHashMap()
    private val componentDefinitions: MutableSet<Class<*>> = ConcurrentHashMap.newKeySet()
    private var reflections: Reflections? = null

    /**
     * 스캔 결과를 등록합니다.
     *
     * @param scanResult 등록할 스캔 결과
     */
    fun registerScanResult(scanResult: ScanResult) {
        beanMethodDefinitions.clear()
        componentDefinitions.clear()

        beanMethodDefinitions.putAll(scanResult.beanMethods)
        componentDefinitions.addAll(scanResult.componentClasses)
        reflections = scanResult.reflections
    }

    /**
     * 싱글톤 빈을 저장합니다.
     *
     * @param clazz 빈 타입
     * @param instance 빈 인스턴스
     */
    fun saveSingleton(clazz: Class<*>, instance: Any) {
        singletons[clazz] = instance
    }

    /**
     * 싱글톤 빈을 조회합니다.
     *
     * @param clazz 빈 타입
     * @return 빈 인스턴스 또는 null
     */
    fun getSingleton(clazz: Class<*>): Any? {
        return singletons[clazz]
    }

    /**
     * @Bean 메서드 정의를 조회합니다.
     *
     * @param clazz 빈 타입
     * @return @Bean 메서드 또는 null
     */
    fun getBeanMethod(clazz: Class<*>): Method? {
        return beanMethodDefinitions[clazz]
    }

    /**
     * @Component로 정의된 클래스인지 확인합니다.
     *
     * @param clazz 확인할 클래스
     * @return 컴포넌트이면 true, 아니면 false
     */
    fun isComponent(clazz: Class<*>): Boolean {
        return componentDefinitions.contains(clazz)
    }

    /**
     * 빈 정의가 존재하는지 확인합니다.
     *
     * @param clazz 확인할 클래스
     * @return 빈 정의가 있으면 true, 없으면 false
     */
    fun hasDefinition(clazz: Class<*>): Boolean {
        return componentDefinitions.contains(clazz) || beanMethodDefinitions.contains(clazz)
    }

    /**
     * 모든 컴포넌트 클래스를 조회합니다.
     *
     * @return 컴포넌트 클래스 집합
     */
    fun getAllComponents(): Set<Class<*>> {
        return componentDefinitions.toSet()
    }

    /**
     * 빈 생성 중임을 표시합니다.
     *
     * @param clazz 생성 중인 빈 타입
     */
    fun markInCreation(clazz: Class<*>) {
        if (!inCreation.add(clazz)) {
            throw RuntimeException("Circular dependency detected for class: ${clazz.name}")
        }
    }

    /**
     * 빈 생성 완료를 표시합니다.
     *
     * @param clazz 생성 완료된 빈 타입
     */
    fun unmarkInCreation(clazz: Class<*>) {
        inCreation.remove(clazz)
    }

    /**
     * Reflections 객체를 조회합니다.
     *
     * @return Reflections 객체 또는 null
     */
    fun getReflections(): Reflections? {
        return reflections
    }

    /**
     * 싱글톤 빈 개수를 조회합니다.
     *
     * @return 싱글톤 빈 개수
     */
    fun getSingletonCount(): Int {
        return singletons.size
    }

    /**
     * 모든 데이터를 초기화합니다.
     */
    fun clear() {
        singletons.clear()
        inCreation.clear()
        componentDefinitions.clear()
        beanMethodDefinitions.clear()
        reflections = null
    }
}