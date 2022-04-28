package no.sandramoen.prideart2022.screens.gameplay

import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.utils.BaseGame

class Level3 : BaseLevel() {

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level3, mainStage)
        super.initialize()
    }
}
