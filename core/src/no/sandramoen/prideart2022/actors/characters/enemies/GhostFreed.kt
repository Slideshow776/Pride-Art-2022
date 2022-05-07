package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class GhostFreed(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f

    init {
        loadImage("enemies/ghostFreed")
        setOrigin(Align.center)
        setScale(.5f)
        initializeAnimation()
        occasionallyFlip()
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.waveShader)
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
        shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram.setUniformf("u_amplitude", Vector2(.04f, .005f))
        shaderProgram.setUniformf("u_wavelength", Vector2(3f, .01f))
        shaderProgram.setUniformf("u_velocity", Vector2(10f, 0f))
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun initializeAnimation() {
        addAction(
            Actions.sequence(
                Actions.moveBy(0f, getWorldBounds().height, 5f),
                Actions.removeActor()
            )
        )
    }

    private fun occasionallyFlip() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(2f),
            Actions.run { flip() }
        )))
    }
}
