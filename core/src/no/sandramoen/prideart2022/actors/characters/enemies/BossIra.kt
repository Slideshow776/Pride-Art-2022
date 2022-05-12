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
import no.sandramoen.prideart2022.actors.characters.lost.BaseLost
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.BeamChargeEffect
import no.sandramoen.prideart2022.actors.particles.BloodSplatterEffect
import no.sandramoen.prideart2022.actors.particles.HeartExplosion
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class BossIra(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private lateinit var runSAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runNAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var eatAnimation: Animation<TextureAtlas.AtlasRegion>

    private val movementSpeed = player.originalMovementSpeed * .2f
    private var state = State.RunningN
    private var beam: BossBeam? = null
    private var beamActor = BaseActor(0f, 0f, stage)
    private var isShootingBeam = false

    var lost: BaseLost? = null
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
        addActor(GameUtils.statementLabel(width, height, "ira", 13, 2f))

        shootCircleShot()
        shootSpiderWeb()
        shootBeam()
        shootChains()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (isDying || pause) return

        decidePlayerOrLost()

        if (beam != null)
            beam!!.centerAtActor(this)

        setAnimationDirection()
        applyPhysics(dt)
    }

    override fun death() {
        super.death()
        beamActor.clearActions()
        BaseGame.beamChargeSound!!.stop()
        isDying = true
        isCollisionEnabled = false
        clearActions()
        for (i in 0 until 5)
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

    private fun shootSpiderWeb() {
        BaseActor(0f, 0f, stage).addAction(Actions.forever(Actions.sequence(
            Actions.delay(5f),
            Actions.run {
                if (!isDying)
                    SpiderWebShot(x, y, stage, player)
            }
        )))
    }

    private fun decidePlayerOrLost() {
        if (count(stage, BaseLost::class.java.canonicalName) <= 0)
            lost = null

        if (lost != null) {
            accelerateAtAngle(getAngleTowardActor(lost!!))
            if (state != State.Eat) {
                state = State.Eat
                setAnimation(eatAnimation)
            }

            if (overlaps(lost!!)) {
                bloodSplatterEffect()
                addAction(Actions.sequence(
                    Actions.delay(1f),
                    Actions.run { state = State.RunningN }
                ))
                lost!!.death()
                lost = null
            }
        } else {
            accelerateAtAngle(getAngleTowardActor(player))
            if (!isWithinDistance2(50f, player) && !isShootingBeam)
                teleportToPlayer()

            if (state == State.Eat) {
                state = State.RunningN
            }
        }
    }

    private fun bloodSplatterEffect() {
        val effect = BloodSplatterEffect()
        effect.setScale(.025f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun shootBeam() {
        beamActor.addAction(Actions.forever(Actions.sequence(
            Actions.delay(5f),
            Actions.run { beamCharge() },
            Actions.delay(2.5f),
            Actions.run { beam() },
            Actions.delay(3f),
            Actions.run { isShootingBeam = false }
        )))
    }

    private fun beamCharge() {
        if (!isDying) {
            isShootingBeam = true
            BaseGame.beamChargeSound!!.play(BaseGame.soundVolume)
            val effect = BeamChargeEffect()
            effect.setScale(.05f)
            effect.centerAtActor(this)
            stage.addActor(effect)
            effect.start()
        }
    }

    private fun beam() {
        if (!isDying)
            beam = BossBeam(width / 2, height / 2, stage, getAngleTowardActor(player) - 90f)
    }

    private fun shootCircleShot() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(2f),
            Actions.run { circleShot(8) }
        )))
    }

    private fun circleShot(numShots: Int) {
        if (!isDying) {
            BaseGame.enemyShootSound!!.play(BaseGame.soundVolume)
            for (i in 0 until numShots)
                Shot(
                    x + width / 2,
                    y + height / 2,
                    stage,
                    i.toFloat() * (360 / numShots),
                    player.originalMovementSpeed
                )
        }
    }

    private fun shootChains() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(8f),
            Actions.run { chains(8) }
        )))
    }

    private fun chains(numChains: Int) {
        if (!isDying) {
            BaseGame.chainSound!!.play(BaseGame.soundVolume)
            for (i in 0 until numChains)
                Chain(
                    x + width / 2,
                    y + height / 2,
                    stage,
                    this,
                    i.toFloat() * (360 / numChains)
                )
        }
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

    private enum class State { RunningN, RunningS, Eat }

    private fun teleportToPlayer() {
        TeleportHazard(this, stage, player)
        val distance = 25f
        if (MathUtils.randomBoolean()) { // horizontal
            x = MathUtils.random(player.x - distance, player.x + distance)
            if (MathUtils.randomBoolean())
                y = player.y + distance
            else
                y = player.y - distance
        } else { // vertical
            if (MathUtils.randomBoolean())
                x = player.x + distance
            else
                x = player.x - distance
            y = MathUtils.random(player.y - distance, player.y + distance)
        }
        TeleportHazard(this, stage, player)
        /*shootBeam()*/
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
        if (getMotionAngle() in 45.0..135.0 && state != State.RunningN && state != State.Eat) {
            state = State.RunningN
            setAnimation(runNAnimation)
        } else if (getMotionAngle() !in 45.0..135.0 && state != State.RunningS && state != State.Eat) {
            state = State.RunningS
            setAnimation(runSAnimation)
        }
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/runN2"))
        runNAnimation = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/runS2"))
        runSAnimation = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/eat1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/eat2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/eat2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossIra/eat2"))
        eatAnimation = Animation(.125f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        setAnimation(runNAnimation)
    }
}
