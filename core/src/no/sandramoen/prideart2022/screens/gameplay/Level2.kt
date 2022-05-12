package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.TheArtifact
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.actors.characters.lost.Lost0
import no.sandramoen.prideart2022.actors.characters.lost.Lost1
import no.sandramoen.prideart2022.actors.characters.lost.Lost2
import no.sandramoen.prideart2022.actors.characters.lost.Lost3
import no.sandramoen.prideart2022.ui.BossBar
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseGame.Companion.myBundle
import no.sandramoen.prideart2022.utils.GameUtils

class Level2 : BaseLevel() {
    private lateinit var lost0: Lost0
    private var lost1: Lost1? = null
    private var lost2: Lost2? = null
    private var lost3: Lost3? = null
    private var isSpawnedBoss = false
    private var isReadyToSpawnBoss = false
    private var lastLostPickup = BaseActor(0f, 0f, mainStage)

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
        if (isRestartable && !isButtonCodeDpad(keycode)) {
            BaseGame.windAmbianceMusic!!.stop()
            BaseGame.setActiveScreen(Level2())
        }
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(buttonCode)) {
            BaseGame.windAmbianceMusic!!.stop()
            BaseGame.setActiveScreen(Level2())
        }
        return super.buttonDown(controller, buttonCode)
    }

    private fun checkIfBossShouldSpawn() {
        if (isReadyToSpawnBoss && experienceBar.level >= 5 && !isSpawnedBoss) {
            isSpawnedBoss = true
            spawnBoss()
        } else if (
            isReadyToSpawnBoss && BaseActor.count(
                mainStage,
                Experience::class.java.canonicalName
            ) <= 0 && !isSpawnedBoss
        ) {
            isSpawnedBoss = true
            spawnBoss()
        }
    }

    private fun spawnBoss() {
        lastLostPickup.clearActions()
        objectivesLabel.setMyText(myBundle!!.get("objective1"))
        BaseGame.level2IntroMusic!!.stop()
        BaseGame.level2Music!!.stop()
        GameUtils.playAndLoopMusic(BaseGame.bossMusic)
        val position = bossSpawn()
        BossKG(position.x, position.y, mainStage, player)
        bossBar = BossBar(0f, Gdx.graphics.height.toFloat(), uiStage, "Kjersti Gulbrandsen")
        bossBar!!.countDown()
        enemySpawner1.clearActions()
        enemySpawner2.clearActions()
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral13"), 5f)
    }

    private fun handleBoss() {
        for (enemy: BaseActor in BaseActor.getList(mainStage, BossKG::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy as BossKG, false, player.health)
            handleDestructibles(enemy)
            if (bossBar != null && bossBar!!.complete && !enemy.isDying) {
                enemy.death()
                bossDeath()
            }
        }
    }

    private fun bossDeath() {
        objectivesLabel.fadeOut()
        BaseGame.bossMusic!!.stop()
        BaseGame.windAmbianceMusic!!.play()
        BaseGame.windAmbianceMusic!!.volume = BaseGame.musicVolume
        experienceBar.level++
        if (bossBar != null)
            bossBar!!.isVisible = false
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral14"))
        for (enemy: BaseActor in BaseActor.getList(mainStage, Follower::class.java.canonicalName)) {
            enemy.death()
        }
        for (enemy: BaseActor in BaseActor.getList(
            mainStage,
            TeleportHazard::class.java.canonicalName
        )) {
            enemy.death()
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shot::class.java.canonicalName)) {
            enemy.death()
        }
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(6f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral6"), 9f)
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
            Actions.delay(1.3f),
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
            Actions.delay(2.7f),
            Actions.run {
                var position = spawnAroundPlayer(50f)
                Follower(position.x, position.y, mainStage, player)
            }
        )))
    }

    private fun spawnLost0() {
        var position = spawnAtEdgesOfMap(10f)
        lost0 = Lost0(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral8"), 7f)
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(3f),
                Actions.run {
                    if (!lost0.isPickedUp)
                        objectivesLabel.setMyText(myBundle!!.get("objective11"))
                    else
                        objectivesLabel.setMyText(myBundle!!.get("objective2"))
                },
                Actions.delay(1f),
                Actions.run { GameUtils.playAndLoopMusic(BaseGame.level2IntroMusic) }
            )
        )
    }

    private fun spawnLost1() {
        var position = spawnAtEdgesOfMap(10f)
        lost1 = Lost1(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral10"), 5f)
        objectivesLabel.glintToWhiteAndBack()
    }

    private fun spawnLost2() {
        var position = spawnAtEdgesOfMap(10f)
        lost2 = Lost2(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral15"), 5f)
        objectivesLabel.glintToWhiteAndBack()
    }

    private fun spawnLost3() {
        var position = spawnAtEdgesOfMap(10f)
        lost3 = Lost3(position.x, position.y, mainStage)
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral11"), 5f)
        objectivesLabel.glintToWhiteAndBack()
    }

    private fun lost0Pickup() {
        if (lost0.isPickedUp) {
            lost0.isPickedUp = false
            objectivesLabel.setMyText(myBundle!!.get("objective2"))
            fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral16"), 6f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                Actions.delay(7f),
                Actions.run { spawnLost1() }
            ))
        }
    }

    private fun lost1Pickup() {
        if (lost1 != null && lost1!!.isPickedUp) {
            objectivesLabel.setMyText(myBundle!!.get("objective3"))
            lost1!!.isPickedUp = false
            BaseGame.level2IntroMusic!!.stop()
            GameUtils.playAndLoopMusic(BaseGame.level2Music)
            spawnFollowers()
            fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral9"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(
                Actions.sequence(
                    Actions.delay(25f),
                    Actions.run { spawnLost2() }
                )
            )
        }
    }

    private fun lost2Pickup() {
        if (lost2 != null && lost2!!.isPickedUp) {
            spawnEnemies()
            objectivesLabel.setMyText(myBundle!!.get("objective4"))
            lost2!!.isPickedUp = false
            fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral12"), 5f)
            BaseActor(0f, 0f, mainStage).addAction(Actions.parallel(
                Actions.sequence(
                    Actions.delay(10f),
                    Actions.run { dropShield() }
                ),
                Actions.sequence(
                    Actions.delay(25f),
                    Actions.run { spawnLost3() }
                )
            ))
        }
    }

    private fun lost3Pickup() {
        if (lost3 != null && lost3!!.isPickedUp) {
            objectivesLabel.setMyText(myBundle!!.get("objective5"))
            lastLostPickup.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { objectivesLabel.fadeOut() },
                Actions.delay(3f),
                Actions.run { objectivesLabel.setMyText(myBundle!!.get("objective0")) }
            ))
            lost3!!.isPickedUp = false
            isReadyToSpawnBoss = true

            enemySpawner1.clearActions()
            enemySpawner2.clearActions()
        }
    }
}
