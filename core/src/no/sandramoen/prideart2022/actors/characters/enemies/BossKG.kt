package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.BigExplosion
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.BeamChargeEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class BossKG(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>

    private val movementSpeed = player.originalMovementSpeed * .2f
    private var state = State.RunningN

    var isDying = false

    init {
        loadAnimation()
        centerAtPosition(x, y)

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        fadeIn()

        shakyCamIntensity = 1f
        addActor(GameUtils.statementLabel(width, height, "guldbrandsen", 4, 2f))

        addActor(Tentacle(width * .95f, height * .5f, stage, this))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (isDying || pause) return

        accelerateAtAngle(getAngleTowardActor(player))
        setAnimationDirection()
        applyPhysics(dt)
    }

    override fun death() {
        super.death()
        BaseGame.beamChargeSound!!.stop()
        isDying = true
        isCollisionEnabled = false
        clearActions()
        for (i in 0 until 8)
            BigExplosion(this, stage)
        isShakyCam = true
        shakyCamIntensity *= 4f
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeOut(1f),
                cardboardFlipSpin()
            ),
            Actions.run {
                Remains(stage, player)
                isShakyCam = false
                remove()
            }
        ))
    }

    private fun fadeIn() {
        isShakyCam = true
        color.a = 0f
        addAction(
            Actions.sequence(
                Actions.fadeIn(.25f),
                Actions.alpha(.9f, .5f),
                Actions.delay(.5f),
                Actions.run { isShakyCam = false }
            )
        )
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runS2"))
        runAnimationS = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        setAnimation(runAnimationN)
    }

    private enum class State {
        RunningN, RunningS
    }

    private fun cardboardFlipSpin(): SequenceAction {
        val duration = .1f
        return Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(.025f, 1f, duration),
                Actions.color(Color.BLACK, duration)
            ),
            Actions.run { flip() },
            Actions.parallel(
                Actions.scaleTo(1f, 1f, duration),
                Actions.color(Color.WHITE, duration)
            ),
            Actions.run { flip() },
            Actions.parallel(
                Actions.scaleTo(.025f, 1f, duration),
                Actions.color(Color.BLACK, duration)
            ),
            Actions.run { flip() },
            Actions.parallel(
                Actions.scaleTo(1f, 1f, duration),
                Actions.color(Color.WHITE, duration)
            ),
            Actions.run { flip() },
            Actions.parallel(
                Actions.scaleTo(.025f, 1f, duration),
                Actions.color(Color.BLACK, duration)
            ),
            Actions.run { flip() },
            Actions.parallel(
                Actions.scaleTo(1f, 1f, duration),
                Actions.color(Color.WHITE, duration)
            ),
            Actions.run { flip() },
            Actions.parallel(
                Actions.scaleTo(.025f, 1f, duration),
                Actions.color(Color.BLACK, duration)
            ),
            Actions.run { flip() }
        )
    }

    private fun setAnimationDirection() {
        if (getMotionAngle() in 45.0..135.0 && state != State.RunningN) {
            state = State.RunningN
            setAnimation(runAnimationN)
        } else if (getMotionAngle() !in 45.0..135.0 && state != State.RunningS) {
            state = State.RunningS
            setAnimation(runAnimationS)
        }
    }
}
