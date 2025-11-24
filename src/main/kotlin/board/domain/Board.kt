package board.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "boards")
class Board(
    id: Long = 0L,
    title: String,
    content: String,
    author: String,
    createdAt: Instant = Instant.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = id

    @Column
    var title: String = title
        protected set

    @Column
    var content: String = content
        protected set

    @Column
    val author: String = author

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = createdAt

    fun updateBoard(title: String, content: String) {
        this.title = title
        this.content = content
    }
}
