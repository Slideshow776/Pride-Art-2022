package no.sandramoen.transagentx.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.transagentx.utils.BaseActor

open class Impassable(x: Float, y: Float, width: Float, height: Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        setSize(width, height)
        setBoundaryRectangle()
    }
}
