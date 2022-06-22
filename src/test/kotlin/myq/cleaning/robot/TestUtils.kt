package myq.cleaning.robot

import myq.cleaning.model.Cell
import myq.cleaning.model.CellType

/**
 * converts a string input to 2D list of cell types
 * @param input a string in the format "CSC,CSS,SCC" where C is a C-cell, S is a S-cell
 * @return initialized 2D list of unvisited, uncleaned Cells of corresponding cell types
 */
fun parseCellTypes(input: String): List<List<CellType>> {
    return input.split(",").map { row -> row.map { CellType.valueOf("$it") } }
}

/**
 * converts a string input to a list of Command objects
 * @param input a string in the format "TL,A,B,C" where each comma separated value corresponds to a command
 * @return a list of actual Command objects
 */
fun parseCommands(input: String): List<Command> {
    return input.split(",").map { Command.valueOf(it) }
}

/**
 * converts a string input to a 2D list of cells
 * @param input a string in the format "S++|C--|S+-,S--|C--|S++" where each comma-separated block represents a row,
 * and each pipe-separated block within a row represents a cell of a corresponding type (C or S) and if it's
 * visited (+ or -) and cleaned (again + or -). the +/- signs follow the standard rules of optional parameters (i.e.
 * all - signs, that are not followed by a + sign can be omitted)
 * @return a 2D list of Cell objects corresponding to the input
 */
fun parseCells(input: String): List<List<Cell>> {
    return input.split(",").map {
            row -> row.split("|").map {
        Cell(
            CellType.valueOf("${it[0]}"),
            it.getOrElse(1, { '-' }) == '+',
            it.getOrElse(2, { '-' }) == '+'
        )
    }
    }
}
