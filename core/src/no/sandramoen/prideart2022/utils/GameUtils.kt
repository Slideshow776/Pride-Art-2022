package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.rafaskoberg.gdx.typinglabel.TypingLabel

class GameUtils {
    companion object {

        fun isTouchDownEvent(event: Event): Boolean { // Custom type checker
            return event is InputEvent && event.type == InputEvent.Type.touchDown
        }

        fun saveGameState() {
            BaseGame.prefs!!.putBoolean("loadPersonalParameters", true)
            BaseGame.prefs!!.putFloat("musicVolume", BaseGame.musicVolume)
            BaseGame.prefs!!.putFloat("soundVolume", BaseGame.soundVolume)
            BaseGame.prefs!!.putString("locale", BaseGame.currentLocale)
            BaseGame.prefs!!.flush()
        }

        fun loadGameState() {
            BaseGame.prefs = Gdx.app.getPreferences("prideArt2022GameState")
            BaseGame.loadPersonalParameters = BaseGame.prefs!!.getBoolean("loadPersonalParameters")
            BaseGame.musicVolume = BaseGame.prefs!!.getFloat("musicVolume")
            BaseGame.soundVolume = BaseGame.prefs!!.getFloat("soundVolume")
            BaseGame.currentLocale = BaseGame.prefs!!.getString("locale")
        }

        fun initShaderProgram(vertexShader: String?, fragmentShader: String?): ShaderProgram {
            ShaderProgram.pedantic = false
            val shaderProgram = ShaderProgram(vertexShader, fragmentShader)
            if (!shaderProgram.isCompiled)
                Gdx.app.error(javaClass.simpleName, "Couldn't compile shader: " + shaderProgram.log)
            return shaderProgram
        }

        fun setMusicVolume(volume: Float) {
            if (volume > 1f || volume < 0f)
                Gdx.app.error(javaClass.simpleName, "Volume needs to be within [0-1]. Volume is: $volume")
            BaseGame.musicVolume = volume
            BaseGame.level1Music!!.volume = BaseGame.musicVolume
            BaseGame.menuMusic!!.volume = BaseGame.musicVolume
            BaseGame.menuMusic!!.volume = BaseGame.musicVolume
        }

        fun playAndLoopMusic(music: Music?, volume: Float = BaseGame.musicVolume) {
            music!!.play()
            music!!.volume = volume
            music!!.isLooping = true
        }

        fun addTextButtonEnterExitEffect(textButton: TextButton, enterColor: Color = BaseGame.lightPink, exitColor: Color = Color.WHITE) {
            textButton.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    textButton.label.addAction(Actions.color(enterColor, .125f))
                    BaseGame.hoverOverEnterSound!!.play(BaseGame.soundVolume)
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    textButton.label.addAction(Actions.color(exitColor, .125f))
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }

        fun addWidgetEnterExitEffect(widget: Widget, enterColor: Color = BaseGame.lightPink, exitColor: Color = Color.WHITE) {
            widget.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    widget.addAction(Actions.color(enterColor, .125f))
                    BaseGame.hoverOverEnterSound!!.play(BaseGame.soundVolume)
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    widget.addAction(Actions.color(exitColor, .125f))
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }

        fun normalizeValue(value: Float, min: Float, max: Float): Float { return (value - min) / (max - min) }

        fun pulseWidget(actor: Actor, lowestAlpha: Float = .7f, duration: Float = 1f) {
            actor.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(lowestAlpha, duration / 2),
                    Actions.alpha(1f, duration / 2)
            )))
        }

        fun statementLabel(
            width: Float,
            height: Float,
            statement: String = "statement",
            numStatements: Int = 106,
            scaleModifier: Float = 1f,
            color: Color = Color(0.816f, 0.855f, 0.569f, 1f),
            labelStyle: Label.LabelStyle? = BaseGame.spookySmallLabelStyle
        ): Group {
            val label = TypingLabel("{SPEED=50}${BaseGame.myBundle!!.get("$statement${MathUtils.random(0, numStatements)}")}", labelStyle)
            label.color = color
            label.setAlignment(Align.center)
            val defaultTokens = "{SICK=.4;.4;180}"
            label.defaultToken = defaultTokens

            val group = Group()
            group.addActor(label)
            group.setScale(.02f * scaleModifier)
            group.setPosition(width / 2 - label.prefWidth * .01f * scaleModifier, height)

            group.addAction(Actions.forever(Actions.sequence( // display random statement
                Actions.delay(5f),
                Actions.run {
                    group.isVisible = false
                    label.setText(BaseGame.myBundle!!.get("$statement${MathUtils.random(0, numStatements)}"))
                },
                Actions.delay(5f),
                Actions.run { group.isVisible = true }
            )))
            return group
        }
    }
}
