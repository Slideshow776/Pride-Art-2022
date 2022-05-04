package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.*
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.actors.characters.lost.Lost1
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class Level3 : BaseLevel() {
    private val lostSoulsSpawner = BaseActor(0f, 0f, mainStage)
    private lateinit var orangePortal: Portal
    private lateinit var bluePortal: Portal

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level3, mainStage)
        super.initialize()

        var position = randomWorldPosition(10f)
        orangePortal = Portal(position.x, position.y, mainStage, orange = true)
        position = randomWorldPosition(10f)
        bluePortal = Portal(position.x, position.y, mainStage, orange = false)

        spawnEnemies()
        /*spawnFairies()*/
    }

    override fun update(dt: Float) {
        super.update(dt)

        if (player.overlaps(bluePortal)) {
            player.setPosition(orangePortal.x, orangePortal.y)
            setNewPortalsPositions()
        }

        if (player.overlaps(orangePortal)) {
            player.setPosition(bluePortal.x, bluePortal.y)
            setNewPortalsPositions()
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (isGameOver) BaseGame.setActiveScreen(Level3())
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (isGameOver) BaseGame.setActiveScreen(Level3())
        return super.buttonDown(controller, buttonCode)
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

    private fun spawnEnemies() {
        enemySpawner1 = BaseActor(0f, 0f, mainStage)
        enemySpawner1.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(5f),
                    Actions.run {
                        val position = spawnAroundPlayer(50f)
                        Follower(position.x, position.y, mainStage, player)
                    }
                )
            )
        )

        enemySpawner2 = BaseActor(0f, 0f, mainStage)
        enemySpawner2.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(5f),
                    Actions.run {
                        var position = spawnAroundPlayer(50f)
                        Beamer(position.x, position.y, mainStage, player)
                        position = spawnAroundPlayer(50f)
                        Shooter(position.x, position.y, mainStage, player)
                        position = spawnAroundPlayer(50f)
                        Charger(position.x, position.y, mainStage, player)
                    }
                )
            )
        )
    }
}
