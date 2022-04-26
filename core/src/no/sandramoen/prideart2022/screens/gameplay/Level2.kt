package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.Crystal
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.TintOverlay
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level2 : BaseLevel() {
    private lateinit var whiteCrystal: Crystal
    private var pinkCrystal: Crystal? = null
    private var blueCrystal: Crystal? = null
    private var isSpawnedBoss = false

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
        spawnBoss()
    }

    override fun update(dt: Float) {
        super.update(dt)
        whiteCrystalPickup()
        pinkCrystalPickup()
        blueCrystalPickup()

        checkIfBossShouldSpawn()
        handleBoss()
    }

    private fun checkIfBossShouldSpawn() {
        if (crystalLabel.textEquals("3/3") && experienceBar.level >= 5 && !isSpawnedBoss) {
            isSpawnedBoss = true
            spawnBoss()
        }
    }

    private fun spawnBoss() {
        BossKG(player.x + 20f, player.y + 20f, mainStage, player)
        bossBar.countDown()
        enemySpawner1.clearActions()
        enemySpawner2.clearActions()
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral13"), 5f)
    }

    private fun handleBoss() {
        for (enemy: BaseActor in BaseActor.getList(mainStage, BossKG::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy as BossKG, false, player.health)
            handleDestructibles(enemy)
            if (bossBar.complete && !enemy.isDying) {
                enemy.death()
                bossDeath()
            }
        }
    }

    private fun bossDeath() {
        experienceBar.level++
        bossBar.isVisible = false
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral5"))
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(6f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(
                        BaseGame.myBundle!!.get("fleetAdmiral6"),
                        6f
                    )
                },
                Actions.delay(5f),
                Actions.run { playerExitLevel() },
                Actions.delay(2f),
                Actions.run {
                    BaseGame.levelMusic!!.stop()
                    BaseGame.setActiveScreen(Level2())
                }
            ))
    }

    private fun spawnEnemies() {
        enemySpawner1 = BaseActor(0f, 0f, mainStage)
        enemySpawner1.addAction(Actions.forever(Actions.sequence(
            Actions.delay(1.5f),
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
            Actions.delay(3f),
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
        blueCrystal = Crystal(position.x, position.y, mainStage, "blue")
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
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral12"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.parallel(
                Actions.sequence(
                    Actions.delay(10f),
                    Actions.run { dropShield() }
                ),
                Actions.sequence(
                    Actions.delay(30f),
                    Actions.run { spawnBlueCrystal() }
                )
            ))
        }
    }

    private fun blueCrystalPickup() {
        if (blueCrystal != null && blueCrystal!!.isPickedUp) {
            crystalLabel.setText("3/3")
            crystalLabel.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run { crystalLabel.fadeOut() }
            ))
            blueCrystal!!.isPickedUp = false

            enemySpawner1.clearActions()
            enemySpawner2.clearActions()
        }
    }
}
