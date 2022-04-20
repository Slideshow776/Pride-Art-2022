package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.characters.player.BeamIn
import no.sandramoen.prideart2022.actors.characters.player.GroundCrack
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.actors.particles.HeartExplosion
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class HealthDrop(x: Float, y: Float, stage: Stage, val player: Player) : BaseActor(x, y, stage) {
    init {
        loadImage("heart")
        BeamIn(x, y, stage, this)
        initializeGroundCrack()
        initializeAnimation()
    }

    fun pickup(isSuccess: Boolean) {
        if (isSuccess)
            BaseGame.healthPickupSuccessSound!!.play(BaseGame.soundVolume, MathUtils.random(.97f, 1.03f), 0f)
        else
            BaseGame.healthPickupFailSound!!.play(BaseGame.soundVolume, MathUtils.random(.97f, 1.03f), 0f)
        heartExplosionEffect()
        removeAnimation()
        isCollisionEnabled = false
    }

    private fun removeAnimation() {
        clearActions()
        addAction(
            Actions.sequence(
            Actions.scaleTo(0f, 1f, .25f),
            Actions.run { remove() }
        ))
    }

    private fun heartExplosionEffect() {
        val effect = HeartExplosion()
        effect.setScale(.05f)
        effect.centerAtActor(this)
        stage.addActor(effect)
        effect.start()
    }

    private fun initializeGroundCrack() {
        val groundCrack = GroundCrack(x, y, stage)
        groundCrack.centerAtActor(this)
        groundCrack.zIndex = player.zIndex - 1
    }

    private fun initializeAnimation() {
        addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(1.1f, 1.1f, .125f),
            Actions.scaleTo(1.0f, 1.0f, .75f)
        )))
    }
}
