package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor

class Enemy(x: Float, y: Float, stage: Stage, player: Player) : BaseActor(x, y, stage) {
    private val player = player

    private val chargeDistance = 10f
    private val movementSpeed = 18f
    private val chargeDuration = .5f
    private val chargeSpeed = 40f

    private var isStoppingToCharge = false
    private var isCharging = false
    private var angleToCharge = -1f
    private var dying = false

    init {
        loadImage("ghost")
        setSize(4f, 4f)
        color = Color.RED
        debug = true

        setAcceleration(200f)
        setMaxSpeed(movementSpeed)
        setDeceleration(200f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        color.a = 0f
        addAction(Actions.sequence(Actions.fadeIn(.25f)))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (dying || pause) return

        if (!isWithinDistance(chargeDistance, player) && !isStoppingToCharge) {
            accelerateAtAngle(getAngleTowardPlayer())
        } else if (!isStoppingToCharge) {
            stopToCharge()
        } else if (isCharging) {
            accelerateAtAngle(angleToCharge)
        }

        applyPhysics(dt)
    }

    private fun stopToCharge() {
        isStoppingToCharge = true
        angleToCharge = getAngleTowardPlayer()
        addAction(
            Actions.sequence(
                stoppingToChargeAnimation(),
                Actions.run {
                    isCharging = true
                    setAcceleration(chargeSpeed * 10)
                    setMaxSpeed(chargeSpeed)
                    setDeceleration(chargeSpeed * 10)
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
            },
            Actions.fadeOut(1f),
            Actions.run {
                Experience(x + width / 2, y + height / 2, stage, 1)
                remove()
            }
        )
    }

    private fun stoppingToChargeAnimation(): ParallelAction? {
        return Actions.parallel(
            Actions.scaleBy(.3f, -.3f, 1f),
            Actions.color(Color.PINK, 1f)
        )
    }

    private fun resetStoppingToChargeAnimation(): ParallelAction? {
        return Actions.parallel(
            Actions.scaleTo(1f, 1f, .25f, Interpolation.bounceOut),
            Actions.color(Color.RED, .25f)
        )
    }

    private fun getAngleTowardPlayer() =
        (MathUtils.atan2(y - player.y, x - player.x) * MathUtils.radiansToDegrees) + 180
}
