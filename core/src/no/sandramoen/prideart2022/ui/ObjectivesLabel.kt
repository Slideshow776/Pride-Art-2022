package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Null
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import no.sandramoen.prideart2022.utils.BaseGame

class ObjectivesLabel : TypingLabel("", BaseGame.smallLabelStyle) {
    private val white = Color(0.922f, 0.929f, 0.914f, 1f)
    private val lightBlue = Color(0.643f, 0.867f, 0.859f, 1f)

    init {
        isVisible = false
        color = lightBlue
        setText("")
    }

    fun setMyText(newText: CharSequence){
        restart()
        setText(newText)
        fadeIn()
        addAction(glintToWhiteAndBack())
    }

    fun fadeOut() {
        clearActions()
        addAction(Actions.fadeOut(.2f))
    }

    fun glintToWhiteAndBack(): SequenceAction? {
        return Actions.sequence(
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
}
