package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import no.sandramoen.prideart2022.actors.particles.blueCrystalExplosion
import no.sandramoen.prideart2022.actors.particles.pinkCrystalExplosion
import no.sandramoen.prideart2022.actors.particles.whiteCrystalExplosion
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Crystal(x: Float, y: Float, stage: Stage, val color: String) : BaseActor(x, y, stage) {
    private var shaderProgram: ShaderProgram
    private var time = 0f

    var isPickedUp = false

    init {
        when (color) {
            "white" -> loadImage("whiteCrystal")
            "pink" -> loadImage("pinkCrystal")
            "blue" -> loadImage("blueCrystal")
            else -> Gdx.app.error(
                javaClass.canonicalName,
                "Error creating crystal, color was: $color. (acceptable colors are: 'white', 'pink' and 'blue)"
            )
        }

        animation()
        setBoundaryRectangle()
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.glowShader)
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

    fun pickup() {
        isCollisionEnabled = false
        isPickedUp = true
        BaseGame.crystalPickupSound!!.play(BaseGame.soundVolume)
        addAction(removeAnimation())
        crystalExplosionEffect()
    }

    private fun removeAnimation(): SequenceAction? {
        return Actions.sequence(
            Actions.parallel(
                Actions.color(Color.BLACK, .25f),
                Actions.scaleTo(.1f, 1f, .25f)
            ),
            Actions.removeActor()
        )
    }

    private fun crystalExplosionEffect() {
        val effect = when (color) {
            "white" -> whiteCrystalExplosion()
            "pink" -> pinkCrystalExplosion()
            else -> blueCrystalExplosion()
        }
        effect.setScale(.03f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram!!.setUniformf("u_time", time * .25f)
        shaderProgram!!.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram!!.setUniformf("u_glowRadius", 1f)
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun animation() {
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.moveBy(0f, 1f, 1f),
                    Actions.moveBy(0f, -1f, 2f)
                )
            )
        )
    }
}
