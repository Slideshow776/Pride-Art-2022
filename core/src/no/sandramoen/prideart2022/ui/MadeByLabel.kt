package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class MadeByLabel :
    Label("${BaseGame.myBundle!!.get("madeBy")} Sandra Moen 2022", BaseGame.smallLabelStyle) {

    init {
        setFontScale(1f)
        setAlignment(Align.center)
        color = Color.GRAY
        clickListener()
        GameUtils.addWidgetEnterExitEffect(this, BaseGame.lightPink, Color.GRAY)
    }

    private fun clickListener() {
        addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                openURIWithDelay()
            }
            false
        }
    }

    private fun openURIWithDelay() {
        addAction(
            Actions.sequence(
                Actions.delay(.5f),
                Actions.run { Gdx.net.openURI("https://sandramoen.no"); }
            ))
    }
}
