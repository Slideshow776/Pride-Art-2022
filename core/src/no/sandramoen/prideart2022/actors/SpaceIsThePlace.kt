package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class SpaceIsThePlace(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        animate()
        if (MathUtils.randomBoolean())
            rotateBy(90f)

        if (MathUtils.randomBoolean())
            rotateBy(180f)

        addAction(introShakeAnimation())
    }

    private fun animate() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..31)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("space/$i"))
        setAnimation(Animation(.025f, animationImages, Animation.PlayMode.LOOP))
    }

    private fun introShakeAnimation(): SequenceAction? {
        val amountX = .4f
        val duration = .04f
        return Actions.sequence(
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration),
            Actions.moveBy(amountX, 0f, duration),
            Actions.moveBy(-amountX, 0f, duration)
        )
    }
}
