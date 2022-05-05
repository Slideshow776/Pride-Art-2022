package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.utils.BaseActor

class SpiderWeb(baseActor: BaseActor, player: Player) : BaseActor(0f, 0f, baseActor.stage) {
    init {
        loadImage("enemies/spiderWeb")
        centerAtActor(baseActor)
        zIndex = player.zIndex - 1

        rotation = MathUtils.random(0f, 360f)
        setScale(MathUtils.random(.9f, 1.1f), MathUtils.random(.9f, 1.1f))
        removeAfterDelay()
    }

    private fun removeAfterDelay() {
        addAction(Actions.sequence(
            Actions.delay(15f),
            Actions.fadeOut(1f),
            Actions.removeActor()
        ))
    }
}