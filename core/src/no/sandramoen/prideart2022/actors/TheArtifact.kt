package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import no.sandramoen.prideart2022.actors.particles.ExplosionArtifactEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class TheArtifact(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f

    init {
        loadImage("theArtifact")
        centerAtPosition(x, y)
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.glowShader)
        startValueAnimation()
        addEffects()

        addAction(Actions.sequence(
            Actions.delay(.75f),
            Actions.run { BaseGame.demonWhispersSound!!.play(BaseGame.soundVolume * .8f) }
        ))
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
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

    override fun death() {
        super.death()
        BaseGame.artifactPickUpSound!!.play(BaseGame.soundVolume)
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 0f, .5f),
            Actions.removeActor()
        ))

    }

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram.setUniformf("u_time", time * 1f)
        shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram.setUniformf("u_glowRadius", 1f)
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun addEffects() {
        val effect = ExplosionArtifactEffect()
        effect.setScale(.05f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun startValueAnimation() {
        addAction(
            Actions.forever(
                Actions.parallel(
                    pulseAnimation(),
                    rotateAnimation()
                )
            )
        )
    }

    private fun pulseAnimation(): SequenceAction? {
        return Actions.sequence(
            Actions.scaleTo(1.025f, 1.025f, 2f),
            Actions.scaleTo(.975f, .975f, 2f)
        )
    }

    private fun rotateAnimation(): SequenceAction? {
        return Actions.sequence(
            Actions.rotateBy(1f, 1f),
            Actions.rotateBy(-2f, 2f),
            Actions.rotateBy(1f, 1f)
        )
    }
}
