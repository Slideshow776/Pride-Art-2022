package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.characters.player.BeamIn
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.ShieldExplosion
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class ShieldDrop(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f

    init {
        loadImage("shieldDrop")
        setOrigin(Align.center)
        BeamIn(x, y, stage, this)
        initializeGroundCrack()
        initializeAnimation()
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.glowShader)
    }

    fun pickup() {
        removeAnimation()
        isCollisionEnabled = false
        rainbowExplosionEffect()
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
        shaderProgram!!.setUniformf("u_time", time * .5f)
        shaderProgram!!.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram!!.setUniformf("u_glowRadius", 1f)
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun removeAnimation() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.scaleTo(0f, 1f, .25f),
                Actions.run { remove() }
            ))
    }

    private fun initializeGroundCrack() {
        val groundCrack = GroundCrack(x, y, stage, 10f)
        groundCrack.centerAtActor(this)
        groundCrack.zIndex = player.zIndex - 1
    }

    private fun rainbowExplosionEffect() {
        val effect = ShieldExplosion()
        effect.setScale(.03f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun initializeAnimation() {
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.8f, 1.8f, .125f),
                    Actions.scaleTo(1.5f, 1.5f, .75f)
                )
            )
        )
    }
}
