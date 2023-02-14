package no.sandramoen.transagentx.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.transagentx.actors.characters.player.BeamIn
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.GameUtils

class GroundCrack(x: Float, y: Float, stage: Stage, private val glowDuration: Float = 5f) : BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f
    private var enableShader = true

    init {
        loadImage("groundCrack")
        rotation = MathUtils.random(-20f, 20f)

        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.glowShader)

        color = Color(0.875f, 0.518f, 0.647f, 0f) // pink
        animation()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (enableShader)
            time += dt
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (BaseGame.isCustomShadersEnabled && enableShader) {
            try {
                drawWithShader(batch, parentAlpha)
            } catch (error: Throwable) {
                super.draw(batch, parentAlpha)
            }
        } else {
            super.draw(batch, parentAlpha)
        }
    }

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram.setUniformf("u_time", time * .5f)
        shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram.setUniformf("u_glowRadius", 1f)
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun animation() {
        addAction(
            Actions.sequence(
                Actions.delay(BeamIn.animationDuration),
                Actions.run { Explosion(this, stage) },
                Actions.fadeIn(.1f),
                Actions.delay(glowDuration),
                Actions.run { enableShader = false },
                Actions.color(Color.BLACK, 1f)
            )
        )
    }
}
