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

class RainSplatter(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        animate()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (isAnimationFinished())
            remove()
    }

    private fun animate() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..3)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("rain splatter/$i"))
        setAnimation(Animation(.1f, animationImages, Animation.PlayMode.NORMAL))
    }
}
