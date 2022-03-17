package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.GameUtils

class Experience(x: Float, y: Float, stage: Stage, amount: Int) : BaseActor(x, y, stage) {
    val amount = amount

    init {
        loadImage("whitePixel")
        color = Color.PINK
        setSize(2f, 2f)
        centerAtPosition(x, y)
        setBoundaryRectangle()
        setOrigin(Align.center)

        GameUtils.pulseWidget(this)
    }
}
