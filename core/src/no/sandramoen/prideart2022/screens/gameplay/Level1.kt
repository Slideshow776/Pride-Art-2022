package no.sandramoen.prideart2022.screens.gameplay

import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.TintOverlay
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level1 : BaseLevel() {
    override fun initialize() {
        super.initialize()
        tilemap = TilemapActor(BaseGame.level1, mainStage)
        TintOverlay(0f, 0f, mainStage)
        initializePlayer()
        initializeDestructibles()
        initializeImpassables()

        spawnEnemies()
        GameUtils.playAndLoopMusic(BaseGame.levelMusic)
    }
}
