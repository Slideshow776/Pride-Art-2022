package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.particles.FlameExplosion
import no.sandramoen.prideart2022.actors.particles.HeartExplosion
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Experience(x: Float, y: Float, stage: Stage, amount: Int) : BaseActor(x, y, stage) {
    val amount = amount

    init {
        loadImage("hrt")
        centerAtPosition(x, y)
        setBoundaryRectangle()
        setOrigin(Align.center)

        GameUtils.pulseWidget(this)
        setPulseAnimation()
    }

    fun pickup() {
        BaseGame.experiencePickupSound!!.play(BaseGame.soundVolume)
        heartExplosionEffect()
        remove()
    }

    private fun setPulseAnimation() {
        addAction(
            Actions.forever(
                Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, .5f),
            Actions.scaleTo(.95f, .95f, .5f)
        )))
    }

    private fun heartExplosionEffect() {
        val effect = HeartExplosion()
        effect.setScale(.05f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }
}
