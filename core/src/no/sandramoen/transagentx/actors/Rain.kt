package no.sandramoen.transagentx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Array
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame

class Rain(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        animate()
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        touchable = Touchable.disabled
        zIndex = 0
    }

    private fun animate() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 4 downTo 1) {
            animationImages.add(BaseGame.textureAtlas!!.findRegion("rain/$i"))
        }
        setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP))
    }
}
