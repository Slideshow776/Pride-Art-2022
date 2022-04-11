package no.sandramoen.prideart2022.actors.enemies

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.utils.BaseActor

class Remains(x: Float, y: Float, stage: Stage, baseActor: BaseActor) : BaseActor(x, y, stage) {
    init {
        loadImage("ghostRemains")
        centerAtActor(baseActor)
        zIndex = 1
    }
}
