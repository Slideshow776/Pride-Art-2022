package no.sandramoen.prideart2022.screens.shell

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.Vignette
import no.sandramoen.prideart2022.ui.*
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen
import no.sandramoen.prideart2022.utils.GameUtils

class OptionsScreen : BaseScreen() {
    override fun initialize() {
        Vignette(uiStage)

        val table = Table()
        table.add(mainLabel()).padBottom(Gdx.graphics.height * .02f)
        table.row()
        table.add(optionsTable()).fillY().expandY()
        table.row()
        table.add(backButton()).padBottom(Gdx.graphics.height * .02f)
        table.row()
        table.add(MadeByLabel()).padBottom(Gdx.graphics.height * .02f)
        /*table.debug = true*/

        uiTable.add(table).fill().expand()
    }

    override fun update(dt: Float) {}

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE || keycode == Keys.Q) {
            BaseGame.clickSound!!.play(BaseGame.soundVolume)
            BaseGame.setActiveScreen(MenuScreen(playMusic = false))
        }
        return false
    }

    private fun optionsTable(): Table {
        val table = Table()
        if (BaseGame.skin != null) {
            table.add(BaseSlider("sound", BaseGame.myBundle!!.get("sound"))).padBottom(Gdx.graphics.height * .01f)
            table.row()
            table.add(BaseSlider("music", BaseGame.myBundle!!.get("music"))).padBottom(Gdx.graphics.height * .02f)
            table.row()
        }

        table.add(LanguageCarousel()).padBottom(Gdx.graphics.height * .04f).row()

        /*table.debug = true*/
        return table
    }

    private fun mainLabel(): Label {
        val label = Label(BaseGame.myBundle!!.get("options"), BaseGame.bigLabelStyle)
        label.setFontScale(.6f)
        label.setAlignment(Align.center)
        return label
    }

    private fun backButton(): TextButton {
        val textButton = TextButton(BaseGame.myBundle!!.get("back"), BaseGame.textButtonStyle)
        textButton.label.setFontScale(1f)
        textButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                textButton.touchable = Touchable.disabled
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                textButton.addAction(Actions.sequence(
                    Actions.delay(.5f),
                    Actions.run { BaseGame.setActiveScreen(MenuScreen(playMusic = false)) }
                ))
            }
        })
        GameUtils.addTextButtonEnterExitEffect(textButton)
        return textButton
    }
}
