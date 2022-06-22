package myq.cleaning.parser

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import myq.cleaning.model.Cell
import myq.cleaning.model.CellType
import myq.cleaning.model.Position
import myq.cleaning.model.RobotState
import myq.cleaning.robot.Robot
import java.io.File
import java.lang.IllegalArgumentException

/**
 * Class for loading inputs from input file, producing an initialized Robot, and for writing output file from
 * a finished Robot.
 */
class Serializer {
    /**
     * Loads input file and uses it to construct a robot with a room map, list of commands, and at specified initial position
     * @param inputFilePath to the input JSON file
     * @return initialized robot
     */
    @ExperimentalSerializationApi
    fun buildRobot(inputFilePath: String): Robot {
        val inputFile =
            Json.decodeFromString<RobotInputFile>(File(inputFilePath).readText())

        val initialState = RobotState(
            Position(inputFile.start.X, inputFile.start.Y),
            inputFile.battery,
            inputFile.start.facing
        )

        val roomMap = inputFile.map.map {
            row -> row.map {
                if (it == "S")
                    CellType.S
                else if (it == "C" || it == "null")
                    CellType.C
                else
                    throw IllegalArgumentException("Unknown cell type found in map: $it")
            }
        }

        if (initialState.position.row < 0 || initialState.position.row >= roomMap.size
            || initialState.position.column < 0 || initialState.position.column >= roomMap[initialState.position.row].size) {
            throw IllegalArgumentException("Initial robot position out of bounds: ${initialState.position}")
        }

        return Robot(roomMap, inputFile.commands, initialState)
    }

    /**
     * Writes results to specified output file
     * @param outputFilePath path where the output JSON file should be written
     * @param roomMap final roomMap containing information about visited and cleaned cells
     * @param finalState final state of the robot
     */
    @ExperimentalSerializationApi
    fun writeResults(outputFilePath: String,  roomMap: List<List<Cell>>, finalState: RobotState) {
        val visited = roomMap.filterCells { it.visited }
        val cleaned = roomMap.filterCells { it.cleaned }

        val format = Json { prettyPrint = true }
        val outputString: String = format.encodeToString(
            OutputFile(
                visited,
                cleaned,
                CompletePosition(finalState.position.column, finalState.position.row, finalState.facing),
                finalState.battery
            )
        )

        File(outputFilePath).writeText(outputString)
    }

    /**
     * transforms a 2D list of cells to a 1D list of their coordinates. Only outputs cells which pass the 'predicate'
     * @param predicate test to be executed on each cell. if it passes it is allowed in the output, otherwise it's filtered
     */
    private fun List<List<Cell>>.filterCells(predicate: (Cell) -> Boolean): List<InPosition> {
        return this.mapIndexed {
            rowIndex, row  -> row.mapIndexed {
                colIndex, cell -> if (predicate(cell)) InPosition(colIndex, rowIndex) else null
            }
        }.flatten().filterNotNull()
    }
}