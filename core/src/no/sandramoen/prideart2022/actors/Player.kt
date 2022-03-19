package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.XBoxGamepad

class Player(x: Float, y: Float, stage: Stage) : BaseActor(0f, 0f, stage) {
    init {
        loadImage("ghost")
        centerAtPosition(x, y)
        debug = true

        setAcceleration(200f)
        setMaxSpeed(25f)
        setDeceleration(200f)

        alignCamera()
        zoomCamera(.6f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (pause) return

        movementPolling(dt)

        boundToWorld()
        alignCamera(lerp = .1f)
    }

    fun flashColor(color: Color) {
        actions.clear()
        val duration = .25f
        addAction(
            Actions.sequence(
                Actions.color(color, duration / 2),
                Actions.color(Color.WHITE, duration / 2)
            )
        )
    }

    private fun movementPolling(dt: Float) {
        if (Controllers.getControllers().size > 0)
            controllerPolling()
        keyboardPolling()
        applyPhysics(dt)
    }

    private fun controllerPolling() {
        val controller = Controllers.getControllers()[0] // TODO: Warning => this is dangerous
        controllerAxisPolling(controller)
        controllerDirectionalPadPolling(controller)
    }

    private fun controllerAxisPolling(controller: Controller) {
        val direction = Vector2(
            controller.getAxis(XBoxGamepad.AXIS_LEFT_Y),
            -controller.getAxis(XBoxGamepad.AXIS_LEFT_X)
        )

        val length = direction.len()
        val deadZone = .1f
        if (length > deadZone) {
            setSpeed(length * 100f)
            setMotionAngle(direction.angleDeg())
        }
    }

    private fun controllerDirectionalPadPolling(controller: Controller) {
        if (controller.getButton(XBoxGamepad.DPAD_UP))
            accelerateAtAngle(90f)
        if (controller.getButton(XBoxGamepad.DPAD_LEFT))
            accelerateAtAngle(180f)
        if (controller.getButton(XBoxGamepad.DPAD_DOWN))
            accelerateAtAngle(270f)
        if (controller.getButton(XBoxGamepad.DPAD_RIGHT))
            accelerateAtAngle(0f)
    }

    private fun keyboardPolling() {
        if (Gdx.input.isKeyPressed(Keys.W))
            accelerateAtAngle(90f)
        if (Gdx.input.isKeyPressed(Keys.A))
            accelerateAtAngle(180f)
        if (Gdx.input.isKeyPressed(Keys.S))
            accelerateAtAngle(270f)
        if (Gdx.input.isKeyPressed(Keys.D))
            accelerateAtAngle(0f)
    }
}
