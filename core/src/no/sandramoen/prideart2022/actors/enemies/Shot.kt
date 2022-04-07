package no.sandramoen.prideart2022.actors.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor

class Shot(x: Float, y: Float, stage: Stage, angle: Float) : BaseActor(x, y, stage) {
    private val angle = angle
    private val movementSpeed = 10f

    init {
        loadImage("shot")
        centerAtPosition(x, y)
        color = Color.YELLOW

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)
        setPulseAnimation()
    }

    override fun act(dt: Float) {
        super.act(dt)
        accelerateAtAngle(angle)
        applyPhysics(dt)

        if (outOfBounds())
            remove()
    }

    private fun setPulseAnimation() {
        addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, .5f),
            Actions.scaleTo(.95f, .95f, .5f)
        )))
    }
}
