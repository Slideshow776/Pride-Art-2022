package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.utils.BaseActor

class Impassable(x: Float, y: Float, width: Float, height: Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        setSize(width, height)
        setBoundaryRectangle()
    }
}
