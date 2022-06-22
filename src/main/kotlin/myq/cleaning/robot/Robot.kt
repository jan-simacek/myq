package myq.cleaning.robot

import mu.KotlinLogging
import myq.cleaning.model.Cell
import myq.cleaning.model.CellType
import myq.cleaning.model.Position
import myq.cleaning.model.RobotState
import java.util.*

enum class ProgramExecutionResult {
    OK, STUCK, BATTERY_DEAD
}

/**
 * Class representing a cleaning robot. The robot moves on supplied 'roomMap', starting with initialState and tries to
 * execute inputCommands
 * @param inputMap map of the room in which the robot moves as a 2d list
 * @param inputCommands commands the robot will try to execute
 * @param initialState starting state of the robot, specifying initial position and battery state
 */
class Robot(inputMap: List<List<CellType>>, private val inputCommands: Iterable<Command>, initialState: RobotState) {
    companion object {
        val BACKOFF_STRATEGIES = arrayOf(
            arrayOf(Command.TR, Command.A, Command.TL),
            arrayOf(Command.TR, Command.A, Command.TR),
            arrayOf(Command.TR, Command.A, Command.TR),
            arrayOf(Command.TR, Command.B, Command.TR, Command.A),
            arrayOf(Command.TL, Command.TL, Command.A),
        )

        private enum class CommandExecutionResult {
            OK, BATTERY_DEAD, POSITION_INVALID
        }
    }
    private val backoffQueue: Queue<Command> = LinkedList()
    private val logger = KotlinLogging.logger {}

    /**
     * -1 - no backoff strategy is currently active
     * 0..4 - corresponding strategy from BACKOFF_STRATEGIES should be executed
     * 5+ - robot is stuck
     */
    private var backoffStrategyIndex: Int = -1

    private var _state = initialState
    /**
     * current state of the robot
     */
    val state: RobotState get() = _state

    private val _roomMap: List<List<Cell>> = inputMap.map { row -> row.map { Cell(it) } }
    /**
     * immutable 2D list of cells - map of the room
     */
    val roomMap: List<List<Cell>> get() = _roomMap

    private val _commands: Queue<Command> = LinkedList<Command>().apply { addAll(inputCommands) }

    /**
     * Immutable list of commands currently in the queue
     */
    val commands: List<Command> get() = _commands.toList()

    /**
     * tries to execute supplied 'inputCommands' program
     * @return OK - if all of inputCommands were successfully executed, BATTERY_DEAD - if battery reached 0 or less
     * during execution, STUCK - if one of the commands would result in an invalid position and none of the
     * BACKOFF_STRATEGIES was able to correct it
     */
    fun run(): ProgramExecutionResult {
        logger.debug { "Starting run. Initial state: $_state" }
        while (hasNextCommand() && state.battery > 0) {
            val nextCommand = nextCommand()
            when (executeCommand(nextCommand)) {
                CommandExecutionResult.OK -> { logger.debug { "Command executed: $nextCommand" } }

                CommandExecutionResult.BATTERY_DEAD -> {
                    logger.debug { "Battery dead! Final state: $_state" }
                    return ProgramExecutionResult.BATTERY_DEAD
                }

                CommandExecutionResult.POSITION_INVALID -> {
                    initiateNextBackoff()
                    if (backoffStrategyIndex >= BACKOFF_STRATEGIES.size) {
                        logger.debug { "Got stuck! Final state: $_state" }
                        return ProgramExecutionResult.STUCK
                    }
                    logger.debug { "Executing backoff strategy $backoffStrategyIndex: ${BACKOFF_STRATEGIES[backoffStrategyIndex].joinToString()}" }
                }
            }
            logger.debug { "State: $_state" }
        }

        return ProgramExecutionResult.OK
    }

    private fun hasNextCommand(): Boolean {
        return !backoffQueue.isEmpty() || !_commands.isEmpty()
    }

    /**
     * attempts to execute one command, checking for position validity and battery state
     * @param command command to be executed
     * @return POSITION_INVALID if the command would take robot to an invalid position, BATTERY_DEAD if the command
     * drained battery to or below 0, OK otherwise
     */
    private fun executeCommand(command: Command): CommandExecutionResult {
        val oldState = _state
        _state = command.execute(_state)

        if (_state.battery < 0) {
            // if battery got BELOW zero, undo the last move (not specified in the spec, but feels right)
            _state = _state.copy(position = oldState.position, facing = oldState.facing)
            return CommandExecutionResult.BATTERY_DEAD
        }

        if (!isPositionValid(_state.position)) {
            // new position is invalid -> restore old position
            _state = _state.copy(position = oldState.position)
            return CommandExecutionResult.POSITION_INVALID
        }

        // mark current position as visited
        _roomMap[_state.position.row][_state.position.column].visited = true

        if (command == Command.C) {
            // we had enough battery to clean this field -> mark as cleaned
            _roomMap[_state.position.row][_state.position.column].cleaned = true
        }

        return CommandExecutionResult.OK
    }

    private fun initiateNextBackoff() {
        backoffStrategyIndex++
        backoffQueue.clear()
        // a sneaky kotlin way to avoid arrayIndexOutOfBounds
        backoffQueue.addAll(BACKOFF_STRATEGIES.getOrElse(backoffStrategyIndex, { arrayOf() }))
    }

    /**
     * gets the next command to execute. If there is nothing in the backoffQueue, resets backoffStrategyIndex to -1
     * @return next command on the 'backoffQueue', if present. otherwise returns next command on the 'command' queue.
     */
    private fun nextCommand(): Command {
        if (!backoffQueue.isEmpty()) {
            return backoffQueue.poll()
        }
        backoffStrategyIndex = -1
        return _commands.poll()
    }

    /**
     * validates if a position is valid within roomMap
     * @param newPosition state whose validity within 'roomMap' is to be verified
     * @return true if newState is a valid state within 'roomMap', false otherwise
     */
    private fun isPositionValid(newPosition: Position): Boolean {
        // out of bounds
        if (newPosition.row < 0 || newPosition.row >= _roomMap.size
            || newPosition.column < 0 || newPosition.column >= _roomMap[newPosition.row].size) {
            return false
        }

        // inaccessible cell
        if (_roomMap[newPosition.row][newPosition.column].type != CellType.S) {
            return false
        }

        return true
    }
}