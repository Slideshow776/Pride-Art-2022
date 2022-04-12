package no.sandramoen.prideart2022.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.actors.enemies.Charger
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.player.Player
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.actors.Vignette
import no.sandramoen.prideart2022.actors.enemies.Shooter
import no.sandramoen.prideart2022.actors.enemies.Shot
import no.sandramoen.prideart2022.actors.player.GroundCrack
import no.sandramoen.prideart2022.myUI.ExperienceBar
import no.sandramoen.prideart2022.myUI.HealthBar
import no.sandramoen.prideart2022.utils.*

class LevelScreen : BaseScreen() {
    private lateinit var player: Player
    private lateinit var tilemap: TilemapActor
    private lateinit var enemySpawner: BaseActor
    private var isGameOver = false

    private lateinit var gameOverLabel: Label
    private lateinit var experienceBar: ExperienceBar
    private lateinit var healthBar: HealthBar

    override fun initialize() {
        tilemap = TilemapActor(BaseGame.level1, mainStage)
        Vignette(uiStage)

        val tintOverlay = BaseActor(0f, 0f, mainStage)
        tintOverlay.loadImage("whitePixel")
        tintOverlay.color = Color(0f, 0f, 0f, .6f)
        tintOverlay.setSize(BaseActor.getWorldBounds().width, BaseActor.getWorldBounds().height)

        val startPoint = tilemap.getRectangleList("player start")[0]
        val playerPosX = startPoint.properties.get("x") as Float * TilemapActor.unitScale
        val playerPosY = startPoint.properties.get("y") as Float * TilemapActor.unitScale

        val groundCrack = GroundCrack(0f, 0f, mainStage)
        player = Player(playerPosX, playerPosY, mainStage)
        groundCrack.centerAtActor(player)

        spawnEnemies()
        uiSetup()

        /*GameUtils.playAndLoopMusic(BaseGame.levelMusic)*/
        Gdx.input.setCursorPosition(Gdx.graphics.width / 2, Gdx.graphics.height + 10)
    }

    override fun update(dt: Float) {
        if (isGameOver) return
        handleEnemies()
        handlePickups()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.R) BaseGame.setActiveScreen(LevelScreen())
        if (keycode == Keys.Q) Gdx.app.exit()
        if (keycode == Keys.E) experienceBar.increment(1)
        if (keycode == Keys.NUM_1) setGameOver()
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (buttonCode == XBoxGamepad.BUTTON_A)
            player.flashColor(Color.GREEN)
        if (buttonCode == XBoxGamepad.BUTTON_B)
            player.flashColor(Color.RED)
        if (buttonCode == XBoxGamepad.BUTTON_X)
            player.flashColor(Color.BLUE)
        if (buttonCode == XBoxGamepad.BUTTON_Y)
            player.flashColor(Color.YELLOW)
        if (buttonCode == XBoxGamepad.BUTTON_START)
            BaseGame.setActiveScreen(LevelScreen())
        return super.buttonDown(controller, buttonCode)
    }

    private fun handleEnemies() {
        for (enemy: BaseActor in BaseActor.getList(mainStage, Charger::class.java.canonicalName))
            enemyCollidedWithPlayer(enemy)
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shooter::class.java.canonicalName))
            enemyCollidedWithPlayer(enemy)
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shot::class.java.canonicalName))
            enemyCollidedWithPlayer(enemy, remove = true)
    }

    private fun handlePickups() {
        for (experience: BaseActor in BaseActor.getList(
            mainStage,
            Experience::class.java.canonicalName
        )) {
            if (player.overlaps(experience as Experience)) {
                experienceBar.increment(experience.amount)
                experience.pickup()
            }
        }
    }

    private fun spawnEnemies() {
        enemySpawner = BaseActor(0f, 0f, mainStage)
        enemySpawner.addAction(Actions.forever(Actions.sequence(
            Actions.delay(2.5f),
            Actions.run {
                val range = 30f
                val xPos = if (MathUtils.randomBoolean()) player.x - range else player.x + range
                val yPos = if (MathUtils.randomBoolean()) player.y - range else player.y + range
                Charger(xPos, yPos, mainStage, player)
                Shooter(xPos, yPos, mainStage, player)
            }
        )))
    }

    private fun enemyCollidedWithPlayer(enemy: BaseActor, remove: Boolean = false) {
        player.preventOverlap(enemy)
        if (player.overlaps(enemy) && !isGameOver) {
            if (player.health <= 0) setGameOver()
            else player.hit()
            BaseGame.playerDeathSound!!.play(BaseGame.soundVolume, 1.5f, 0f)
            healthBar.subtractHealth()
            if (remove) enemy.death()
        }
    }

    private fun setGameOver() {
        isGameOver = true
        gameOverLabel.isVisible = true
        dtModifier = .125f
        player.death()
    }

    private fun uiSetup() {
        experienceBar = ExperienceBar(0f, Gdx.graphics.height.toFloat(), uiStage)

        healthBar = HealthBar()
        val padding = Gdx.graphics.height * .05f
        uiTable.add(healthBar).top().padTop(padding).padBottom(-healthBar.prefHeight - padding)
            .row()

        gameOverLabel = Label("Game Over!", BaseGame.bigLabelStyle)
        gameOverLabel.isVisible = false
        uiTable.add(gameOverLabel).expandY()
    }
}
