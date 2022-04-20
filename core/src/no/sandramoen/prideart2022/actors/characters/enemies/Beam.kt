package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Beam(x: Float, y: Float, stage: Stage, angleTowardsPlayer: Float) : BaseActor(x, y, stage) {
    init {
        BaseGame.spaceStationBeamSound!!.play(BaseGame.soundVolume)
        loadImage("whitePixel")
        color = Color.ORANGE
        setOrigin(Align.bottom)
        rotation = angleTowardsPlayer

        addAction(
            Actions.sequence(
                Actions.scaleTo(20f, 2_000f, 1.5f)
            )
        )

        addAction(
            Actions.sequence(
                Actions.delay(3f),
                Actions.run { remove() }
            ))
    }
}
