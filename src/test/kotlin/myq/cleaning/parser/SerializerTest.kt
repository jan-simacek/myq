package myq.cleaning.parser

import kotlinx.serialization.ExperimentalSerializationApi
import myq.cleaning.model.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

@ExperimentalSerializationApi
class SerializerTest {
    val testSubject = Serializer()

    private fun getInputFilePath(fileName: String) = javaClass.classLoader.getResource(fileName)!!.path

    @Test
    fun `can read in valid test`() {
        val result = testSubject.buildRobot(getInputFilePath("valid_input.json"))
        assertEquals(result.state, RobotState(Position(3, 0), 80, Direction.N))
        assertEquals(result.commands.joinToString(","), "TL,A,C,A,C,TR,A,C")
        assertEquals(result.roomMap.joinToString(",") { it.joinToString ("") { it.type.name } }, "SSSS,SSCS,SSSS,SCSS")
    }


    @Test
    fun `throws exception when a field is missing in the input file`() {
        val testCases = arrayOf(
            Pair("missing_map.json", "map"),
            Pair("missing_start.json", "start"),
            Pair("missing_commands.json", "commands"),
            Pair("missing_battery.json", "battery")
        )

        testCases.forEach {
            val exception = assertThrows(RuntimeException::class.java) {
                testSubject.buildRobot(getInputFilePath(it.first))
            }
            assertEquals(
                "Field '${it.second}' is required for type with serial name 'myq.cleaning.parser.RobotInputFile', but it was missing",
                exception.message
            )
        }
    }

    @Test
    fun `throws exception when input is not a valid JSON`() {
        val exception = assertThrows(RuntimeException::class.java) {
            testSubject.buildRobot(getInputFilePath("malformed_input.json"))
        }
        // exception classes are internal, so they cannot be asserted directly
        assertEquals("JsonDecodingException", exception::class.simpleName)
    }

    @Test
    fun `throws exception when invalid value is used for direction, command or map`() {
        val testCases = arrayOf(
            Pair("invalid_facing.json", "myq.cleaning.model.Direction does not contain element with name 'R'"),
            Pair("invalid_command.json", "myq.cleaning.robot.Command does not contain element with name 'WWW'"),
            Pair("invalid_map.json", "Unknown cell type found in map: RRR"),
            Pair("invalid_initial_position1.json", "Initial robot position out of bounds: Position(column=-1, row=0)"),
            Pair("invalid_initial_position2.json", "Initial robot position out of bounds: Position(column=1, row=-1)"),
            Pair("invalid_initial_position3.json", "Initial robot position out of bounds: Position(column=4, row=0)"),
            Pair("invalid_initial_position4.json", "Initial robot position out of bounds: Position(column=1, row=4)"),
        )

        testCases.forEach {
            val exception = assertThrows(RuntimeException::class.java) {
                testSubject.buildRobot(getInputFilePath(it.first))
            }
            assertEquals(it.second, exception.message)
        }
    }

    @Test
    fun `serializes output to file`() {
        val roomMap = listOf(
            listOf(Cell(CellType.S),Cell(CellType.S,true), Cell(CellType.S, false, true)),
            listOf(Cell(CellType.S, false, true),Cell(CellType.S,true), Cell(CellType.S)),
            listOf(Cell(CellType.S, true),Cell(CellType.S,true, true), Cell(CellType.S))
        )

        val finalState = RobotState(Position(2, 1), 42, Direction.W)

        val tempFile = kotlin.io.path.createTempFile(suffix = ".json")
        testSubject.writeResults(tempFile.absolutePathString(), roomMap, finalState)

        val actualResult = tempFile.readText()
        val expectedResult = javaClass.classLoader.getResource("expected_serializer_output.json")!!.readText()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `works with empty room map`() {
        val roomMap: List<List<Cell>> = listOf(listOf())
        val finalState = RobotState(Position(-1, -1), -1, Direction.E)

        val tempFile = kotlin.io.path.createTempFile(suffix = ".json")
        testSubject.writeResults(tempFile.absolutePathString(), roomMap, finalState)

        val actualResult = tempFile.readText()
        val expectedResult = javaClass.classLoader.getResource("expected_serializer_output_empty_map.json")!!.readText()

        assertEquals(expectedResult, actualResult)
    }
}