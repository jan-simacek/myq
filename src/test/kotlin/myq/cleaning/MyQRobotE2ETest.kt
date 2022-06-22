package myq.cleaning

import kotlinx.serialization.ExperimentalSerializationApi
import main
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.io.path.readText

@ExperimentalSerializationApi
class MyQRobotE2ETest {
    private fun getResourcesFilePath(fileName: String) = javaClass.classLoader.getResource(fileName)!!.path

    @Test
    fun `expects 2 arguments`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf())
        }
        assertEquals("2 arguments expected", exception.message)
    }

    @Test
    fun `throws exception if first argument is not a readable file`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf("nonsense/path", "nonsense/path"))
        }
        assertEquals("First argument is not an existing and readable file: nonsense/path", exception.message)
    }

    @Test
    fun `throws exception if second argument is not a readable file`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf(getResourcesFilePath("valid_input.json"), "nonsense/path"))
        }
        assertEquals("Second argument is not a writeable path: nonsense/path", exception.message)
    }

    @Test
    fun `throws exception if errors are detected in the input file`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf(getResourcesFilePath("invalid_facing.json"), createTempFile().absolutePathString()))
        }
        assertEquals("myq.cleaning.model.Direction does not contain element with name 'R'", exception.message)
    }

    @Test
    fun `successfully runs a valid input file and produces expected output`() {
        val inputFile = getResourcesFilePath("valid_input.json")
        val outputFile = createTempFile()

        main(arrayOf(inputFile, outputFile.absolutePathString()))

        val expectedOutput = File(getResourcesFilePath("valid_e2e_output.json")).readText()
        assertEquals(expectedOutput, outputFile.readText())
    }
}