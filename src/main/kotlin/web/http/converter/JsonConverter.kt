package web.http.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KClass

class JsonConverter {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    /**
     * 객체를 JSON 바이트 배열로 직렬화합니다.
     *
     * @param data 직렬화할 객체
     * @return JSON 바이트 배열
     */
    fun serialize(data: Any): ByteArray {
        return objectMapper.writeValueAsBytes(data)
    }

    /**
     * JSON 바이트 배열을 객체로 역직렬화합니다.
     *
     * @param jsonBytes JSON 바이트 배열
     * @param type 역직렬화할 타입
     * @return 역직렬화된 객체
     */
    fun <T : Any> deserialize(jsonBytes: ByteArray, type: KClass<T>): T {
        return objectMapper.readValue(jsonBytes, type.java)
    }
}