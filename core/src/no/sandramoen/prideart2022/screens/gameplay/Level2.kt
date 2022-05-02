package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.actors.characters.lost.Lost0
import no.sandramoen.prideart2022.actors.characters.lost.Lost1
import no.sandramoen.prideart2022.actors.characters.lost.Lost2
import no.sandramoen.prideart2022.actors.characters.lost.Lost3
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level2 : BaseLevel() {
    private lateinit var lost0: Lost0
    private var lost1: Lost1? = null
    private var lost2: Lost2? = null
    private var lost3: Lost3? = null
    private var isSpawnedBoss = false
    private var isReadyToSpawnBoss = false

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
        lost3Pickup()

        checkIfBossShouldSpawn()
        handleBoss()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (isGameOver) BaseGame.setActiveScreen(Level2())
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isGameOver) BaseGame.setActiveScreen(Level2())
        return super.buttonDown(controller, buttonCode)
    }

    private fun checkIfBossShouldSpawn() {
        if (isReadyToSpawnBoss && experienceBar.level >= 5 && !isSpawnedBoss) {
            isSpawnedBoss = true
            spawnBoss()
        } else if (isReadyToSpawnBoss && BaseActor.count(
                mainStage,
                Experience::class.java.canonicalName
            ) <= 0
        ) {
            spawnBoss()
        }
    }

    private fun spawnBoss() {
        BaseGame.level2Music!!.stop()
        GameUtils.playAndLoopMusic(BaseGame.bossMusic)
        val position = bossSpawn()
        BossKG(position.x, position.y, mainStage, player)
        bossBar.label.setText("Kjersti Gulbrandsen")
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
        BaseGame.bossMusic!!.stop()
        experienceBar.level++
        bossBar.isVisible = false
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral14"))
        for (enemy: BaseActor in BaseActor.getList(mainStage, Follower::class.java.canonicalName)) {
            enemy.death()
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Teleport::class.java.canonicalName)) {
            enemy.death()
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shot::class.java.canonicalName)) {
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
                    BaseGame.level1Music!!.stop()
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
                Actions.run {
                    objectivesLabel.fadeIn()
                    objectivesLabel.glintToWhiteAndBack()
                },
                Actions.delay(1f),
                Actions.run { GameUtils.playAndLoopMusic(BaseGame.level2IntroMusic) }
            )
        )
    }

    private fun spawnLost1() {
        var position = spawnAtEdgesOfMap(10f)
        lost1 = Lost1(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral10"), 5f)
        objectivesLabel.glintToWhiteAndBack()
    }

    private fun spawnLost2() {
        var position = spawnAtEdgesOfMap(10f)
        lost2 = Lost2(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral15"), 5f)
        objectivesLabel.glintToWhiteAndBack()
    }

    private fun spawnLost3() {
        var position = spawnAtEdgesOfMap(10f)
        lost3 = Lost3(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral11"), 5f)
        objectivesLabel.glintToWhiteAndBack()
    }

    private fun lost0Pickup() {
        if (lost0.isPickedUp) {
            lost0.isPickedUp = false
            objectivesLabel.setText("Redd 1/4 transpersoner")
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral16"), 6f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                Actions.delay(10f),
                Actions.run { spawnLost1() }
            ))
        }
    }

    private fun lost1Pickup() {
        if (lost1 != null && lost1!!.isPickedUp) {
            objectivesLabel.setText("Redd 2/4 transpersoner")
            lost1!!.isPickedUp = false
            BaseGame.level2IntroMusic!!.stop()
            GameUtils.playAndLoopMusic(BaseGame.level2Music)
            spawnFollowers()
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral9"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(
                Actions.sequence(
                    Actions.delay(35f),
                    Actions.run { spawnLost2() }
                )
            )
        }
    }

    private fun lost2Pickup() {
        if (lost2 != null && lost2!!.isPickedUp) {
            println("lost2Pickup")
            spawnEnemies()
            objectivesLabel.setText("Redd 3/4 transpersoner")
            lost2!!.isPickedUp = false
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral12"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.parallel(
                Actions.sequence(
                    Actions.delay(10f),
                    Actions.run { dropShield() }
                ),
                Actions.sequence(
                    Actions.delay(35f),
                    Actions.run { spawnLost3() }
                )
            ))
        }
    }

    private fun lost3Pickup() {
        if (lost3 != null && lost3!!.isPickedUp) {
            println("lost3Pickup")
            objectivesLabel.setText("Redd 4/4 transpersoner")
            objectivesLabel.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run { objectivesLabel.fadeOut() }
            ))
            lost3!!.isPickedUp = false
            isReadyToSpawnBoss = true

            enemySpawner1.clearActions()
            enemySpawner2.clearActions()
        }
    }
}
