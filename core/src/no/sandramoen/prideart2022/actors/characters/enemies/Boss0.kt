package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.BigExplosion
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.BeamChargeEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Boss0(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>

    private val movementSpeed = player.originalMovementSpeed * .2f
    private var state = State.RunningN
    private var beam: Beam? = null

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

        shootMultiSho()
        shootCircleShot()
        shootBeam()

        shakyCamIntensity = 1f
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (isDying || pause) return

        accelerateAtAngle(getAngleTowardActor(player))
        setAnimationDirection()
        applyPhysics(dt)

        if (beam != null)
            beam!!.centerAtActor(this)
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

    private fun shootMultiSho() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(5f),
            Actions.run { multiShot(3) }
        )))
    }

    private fun shootCircleShot() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(1f),
            Actions.run { circleShot(9) }
        )))
    }

    private fun shootBeam() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(5f),
            Actions.run { beamCharge() },
            Actions.delay(2.5f),
            Actions.run { beam() }
        )))
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

    private fun circleShot(shots: Int) {
        BaseGame.enemyShootSound!!.play(BaseGame.soundVolume)
        val angleDisplacement = MathUtils.random(0f, 360f)
        for (i in 0 until shots)
            Shot(
                x + width / 2,
                y + height / 2,
                stage,
                i * 40f + angleDisplacement,
                player.originalMovementSpeed
            )
    }

    private fun multiShot(shots: Int) {
        BaseGame.enemyShootSound!!.play(BaseGame.soundVolume)
        val angleDisplacement = 10f
        val angleStart = getAngleTowardActor(player) - angleDisplacement
        for (i in 0 until shots)
            Shot(
                x + width / 2,
                y + height / 2,
                stage,
                angleStart + (i * angleDisplacement),
                player.originalMovementSpeed
            )
    }

    private fun beam() {
        beam = Beam(width / 2, height / 2, stage, getAngleTowardActor(player) - 90f)
    }

    private fun beamCharge() {
        BaseGame.beamChargeSound!!.play(BaseGame.soundVolume)

        val effect = BeamChargeEffect()
        effect.setScale(.05f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/boss0/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/boss0/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/boss0/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/boss0/runS2"))
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
