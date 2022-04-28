package no.sandramoen.prideart2022.actors.characters.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.particles.StarsVerticalEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class BeamOut(x: Float, y: Float, stage: Stage, baseActor: BaseActor) : BaseActor(x, y, stage) {
    private val player = baseActor

    companion object {
        const val animationDuration = .4f
    }

    init {
        BaseGame.beamInSound!!.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f)
        loadImage("beam")
        centerAtActor(baseActor)
        setY(baseActor.y - baseActor.height / 8)
        setOrigin(Align.bottom)
        zIndex = baseActor.zIndex - 1

        isShakyCam = true
        animation()
        particles()
        color = Color.WHITE
    }

    private fun animation() {
        addAction(
            Actions.sequence(
                Actions.scaleTo(1f, 300f, animationDuration / 2),
                Actions.moveTo(x, y + 100, animationDuration / 2),
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
