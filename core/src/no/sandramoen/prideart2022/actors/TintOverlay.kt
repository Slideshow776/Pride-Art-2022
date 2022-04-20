package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.utils.BaseActor

class TintOverlay(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        loadImage("whitePixel")
        color = Color(0f, 0f, 0f, .65f)
        setSize(getWorldBounds().width, getWorldBounds().height)
    }
}
