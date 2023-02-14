package no.sandramoen.transagentx.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.transagentx.actors.particles.Explosion0Effect
import no.sandramoen.transagentx.utils.BaseActor

class SmallExplosion(val baseActor: BaseActor, val stage: Stage) {
    init {
        val effect = Explosion0Effect()
        effect.setScale(.03f)
        effect.centerAtActor(baseActor)
        stage.addActor(effect)
        effect.start()
    }
}
