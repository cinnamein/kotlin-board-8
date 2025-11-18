package di.context

import di.stereotype.Bean
import di.stereotype.Configuration
import web.http.HttpResponseBuilder
import web.http.converter.JsonConverter

@Configuration
class WebConfiguration {

    @Bean
    fun jsonConverter(): JsonConverter {
        return JsonConverter()
    }

    @Bean
    fun httpResponseBuilder(jsonConverter: JsonConverter): HttpResponseBuilder {
        return HttpResponseBuilder(jsonConverter)
    }
}