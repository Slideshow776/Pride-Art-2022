package no.sandramoen.prideart2022.actors.characters.player

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Shield(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    var isActive = false

    init {
        loadImage("shield")
        centerAtPosition(x, y)
        setOrigin(Align.center)
        color.a = 0f
    }

    fun fadeIn() {
        isActive = true
        BaseGame.shieldInSound!!.play(BaseGame.soundVolume)
        addAction(
            Actions.sequence(
                Actions.fadeIn(1f),
                animate()
            )
        )
    }

    fun fadeOut() {
        isActive = false
        BaseGame.shieldOutSound!!.play(BaseGame.soundVolume)
        clearActions()
        addAction(Actions.fadeOut(1f))
    }

    private fun animate(): RepeatAction? {
        return Actions.forever(
            Actions.parallel(
                Actions.rotateBy(45f, 2f),
                Actions.sequence(
                    Actions.alpha(.5f, 1f, Interpolation.linear),
                    Actions.alpha(.7f, 1f, Interpolation.linear)
                ),
                Actions.sequence(
                    Actions.scaleTo(1.4f, 1.4f, 1f),
                    Actions.scaleTo(1.5f, 1.5f, 1f)
                )
            )
        )
    }
}
