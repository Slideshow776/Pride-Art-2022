package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.TheArtifact
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
    private val lostSoulSpawner = BaseActor(0f, 0f, mainStage)

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level4, mainStage)
        super.initialize()

        spawnBoss()
        spawnLostSouls()
        objectivesLabel.setMyText("${myBundle!!.get("objective1")}")
    }

    override fun keyDown(keycode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(keycode)) {
            BaseGame.windAmbianceMusic!!.stop()
            BaseGame.setActiveScreen(Level4())
        }
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(buttonCode)) {
            BaseGame.windAmbianceMusic!!.stop()
            BaseGame.setActiveScreen(Level4())
        }
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
        handleTheArtifact()
    }

    private fun handleTheArtifact() {
        for (artifact: BaseActor in BaseActor.getList(
            mainStage,
            TheArtifact::class.java.canonicalName
        )) {
            if (player.overlaps(artifact)) {
                artifact.death()
                artifact.isCollisionEnabled = false
                player.smile()
                fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral33"))
                BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                    Actions.delay(5f),
                    Actions.run { playerExitLevel() },
                    Actions.delay(2f),
                    Actions.run {
                        BaseGame.level1Music!!.stop()
                        BaseGame.setActiveScreen(Level5())
                    }
                ))
            }
        }
    }

    private fun handleBoss() {
        for (enemy: BaseActor in BaseActor.getList(
            mainStage,
            BossIra::class.java.canonicalName
        )) {
            enemyCollidedWithPlayer(enemy as BossIra, false, player.health)
            handleDestructibles(enemy)
            if (bossBar != null && bossBar!!.complete && !enemy.isDying) {
                TheArtifact(enemy.x, enemy.y, mainStage)
                enemy.death()
                bossDeath()
            }
        }
    }

    private fun bossDeath() {
        BaseGame.bossMusic!!.stop()
        BaseGame.windAmbianceMusic!!.play()
        BaseGame.windAmbianceMusic!!.volume = BaseGame.musicVolume
        lostSoulSpawner.clearActions()
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
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral14"))
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(4f),
                Actions.run { fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral32"), 6f) }
            )
        )
    }

    private fun spawnLostSouls() {
        lostSoulSpawner.addAction(Actions.forever(Actions.sequence(
            Actions.delay(15f),
            Actions.run {
                if (bossIra!!.lost == null) {
                    fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral27"))
                    objectivesLabel.setMyText(myBundle!!.get("objective9"))
                    objectivesLabel.fadeIn()
                    var position = randomWorldPosition(50f)
                    when (MathUtils.random(0, 3)) {
                        0 -> lost = Lost0(position.x, position.y, mainStage)
                        1 -> lost = Lost1(position.x, position.y, mainStage)
                        2 -> lost = Lost2(position.x, position.y, mainStage)
                        3 -> lost = Lost3(position.x, position.y, mainStage)
                    }

                    while (lost?.let { bossIra!!.isWithinDistance2(40f, it) } == true) {
                        position = randomWorldPosition(50f)
                        println("${MathUtils.random(1000, 9999)} lost too close, repositioning: ${position.x}, ${position.y}")
                        lost!!.setPosition(position.x, position.y)
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
