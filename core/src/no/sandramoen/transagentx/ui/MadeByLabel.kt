package no.sandramoen.transagentx.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.GameUtils

class MadeByLabel :
    Label("${BaseGame.myBundle!!.get("madeBy")} Sandra Moen 2022", BaseGame.smallLabelStyle) {
    val grayColor = Color(0.506f, 0.592f, 0.588f, 1f)

    init {
        setFontScale(1f)
        setAlignment(Align.center)
        color = grayColor
        clickListener()
        GameUtils.addWidgetEnterExitEffect(this, BaseGame.lightPink, grayColor)
    }

    fun openURIWithDelay() {
        BaseGame.click1Sound!!.play(BaseGame.soundVolume)
        addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { Gdx.net.openURI("https://sandramoen.no"); }
        ))
    }

    private fun clickListener() {
        addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e))
                openURIWithDelay()
            false
        }
    }
}
