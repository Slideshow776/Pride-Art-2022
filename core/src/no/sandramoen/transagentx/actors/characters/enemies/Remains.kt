package no.sandramoen.transagentx.actors.characters.enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.transagentx.actors.characters.player.Player
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame

class Remains(stage: Stage, player: Player) : BaseActor(player.x, player.y, stage) {
    init {
        initializeAnimation()
        centerAtActor(player)
        zIndex = player.zIndex - 1
    }

    private fun initializeAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        for (i in 1..20)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/remains1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/remains2"))
        setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP))
    }
}
