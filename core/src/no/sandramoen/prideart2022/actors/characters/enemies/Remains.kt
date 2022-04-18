package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.utils.BaseActor

class Remains(stage: Stage, player: Player) : BaseActor(player.x, player.y, stage) {
    init {
        loadImage("ghostRemains")
        centerAtActor(player)
        zIndex = player.zIndex - 1
    }
}
