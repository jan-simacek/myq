package myq.cleaning.parser

import kotlinx.serialization.Serializable

@Serializable
data class InPosition(val X: Int, val Y: Int)
@Serializable
data class OutputFile(val visited: List<InPosition>, val cleaned: List<InPosition>, val final: CompletePosition, val battery: Int)
