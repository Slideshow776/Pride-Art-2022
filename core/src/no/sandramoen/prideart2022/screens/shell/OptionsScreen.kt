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
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.Vignette
import no.sandramoen.prideart2022.ui.*
import no.sandramoen.prideart2022.utils.*

class OptionsScreen : BaseScreen() {
    private var madeByLabel = MadeByLabel()
    private var backButton = backButton()
    private var soundSlider = BaseSlider("sound", BaseGame.myBundle!!.get("sound"))
    private var musicSlider = BaseSlider("music", BaseGame.myBundle!!.get("music"))
    private var languageCarousel = LanguageCarousel()
    private lateinit var highlightedActor: Actor
    private var usingMouse = true
    private var isAxisFreeToMove = true
    private var axisCounter = 0f

    override fun initialize() {
        Vignette(uiStage)

        val table = Table()
        table.add(mainLabel()).padBottom(Gdx.graphics.height * .02f).row()
        table.add(optionsTable()).fillY().expandY().row()
        table.add(backButton).padBottom(Gdx.graphics.height * .02f).row()
        table.add(madeByLabel).padBottom(Gdx.graphics.height * .02f)
        uiTable.add(table).fill().expand()

        if (Controllers.getControllers().size > 0) {
            BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
                Actions.delay(.05f),
                Actions.run {
                    highlightedActor = soundSlider
                    usingMouse = false
                }
            ))
        }
    }

    override fun update(dt: Float) {
        if (axisCounter > .25f)
            isAxisFreeToMove = true
        else
            axisCounter += dt
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!usingMouse) {
            soundSlider.label.color = Color.WHITE
            musicSlider.label.color = Color.WHITE
            languageCarousel.chooseLanguageLabel.color = Color.WHITE
            madeByLabel.color = madeByLabel.grayColor
        }
        usingMouse = true
        return super.mouseMoved(screenX, screenY)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE || keycode == Keys.Q)
            setMenuScreen()
        return false
    }

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        if (isAxisFreeToMove && value > .1f) {
            isAxisFreeToMove = false
            axisCounter = 0f

            val direction = Vector2(
                controller!!.getAxis(XBoxGamepad.AXIS_LEFT_Y),
                -controller!!.getAxis(XBoxGamepad.AXIS_LEFT_X)
            )

            if (direction.angleDeg() in 60.0..120.0) {
                swapButtons(up = true)
                return false
            } else if (direction.angleDeg() in 240.0..300.0) {
                swapButtons(up = false)
                return false
            }

            else if (direction.angleDeg() in 120.0..240.0 && highlightedActor == soundSlider) {
                soundSlider.slider.value -= soundSlider.stepSize
                return false
            }else if (direction.angleDeg() > 300f || direction.angleDeg() < 60 && highlightedActor == soundSlider) {
                soundSlider.slider.value += soundSlider.stepSize
                return false

            }else if (direction.angleDeg() in 120.0..240.0 && highlightedActor == musicSlider) {
                musicSlider.slider.value -= musicSlider.stepSize
                return false
            }else if (direction.angleDeg() > 300f || direction.angleDeg() < 60 && highlightedActor == musicSlider) {
                musicSlider.slider.value += musicSlider.stepSize
                return false
            }
        }
        return super.axisMoved(controller, axisCode, value)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        usingMouse = false
        if (isDirectionalPad(controller)) {
            if (controller!!.getButton(XBoxGamepad.DPAD_UP))
                swapButtons(up = true)
            else if (controller!!.getButton(XBoxGamepad.DPAD_DOWN))
                swapButtons(up = false)
            else if (controller!!.getButton(XBoxGamepad.DPAD_LEFT) && highlightedActor == soundSlider)
                soundSlider.slider.value -= soundSlider.stepSize
            else if (controller!!.getButton(XBoxGamepad.DPAD_RIGHT) && highlightedActor == soundSlider)
                soundSlider.slider.value += soundSlider.stepSize
            else if (controller!!.getButton(XBoxGamepad.DPAD_LEFT) && highlightedActor == musicSlider)
                musicSlider.slider.value -= musicSlider.stepSize
            else if (controller!!.getButton(XBoxGamepad.DPAD_RIGHT) && highlightedActor == musicSlider)
                musicSlider.slider.value += musicSlider.stepSize
        } else if (controller!!.getButton(XBoxGamepad.BUTTON_B)) {
            setMenuScreen()
        } else if (controller!!.getButton(XBoxGamepad.BUTTON_A) && highlightedActor == languageCarousel) {
            languageCarousel.changeLanguage()
        } else if (controller!!.getButton(XBoxGamepad.BUTTON_A) && highlightedActor == backButton) {
            setMenuScreen()
        } else if (controller!!.getButton(XBoxGamepad.BUTTON_A) && highlightedActor == madeByLabel) {
            madeByLabel.openURIWithDelay()
        }
        return super.buttonDown(controller, buttonCode)
    }

    private fun isDirectionalPad(controller: Controller?): Boolean =
        controller!!.getButton(XBoxGamepad.DPAD_UP) ||
                controller!!.getButton(XBoxGamepad.DPAD_DOWN) ||
                controller!!.getButton(XBoxGamepad.DPAD_LEFT) ||
                controller!!.getButton(XBoxGamepad.DPAD_RIGHT)

    private fun swapButtons(up: Boolean) {
        if (up) {
            if (highlightedActor == soundSlider) {
                highlightedActor = madeByLabel
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = Color.WHITE
                madeByLabel.color = BaseGame.lightPink
            } else if (highlightedActor == musicSlider) {
                highlightedActor = soundSlider
                soundSlider.label.color = BaseGame.lightPink
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = Color.WHITE
                madeByLabel.color = Color.WHITE
            } else if (highlightedActor == languageCarousel) {
                highlightedActor = musicSlider
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = BaseGame.lightPink
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = Color.WHITE
                madeByLabel.color = Color.WHITE
            } else if (highlightedActor == backButton) {
                highlightedActor = languageCarousel
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = BaseGame.lightPink
                backButton.label.color = Color.WHITE
                madeByLabel.color = Color.WHITE
            } else if (highlightedActor == madeByLabel) {
                highlightedActor = backButton
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = BaseGame.lightPink
                madeByLabel.color = Color.WHITE
            }
        } else {
            if (highlightedActor == soundSlider) {
                highlightedActor = musicSlider
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = BaseGame.lightPink
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = Color.WHITE
                madeByLabel.color = Color.WHITE
            } else if (highlightedActor == musicSlider) {
                highlightedActor = languageCarousel
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = BaseGame.lightPink
                backButton.label.color = Color.WHITE
                madeByLabel.color = Color.WHITE
            } else if (highlightedActor == languageCarousel) {
                highlightedActor = backButton
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = BaseGame.lightPink
                madeByLabel.color = Color.WHITE
            } else if (highlightedActor == backButton) {
                highlightedActor = madeByLabel
                soundSlider.label.color = Color.WHITE
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = Color.WHITE
                madeByLabel.color = BaseGame.lightPink
            } else if (highlightedActor == madeByLabel) {
                highlightedActor = soundSlider
                soundSlider.label.color = BaseGame.lightPink
                musicSlider.label.color = Color.WHITE
                languageCarousel.chooseLanguageLabel.color = Color.WHITE
                backButton.label.color = Color.WHITE
                madeByLabel.color = Color.WHITE
            }
        }
    }

    private fun optionsTable(): Table {
        val table = Table()
        if (BaseGame.skin != null) {
            table.add(soundSlider).padBottom(Gdx.graphics.height * .01f).row()
            table.add(musicSlider).padBottom(Gdx.graphics.height * .02f).row()
        }
        table.add(languageCarousel).padBottom(Gdx.graphics.height * .04f).row()
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
        textButton.addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                textButton.touchable = Touchable.disabled
                setMenuScreen()
            }
            false
        }
        GameUtils.addTextButtonEnterExitEffect(textButton)
        return textButton
    }

    private fun setMenuScreen() {
        BaseGame.clickSound!!.play(BaseGame.soundVolume)
        BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { BaseGame.setActiveScreen(MenuScreen(playMusic = false)) }
        ))
    }
}
