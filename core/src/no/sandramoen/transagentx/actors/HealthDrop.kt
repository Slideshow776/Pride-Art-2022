package no.sandramoen.transagentx.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.transagentx.actors.characters.player.BeamIn
import no.sandramoen.transagentx.actors.characters.player.Player
import no.sandramoen.transagentx.actors.particles.HeartExplosion
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.GameUtils

class HealthDrop(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f

    init {
        loadImage("heart")
        setOrigin(Align.center)
        BeamIn(x, y, stage, this)
        initializeGroundCrack()
        initializeAnimation()
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.glowShader)
    }

    fun pickup(isSuccess: Boolean) {
        if (isSuccess)
            BaseGame.healthPickupSuccessSound!!.play(
                BaseGame.soundVolume,
                MathUtils.random(.97f, 1.03f),
                0f
            )
        else
            BaseGame.healthPickupFailSound!!.play(
                BaseGame.soundVolume,
                MathUtils.random(.97f, 1.03f),
                0f
            )
        heartExplosionEffect()
        removeAnimation()
        isCollisionEnabled = false
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

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram.setUniformf("u_time", time * .5f)
        shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram.setUniformf("u_glowRadius", 1f)
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun removeAnimation() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.scaleTo(.1f, 1f, .25f),
                Actions.run { remove() }
            ))
    }

    private fun heartExplosionEffect() {
        val effect = HeartExplosion()
        effect.setScale(.05f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun initializeGroundCrack() {
        val groundCrack = GroundCrack(x, y, stage, 10f)
        groundCrack.centerAtActor(this)
        groundCrack.zIndex = player.zIndex - 1
    }

    private fun initializeAnimation() {
        addAction(
            Actions.forever(
                Actions.parallel(
                    Actions.sequence(
                        Actions.rotateTo(10f, .4375f),
                        Actions.rotateTo(-10f, .4375f)
                    ),
                    Actions.sequence(
                        Actions.scaleTo(1.8f, 1.8f, .125f),
                        Actions.scaleTo(1.5f, 1.5f, .75f)
                    )
                )
            )
        )
    }
}
