package no.sandramoen.transagentx.actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.transagentx.actors.particles.HeartExplosion
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame

class Experience(x: Float, y: Float, stage: Stage, amount: Int) : BaseActor(x, y, stage) {
    val amount = amount

    init {
        loadImage("hrt")
        centerAtPosition(x, y)
        setBoundaryRectangle()
        setOrigin(Align.center)
        setPulseAnimation()
    }

    fun pickup() {
        BaseGame.experiencePickupSound!!.play(BaseGame.soundVolume, MathUtils.random(.97f, 1.03f), 0f)
        heartExplosionEffect()
        removeAnimation()
        isCollisionEnabled = false
    }

    private fun removeAnimation() {
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 1f, .25f),
            Actions.run { remove() }
        ))
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

    private fun heartExplosionEffect() {
        val effect = HeartExplosion()
        effect.setScale(.05f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }
}
