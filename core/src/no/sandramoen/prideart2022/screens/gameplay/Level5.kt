package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.*
import no.sandramoen.prideart2022.actors.characters.lost.Lost1
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseGame.Companion.myBundle
import no.sandramoen.prideart2022.utils.GameUtils

class Level5 : BaseLevel() {
    private val lostSoulsSpawner = BaseActor(0f, 0f, mainStage)

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level4, mainStage)
        super.initialize()

        triggerLevelProgression()
        playMusicWithDelay()
    }

    private fun playMusicWithDelay() {
        BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
            Actions.delay(5f),
            Actions.run { GameUtils.playAndLoopMusic(BaseGame.menuMusic) }
        ))
    }

    private fun triggerLevelProgression() {
        val rain = Rain(0f, 0f, uiStage)
        rain.color = Color(0.647f, 0.188f, 0.188f, 1f) // red
        objectivesLabel.setMyText(myBundle!!.get("objective7"))
        player.shakyCamIntensity = .0125f
        player.isShakyCam = true
        spawnSpace()
        spawnRainSplatter()
        GameUtils.playAndLoopMusic(BaseGame.rainMusic)
        spawnLostSouls()
        DarkThunder(uiStage)
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.run { fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral28")) },
                Actions.delay(3.5f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral29"))
                    player.shakyCamIntensity = .0125f
                },
                Actions.delay(2f),
                Actions.run { objectivesLabel.fadeIn() },
                Actions.delay(38f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral30"))
                    lostSoulsSpawner.clearActions()
                    player.shakyCamIntensity = .05f
                },
                Actions.delay(20f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(
                        myBundle!!.get("fleetAdmiral31"),
                        6f
                    )
                    player.shakyCamIntensity = .1f
                    objectivesLabel.fadeOut()
                },
                Actions.delay(6f),
                Actions.run {
                    playerExitLevel()
                    player.isShakyCam = false
                },
                Actions.delay(3f),
                Actions.run {
                    BaseGame.rainMusic!!.stop()
                    BaseGame.thunderSound!!.play(
                        BaseGame.soundVolume,
                        MathUtils.random(.5f, 1.5f),
                        0f
                    )
                    BaseGame.setActiveScreen(MenuScreen())
                }
            ))
    }

    private fun spawnRainSplatter() {
        BaseActor(0f, 0f, mainStage).addAction(Actions.forever(Actions.sequence(
            Actions.delay(0f),
            Actions.run {
                for (i in 0..3) {
                    val position = randomWorldPosition(0f)
                    val rainSplatter = RainSplatter(position.x, position.y, mainStage)
                    rainSplatter.color = Color(0.647f, 0.188f, 0.188f, 1f) // red
                }
            }
        )))
    }

    private fun spawnSpace() {
        BaseActor(0f, 0f, mainStage).addAction(Actions.forever(Actions.sequence(
            Actions.delay(.075f),
            Actions.run {
                val position = randomWorldPosition(0f)
                SpaceIsThePlace(position.x, position.y, mainStage)
            }
        )))
    }

    private fun spawnLostSouls() {
        lostSoulsSpawner.addAction(Actions.forever(Actions.sequence(
            Actions.delay(2f),
            Actions.run {
                val position = randomWorldPosition()
                Lost1(position.x, position.y, mainStage, player)
            }
        )))
    }
}