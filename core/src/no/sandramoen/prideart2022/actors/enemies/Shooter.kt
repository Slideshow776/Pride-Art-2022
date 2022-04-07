package no.sandramoen.prideart2022.actors.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.Player
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

    init {
        loadImage("ghost")
        centerAtPosition(x, y)
        color = Color.MAGENTA
        debug = true

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        color.a = 0f
        addAction(Actions.sequence(Actions.fadeIn(.25f)))
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
        }

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
        addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run {
                Experience(x + width / 2, y + height / 2, stage, 1)
                remove()
            }
        ))
        BaseGame.enemyDeathSound!!.play(BaseGame.soundVolume, .8f, 0f)
    }
}