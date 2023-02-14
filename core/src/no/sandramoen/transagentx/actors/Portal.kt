package no.sandramoen.transagentx.actors

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import no.sandramoen.transagentx.actors.characters.player.Player
import no.sandramoen.transagentx.actors.particles.BluePortalEffect
import no.sandramoen.transagentx.actors.particles.OrangePortalEffect
import no.sandramoen.transagentx.utils.BaseActor

class Portal(x: Float, y: Float, stage: Stage, val orange: Boolean, player: Player) :
    BaseActor(x, y, stage) {
    private var orangePortalEffect: OrangePortalEffect
    private var bluePortalEffect: BluePortalEffect

    init {
        orangePortalEffect = OrangePortalEffect()
        bluePortalEffect = BluePortalEffect()

        if (orange) {
            loadImage("orangePortal")
            startOrangeEffect()
        } else {
            loadImage("bluePortal")
            startBlueEffect()
        }

        startValueAnimation()
        setScale(0f)
        zIndex = player.zIndex - 1
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        orangePortalEffect.centerAtActor(this)
        bluePortalEffect.centerAtActor(this)
    }

    override fun remove(): Boolean {
        isCollisionEnabled = false
        orangePortalEffect.remove()
        bluePortalEffect.remove()
        return super.remove()
    }

    fun setNewPosition(position: Vector2) {
        fadeOut()
        isShakyCam = true
        addAction(Actions.sequence(
            Actions.delay(.75f),
            Actions.run { isShakyCam = false },
            Actions.delay(9.25f),
            Actions.run {
                setPosition(position.x, position.y)
                fadeIn()
            }
        ))
    }

    private fun fadeOut() {
        isCollisionEnabled = false
        clearActions()
        addAction(Actions.scaleTo(0f, 0f, 1f))
        if (orange) orangePortalEffect.stop()
        else bluePortalEffect.stop()
    }

    private fun fadeIn() {
        isCollisionEnabled = true
        startValueAnimation()
        if (orange) orangePortalEffect.start()
        else bluePortalEffect.start()
    }

    private fun startValueAnimation() {
        addAction(
            Actions.forever(
                Actions.parallel(
                    pulseAnimation(),
                    rotateAnimation()
                )
            )
        )
    }

    private fun pulseAnimation(): SequenceAction? {
        return Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, 1f),
            Actions.scaleTo(.95f, .95f, 1f)
        )
    }

    private fun rotateAnimation(): SequenceAction? {
        return Actions.sequence(
            Actions.rotateBy(2.5f, .5f),
            Actions.rotateBy(-5f, 1f),
            Actions.rotateBy(2.5f, .5f)
        )
    }

    private fun startOrangeEffect() {
        orangePortalEffect.setScale(.01f)
        orangePortalEffect.centerAtActor(this)
        stage.addActor(orangePortalEffect)
        orangePortalEffect.start()
    }

    private fun startBlueEffect() {
        bluePortalEffect.setScale(.01f)
        bluePortalEffect.centerAtActor(this)
        stage.addActor(bluePortalEffect)
        bluePortalEffect.start()
    }
}
