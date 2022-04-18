package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.Explosion
import no.sandramoen.prideart2022.actors.particles.GhostSprinklesEffect
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Charger(x: Float, y: Float, stage: Stage, player: Player) :
    BaseActor(x, y, stage) {
    private val player = player

    private val chargeDistance = 10f
    private val movementSpeed = player.movementSpeed * .72f
    private val chargeDuration = .5f
    private val chargeSpeed = 40f

    private var isStoppingToCharge = false
    private var isCharging = false
    private var angleToCharge = -1f
    private var dying = false

    private lateinit var sprinkles: RepeatAction
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private var state = State.RunningN

    init {
        loadAnimation()
        centerAtPosition(x, y)

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        color.a = 0f
        addAction(
            Actions.sequence(
                Actions.fadeIn(.25f),
                Actions.alpha(.9f, .5f)
            )
        )
        // GameUtils.pulseWidget(this)
        addSprinkles()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (dying || pause) return

        if (!isWithinDistance(chargeDistance, player) && !isStoppingToCharge) {
            accelerateAtAngle(getAngleTowardActor(player))
        } else if (!isStoppingToCharge) {
            stopToCharge()
        } else if (isCharging) {
            accelerateAtAngle(angleToCharge)
        }

        setAnimationDirection()
        applyPhysics(dt)
    }

    private fun stopToCharge() {
        isStoppingToCharge = true
        angleToCharge = getAngleTowardActor(player) + MathUtils.random(-5f, 5f)
        BaseGame.enemyChargeUpSound!!.play(BaseGame.soundVolume)
        addAction(
            Actions.sequence(
                stoppingToChargeAnimation(),
                Actions.run {
                    isCharging = true
                    setAcceleration(chargeSpeed * 10)
                    setMaxSpeed(chargeSpeed)
                    setDeceleration(chargeSpeed * 10)
                    BaseGame.enemyChargeSound!!.play(BaseGame.soundVolume)
                },
                resetStoppingToChargeAnimation(),
                die()
            )
        )
    }

    private fun die(): SequenceAction? {
        return Actions.sequence(
            Actions.delay(chargeDuration),
            Actions.run {
                dying = true
                isCollisionEnabled = false
                removeAction(sprinkles)
                BaseGame.enemyDeathSound!!.play(BaseGame.soundVolume)
                Explosion(this, stage)
                isShakyCam = true
                shakyCamIntensity *= .125f
            },
            Actions.parallel(
                Actions.fadeOut(1f),
                cardboardFlipSpin()
            ),
            Actions.run {
                isShakyCam = false
                Experience(x + width / 2, y + height / 2, stage, 1)
                BaseGame.enemyDeathSound!!.play(BaseGame.soundVolume, .8f, 0f)
                Remains(stage, player)
                remove()
            }
        )
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

    private fun stoppingToChargeAnimation(): ParallelAction? {
        return Actions.parallel(
            Actions.scaleBy(.3f, -.3f, 1f),
            Actions.color(Color(0.875f, 0.518f, 0.647f, 1f), 1f)
        )
    }

    private fun resetStoppingToChargeAnimation(): ParallelAction? {
        return Actions.parallel(
            Actions.scaleTo(1f, 1f, .25f, Interpolation.bounceOut),
            Actions.color(Color.WHITE, .25f)
        )
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/charger/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/charger/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/charger/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/charger/runS2"))
        runAnimationS = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        setAnimation(runAnimationN)
    }

    private enum class State {
        RunningN, RunningS
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

    private fun addSprinkles() {
        sprinkles = Actions.forever(Actions.sequence(
            Actions.delay(.1f),
            Actions.run {
                val effect = GhostSprinklesEffect()
                effect.setScale(.01f)
                effect.centerAtActor(this)
                stage.addActor(effect)
                effect.start()
            }
        ))
        addAction(sprinkles)
    }
}
