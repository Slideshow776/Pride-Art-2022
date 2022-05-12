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
import no.sandramoen.prideart2022.actors.particles.BloodBeamEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class BossKG(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>
    private lateinit var screamAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var tentacle0: Tentacle
    private lateinit var tentacle1: Tentacle
    private lateinit var tentacle2: Tentacle
    private lateinit var tentacle3: Tentacle
    private lateinit var tentacle4: Tentacle
    private lateinit var tentacle5: Tentacle

    private val movementSpeed = player.originalMovementSpeed * .27f
    private var state = State.RunningN
    private var bloodScreamEffect: BloodBeamEffect? = null

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
        addActor(GameUtils.statementLabel(width, height, "guldbrandsen", 8, 2f))

        initializeTentacles()
        shootCircleShot()
        scream()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (isDying || pause) return

        accelerateAtAngle(getAngleTowardActor(player))
        setAnimationDirection()
        applyPhysics(dt)

        setTentaclePositions()

        if (!isWithinDistance2(48f, player))
            teleportToPlayer()

        bloodScreamEffect?.setPosition(x + width / 2, y + height * 4/5)
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
                tentacle0.remove()
                tentacle1.remove()
                tentacle2.remove()
                tentacle3.remove()
                tentacle4.remove()
                tentacle5.remove()
            }
        ))
    }

    private fun scream() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(4f),
            Actions.run {
                state = State.Screaming
                setAnimation(screamAnimation)
                BaseGame.scream2Sound!!.play(BaseGame.soundVolume * .7f)
                startEffect()
            },
            Actions.delay(1.5f),
            Actions.run {
                state = State.RunningN
                bloodScreamEffect!!.stop()
            }
        )))
    }

    private fun startEffect() {
        bloodScreamEffect = BloodBeamEffect()
        bloodScreamEffect!!.setScale(.02f)
        stage.addActor(bloodScreamEffect)
        bloodScreamEffect!!.start()
    }

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

    private enum class State {
        RunningN, RunningS, Screaming
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
        if (getMotionAngle() in 45.0..135.0 && state != State.RunningN && state != State.Screaming) {
            state = State.RunningN
            setAnimation(runAnimationN)
        } else if (getMotionAngle() !in 45.0..135.0 && state != State.RunningS && state != State.Screaming) {
            state = State.RunningS
            setAnimation(runAnimationS)
        }
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/runS2"))
        runAnimationS = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/scream1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/bossKG/scream2"))
        screamAnimation = Animation(.025f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        setAnimation(runAnimationN)
    }

    private fun shootCircleShot() {
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(.8f),
            Actions.run { circleShot(10) }
        )))
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

    private fun setTentaclePositions() {
        tentacle0.setPosition(x + width / 2 - tentacle0.width / 2, y + height / 2)
        tentacle1.setPosition(x + width / 2 - tentacle0.width / 2, y + height / 2)
        tentacle2.setPosition(x + width / 2 - tentacle0.width / 2, y + height / 2)
        tentacle3.setPosition(x + width / 2 - tentacle0.width / 2, y + height / 2)
        tentacle4.setPosition(x + width / 2 - tentacle0.width / 2, y + height / 2)
        tentacle5.setPosition(x + width / 2 - tentacle0.width / 2, y + height / 2)
    }

    private fun initializeTentacles() {
        tentacle0 = Tentacle(stage, this, player)
        tentacle1 = Tentacle(stage, this, player)
        tentacle2 = Tentacle(stage, this, player)
        tentacle3 = Tentacle(stage, this, player)
        tentacle4 = Tentacle(stage, this, player)
        tentacle5 = Tentacle(stage, this, player)

        addAction(Actions.forever(Actions.sequence(
            Actions.delay(5f),
            Actions.run { tentacle0.attack() }
        )))
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(7f),
            Actions.run { tentacle1.attack() }
        )))
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(11f),
            Actions.run { tentacle2.attack() }
        )))
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(13f),
            Actions.run { tentacle3.attack() }
        )))
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(17f),
            Actions.run { tentacle4.attack() }
        )))
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(19f),
            Actions.run { tentacle5.attack() }
        )))
    }
}
