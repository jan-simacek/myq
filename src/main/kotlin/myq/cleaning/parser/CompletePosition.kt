package myq.cleaning.parser

import kotlinx.serialization.Serializable
import myq.cleaning.model.Direction

@Serializable
data class CompletePosition(val X: Int, val Y: Int, val facing: Direction)
