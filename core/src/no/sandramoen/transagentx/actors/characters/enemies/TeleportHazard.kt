package no.sandramoen.transagentx.actors.characters.enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.transagentx.actors.Explosion
import no.sandramoen.transagentx.actors.characters.player.Player
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame

class TeleportHazard(baseActor: BaseActor, stage: Stage, player: Player) :
    BaseActor(baseActor.x, baseActor.y, stage) {
    init {
        Explosion(this, stage)
        zIndex = player.zIndex - 1

        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/teleport1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("enemies/teleport2"))
        val wiggleAnimation = Animation(.5f, animationImages, Animation.PlayMode.LOOP)
        setAnimation(wiggleAnimation)
    }

    override fun death() {
        super.death()
        addAction(
            Actions.sequence(
                Actions.scaleTo(0f, 1f, .5f),
                Actions.removeActor()
            )
        )
    }
}
