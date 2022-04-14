package no.sandramoen.prideart2022.actors.enemies

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.utils.BaseActor

class Remains(stage: Stage, baseActor: BaseActor) : BaseActor(baseActor.x, baseActor.y, stage) {
    init {
        loadImage("ghostRemains")
        centerAtActor(baseActor)
        zIndex = baseActor.zIndex - 1
    }
}
