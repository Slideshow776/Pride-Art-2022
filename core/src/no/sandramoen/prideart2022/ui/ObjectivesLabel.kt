package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseGame

class ObjectivesLabel : Label("Redd 0/4 transpersoner", BaseGame.smallLabelStyle) {
    private val white = Color(0.922f, 0.929f, 0.914f, 1f)
    private val lightBlue = Color(0.643f, 0.867f, 0.859f, 1f)

    init {
        isVisible = false
        color = lightBlue
    }

    fun fadeIn() {
        addAction(
            Actions.sequence(
                Actions.fadeOut(0f),
                Actions.run { isVisible = true },
                Actions.fadeIn(.2f),
                Actions.run { BaseGame.lostPickupSound!!.play(BaseGame.soundVolume * .5f) }
            ))
    }

    fun fadeOut() = addAction(Actions.fadeOut(.2f))

    fun glintToWhiteAndBack() {
        addAction(
            Actions.sequence(
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f),
                Actions.color(white, .5f),
                Actions.color(lightBlue, .5f)
            )
        )
    }
}
