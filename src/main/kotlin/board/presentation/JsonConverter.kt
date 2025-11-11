package board.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KClass

class JsonConverter {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun serialize(data: Any): ByteArray {
        return objectMapper.writeValueAsBytes(data)
    }

    fun <T : Any> deserialize(jsonBytes: ByteArray, type: KClass<T>): T {
        return objectMapper.readValue(jsonBytes, type.java)
    }
}