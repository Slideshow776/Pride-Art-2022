package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class BossBar(x: Float, y: Float, stage: Stage) : BaseActor(0f, 0f, stage) {
    private var progress: BaseActor
    private var time = 10f

    var complete = false

    init {
        loadImage("whitePixel")
        color = Color.BLACK
        isVisible = false
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height * .05f)
        setPosition(x, y - height)

        progress = BaseActor(0f, 0f, stage)
        progress.loadImage("whitePixel")
        progress.color = Color(0.459f, 0.141f, 0.22f, 1f) // wine red
        progress.setSize(width, height)
        addActor(progress)
    }

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
}
