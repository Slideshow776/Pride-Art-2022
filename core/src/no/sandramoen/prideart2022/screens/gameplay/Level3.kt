package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.Beamer
import no.sandramoen.prideart2022.actors.characters.enemies.Charger
import no.sandramoen.prideart2022.actors.characters.enemies.Follower
import no.sandramoen.prideart2022.actors.characters.enemies.Shooter
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Level3 : BaseLevel() {

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level3, mainStage)
        super.initialize()

        spawnEnemies()
    }

    private fun spawnEnemies() {
        enemySpawner1 = BaseActor(0f, 0f, mainStage)
        enemySpawner1.addAction(
            Actions.forever(
                Actions.sequence(
            Actions.delay(2f),
            Actions.run {
                var position = spawnAroundPlayer(50f)
                Beamer(position.x + 30f, position.y + 30f, mainStage, player)
                /*position = spawnAroundPlayer(50f)
                Follower(position.x, position.y, mainStage, player)
                position = spawnAroundPlayer(50f)
                Charger(position.x, position.y, mainStage, player)*/
                position = spawnAroundPlayer(50f)
                Shooter(position.x, position.y, mainStage, player)
            }
        )))
    }
}
