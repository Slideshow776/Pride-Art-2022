package no.sandramoen.prideart2022.screens

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import no.sandramoen.prideart2022.actors.Player
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen
import no.sandramoen.prideart2022.utils.XBoxGamepad


class LevelScreen : BaseScreen() {
    private lateinit var ground: BaseActor
    private lateinit var player: Player

    override fun initialize() {
        ground = BaseActor(0f, 0f, mainStage)
        // ground.loadImage("ground")
        ground.loadTexture("images/excluded/ground1.png")
        ground.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT)

        player = Player(mainStage)
    }

    override fun update(dt: Float) {}

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (buttonCode == XBoxGamepad.BUTTON_A)
            player.flashColor(Color.GREEN)
        if (buttonCode == XBoxGamepad.BUTTON_B)
            player.flashColor(Color.RED)
        if (buttonCode == XBoxGamepad.BUTTON_X)
            player.flashColor(Color.BLUE)
        if (buttonCode == XBoxGamepad.BUTTON_Y)
            player.flashColor(Color.YELLOW)
        return super.buttonDown(controller, buttonCode)
    }
}
