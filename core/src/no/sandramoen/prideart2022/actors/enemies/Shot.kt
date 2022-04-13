package no.sandramoen.prideart2022.actors.enemies

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.particles.FlameExplosion
import no.sandramoen.prideart2022.actors.particles.FlameSprinklesEffect
import no.sandramoen.prideart2022.utils.BaseActor

class Shot(x: Float, y: Float, stage: Stage, angle: Float, playerMovementSpeed: Float) : BaseActor(x, y, stage) {
    private val angle = angle
    private val movementSpeed = playerMovementSpeed * .4f

    init {
        loadImage("shot")
        centerAtPosition(x, y)

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)
        setPulseAnimation()

        addFlameSprinkles()
    }

    override fun act(dt: Float) {
        super.act(dt)
        accelerateAtAngle(angle)
        applyPhysics(dt)

        if (outOfBounds())
            remove()
    }

    override fun death() {
        super.death()
        flameExplosionEffect()
        remove()
    }

    private fun flameExplosionEffect() {
        val effect = FlameExplosion()
        effect.setScale(.01f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun setPulseAnimation() {
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.05f, 1.05f, .5f),
                    Actions.scaleTo(.95f, .95f, .5f)
                )
            )
        )
    }

    private fun addFlameSprinkles() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(.1f),
            Actions.run {
                val effect = FlameSprinklesEffect()
                effect.setScale(.01f)
                effect.centerAtActor(this)
                stage.addActor(effect)
                effect.start()
            }
        )))
    }
}
