package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.actors.characters.lost.Lost0
import no.sandramoen.prideart2022.actors.characters.lost.Lost1
import no.sandramoen.prideart2022.actors.characters.lost.Lost2
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level2 : BaseLevel() {
    private lateinit var lost0: Lost0
    private var lost1: Lost1? = null
    private var lost2: Lost2? = null
    private var isSpawnedBoss = false

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level2, mainStage)
        super.initialize()
        spawnLost0()
    }

    override fun update(dt: Float) {
        super.update(dt)
        lost0Pickup()
        lost1Pickup()
        lost2Pickup()

        checkIfBossShouldSpawn()
        handleBoss()
    }

    private fun checkIfBossShouldSpawn() {
        if (lostLabel.textEquals("3/3") && experienceBar.level >= 5 && !isSpawnedBoss) {
            isSpawnedBoss = true
            spawnBoss()
        }
    }

    private fun spawnBoss() {
        val position = bossSpawn()
        BossKG(position.x, position.y, mainStage, player)
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
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral14"))
        for (enemy: BaseActor in BaseActor.getList(mainStage, Follower::class.java.canonicalName)) {
            enemy.death()
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Teleport::class.java.canonicalName)) {
            enemy.death()
        }
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(6f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(
                        BaseGame.myBundle!!.get("fleetAdmiral6"),
                        9f
                    )
                },
                Actions.delay(5f),
                Actions.run { playerExitLevel() },
                Actions.delay(2f),
                Actions.run {
                    BaseGame.levelMusic!!.stop()
                    BaseGame.setActiveScreen(Level3())
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

    private fun spawnLost0() {
        var position = spawnAtEdgesOfMap(10f)
        lost0 = Lost0(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral8"), 7f)
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(9f),
                Actions.run { lostLabel.fadeIn() },
                Actions.delay(1f),
            Actions.run { GameUtils.playAndLoopMusic(BaseGame.level2IntroMusic) }
            )
        )
    }

    private fun spawnLost1() {
        var position = spawnAtEdgesOfMap(10f)
        lost1 = Lost1(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral10"), 5f)
        lostLabel.glintToWhiteAndBack()
    }

    private fun spawnLost2() {
        var position = spawnAtEdgesOfMap(10f)
        lost2 = Lost2(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral11"), 5f)
        lostLabel.glintToWhiteAndBack()
    }

    private fun lost0Pickup() {
        if (lost0.isPickedUp) {
            lost0.isPickedUp = false
            lostLabel.setText("Redd 1/3 transpersoner")
            spawnFollowers()
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral9"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                Actions.delay(30f),
                Actions.run { spawnLost1() }
            ))
        }
    }

    private fun lost1Pickup() {
        if (lost1 != null && lost1!!.isPickedUp) {
            lostLabel.setText("Redd 2/3 transpersoner")
            lost1!!.isPickedUp = false
            spawnEnemies()
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral12"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.parallel(
                Actions.sequence(
                    Actions.delay(10f),
                    Actions.run { dropShield() }
                ),
                Actions.sequence(
                    Actions.delay(30f),
                    Actions.run { spawnLost2() }
                )
            ))
        }
    }

    private fun lost2Pickup() {
        if (lost2 != null && lost2!!.isPickedUp) {
            lostLabel.setText("Redd 3/3 transpersoner")
            lostLabel.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run { lostLabel.fadeOut() }
            ))
            lost2!!.isPickedUp = false

            enemySpawner1.clearActions()
            enemySpawner2.clearActions()
        }
    }
}
