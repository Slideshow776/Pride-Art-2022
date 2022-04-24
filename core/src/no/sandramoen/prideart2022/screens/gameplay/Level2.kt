package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.Crystal
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.TintOverlay
import no.sandramoen.prideart2022.actors.characters.enemies.Charger
import no.sandramoen.prideart2022.actors.characters.enemies.Shooter
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level2 : BaseLevel() {
    override fun initialize() {
        super.initialize()
        tilemap = TilemapActor(BaseGame.level2, mainStage)
        TintOverlay(0f, 0f, mainStage)
        initializePlayer()
        initializeDestructibles()
        initializeImpassables()

        Crystal(player.x + 10, player.y + 10, mainStage, "white")
        Crystal(player.x - 10, player.y + 10, mainStage, "pink")
        Crystal(player.x - 10, player.y - 10, mainStage, "blue")

        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral8"), 5f)

        /*spawnEnemies()*/
        /*GameUtils.playAndLoopMusic(BaseGame.levelMusic)*/
    }

    override fun spawnEnemies() {
        enemySpawner = BaseActor(0f, 0f, mainStage)
        enemySpawner.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(5f),
                    Actions.run {
                        val range = 30f
                        val xPos =
                            if (MathUtils.randomBoolean()) player.x - range else player.x + range
                        val yPos =
                            if (MathUtils.randomBoolean()) player.y - range else player.y + range
                        Charger(xPos, yPos, mainStage, player)
                        Shooter(xPos, yPos, mainStage, player)
                    }
                )))
    }
}
