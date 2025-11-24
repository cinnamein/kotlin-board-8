package web.router

import org.slf4j.LoggerFactory

class PathMatcher {
    private val logger = LoggerFactory.getLogger(PathMatcher::class.java)

    /**
     * 패턴과 경로를 매칭하고 경로 변수를 추출합니다.
     *
     * @param pattern 매칭할 패턴
     * @param path 실제 요청 경로
     * @return 매칭 성공 시 경로 변수 map, 실패 시 null
     */
    fun match(pattern: String, path: String): Map<String, String>? {
        val patternSegments = splitPath(pattern)
        val pathSegments = splitPath(path)

        if (!isSameLengthSegments(patternSegments, pathSegments)) {
            return null
        }
        return matchSegments(patternSegments, pathSegments)
    }

    /**
     * 경로를 세그먼트로 분리합니다.
     *
     * @param path 분리할 경로
     * @return 빈 문자열을 제외한 경로 세그먼트 리스트
     */
    private fun splitPath(path: String): List<String> {
        return path.split("/").filter { it.isNotEmpty() }
    }

    /**
     * 두 세그먼트 리스트의 크기가 동일한지 확인합니다.
     *
     * @param patternSegments 패턴 세그먼트 리스트
     * @param pathSegments 경로 세그먼트 리스트
     * @return 크기가 동일하면 true, 아니면 false
     */
    private fun isSameLengthSegments(patternSegments: List<String>, pathSegments: List<String>): Boolean {
        return patternSegments.size == pathSegments.size
    }

    /**
     * 패턴 세그먼트와 경로 세그먼트를 매칭합니다.
     *
     * @param patternSegments 패턴 세그먼트 리스트
     * @param pathSegments 경로 세그먼트 리스트
     * @return 매칭 성공 시 경로 변수 map, 실패 시 null
     */
    private fun matchSegments(patternSegments: List<String>, pathSegments: List<String>): Map<String, String>? {
        val pathVariables = mutableMapOf<String, String>()

        for (i in patternSegments.indices) {
            if (!matchSegment(patternSegments[i], pathSegments[i], pathVariables)) {
                return null
            }
        }

        logger.debug("Match success: {}", pathVariables)
        return pathVariables
    }

    /**
     * 단일 패턴 세그먼트와 경로 세그먼트를 매칭합니다.
     *
     * @param patternSegment 패턴 세그먼트
     * @param pathSegment 경로 세그먼트
     * @param pathVariables 경로 변수를 저장할 map
     * @return 매칭 성공 시 true, 실패 시 false
     */
    private fun matchSegment(
        patternSegment: String,
        pathSegment: String,
        pathVariables: MutableMap<String, String>
    ): Boolean {
        return when {
            isPathVariable(patternSegment) -> {
                extractAndStoreVariable(patternSegment, pathSegment, pathVariables)
                true
            }
            patternSegment == pathSegment -> {
                logger.debug("Exact match: {}", patternSegment)
                true
            }
            else -> {
                logger.debug("Mismatch: {} != {}", patternSegment, pathSegment)
                false
            }
        }
    }

    /**
     * 경로 변수를 추출하여 저장합니다.
     *
     * @param patternSegment 경로 변수 패턴
     * @param pathSegment 실제 경로 값
     * @param pathVariables 경로 변수를 저장할 map
     */
    private fun extractAndStoreVariable(
        patternSegment: String,
        pathSegment: String,
        pathVariables: MutableMap<String, String>
    ) {
        val variableName = extractVariableName(patternSegment)
        pathVariables[variableName] = pathSegment
        logger.debug("Path variable: {} = {}", variableName, pathSegment)
    }

    /**
     * 세그먼트가 경로 변수인지 확인합니다.
     *
     * @param segment 확인할 세그먼트
     * @return 경로 변수이면 true, 아니면 false
     */
    private fun isPathVariable(segment: String): Boolean {
        return segment.startsWith("{") && segment.endsWith("}")
    }

    /**
     * 경로 변수 패턴에서 변수명을 추출합니다.
     *
     * @param segment 경로 변수 패턴
     * @return 변수명
     */
    private fun extractVariableName(segment: String): String {
        return segment.removeSurrounding("{", "}")
    }

    /**
     * 패턴의 구체성 점수를 계산하여 라우터에 매칭시킵니다.
     *
     *  @param pattern 점수를 계산할 패턴
     * @return 구체성 점수
     */
    fun getSpecificityScore(pattern: String): Int {
        return splitPath(pattern).count { !isPathVariable(it) }
    }
}