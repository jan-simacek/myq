import kotlinx.serialization.ExperimentalSerializationApi
import myq.cleaning.parser.Serializer
import java.io.File
import java.lang.IllegalArgumentException

fun printUsage() {
    println("Usage: main <input_file_path> <output_file_path>")
}

fun validateArgs(args: Array<String>) {
    if (args.size != 2) {
        printUsage()
        throw IllegalArgumentException("2 arguments expected")
    }

    if (!File(args[0]).canRead()) {
        printUsage()
        throw IllegalArgumentException("First argument is not an existing and readable file: ${args[0]}")
    }

    if (!File(args[1]).canWrite()) {
        printUsage()
        throw IllegalArgumentException("Second argument is not a writeable path: ${args[1]}")
    }
}

@ExperimentalSerializationApi
fun main(args: Array<String>) {
    validateArgs(args)

    val serializer = Serializer()
    val robot = serializer.buildRobot(args[0])
    robot.run()
    serializer.writeResults(args[1], robot.roomMap, robot.state)
}