package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor

class Chain(x: Float, y: Float, stage: Stage, baseActor: BaseActor, angle: Float) :
    BaseActor(x, y, stage) {
    init {
        loadImage("enemies/chain")
        setScale(0f)

        setOrigin(Align.bottom)
        zIndex = baseActor.zIndex - 1
        rotation = angle
        if (MathUtils.randomBoolean())
            flip()

        throwOutAnimation()
    }

    private fun throwOutAnimation() {
        addAction(
            Actions.sequence(
                Actions.scaleTo(1f, 1f, 2f),
                Actions.delay(2f),
                Actions.fadeOut(1f),
                Actions.removeActor()
            )
        )
    }
}
