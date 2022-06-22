import kotlinx.serialization.ExperimentalSerializationApi
import myq.cleaning.parser.Serializer
import myq.cleaning.robot.ProgramExecutionResult
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess

/**
 * Verifies if a writeable file can be open or created in 'path'. The complication stems from the fact, that there
 * is no way of verifying if a writeable file can be created in a path without actually creating it.
 */
internal fun isPathWriteable(path: String): Boolean {
    if (File(path).canWrite()) {
        return true
    }

    try {
        File(path).createNewFile()
    } catch (e: IOException) {
        return false
    }
    return true
}

/**
 * validates command line arguments
 * @param args expects 2 arguments: first is a path of a readable JSON file with input, second is a path where a
 * writeable file either exists or can be created, into which output will be written.
 */
internal fun validateArgs(args: Array<String>) {
    fun printUsage() {
        println("Usage: main <input_file_path> <output_file_path>")
    }

    if (args.size < 2 || args.size > 3) { // 3rd argument only for testing - otherwise it cannot be e2e tested
        printUsage()
        throw IllegalArgumentException("2 arguments expected")
    }

    if (!File(args[0]).canRead()) {
        printUsage()
        throw IllegalArgumentException("First argument is not an existing and readable file: ${args[0]}")
    }

    if (!isPathWriteable(args[1])) {
        printUsage()
        throw IllegalArgumentException("Second argument is not a writeable path: ${args[1]}")
    }
}

@ExperimentalSerializationApi
fun main(args: Array<String>) {
    validateArgs(args)

    val serializer = Serializer()
    val robot = serializer.buildRobot(args[0])
    val result = robot.run()
    serializer.writeResults(args[1], robot.roomMap, robot.state)

    if (args.size != 3 || args[2] != "--test") { // if last argument is --test don't exit the process or else JUnit ignores the test
        exitProcess(result.ordinal) // propagate error code to system
    }
}