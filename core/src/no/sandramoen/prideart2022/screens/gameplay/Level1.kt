package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.BossKim
import no.sandramoen.prideart2022.actors.characters.enemies.Charger
import no.sandramoen.prideart2022.actors.characters.enemies.Shooter
import no.sandramoen.prideart2022.actors.characters.enemies.Shot
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level1 : BaseLevel() {
    private var isSpawnedBoss = false

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level1, mainStage)
        super.initialize()

        spawnEnemies()
        GameUtils.playAndLoopMusic(BaseGame.level1Music)
    }

    override fun update(dt: Float) {
        super.update(dt)
        handleBoss()

        if (experienceBar.level == 3 && !isSpawnedBoss) {
            isSpawnedBoss = true
            spawnBoss()
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (isGameOver) BaseGame.setActiveScreen(Level1())
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isGameOver) BaseGame.setActiveScreen(Level1())
        return super.buttonDown(controller, buttonCode)
    }

    private fun spawnEnemies() {
        spawnEnemyChargers()
        delaySpawnShooters()
    }

    private fun delaySpawnShooters() {
        enemySpawner2.addAction(Actions.sequence(
            Actions.delay(30f),
            Actions.run { spawnEnemyShooters() }
        ))
    }

    private fun spawnEnemyChargers() {
        enemySpawner1 = BaseActor(0f, 0f, mainStage)
        enemySpawner1.addAction(Actions.forever(
            Actions.sequence(
                Actions.delay(2.4f),
                Actions.run {
                    val position = spawnAroundPlayer(50f)
                    Charger(position.x, position.y, mainStage, player)
                }
            )))
    }

    private fun spawnEnemyShooters() {
        enemySpawner2.clearActions()
        enemySpawner2 = BaseActor(0f, 0f, mainStage)
        enemySpawner2.addAction(Actions.forever(
            Actions.sequence(
                Actions.delay(2.4f),
                Actions.run {
                    val position = spawnAroundPlayer(50f)
                    Shooter(position.x, position.y, mainStage, player)
                }
            )))
    }

    private fun handleBoss() {
        for (enemy: BaseActor in BaseActor.getList(mainStage, BossKim::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy as BossKim, false, player.health)
            handleDestructibles(enemy)
            if (bossBar.complete && !enemy.isDying) {
                enemy.death()
                bossDeath()
            }
        }
    }

    private fun spawnBoss() {
        val position = bossSpawn()
        BaseGame.level1Music!!.stop()
        GameUtils.playAndLoopMusic(BaseGame.bossMusic)
        BossKim(position.x, position.y, mainStage, player)
        bossBar.label.setText("Kim Alexander Tønseth")
        bossBar.countDown()
        enemySpawner1.clearActions()
        enemySpawner2.clearActions()
        fadeFleetAdmiralInAndOut(
            BaseGame.myBundle!!.get("fleetAdmiral4"),
            5f
        )
    }

    private fun bossDeath() {
        BaseGame.bossMusic!!.stop()
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shot::class.java.canonicalName)) {
            enemy.death()
        }
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
                    BaseGame.level1Music!!.stop()
                    BaseGame.setActiveScreen(Level2())
                }
            ))
    }
}
