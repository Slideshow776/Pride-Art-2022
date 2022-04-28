package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseGame

class LostLabel : Label("0/3", BaseGame.smallLabelStyle) {
    init {
        isVisible = false
        color = Color(0.643f, 0.867f, 0.859f, 1f)
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
}
