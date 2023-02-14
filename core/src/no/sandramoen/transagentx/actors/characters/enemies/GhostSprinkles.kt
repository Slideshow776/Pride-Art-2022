package no.sandramoen.transagentx.actors.characters.enemies

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.transagentx.actors.particles.GhostSprinklesEffect
import no.sandramoen.transagentx.utils.BaseActor

class GhostSprinkles(baseActor: BaseActor, stage: Stage) {
    val effect: GhostSprinklesEffect

    init {
        effect = GhostSprinklesEffect()
        effect.setScale(.05f)
        effect.centerAtActor(baseActor)
        stage.addActor(effect)
        effect.start()
    }

    fun setPosition(x: Float, y: Float) {
        effect.setPosition(x, y)
    }

    fun stop() {
        effect.stop()
    }
}
