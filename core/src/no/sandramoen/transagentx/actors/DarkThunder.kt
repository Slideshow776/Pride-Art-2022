package no.sandramoen.transagentx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.GameUtils

class DarkThunder(stage: Stage) : BaseActor(0f, 0f, stage) {
    init {
        loadImage("whitePixel")
        color = Color.BLACK
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        animate()
    }

    private fun animate() {
        addAction(Actions.forever(Actions.sequence(
            Actions.alpha(.25f, .1f),
            Actions.alpha(0f, .1f),
            Actions.delay(MathUtils.random(6f, 18f)),
            Actions.run {
                GameUtils.vibrateController(500, .1f)
                BaseGame.thunderSound!!.play(
                    BaseGame.soundVolume,
                    MathUtils.random(.5f, 1.5f),
                    0f
                )
            }
        )))
    }
}