package no.sandramoen.prideart2022.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.actors.Enemy
import no.sandramoen.prideart2022.actors.Experience
import no.sandramoen.prideart2022.actors.Player
import no.sandramoen.prideart2022.myUI.ExperienceBar
import no.sandramoen.prideart2022.utils.*

class LevelScreen : BaseScreen() {
    private lateinit var ground: BaseActor
    private lateinit var player: Player
    private lateinit var enemySpawner: BaseActor
    private var isGameOver = false

    private lateinit var gameOverLabel: Label
    private lateinit var experienceBar: ExperienceBar

    override fun initialize() {
        ground = BaseActor(0f, 0f, mainStage)
        // ground.loadImage("ground")
        ground.loadTexture("images/excluded/ground1.png")
        ground.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT)

        player = Player(mainStage)

        spawnEnemies()

        uiSetup()
    }

    override fun update(dt: Float) {
        if (isGameOver) return
        handleEnemies()

        for (experience: BaseActor in BaseActor.getList(mainStage, Experience::class.java.canonicalName)) {
            if (player.overlaps(experience)) {
                experience as Experience
                experienceBar.increment(experience.amount)
                experience.remove()
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.R) BaseGame.setActiveScreen(LevelScreen())
        if (keycode == Keys.Q) Gdx.app.exit()
        if (keycode == Keys.E) experienceBar.increment(1)
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
        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            player.preventOverlap(enemy)
            if (player.overlaps(enemy) && !isGameOver) {
                isGameOver = true
                gameOverLabel.isVisible = true
                pauseLevel()
            }
        }
    }

    private fun spawnEnemies() {
        enemySpawner = BaseActor(0f, 0f, mainStage)
        enemySpawner.addAction(Actions.forever(Actions.sequence(
            Actions.delay(1f),
            Actions.run {
                val range = 30f
                val xPos = if (MathUtils.randomBoolean()) player.x - range else player.x + range
                val yPos = if (MathUtils.randomBoolean()) player.y - range else player.y + range
                Enemy(xPos, yPos, mainStage, player)
            }
        )))
    }

    private fun pauseLevel() {
        enemySpawner.clearActions()
        player.pause = true
        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName))
            enemy.pause = true
    }

    private fun uiSetup() {
        gameOverLabel = Label("Game Over!", BaseGame.bigLabelStyle)
        gameOverLabel.isVisible = false
        uiTable.add(gameOverLabel)

        experienceBar = ExperienceBar(0f, Gdx.graphics.height.toFloat(), uiStage)
    }
}
