package myq.cleaning.parser

import kotlinx.serialization.Serializable
import myq.cleaning.robot.Command

@Serializable
data class RobotInputFile(val map: List<List<String>>, val start: CompletePosition, val commands: List<Command>, val battery: Int)
