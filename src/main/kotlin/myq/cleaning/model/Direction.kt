package myq.cleaning.model

enum class Direction {
    N {
        override val facingPosition = Position(0, -1)
    },
    E {
        override val facingPosition = Position(1, 0)
    },
    S {
        override val facingPosition = Position(0, 1)
    },
    W {
        override val facingPosition = Position(-1, 0)
    };

    fun next(): Direction {
        val values = enumValues<Direction>()
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }

    fun previous(): Direction {
        val values = enumValues<Direction>()
        val nextOrdinal = (ordinal + values.size - 1) % values.size
        return values[nextOrdinal]
    }

    abstract val facingPosition: Position
    fun reversePosition() = Position(-facingPosition.column, -facingPosition.row)
}