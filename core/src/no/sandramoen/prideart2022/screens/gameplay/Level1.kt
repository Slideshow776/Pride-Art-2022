package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.BossKim
import no.sandramoen.prideart2022.actors.characters.enemies.Charger
import no.sandramoen.prideart2022.actors.characters.enemies.Shooter
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level1 : BaseLevel() {
    private var isSpawnedBoss = false

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level1, mainStage)
        super.initialize()

        spawnEnemyChargers()
        BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
            Actions.delay(30f),
            Actions.run { spawnEnemyShooters() }
        ))
        GameUtils.playAndLoopMusic(BaseGame.levelMusic)
    }

    override fun update(dt: Float) {
        super.update(dt)
        handleBoss()

        if (experienceBar.level == 3 && !isSpawnedBoss) {
            isSpawnedBoss = true
            spawnBoss()
        }
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
        BossKim(player.x + 20f, player.y + 20f, mainStage, player)
        bossBar.countDown()
        enemySpawner1.clearActions()
        enemySpawner2.clearActions()
        fadeFleetAdmiralInAndOut(
            BaseGame.myBundle!!.get("fleetAdmiral4"),
            5f
        )
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
}
