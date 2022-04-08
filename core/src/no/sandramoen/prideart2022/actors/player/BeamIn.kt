package no.sandramoen.prideart2022.actors.player

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor

class BeamIn(x: Float, y: Float, stage: Stage, player: Player) : BaseActor(x, y, stage) {
    val animationDuration = .2f

    init {
        loadImage("beam")
        centerAtActor(player)
        setOrigin(Align.bottom)
        setScale(1f, 300f)
        addAction(
            Actions.sequence(
                Actions.moveTo(this.x, player.y, animationDuration / 2),
                Actions.scaleTo(1f, 0f, animationDuration / 2)
            )
        )
    }
}
