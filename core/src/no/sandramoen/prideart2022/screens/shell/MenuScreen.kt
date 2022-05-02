package no.sandramoen.prideart2022.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.Vignette
import no.sandramoen.prideart2022.screens.shell.intro.SaturnScreen
import no.sandramoen.prideart2022.ui.ControllerMessage
import no.sandramoen.prideart2022.ui.MadeByLabel
import no.sandramoen.prideart2022.utils.*

class MenuScreen(private val playMusic: Boolean = true) : BaseScreen() {
    private lateinit var startButton: TextButton
    private lateinit var optionsButton: TextButton
    private lateinit var titleLabel: Label
    private lateinit var highlightedActor: Actor
    private lateinit var controllerMessage: ControllerMessage
    private var madeByLabel = MadeByLabel()
    private var usingMouse = true
    private var isAxisFreeToMove = true
    private var axisCounter = 0f

    override fun initialize() {
        titleLabel = Label(BaseGame.myBundle!!.get("title"), BaseGame.bigLabelStyle)
        titleLabel.setFontScale(1f)
        titleLabel.setAlignment(Align.center)

        Vignette(uiStage)
        controllerMessage = ControllerMessage()

        val table = Table()
        table.add(titleLabel).padTop(Gdx.graphics.height * .1f)
        table.row()
        table.add(menuButtonsTable()).fillY().expandY()
        table.row()
        table.add(controllerMessage)
        table.row()
        table.add(madeByLabel).padBottom(Gdx.graphics.height * .02f)
        /*table.debug = true*/
        uiTable.add(table).fill().expand()

        if (playMusic)
            GameUtils.playAndLoopMusic(BaseGame.menuMusic)

        if (Controllers.getControllers().size > 0) {
            BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
                Actions.delay(.05f),
                Actions.run {
                    highlightedActor = startButton
                    usingMouse = false
                }
            ))
        }

        checkControllerConnected()
    }

    override fun update(dt: Float) {
        if (axisCounter > .25f)
            isAxisFreeToMove = true
        else
            axisCounter += dt
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!usingMouse) {
            startButton.label.color = Color.WHITE
            optionsButton.label.color = Color.WHITE
            madeByLabel.color = madeByLabel.grayColor
        }
        usingMouse = true
        return super.mouseMoved(screenX, screenY)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE || keycode == Keys.Q)
            exitGame()
        else if (keycode == Keys.ENTER) {
            setLevelScreenWithDelay()
        }
        return false
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        usingMouse = false
        if (isDirectionalPad(controller)) {
            if (controller!!.getButton(XBoxGamepad.DPAD_UP))
                swapButtons(up = true)
            else if (controller!!.getButton(XBoxGamepad.DPAD_DOWN))
                swapButtons(up = false)
        }/* else if (controller!!.getButton(XBoxGamepad.BUTTON_B)) {
            exitGame()
        }*/ else if (controller!!.getButton(XBoxGamepad.BUTTON_A) && highlightedActor == startButton) {
            setLevelScreenWithDelay()
        } else if (controller!!.getButton(XBoxGamepad.BUTTON_A) && highlightedActor == optionsButton) {
            setOptionsScreenWithDelay()
        } else if (controller!!.getButton(XBoxGamepad.BUTTON_A) && highlightedActor == madeByLabel) {
            madeByLabel.openURIWithDelay()
        }
        return super.buttonDown(controller, buttonCode)
    }

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        if (isAxisFreeToMove && value > .1f) {
            val direction = Vector2(
                controller!!.getAxis(XBoxGamepad.AXIS_LEFT_Y),
                -controller!!.getAxis(XBoxGamepad.AXIS_LEFT_X)
            )

            if (direction.angleDeg() in 30.0..150.0) {
                swapButtons(up = true)
            } else if (direction.angleDeg() in 210.0..330.0)
                swapButtons(up = false)

            isAxisFreeToMove = false
            axisCounter = 0f
        }
        return super.axisMoved(controller, axisCode, value)
    }

    override fun connected(controller: Controller?) {
        if (controller!!.canVibrate() && BaseGame.isVibrationEnabled)
            controller!!.startVibration(1000, .2f)
        controllerMessage.showConnected()
        BaseGame.controllerConnectedSound!!.play(BaseGame.soundVolume)
        pause()
    }

    override fun disconnected(controller: Controller?) {
        controllerMessage.showDisConnected()
        BaseGame.controllerDisconnectedSound!!.play(BaseGame.soundVolume)
        pause()
    }

    private fun checkControllerConnected() {
        if (!BaseGame.isControllerChecked) {
            BaseGame.isControllerChecked = true
            if (Controllers.getControllers().size > 0) {
                controllerMessage.showConnected()
                val controller = Controllers.getControllers()[0]
                if (controller.canVibrate() && BaseGame.isVibrationEnabled)
                    controller.startVibration(1000, .2f)
            } else {
                controllerMessage.showNoControllerFound()
            }
        }
    }

    private fun swapButtons(up: Boolean) {
        if (up) {
            if (highlightedActor == startButton) {
                highlightedActor = madeByLabel
                startButton.label.color = Color.WHITE
                optionsButton.label.color = Color.WHITE
                madeByLabel.color = BaseGame.lightPink
            } else if (highlightedActor == optionsButton) {
                highlightedActor = startButton
                startButton.label.color = BaseGame.lightPink
                optionsButton.label.color = Color.WHITE
                madeByLabel.color = madeByLabel.grayColor
            } else if (highlightedActor == madeByLabel) {
                highlightedActor = optionsButton
                startButton.label.color = Color.WHITE
                optionsButton.label.color = BaseGame.lightPink
                madeByLabel.color = madeByLabel.grayColor
            }
        } else {
            if (highlightedActor == startButton) {
                highlightedActor = optionsButton
                startButton.label.color = Color.WHITE
                optionsButton.label.color = BaseGame.lightPink
                madeByLabel.color = madeByLabel.grayColor
            } else if (highlightedActor == optionsButton) {
                highlightedActor = madeByLabel
                startButton.label.color = Color.WHITE
                optionsButton.label.color = Color.WHITE
                madeByLabel.color = BaseGame.lightPink
            } else if (highlightedActor == madeByLabel) {
                highlightedActor = startButton
                startButton.label.color = BaseGame.lightPink
                optionsButton.label.color = Color.WHITE
                madeByLabel.color = madeByLabel.grayColor
            }
        }
    }

    private fun isDirectionalPad(controller: Controller?): Boolean =
        controller!!.getButton(XBoxGamepad.DPAD_UP) ||
                controller!!.getButton(XBoxGamepad.DPAD_DOWN) ||
                controller!!.getButton(XBoxGamepad.DPAD_LEFT) ||
                controller!!.getButton(XBoxGamepad.DPAD_RIGHT)

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
                setOptionsScreenWithDelay()
            }
            false
        }
        GameUtils.addTextButtonEnterExitEffect(textButton)
        return textButton
    }

    private fun setLevelScreenWithDelay() {
        prepLeaveMenuScreen()
        startButton.addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { BaseGame.setActiveScreen(SaturnScreen()) }
        ))
    }

    private fun setOptionsScreenWithDelay() {
        prepLeaveMenuScreen(stopMusic = false)
        optionsButton.addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { BaseGame.setActiveScreen(OptionsScreen()) }
        ))
    }

    private fun exitGame() {
        prepLeaveMenuScreen()
        BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run {
                super.dispose()
                Gdx.app.exit()
            }
        ))
    }

    private fun prepLeaveMenuScreen(stopMusic: Boolean = true) {
        if (stopMusic)
            BaseGame.menuMusic!!.stop()
        BaseGame.click1Sound!!.play(BaseGame.soundVolume)
        startButton.touchable = Touchable.disabled
        optionsButton.touchable = Touchable.disabled
    }
}
