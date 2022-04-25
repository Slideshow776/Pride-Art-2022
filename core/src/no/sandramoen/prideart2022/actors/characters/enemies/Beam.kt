package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Beam(x: Float, y: Float, stage: Stage, angleTowardsPlayer: Float) :
    BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f

    init {
        BaseGame.spaceStationBeamSound!!.play(BaseGame.soundVolume)
        loadImage("enemies/beam")
        setScale(.1f, 1f)
        /*color = Color(0.745f, 0.467f, 0.169f, 1f)*/
        setOrigin(Align.bottom)

        rotation = angleTowardsPlayer
        scaleTo()
        removeWithDelay()

        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.waveShader)
    }

    private fun scaleTo() {
        addAction(
            Actions.sequence(
                Actions.scaleTo(.6f, 100f, 1.5f)
            )
        )
    }

    private fun removeWithDelay() {
        addAction(
            Actions.sequence(
                Actions.delay(3f),
                Actions.run { remove() }
            )
        )
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
        shaderProgram.setUniformf("u_amplitude", Vector2(.1f, .005f))
        shaderProgram.setUniformf("u_wavelength", Vector2(.1f, .01f))
        shaderProgram.setUniformf("u_velocity", Vector2(.3f, 0f))
        super.draw(batch, parentAlpha)
        batch.shader = null
    }
}
