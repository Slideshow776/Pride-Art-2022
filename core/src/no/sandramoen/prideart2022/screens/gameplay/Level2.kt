package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.Crystal
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.TintOverlay
import no.sandramoen.prideart2022.actors.characters.enemies.Charger
import no.sandramoen.prideart2022.actors.characters.enemies.Follower
import no.sandramoen.prideart2022.actors.characters.enemies.Shooter
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level2 : BaseLevel() {
    private lateinit var whiteCrystal: Crystal
    private var pinkCrystal: Crystal? = null
    private var blueCrystal: Crystal? = null

    override fun initialize() {
        super.initialize()
        tilemap = TilemapActor(BaseGame.level2, mainStage)
        TintOverlay(0f, 0f, mainStage)
        initializePlayer()
        initializeDestructibles()
        initializeImpassables()

        spawnWhiteCrystal()
        /*GameUtils.playAndLoopMusic(BaseGame.levelMusic)*/

        /*spawnEnemies()*/
        /*spawnFollowers()*/
    }

    override fun update(dt: Float) {
        super.update(dt)
        whiteCrystalPickup()
        pinkCrystalPickup()
    }

    private fun spawnEnemies() {
        enemySpawner1 = BaseActor(0f, 0f, mainStage)
        enemySpawner1.addAction(Actions.forever(Actions.sequence(
            Actions.delay(3f),
            Actions.run {
                var position = spawnAroundPlayer(50f)
                Charger(position.x, position.y, mainStage, player)
                position = spawnAroundPlayer(50f)
                Shooter(position.x, position.y, mainStage, player)
            }
        )))
    }

    private fun spawnFollowers() {
        enemySpawner2 = BaseActor(0f, 0f, mainStage)
        enemySpawner2.addAction(Actions.forever(Actions.sequence(
            Actions.delay(4f),
            Actions.run {
                var position = spawnAroundPlayer(50f)
                Follower(position.x, position.y, mainStage, player)
            }
        )))
    }

    private fun spawnWhiteCrystal() {
        var position = spawnAtEdgesOfMap(10f)
        whiteCrystal = Crystal(position.x, position.y, mainStage, "white")
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral8"), 7f)
        BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
            Actions.delay(2f),
            Actions.run { crystalLabel.fadeIn() },
            Actions.delay(1f),
            Actions.run { GameUtils.playAndLoopMusic(BaseGame.level2IntroMusic) }
        ))
    }

    private fun spawnPinkCrystal() {
        var position = spawnAtEdgesOfMap(10f)
        pinkCrystal = Crystal(position.x, position.y, mainStage, "pink")
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral10"), 5f)
        crystalLabel.changeToPink()
    }

    private fun spawnBlueCrystal() {
        var position = spawnAtEdgesOfMap(10f)
        pinkCrystal = Crystal(position.x, position.y, mainStage, "blue")
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral11"), 5f)
        crystalLabel.changeToBlue()
    }

    private fun whiteCrystalPickup() {
        if (whiteCrystal.isPickedUp) {
            whiteCrystal.isPickedUp = false
            crystalLabel.setText("1/3")
            spawnFollowers()
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral9"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                Actions.delay(30f),
                Actions.run { spawnPinkCrystal() }
            ))
        }
    }

    private fun pinkCrystalPickup() {
        if (pinkCrystal != null && pinkCrystal!!.isPickedUp) {
            crystalLabel.setText("2/3")
            pinkCrystal!!.isPickedUp = false
            spawnEnemies()
        }
    }

    private fun blueCrystalPickup() {
        if (blueCrystal != null && blueCrystal!!.isPickedUp) {
            crystalLabel.setText("3/3")
            blueCrystal!!.isPickedUp = false
            println("blue crystal picked up!")
        }
    }
}
