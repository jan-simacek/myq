package myq.cleaning.robot

import myq.cleaning.model.Direction
import myq.cleaning.model.Position
import myq.cleaning.model.RobotState
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RobotTest {
    /**
     * builds a robot using given list of commands and roomMap and returns its final state
     */
    private fun runRobot(roomMap: String, commands: String, initialState: RobotState): RobotState {
        val robot = Robot(
            parseCellTypes(roomMap),
            parseCommands(commands),
            initialState
        )
        robot.run()
        return robot.state
    }

    @Test
    fun `can advance`() {
        val result = runRobot("SS","A", RobotState(Position(0, 0), 10, Direction.E))
        assertEquals(RobotState(Position(1, 0), 8, Direction.E), result)
    }

    @Test
    fun `can back up`() {
        val result = runRobot("SS","B", RobotState(Position(1, 0), 10, Direction.E))
        assertEquals(RobotState(Position(0, 0), 7, Direction.E), result)
    }

    @Test
    fun `can turn left`() {
        val result = runRobot("S","TL", RobotState(Position(0, 0), 10, Direction.E))
        assertEquals(RobotState(Position(0, 0), 9, Direction.N), result)
    }

    @Test
    fun `can turn right`() {
        val result = runRobot("S","TR", RobotState(Position(0, 0), 10, Direction.E))
        assertEquals(RobotState(Position(0, 0), 9, Direction.S), result)
    }

    @Test
    fun `can initiate back off strategy to go around an obstacle straight ahead`() {
        val result = runRobot("SCS,SSS", "A", RobotState(Position(0, 0), 10, Direction.E))
        assertEquals(RobotState(Position(0, 1), 4, Direction.E), result)
    }

    @Test
    fun `can use back off strategy 0 and then back off strategy 1 if still facing an obstacle`() {
        val result = runRobot("SSC,SCC", "A", RobotState(Position(1, 0), 20, Direction.E))
        assertEquals(RobotState(Position(0, 0), 11, Direction.N), result)
    }

    @Test
    fun `last move isn't executed if robot runs out of battery`() {
        val result = runRobot("SSS", "B,B", RobotState(Position(0, 0), 5, Direction.W))
        assertEquals(RobotState(Position(1, 0), -1, Direction.W), result)
    }

    @Test
    fun `advancing into an obstacle doesn't execute the move, but will subtract the battery`() {
        val result = runRobot("SCS", "A,A", RobotState(Position(0, 0), 2, Direction.E))
        assertEquals(RobotState(Position(0, 0), 0, Direction.E), result)
    }

    @Test
    fun `can get stuck and end its operation without depleting the battery or finishing the command list`() {
        val result = runRobot("CCC,CSC,CCC", "A,A,A,A,A,A", RobotState(Position(1, 1), 1000, Direction.E))
        assertEquals(Position(1, 1), result.position)
        assertTrue(result.battery > 0)
    }

    @Test
    fun `backoff strategy is still executed completely even if command list doesn't contain any more commands`() {
        val result = runRobot("SSC,SSS", "A,A", RobotState(Position(0, 0), 10, Direction.E))
        assertEquals(RobotState(Position(1, 1), 2, Direction.E), result)
    }

    @Test
    fun `complete integration test 1`() {
        val robot = Robot(
            parseCellTypes("SSSS,SSCS,SCSS"),
            parseCommands("TL,A,C,A,C,TR,A,C"),
            RobotState(Position(3, 0), 80, Direction.N)
        )
        robot.run()

        Assert.assertEquals(RobotState(Position(2, 0), 53, Direction.N), robot.state)
        Assert.assertEquals(parseCells("S|S++|S++|S+,S|S|C|S,S|C|S|S"), robot.roomMap)
    }

    @Test
    fun `complete integration test 2`() {
        val robot = Robot(
            parseCellTypes("SSSS,SSCS,SCSS"),
            parseCommands("TR,A,C,A,C,TR,A,C"),
            RobotState(Position(3, 1), 1094, Direction.S)
        )
        robot.run()

        Assert.assertEquals(RobotState(Position(3, 0), 1063, Direction.N), robot.state)
        Assert.assertEquals(parseCells("S|S|S++|S++,S|S|C|S+,S|C|S|S"), robot.roomMap)
    }
}