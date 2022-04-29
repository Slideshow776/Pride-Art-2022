package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class BossBar(x: Float, y: Float, stage: Stage) : BaseActor(0f, 0f, stage) {
    private var progress: BaseActor
    private var time = 60f

    var label: Label
    var complete = false

    init {
        loadImage("whitePixel")
        color = Color.BLACK
        isVisible = false
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height * .05f)
        setPosition(x, y - height)

        progress = BaseActor(0f, 0f, stage)
        progress.loadImage("whitePixel")
        progress.color = Color(0.816f, 0.855f, 0.569f, 1f) // light green
        progress.setSize(width, height)
        progress.addAction(pulse())
        addActor(progress)

        label = Label("Kjersti Gulbrandsen", BaseGame.smallLabelStyle)
        label.setFontScale(.5f)
        label.setPosition(width / 2, 0f)
        addActor(label)
    }

    fun stop() = clearActions()

    fun countDown() {
        isVisible = true
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.run {
                        if (progress.width <= 0) {
                            clearActions()
                            complete = true
                        }
                        progress.addAction(
                            Actions.sizeTo(
                                progress.width - width * 1 / time,
                                height,
                                1f
                            )
                        )
                    },
                    Actions.delay(1f)
                )
            )
        )
    }

    private fun pulse(): RepeatAction? {
        return Actions.forever(
            Actions.sequence(
                Actions.alpha(1f, .5f),
                Actions.alpha(.5f, .5f)
            )
        )
    }
}
