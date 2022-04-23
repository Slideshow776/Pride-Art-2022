package no.sandramoen.prideart2022.actors.characters.player

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.particles.StarsVerticalEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class BeamIn(x: Float, y: Float, stage: Stage, baseActor: BaseActor) : BaseActor(x, y + 100, stage) {
    private val player = baseActor

    companion object {
        const val animationDuration = .2f
    }

    init {
        BaseGame.beamInSound!!.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f)
        loadImage("beam")
        centerAtActor(baseActor)
        setOrigin(Align.bottom)
        setScale(1f, 600f)

        animation()
        particles()
    }

    private fun animation() {
        addAction(
            Actions.sequence(
                Actions.moveTo(this.x, player.y, animationDuration / 2),
                Actions.scaleTo(1f, 0f, animationDuration / 2),
                Actions.run {
                    isShakyCam = true
                    BaseGame.groundCrackSound!!.play(BaseGame.soundVolume)
                },
                Actions.delay(.5f),
                Actions.run {
                    isShakyCam = false
                    remove()
                }
            )
        )
    }

    private fun particles() {
        val effect = StarsVerticalEffect()
        effect.setScale(.05f)
        effect.centerAtActor(player)
        parent.addActor(effect)
        effect.start()
    }
}
