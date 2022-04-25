package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseGame

class CrystalLabel : Label("0/3", BaseGame.smallLabelStyle) {
    init {
        isVisible = false
    }

    fun fadeIn() {
        addAction(
            Actions.sequence(
                Actions.fadeOut(0f),
                Actions.run { isVisible = true },
                Actions.fadeIn(.2f),
                Actions.run { BaseGame.crystalPickupSound!!.play(BaseGame.soundVolume * .5f) }
            ))
    }

    fun fadeOut() = addAction(Actions.fadeOut(.2f))
    fun changeToPink() = addAction(Actions.color(Color(0.875f, 0.518f, 0.647f, 1f), 1f))
    fun changeToBlue() = addAction(Actions.color(Color(0.643f, 0.867f, 0.859f, 1f), 1f))
}
