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
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.Explosion
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.BloodBeamEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Beamer(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private lateinit var shootingAnimation: Animation<TextureAtlas.AtlasRegion>

    private val movementSpeed = player.originalMovementSpeed * .7f
    private val shootDistance = 20f
    private var shotsUntilDeath = 3
    private var dying = false
    private var isStoppedToShoot = false
    private var beam: EnemyBeam? = null
    private var state = State.RunningN
    private val beamDuration = 5f

    init {
        loadAnimation()
        centerAtPosition(x, y)

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        fadeIn()
        addActor(GameUtils.statementLabel(width, height))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (dying || pause) return

        if (!isWithinDistance(shootDistance, player) && !isStoppedToShoot) {
            accelerateAtAngle(getAngleTowardActor(player))
            applyPhysics(dt)
        } else if (!isStoppedToShoot) {
            isStoppedToShoot = true
            shoot()
            setAnimation(shootingAnimation)
            startEffect()
        }

        setAnimationDirection()
        if (shotsUntilDeath == 0 && !dying)
            die()


        if (beam != null)
            beam!!.centerAtActor(this)
    }

    private fun startEffect() {
        val effect = BloodBeamEffect()
        effect.setScale(.02f)
        effect.setPosition(x + width / 2, y + height / 5)
        stage.addActor(effect)
        effect.start()
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

    private fun shoot() {
        val angle = getAngleTowardActor(player) - 90f
        addAction(
            Actions.sequence(
                Actions.run {
                    BaseGame.beamCharge2Sound!!.play(
                        BaseGame.soundVolume * .6f,
                        MathUtils.random(.8f, 1.1f), 0f
                    )
                },
                Actions.delay(1f),
                Actions.run { beam = EnemyBeam(width / 2, height / 2, stage, angle, beamDuration) },
                Actions.delay(beamDuration),
                Actions.run { die() }
            )
        )
    }

    private fun die() {
        dying = true
        isCollisionEnabled = false
        clearActions()
        Explosion(this, stage)
        isShakyCam = true
        shakyCamIntensity *= .125f
        addAction(
            Actions.sequence(
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

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/beamer/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/beamer/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/beamer/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/beamer/runS2"))
        runAnimationS = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/beamer/shooting1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/beamer/shooting2"))
        shootingAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        setAnimation(runAnimationN)
    }
}
