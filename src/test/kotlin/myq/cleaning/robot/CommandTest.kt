package myq.cleaning.robot

import myq.cleaning.model.Direction
import myq.cleaning.model.Position
import myq.cleaning.model.RobotState
import org.junit.Assert.assertEquals
import org.junit.Test

class CommandTest {
    val initState = RobotState(Position(1, 1), 10, Direction.E)

    @Test
    fun `turn left from east makes north`() {
        val result = Command.TL.execute(initState)
        assertEquals(Direction.N, result.facing)
    }

    @Test
    fun `turn left from north makes west`() {
        val result = Command.TL.execute(initState.copy(facing = Direction.N))
        assertEquals(Direction.W, result.facing)
    }

    @Test
    fun `turn left from west makes south`() {
        val result = Command.TL.execute(initState.copy(facing = Direction.W))
        assertEquals(Direction.S, result.facing)
    }

    @Test
    fun `turn left from south makes east`() {
        val result = Command.TL.execute(initState.copy(facing = Direction.S))
        assertEquals(Direction.E, result.facing)
    }

    @Test
    fun `turn left 4 times returns to original direction`() {
        var result: RobotState = initState
        for (i in 1..4) {
            result = Command.TL.execute(result)
        }

        assertEquals(initState.facing, result.facing)
    }

    @Test
    fun `turning left doesn't affect  position`() {
        val result = Command.TL.execute(initState)
        assertEquals(initState.position, result.position)
    }

    @Test
    fun `turning left consumes 1 battery`() {
        val result = Command.TL.execute(initState)
        assertEquals(initState.battery - 1, result.battery)
    }

    @Test
    fun `turn right from south makes west`() {
        val result = Command.TR.execute(initState.copy(facing = Direction.S))
        assertEquals(Direction.W, result.facing)
    }

    @Test
    fun `turn right from west makes north`() {
        val result = Command.TR.execute(initState.copy(facing = Direction.W))
        assertEquals(Direction.N, result.facing)
    }

    @Test
    fun `turn right from north makes east`() {
        val result = Command.TR.execute(initState.copy(facing = Direction.N))
        assertEquals(Direction.E, result.facing)
    }

    @Test
    fun `turn right from east makes south`() {
        val result = Command.TR.execute(initState.copy(facing = Direction.E))
        assertEquals(Direction.S, result.facing)
    }

    @Test
    fun `turn right 4 times returns to original direction`() {
        var result: RobotState = initState
        for (i in 1..4) {
            result = Command.TR.execute(result)
        }

        assertEquals(initState.facing, result.facing)
    }

    @Test
    fun `turning right doesn't affect position`() {
        val result = Command.TR.execute(initState)
        assertEquals(initState.position, result.position)
    }

    @Test
    fun `turning right consumes 1 battery`() {
        val result = Command.TR.execute(initState)
        assertEquals(initState.battery - 1, result.battery)
    }

    @Test
    fun `advancing north subtracts 1 from row`() {
        val result = Command.A.execute(initState.copy(facing = Direction.N))
        assertEquals(Position(1, 0), result.position)
    }

    @Test
    fun `advancing south adds 1 to row`() {
        val result = Command.A.execute(initState.copy(facing = Direction.S))
        assertEquals(Position(1, 2), result.position)
    }

    @Test
    fun `advancing west subtracts 1 from column`() {
        val result = Command.A.execute(initState.copy(facing = Direction.W))
        assertEquals(Position(0, 1), result.position)
    }

    @Test
    fun `advancing east adds 1 to column`() {
        val result = Command.A.execute(initState.copy(facing = Direction.E))
        assertEquals(Position(2, 1), result.position)
    }

    @Test
    fun `advancing doesn't affect facing direction`() {
        val result = Command.A.execute(initState)
        assertEquals(initState.facing, result.facing)
    }

    @Test
    fun `advancing consumes 2 battery`() {
        val result = Command.A.execute(initState)
        assertEquals(initState.battery - 2, result.battery)
    }

    @Test
    fun `backing up when facing north adds 1 from row`() {
        val result = Command.B.execute(initState.copy(facing = Direction.N))
        assertEquals(Position(1, 2), result.position)
    }

    @Test
    fun `backing up when facing subtracts 1 from row`() {
        val result = Command.B.execute(initState.copy(facing = Direction.S))
        assertEquals(Position(1, 0), result.position)
    }

    @Test
    fun `backing up when facing west adds 1 to column`() {
        val result = Command.B.execute(initState.copy(facing = Direction.W))
        assertEquals(Position(2, 1), result.position)
    }

    @Test
    fun `backing up when facing east subtracts 1 from column`() {
        val result = Command.B.execute(initState.copy(facing = Direction.E))
        assertEquals(Position(0, 1), result.position)
    }

    @Test
    fun `backing up doesn't affect facing direction`() {
        val result = Command.B.execute(initState)
        assertEquals(initState.facing, result.facing)
    }

    @Test
    fun `backing up when facing consumes 3 battery`() {
        val result = Command.B.execute(initState)
        assertEquals(initState.battery - 3, result.battery)
    }

    @Test
    fun `advancing and then backing up gives the original position`() {
        val result = Command.B.execute(Command.A.execute(initState))
        assertEquals(initState.position, result.position)
    }

    @Test
    fun `backing up and then advancing gives the original position`() {
        val result = Command.A.execute(Command.B.execute(initState))
        assertEquals(initState.position, result.position)
    }

    @Test
    fun `cleaning doesn't affect facing direction or position`() {
        val result = Command.C.execute(initState)
        assertEquals(initState.position, result.position)
        assertEquals(initState.facing, result.facing)
    }

    @Test
    fun `cleaning consumes 5 battery`() {
        val result = Command.C.execute(initState)
        assertEquals(initState.battery - 5, result.battery)
    }
}