package no.sandramoen.prideart2022.actors.enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.Explosion
import no.sandramoen.prideart2022.actors.player.Player
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Shooter(x: Float, y: Float, stage: Stage, player: Player) : BaseActor(x, y, stage) {
    private val player = player

    private val movementSpeed = 10f
    private val shootDistance = 20f
    private var shotsUntilDeath = 3
    private var shootFrequency = 5f

    private var dying = false
    private var isStoppedToShoot = false
    private lateinit var runAnimationN: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runAnimationS: Animation<TextureAtlas.AtlasRegion>
    private lateinit var shootingAnimation: Animation<TextureAtlas.AtlasRegion>
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
        }

        setAnimationDirection()
        if (shotsUntilDeath == 0 && !dying)
            die()
    }

    private fun shoot() {
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.run {
                        Shot(x + width / 2, y + height / 2, stage, 0f)
                        Shot(x + width / 2, y + height / 2, stage, 90f)
                        Shot(x + width / 2, y + height / 2, stage, 180f)
                        Shot(x + width / 2, y + height / 2, stage, 270f)
                        shotsUntilDeath--
                        BaseGame.enemyShootSound!!.play(BaseGame.soundVolume)
                    },
                    Actions.delay(shootFrequency)
                )
            )
        )
    }

    private fun die() {
        dying = true
        isCollisionEnabled = false
        clearActions()
        Explosion(this, stage)
        addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run {
                Experience(x + width / 2, y + height / 2, stage, 1)
                Remains(x, y, stage, this)
                remove()
            }
        ))
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

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/runN2"))
        runAnimationN = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/runS2"))
        runAnimationS = Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/shooting1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/shooting2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/shooting3"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/shooting4"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/shooter/shooting5"))
        shootingAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        setAnimation(runAnimationN)
    }
}