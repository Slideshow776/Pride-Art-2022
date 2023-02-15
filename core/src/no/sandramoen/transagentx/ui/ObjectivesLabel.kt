package no.sandramoen.transagentx.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TypingLabel

import no.sandramoen.transagentx.utils.BaseGame

class ObjectivesLabel : TypingLabel("", BaseGame.smallLabelStyle) {
    private val white = Color(0.922f, 0.929f, 0.914f, 1f)
    private val lightBlue = Color(0.643f, 0.867f, 0.859f, 1f)

    init {
        isVisible = false
        color = lightBlue
        setText("")
        alignment = Align.center
    }

    fun setMyText(newText: String){
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
