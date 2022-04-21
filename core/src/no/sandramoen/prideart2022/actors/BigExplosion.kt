package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.actors.particles.Explosion1Effect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class BigExplosion(val baseActor: BaseActor, val stage: Stage) {
    init {
        addEffect()
        BaseGame.explosionSound!!.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f)
    }

    private fun addEffect() {
        val effect = Explosion1Effect()
        effect.setScale(.08f)
        effect.centerAtActor(baseActor)
        stage.addActor(effect)
        effect.start()
    }
}
