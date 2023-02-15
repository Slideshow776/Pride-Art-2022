package no.sandramoen.transagentx.screens.gameplay

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.transagentx.actors.Portal
import no.sandramoen.transagentx.actors.TilemapActor
import no.sandramoen.transagentx.actors.characters.enemies.*
import no.sandramoen.transagentx.actors.characters.lost.Lost1
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.BaseGame.Companion.myBundle
import no.sandramoen.transagentx.utils.GameUtils

class Level3 : BaseLevel() {
    private val lostSoulsSpawner = BaseActor(0f, 0f, mainStage)
    private lateinit var orangePortal: Portal
    private lateinit var bluePortal: Portal
    private var isLevel2Reached = false
    private var isLevel3Reached = false
    private var isLevel4Reached = false
    private var isLevelOver = false

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level3, mainStage)
        super.initialize()

        var position = randomWorldPosition(10f)
        orangePortal = Portal(position.x, position.y, mainStage, orange = true, player = player)
        position = randomWorldPosition(10f)
        bluePortal = Portal(position.x, position.y, mainStage, orange = false, player = player)

        intro()
    }

    override fun update(dt: Float) {
        super.update(dt)
        checkIfOverlapPortals()

        if (experienceBar.level == 2 && !isLevel2Reached)
            triggerChapter1()
        if (experienceBar.level == 3 && !isLevel3Reached)
            triggerChapter2()
        if (experienceBar.level == 4 && !isLevel4Reached)
            triggerChapter3()

        checkIfPlayerEnteredBossPortal()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(keycode)) {
            BaseGame.windAmbianceMusic!!.stop()
            BaseGame.setActiveScreen(Level3())
        }
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isRestartable && !isButtonCodeDpad(buttonCode)) {
            BaseGame.windAmbianceMusic!!.stop()
            BaseGame.setActiveScreen(Level3())
        }
        return super.buttonDown(controller, buttonCode)
    }

    private fun checkIfPlayerEnteredBossPortal() {
        for (bossPortal: BaseActor in BaseActor.getList(
            mainStage,
            BossPortal::class.java.canonicalName
        )) {
            if (player.overlaps(bossPortal) && !isLevelOver) {
                player.isInvulnerable = true
                player.disableShield()
                fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral18"))
                isLevelOver = true
                player.isPlaying = false
                BaseGame.setSteamAchievement("ACHIEVEMENT_LEVEL_3")
                player.addAction(
                    Actions.sequence(
                        Actions.parallel(
                            Actions.run {
                                player.fadeOut()
                                bossPortal as BossPortal
                                bossPortal.stopEffect()
                            },
                            Actions.moveTo(
                                bossPortal.x + bossPortal.width / 2,
                                bossPortal.y + bossPortal.height / 2,
                                3f
                            )
                        ),
                        Actions.delay(.5f),
                        Actions.run {
                            bossPortal as BossPortal
                            bossPortal.close()
                        },
                        Actions.delay(2f),
                        Actions.run { BaseGame.setActiveScreen(Level4()) }
                    )
                )
            }
        }
    }

    private fun intro() {
        BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
            Actions.delay(1f),
            Actions.run { fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral19"), 4f) },
            Actions.delay(4f),
            Actions.run { fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral20"), 5f) },
            Actions.delay(10f),
            Actions.run {
                objectivesLabel.setMyText(myBundle!!.get("objective0"))
                GameUtils.playAndLoopMusic(BaseGame.level3Music)
                spawnBeamers(4f)
            },
            Actions.delay(10f),
            Actions.run { spawnBeamers(3f) }
        ))
    }

    private fun triggerChapter1() {
        isLevel2Reached = true
        BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
            Actions.delay(4f),
            Actions.run {
                fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral24"), 5f)
                spawnFollowers(25f)
                spawnLostSouls()
            }
        ))
    }

    private fun triggerChapter2() {
        isLevel3Reached = true
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral25"), 5f)
        BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
            Actions.delay(10f),
            Actions.run { dropShield() }
        ))
        spawnChargers(5f)
        spawnShooters(3f)
    }

    private fun triggerChapter3() {
        isLevel4Reached = true
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral26"), 5f)
        enemySpawner1.clearActions()
        enemySpawner2.clearActions()
        objectivesLabel.setMyText(myBundle!!.get("objective10"))
        BossPortal(143.5f, 143.5f, mainStage, player)
    }

    private fun spawnLostSouls() {
        lostSoulsSpawner.addAction(Actions.forever(Actions.sequence(
            Actions.delay(25f),
            Actions.run {
                val position = randomWorldPosition()
                when (MathUtils.random(0, 3)) {
                    0 -> Lost1(position.x, position.y, mainStage)
                    1 -> Lost1(position.x, position.y, mainStage)
                    2 -> Lost1(position.x, position.y, mainStage)
                    3 -> Lost1(position.x, position.y, mainStage)
                }
            }
        )))
    }

    private fun checkIfOverlapPortals() {
        if (player.overlaps(bluePortal)) {
            GameUtils.vibrateController()
            player.setPosition(
                (orangePortal.x + orangePortal.width / 2) - player.width / 2,
                (orangePortal.y + orangePortal.height / 2) - player.height / 2
            )
            setNewPortalsPositions()
        }

        if (player.overlaps(orangePortal)) {
            GameUtils.vibrateController()
            player.setPosition(
                (bluePortal.x + bluePortal.width / 2) - player.width / 2,
                (bluePortal.y + bluePortal.height / 2) - player.height / 2
            )
            setNewPortalsPositions()
        }
    }

    private fun setNewPortalsPositions() {
        BaseGame.portalSound!!.play(BaseGame.soundVolume)
        bluePortal.setNewPosition(randomWorldPosition(10f))
        orangePortal.setNewPosition(randomWorldPosition(10f))
    }

    private fun spawnFairies() {
        BaseActor(0f, 0f, mainStage).addAction(Actions.forever(Actions.sequence(
            Actions.delay(1f),
            Actions.run {
                val position = spawnAroundPlayer(50f)
                Fairy(position.x, position.y, mainStage, player)
            }
        )))
    }

    private fun spawnBeamers(frequency: Float) {
        enemySpawner1.clearActions()
        enemySpawner1 = BaseActor(0f, 0f, mainStage)
        enemySpawner1.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(frequency),
                    Actions.run {
                        val position = spawnAroundPlayer(50f)
                        Beamer(position.x, position.y, mainStage, player)
                    }
                )
            )
        )
    }

    private fun spawnFollowers(frequency: Float) {
        enemySpawner1.addAction(Actions.forever(Actions.sequence(
            Actions.delay(frequency),
            Actions.run {
                val position = spawnAroundPlayer(50f)
                Follower(position.x, position.y, mainStage, player)
            }
        )))
    }

    private fun spawnChargers(frequency: Float) {
        enemySpawner1.addAction(Actions.forever(Actions.sequence(
            Actions.delay(frequency),
            Actions.run {
                val position = spawnAroundPlayer(50f)
                Charger(position.x, position.y, mainStage, player)
            }
        )))
    }

    private fun spawnShooters(frequency: Float) {
        enemySpawner1.addAction(Actions.forever(Actions.sequence(
            Actions.delay(frequency),
            Actions.run {
                val position = spawnAroundPlayer(50f)
                Shooter(position.x, position.y, mainStage, player)
            }
        )))
    }
}
