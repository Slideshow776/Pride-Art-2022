package no.sandramoen.prideart2022.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.Vignette
import no.sandramoen.prideart2022.screens.gameplay.LevelScreen
import no.sandramoen.prideart2022.ui.MadeByLabel
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen
import no.sandramoen.prideart2022.utils.GameUtils

class MenuScreen(private val playMusic: Boolean = true) : BaseScreen() {
    private lateinit var startButton: TextButton
    private lateinit var optionsButton: TextButton
    private lateinit var titleLabel: Label

    override fun initialize() {
        titleLabel = Label(BaseGame.myBundle!!.get("title"), BaseGame.bigLabelStyle)
        titleLabel.setFontScale(1f)
        titleLabel.setAlignment(Align.center)

        Vignette(uiStage)

        val table = Table()
        table.add(titleLabel).padTop(Gdx.graphics.height * .1f)
        table.row()
        table.add(menuButtonsTable()).fillY().expandY()
        table.row()
        table.add(MadeByLabel()).padBottom(Gdx.graphics.height * .02f)
        uiTable.add(table).fill().expand()

        if (playMusic) {
            BaseGame.menuMusic!!.play()
            BaseGame.menuMusic!!.isLooping = true
            BaseGame.menuMusic!!.volume = BaseGame.musicVolume
        }
    }

    override fun update(dt: Float) {}

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE || keycode == Keys.Q)
            exitGame()
        else if (keycode == Keys.ENTER) {
            BaseGame.menuMusic!!.stop()
            disableButtons()
            setLevelScreenWithDelay()
        }
        return false
    }

    private fun menuButtonsTable(): Table {
        val buttonFontScale = 1f
        startButton = initializeStartButton(buttonFontScale)
        optionsButton = initializeOptionsButton(buttonFontScale)

        val table = Table()
        table.add(startButton).padBottom(Gdx.graphics.height * .03f).row()
        table.add(optionsButton).padBottom(Gdx.graphics.height * .03f)
        return table
    }

    private fun initializeStartButton(buttonFontScale: Float): TextButton {
        val textButton = TextButton(BaseGame.myBundle!!.get("start"), BaseGame.textButtonStyle)
        textButton.label.setFontScale(buttonFontScale)
        textButton.addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                disableButtons()
                BaseGame.menuMusic!!.stop()
                setLevelScreenWithDelay()
            }
            false
        }
        GameUtils.addTextButtonEnterExitEffect(textButton)
        return textButton
    }

    private fun initializeOptionsButton(buttonFontScale: Float): TextButton {
        val textButton = TextButton(BaseGame.myBundle!!.get("options"), BaseGame.textButtonStyle)
        textButton.label.setFontScale(buttonFontScale)
        textButton.addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                disableButtons()
                setOptionsScreenWithDelay()
            }
            false
        }
        GameUtils.addTextButtonEnterExitEffect(textButton)
        return textButton
    }

    private fun setLevelScreenWithDelay() {
        startButton.addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { BaseGame.setActiveScreen(LevelScreen()) }
        ))
    }

    private fun setOptionsScreenWithDelay() {
        optionsButton.addAction(Actions.sequence(
            Actions.delay(.5f)/*,
            Actions.run { BaseGame.setActiveScreen(OptionsScreen()) }*/
        ))
    }

    private fun disableButtons() {
        startButton.touchable = Touchable.disabled
        optionsButton.touchable = Touchable.disabled
    }

    private fun exitGame() {
        BaseGame.clickSound!!.play(BaseGame.soundVolume)
        disableButtons()
        BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run {
                super.dispose()
                Gdx.app.exit()
            }
        ))
    }
}
