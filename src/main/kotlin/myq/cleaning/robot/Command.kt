package myq.cleaning.robot

import myq.cleaning.model.RobotState

enum class Command {
    TL { // turn left
        override fun execute(state: RobotState) = state.copy(facing = state.facing.previous(), battery = state.battery - 1)
    },
    TR { // turn right
        override fun execute(state: RobotState) = state.copy(facing = state.facing.next(), battery = state.battery - 1)
    },
    A { // advance forward
        override fun execute(state: RobotState) = state.copy(position = state.position.add(state.facing.facingPosition), battery = state.battery - 2)
    },
    B { // go backward
        override fun execute(state: RobotState) = state.copy(position = state.position.add(state.facing.reversePosition()), battery = state.battery - 3)
    },
    C { // clean current field
        override fun execute(state: RobotState) = state.copy(battery = state.battery - 5)
    };

    abstract fun execute(state: RobotState): RobotState
}