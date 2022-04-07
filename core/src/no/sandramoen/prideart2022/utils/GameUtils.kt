package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class GameUtils {
    companion object {

        fun isTouchDownEvent(event: Event): Boolean { // Custom type checker
            return event is InputEvent && event.type == InputEvent.Type.touchDown
        }

        fun initShaderProgram(vertexShader: String?, fragmentShader: String?): ShaderProgram {
            ShaderProgram.pedantic = false
            val shaderProgram = ShaderProgram(vertexShader, fragmentShader)
            if (!shaderProgram.isCompiled)
                Gdx.app.error(javaClass.simpleName, "Couldn't compile shader: " + shaderProgram.log)
            return shaderProgram
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
    }
}
