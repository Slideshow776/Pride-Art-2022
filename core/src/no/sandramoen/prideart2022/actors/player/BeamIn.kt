package no.sandramoen.prideart2022.actors.player

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class BeamIn(x: Float, y: Float, stage: Stage, player: Player) : BaseActor(x, y, stage) {
    companion object {
        const val animationDuration = .2f
    }

    init {
        BaseGame.beamInSound!!.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f)
        loadImage("beam")
        centerAtActor(player)
        setOrigin(Align.bottom)
        setScale(1f, 300f)
        isShakyCam = true
        addAction(
            Actions.sequence(
                Actions.moveTo(this.x, player.y, animationDuration / 2),
                Actions.scaleTo(1f, 0f, animationDuration / 2),
                Actions.run {
                    isShakyCam = false
                    remove()
                }
            )
        )
    }
}
