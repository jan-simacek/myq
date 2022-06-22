package myq.cleaning.model

data class Position(val column: Int, val row: Int) {
    fun add(other: Position): Position = Position(column + other.column, row + other.row)
}
