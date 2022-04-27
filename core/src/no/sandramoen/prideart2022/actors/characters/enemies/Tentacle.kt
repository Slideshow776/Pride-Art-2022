package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils
import kotlin.math.pow
import kotlin.math.sqrt

class Tentacle(stage: Stage, private val bossKG: BossKG, private val player: Player) :
    BaseActor(0f, 0f, stage) {
    private var originalWidth = -1f
    private var originalHeight = -1f
    private var shaderProgram: ShaderProgram
    private var time = 0f

    init {
        loadImage("enemies/tentacle0")
        setOrigin(Align.bottom)
        zIndex = bossKG.zIndex - 1
        fadeIn()
        rotation = MathUtils.random(0f, 360f)
        idleRotation()

        originalWidth = width
        originalHeight = height
        if (MathUtils.randomBoolean()) flip()
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.waveShader)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (BaseGame.isCustomShadersEnabled) {
            try {
                drawWithShader(batch, parentAlpha)
            } catch (error: Throwable) {
                super.draw(batch, parentAlpha)
            }
        } else {
            super.draw(batch, parentAlpha)
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }

    fun attack() {
        clearActions()
        addAction(
            Actions.sequence(
                rotateTowardsPlayer(),
                lashOut(),
                resetAttack()
            )
        )
    }

    private fun drawWithShader(batch: Batch, parentAlpha: Float) {
        batch.shader = shaderProgram
        shaderProgram.setUniformf("u_time", time)
        shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
        shaderProgram.setUniformf("u_amplitude", Vector2(.1f, .01f))
        shaderProgram.setUniformf("u_wavelength", Vector2(10f, 40f))
        shaderProgram.setUniformf("u_velocity", Vector2(5f, 0f))
        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    private fun rotateTowardsPlayer(): SequenceAction? {
        return Actions.sequence(
            Actions.run {
                BaseGame.tentacleChargeUpSound!!.play(
                    BaseGame.soundVolume * .25f,
                    MathUtils.random(.9f, 1.1f),
                    0f
                )
            },
            Actions.rotateTo(
                getAngleTowardActor(player, Align.bottom) - 90f,
                .25f,
                Interpolation.circle
            )
        )
    }

    private fun lashOut(): SequenceAction? {
        return Actions.sequence(
            Actions.parallel(
                Actions.sizeTo(width, height / 2, .1f),
                Actions.run {
                    BaseGame.tentacleWhipSound!!.play(
                        BaseGame.soundVolume * .25f,
                        MathUtils.random(.9f, 1.1f),
                        0f
                    )
                }
            ),
            Actions.sizeTo(width * .8f, attackRange(), .75f, Interpolation.elasticIn),
            Actions.run { setBoundaryRectangle() },
            Actions.delay(.1f),
            Actions.run { isCollisionEnabled = false }
        )
    }

    private fun resetAttack(): SequenceAction? {
        return Actions.sequence(
            Actions.sizeTo(originalWidth, originalHeight, 1f),
            Actions.run {
                setBoundaryRectangle()
                isCollisionEnabled = true
            },
            Actions.rotateTo(MathUtils.random(0f, 360f), 1f),
            Actions.run { idleRotation() }
        )
    }

    private fun attackRange(): Float = hypotenuse(bossKG.x - player.x, bossKG.y - player.y) * 1.6f

    private fun hypotenuse(a: Float, b: Float) = sqrt(a.pow(2) + b.pow(2))

    private fun idleRotation() = addAction(Actions.forever(Actions.rotateBy(10f, 1f)))

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
}
