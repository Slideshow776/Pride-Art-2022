package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.Explosion
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.GhostSprinklesEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Follower(x: Float, y: Float, stage: Stage, player: Player) : BaseActor(x, y, stage) {
    private val player = player
    private var movementSpeed = player.originalMovementSpeed * .7f

    private var dying = false
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private lateinit var closeAnimationN: Animation<TextureAtlas.AtlasRegion>
    private lateinit var closeAnimationS: Animation<TextureAtlas.AtlasRegion>
    private var state = State.RunningN
    private var closeDistance = 20f
    private lateinit var sprinkles: RepeatAction

    init {
        loadAnimation()
        centerAtPosition(x, y)

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        fadeIn()
        addSprinkles()
        dieAfterDuration()
        addActor(GameUtils.statementLabel(width, height))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (dying || pause) return

        accelerateAtAngle(getAngleTowardActor(player))
        applyPhysics(dt)
        setAnimationDirection()
    }

    private fun dieAfterDuration() {
        addAction(Actions.sequence(
            Actions.delay(30f),
            Actions.run { death() }
        ))
    }

    private fun fadeIn() {
        color.a = 0f
        addAction(
            Actions.sequence(
                Actions.fadeIn(.25f),
                Actions.alpha(.9f, .5f)
            )
        )
    }

    override fun death() {
        super.death()
        dying = true
        isCollisionEnabled = false
        clearActions()
        Explosion(this, stage)
        isShakyCam = true
        shakyCamIntensity *= .125f
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeOut(1f),
                cardboardFlipSpin()
            ),
            Actions.run {
                Experience(x + width / 2, y + height / 2, stage, 1)
                Remains(stage, player)
                isShakyCam = false
                remove()
            }
        ))
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

    private enum class State {
        RunningN, RunningS, CloseN, CloseS
    }

    private fun setAnimationDirection() {
        if (isWithinDistance2(closeDistance, player)) {
            if (getMotionAngle() in 45.0..135.0 && state != State.CloseN) {
                state = State.CloseN
                setAnimation(closeAnimationN)
            } else if (getMotionAngle() !in 45.0..135.0 && state != State.CloseS) {
                state = State.CloseS
                setAnimation(closeAnimationS)
            }
            movementSpeed = player.originalMovementSpeed * .62f
        } else {
            if (getMotionAngle() in 45.0..135.0 && state != State.RunningN) {
                state = State.RunningN
                setAnimation(runAnimationN)
            } else if (getMotionAngle() !in 45.0..135.0 && state != State.RunningS) {
                state = State.RunningS
                setAnimation(runAnimationS)
            }

            if (isWithinDistance2(closeDistance * 1.5f, player))
                movementSpeed = player.originalMovementSpeed * .52f
            else
                movementSpeed = player.originalMovementSpeed * .8f
        }

        setMaxSpeed(movementSpeed)
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/runS2"))
        runAnimationS = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/closeN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/closeN2"))
        closeAnimationN = Animation(.4f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/closeS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/follower/closeS2"))
        closeAnimationS = Animation(.4f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        setAnimation(runAnimationN)
    }
}