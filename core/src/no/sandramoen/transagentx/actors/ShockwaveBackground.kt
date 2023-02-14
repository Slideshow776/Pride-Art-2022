package no.sandramoen.transagentx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.GameUtils.Companion.initShaderProgram
import no.sandramoen.transagentx.utils.GameUtils.Companion.isTouchDownEvent

class ShockwaveBackground(texturePath: String, s: Stage) : BaseActor(0f, 0f, s) {
    var shaderProgram: ShaderProgram

    private var time = .0f
    private val animationDelay = 1f
    private var shockWavePositionX = -5.0f
    private var shockWavePositionY = -5.0f

    init {
        if (texturePath.isNotBlank()) loadTexture(texturePath)
        else Gdx.app.error(javaClass.simpleName, "texturePath is blank!")

        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shaderProgram = initShaderProgram(BaseGame.defaultShader, BaseGame.shockwaveShader)

        addListener { e: Event ->
            if (isTouchDownEvent(e)) {
                val xNormalized = (Gdx.input.x.toFloat()) / (Gdx.graphics.width)
                val yNormalized = (Gdx.input.y.toFloat()) / (Gdx.graphics.height)
                start(xNormalized, yNormalized)
            }
            false
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (BaseGame.isCustomShadersEnabled) {
            try {
                drawWithShader(batch, parentAlpha)
            } catch (error: Throwable) {
                super.draw(batch, parentAlpha)
            }
        } else {
            super.draw(batch, parentAlpha)
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram.setUniformf("u_time", time)
        shaderProgram.setUniformf("u_center", Vector2(shockWavePositionX, shockWavePositionY))
        shaderProgram.setUniformf("u_shockParams", Vector3(10f, .8f, .1f))
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun start(normalizedPosX: Float, normalizedPosY: Float) {
        if (time >= animationDelay) { // prevents interrupting previous animation
            this.shockWavePositionX = normalizedPosX
            this.shockWavePositionY = normalizedPosY
            time = 0f
        }
    }
}
