package board.presentation.dto

data class BoardRequestDto(
    val title: String,
    val content: String,
    val author: String,
)

data class BoardResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: String,
)