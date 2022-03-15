package no.sandramoen.prideart2022.screens

import no.sandramoen.prideart2022.actors.Player
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen

class LevelScreen : BaseScreen() {
    private lateinit var ground: BaseActor

    override fun initialize() {
        ground = BaseActor(0f, 0f, mainStage)
        // ground.loadImage("ground")
        ground.loadTexture("images/excluded/ground1.png")
        ground.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT)

        Player(mainStage)
    }

    override fun update(dt: Float) {}
}
