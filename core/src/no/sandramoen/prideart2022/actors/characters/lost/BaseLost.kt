package no.sandramoen.prideart2022.actors.characters.lost

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.characters.player.BeamOut
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.whiteCrystalExplosion
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

open class BaseLost(x: Float, y: Float, stage: Stage, val player: Player? = null) : BaseActor(x, y, stage) {
    private var movementSpeed = 26f * .1f
    private var shaderProgram: ShaderProgram
    private var time = 0f
    lateinit var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    var isPickedUp = false

    init {
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.glowShader)
        addAction(shiverAnimation())
        val group = GameUtils.statementLabel(
            0f,
            0 * 211112f,
            "lost",
            2,
            1f,
            Color(0.643f, 0.867f, 0.859f, 1f),
            BaseGame.smallLabelStyle)
        group.setPosition(-2f, 4f)
        addActor(group)

        if (player != null) {
            movementSpeed = player.originalMovementSpeed * .1f
            setAcceleration(movementSpeed * 10f)
            setMaxSpeed(movementSpeed)
            setDeceleration(movementSpeed * 10f)
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt

        if (player != null && isWithinDistance2(50f, player)) {
            accelerateAtAngle(getAngleTowardActor(player))
            applyPhysics(dt)
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

    open fun initializeIdleAnimation(number: Int) {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        for (i in 1..20)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("lost/lost$number/idle1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("lost/lost$number/idle2"))
        idleAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()
        setAnimation(idleAnimation)

        setBoundaryRectangle()
    }

    fun pickup() {
        isCollisionEnabled = false
        isPickedUp = true
        BaseGame.lostPickupSound!!.play(BaseGame.soundVolume)
        addAction(removeAnimation())
        crystalExplosionEffect()
    }

    private fun shiverAnimation(): RepeatAction? {
        val amountX = .1f
        val duration = .04f
        return Actions.forever(Actions.sequence(
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.delay(1f)
        ))
    }

    private fun removeAnimation(): SequenceAction? {
        return Actions.sequence(
            Actions.run { BeamOut(x, y, stage, this) },
            stretchAndMoveOut(),
            Actions.removeActor()
        )
    }

    private fun stretchAndMoveOut(): ParallelAction? {
        return Actions.parallel(
            Actions.scaleTo(.1f, 3f, BeamOut.animationDuration),
            Actions.moveBy(0f, 100f, BeamOut.animationDuration, Interpolation.circleIn),
            Actions.fadeOut(BeamOut.animationDuration, Interpolation.circleIn)
        )
    }

    private fun crystalExplosionEffect() {
        val effect = whiteCrystalExplosion()
        effect.setScale(.03f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram!!.setUniformf("u_time", time * .25f)
        shaderProgram!!.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram!!.setUniformf("u_glowRadius", 20f)
        super.draw(batch, parentAlpha)
        batch.shader = null
    }
}