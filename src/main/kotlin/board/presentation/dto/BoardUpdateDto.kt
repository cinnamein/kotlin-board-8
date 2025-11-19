package board.presentation.dto

data class BoardUpdateRequestDto(
    val id: Long,
    val title: String,
    val content: String,
)

data class BoardUpdateResponseDto(
    val id: Long,
    val title: String,
    val content: String,
)