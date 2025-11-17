package web.router

import org.slf4j.LoggerFactory

class PathMatcher {
    private val logger = LoggerFactory.getLogger(PathMatcher::class.java)

    fun match(pattern: String, path: String): Map<String, String>? {
        val patternSegments = pattern.split("/").filter { it.isNotEmpty() }
        val pathSegments = path.split("/").filter { it.isNotEmpty() }
        val pathVariables = mutableMapOf<String, String>()

        if (patternSegments.size != pathSegments.size) {
            return null
        }

        for (i in patternSegments.indices) {
            val patternSegment = patternSegments[i]
            val pathSegment = pathSegments[i]

            when {
                isPathVariable(patternSegment) -> {
                    val variableName = extractVariableName(patternSegment)
                    pathVariables[variableName] = pathSegment
                    logger.debug("Path variable: {} = {}", variableName, pathSegment)
                }
                patternSegment == pathSegment -> {
                    logger.debug("Exact match: {}", patternSegment)
                }
                else -> {
                    logger.debug("Mismatch: {} != {}", patternSegment, pathSegment)
                    return null
                }
            }
        }

        logger.debug("Match success: {}", pathVariables)
        return pathVariables
    }

    private fun isPathVariable(segment: String): Boolean {
        return segment.startsWith("{") && segment.endsWith("}")
    }

    private fun extractVariableName(segment: String): String {
        return segment.removeSurrounding("{", "}")
    }

    fun getSpecificityScore(pattern: String): Int {
        return pattern.split("/")
            .filter { it.isNotEmpty() }
            .count { !isPathVariable(it) }
    }
}