package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
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
import no.sandramoen.prideart2022.actors.SmallExplosion
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.GhostSprinklesEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Fairy(x: Float, y: Float, stage: Stage, player: Player) : BaseActor(x, y, stage) {
    private val player = player
    private var movementSpeed = player.originalMovementSpeed * 1.1f

    private var dying = false
    private lateinit var runAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var sprinkles: RepeatAction

    private var isJumping = false
    private var jumpCounter = 0f
    private val jumpFrequency = 2f


    init {
        loadAnimation()
        setScale(.5f)
        centerAtPosition(x, y)

        setAcceleration(movementSpeed * 10f)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementSpeed * 10f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        fadeIn()
        addSprinkles()
        dieAfterDuration()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (dying || pause) return

        if (isWithinDistance2(4f, player) && isJumping) {
            spawnAroundPlayer(10f)
            isJumping = false
        } else {
            applyPhysics(dt)
            accelerateAtAngle(getAngleTowardActor(player))
        }

        if (jumpCounter > jumpFrequency) {
            jumpCounter = 0f
            isJumping = true
        }else {
            jumpCounter += dt
        }

    }

    private fun spawnAroundPlayer(offset: Float) {
        var x: Float
        var y: Float
        if (MathUtils.randomBoolean()) { // horizontal
            x = MathUtils.random(player.x - offset, player.x + offset)
            if (MathUtils.randomBoolean())
                y = player.y + offset
            else
                y = player.y - offset
        } else { // vertical
            if (MathUtils.randomBoolean())
                x = player.x + offset
            else
                x = player.x - offset
            y = MathUtils.random(player.y - offset, player.y + offset)
        }
        setPosition(x, y)
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
        SmallExplosion(this, stage)
        addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run {
                Remains(stage, player)
                remove()
            }
        ))
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

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/fairy/1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/fairy/2"))
        runAnimation = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()


        setAnimation(runAnimation)
    }
}