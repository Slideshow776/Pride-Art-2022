package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.characters.enemies.BossIra
import no.sandramoen.prideart2022.actors.characters.enemies.Shot
import no.sandramoen.prideart2022.actors.characters.enemies.TeleportHazard
import no.sandramoen.prideart2022.actors.characters.lost.*
import no.sandramoen.prideart2022.ui.BossBar
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseGame.Companion.myBundle
import no.sandramoen.prideart2022.utils.GameUtils

class Level4 : BaseLevel() {
    private var bossIra: BossIra? = null
    private var lost: BaseLost? = null
    private var lostKilled = 0
    private val maxLostKilled = 4

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level4, mainStage)
        super.initialize()

        /*player.isCollisionEnabled = false*/
        spawnBoss()
        spawnLostSouls()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(keycode))
            BaseGame.setActiveScreen(Level4())
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(buttonCode))
            BaseGame.setActiveScreen(Level4())
        return super.buttonDown(controller, buttonCode)
    }

    override fun update(dt: Float) {
        super.update(dt)

        if (lost != null && lost!!.isPickedUp) {
            if (lost!!.isKilled)
                lostKilled++
            objectivesLabel.setMyText("$lostKilled/$maxLostKilled ${myBundle!!.get("objective8")}")
            lost = null
        }

        if (lostKilled >= 4 && !isGameOver)
            setGameOver()


        handleBoss()
    }

    private fun handleBoss() {
        for (enemy: BaseActor in BaseActor.getList(mainStage, BossIra::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy as BossIra, false, player.health)
            handleDestructibles(enemy)
            if (bossBar != null && bossBar!!.complete && !enemy.isDying) {
                enemy.death()
                bossDeath()
            }
        }
    }

    private fun bossDeath() {
        BaseGame.bossMusic!!.stop()
        for (enemy: BaseActor in BaseActor.getList(
            mainStage,
            TeleportHazard::class.java.canonicalName
        )) {
            enemy.death()
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shot::class.java.canonicalName)) {
            enemy.death()
        }
        experienceBar.level++
        if (bossBar != null)
            bossBar!!.isVisible = false
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral14"))
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
                    BaseGame.setActiveScreen(Level5())
                }
            ))
    }

    private fun spawnLostSouls() {
        BaseActor(0f, 0f, mainStage).addAction(Actions.forever(Actions.sequence(
            Actions.delay(15f),
            Actions.run {
                if (bossIra!!.lost == null) {
                    fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral27"))
                    objectivesLabel.setMyText(myBundle!!.get("objective9"))
                    objectivesLabel.fadeIn()
                    val position = randomWorldPosition(50f)
                    when (MathUtils.random(0, 3)) {
                        0 -> lost = Lost0(position.x, position.y, mainStage)
                        1 -> lost = Lost1(position.x, position.y, mainStage)
                        2 -> lost = Lost2(position.x, position.y, mainStage)
                        3 -> lost = Lost3(position.x, position.y, mainStage)
                    }
                    bossIra!!.lost = lost
                }
            }
        )))
    }

    private fun spawnBoss() {
        BaseGame.level3Music!!.stop()
        GameUtils.playAndLoopMusic(BaseGame.bossMusic)
        val position = bossSpawn()
        bossIra = BossIra(position.x, position.y, mainStage, player)
        bossBar = BossBar(0f, Gdx.graphics.height.toFloat(), uiStage, "Ira Haraldsen")
        if (bossBar != null) {
            bossBar!!.time = 120f
            bossBar!!.countDown()
        }
        enemySpawner1.clearActions()
        enemySpawner2.clearActions()
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral17"), 5f)
    }
}