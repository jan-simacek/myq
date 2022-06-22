package myq.cleaning.model

enum class CellType {
    S, C
}

data class Cell(var type: CellType, var visited: Boolean = false, var cleaned: Boolean = false,)
