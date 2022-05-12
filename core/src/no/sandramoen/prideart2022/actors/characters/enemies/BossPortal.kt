package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.BossPortalEffect
import no.sandramoen.prideart2022.actors.particles.Explosion0Effect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class BossPortal(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    private var effect: BossPortalEffect? = null
    private var isTriggeredClose1 = false
    private var isTriggeredClose2 = false
    private var isTriggeredClose3 = false

    init {
        loadImage("bossPortal")
        startEffect()
        startValueAnimation(3f)
        fadeIn()
        isCollisionEnabled = false
        for (i in 0 until 20)
            explosionEffect()
        zIndex = player.zIndex - 1
        shakyCamIntensity = .01f
        isShakyCam = true
    }

    override fun act(dt: Float) {
        super.act(dt)

        if (player.isWithinDistance2(25f, this) && !isTriggeredClose1) {
            isTriggeredClose1 = true
            shakyCamIntensity = .2f
            GameUtils.vibrateController(duration = 100000, strength = .1f)
            clearActions()
            startValueAnimation(1f)
        }

        if (player.isWithinDistance2(15f, this) && !isTriggeredClose2) {
            isTriggeredClose2 = true
            shakyCamIntensity = .4f
            GameUtils.cancelControllerVibration()
            GameUtils.vibrateController(duration = 100000, strength = .15f)
            clearActions()
            startValueAnimation(.5f)
        }

        if (player.isWithinDistance2(7f, this) && !isTriggeredClose3) {
            isTriggeredClose3 = true
            shakyCamIntensity = 1f
            GameUtils.cancelControllerVibration()
            GameUtils.vibrateController(duration = 100000, strength = .2f)
            clearActions()
            startValueAnimation(.1f)
        }
    }

    fun close() {
        BaseGame.portalSound!!.play(BaseGame.soundVolume, .5f, 0f)
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 0f, 1f),
            Actions.run { isVisible = false }
        ))
    }

    fun stopEffect() {
        effect!!.stop()
    }

    private fun fadeIn() {
        isCollisionEnabled = true
        addAction(Actions.sequence(
            Actions.fadeIn(1f),
            Actions.run { isCollisionEnabled = true }
        ))
    }

    private fun startValueAnimation(duration: Float) {
        addAction(
            Actions.forever(
                Actions.parallel(
                    pulseAnimation(duration),
                    rotateAnimation(duration)
                )
            )
        )
    }

    private fun pulseAnimation(duration: Float): SequenceAction? {
        return Actions.sequence(
            Actions.scaleTo(1.02f, 1.02f, duration * .5f),
            Actions.scaleTo(.98f, .98f, duration * .5f)
        )
    }

    private fun rotateAnimation(duration: Float): SequenceAction? {
        return Actions.sequence(
            Actions.rotateBy(1f, duration * .25f),
            Actions.rotateBy(-2f, duration * .5f),
            Actions.rotateBy(1f, duration * .25f)
        )
    }

    private fun startEffect() {
        effect = BossPortalEffect()
        effect!!.setScale(.01f)
        effect!!.centerAtActor(this)
        stage.addActor(effect)
        effect!!.start()
    }

    private fun explosionEffect() {
        BaseGame.explosionSound!!.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f)
        val effect = Explosion0Effect()
        effect.setScale(.12f)
        var xPos = x - 2.5f
        if (MathUtils.randomBoolean())
            xPos = x + 15f
        var yPos = y - 5f
        if (MathUtils.randomBoolean())
            yPos = y + 5f
        effect.setPosition(xPos, yPos)
        // effect.setPosition(x + width / 2, y)
        stage.addActor(effect)
        effect.start()
    }
}
