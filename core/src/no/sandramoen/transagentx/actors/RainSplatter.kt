package no.sandramoen.transagentx.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame

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
