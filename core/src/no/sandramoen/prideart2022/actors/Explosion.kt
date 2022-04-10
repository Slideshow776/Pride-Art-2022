package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.actors.particles.Explosion0Effect
import no.sandramoen.prideart2022.utils.BaseActor

class Explosion(baseActor: BaseActor, stage: Stage) {
    init {
        val effect = Explosion0Effect()
        effect.setScale(.1f)
        effect.centerAtActor(baseActor)
        stage.addActor(effect)
        effect.start()
    }
}
