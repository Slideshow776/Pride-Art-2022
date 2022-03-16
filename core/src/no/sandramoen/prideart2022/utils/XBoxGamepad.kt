package no.sandramoen.prideart2022.utils

class XBoxGamepad {
    companion object {

        /* button codes */
        const val BUTTON_A: Int = 0
        const val BUTTON_B: Int = 1
        const val BUTTON_X: Int = 2
        const val BUTTON_Y: Int = 3
        const val BUTTON_LEFT_SHOULDER: Int = 9
        const val BUTTON_RIGHT_SHOULDER: Int = 10
        const val BUTTON_BACK: Int = 4
        const val BUTTON_START: Int = 6
        const val BUTTON_LEFT_STICK: Int = 8
        const val BUTTON_RIGHT_STICK: Int = 9

        /* directional pad codes */
        val DPAD_UP: Int = 11
        val DPAD_DOWN: Int = 12
        val DPAD_RIGHT: Int = 14
        val DPAD_LEFT: Int = 13

        /* joystick axis codes */
        // X-axis: -1 = left, +1 = right
        // Y-axis: -1 = up, +1 = down
        const val AXIS_LEFT_X: Int = 1
        const val AXIS_LEFT_Y: Int = 0
        const val AXIS_RIGHT_X: Int = 3
        const val AXIS_RIGHT_Y: Int = 2

        /* trigger codes */
        // Left & Right Trigger buttons treated as a single axis; same ID value
        // Values - Left trigger: 0 to +1. Right trigger: 0 to -1.
        // Note: values are additive; they can cancel each other if both are pressed
        const val AXIS_LEFT_TRIGGER: Int = 4
        const val AXIS_RIGHT_TRIGGER: Int = 5
    }
}