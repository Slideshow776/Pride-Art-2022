package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.actors.*
import no.sandramoen.prideart2022.actors.characters.FleetAdmiral
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.actors.characters.player.BeamOut
import no.sandramoen.prideart2022.actors.GroundCrack
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.ui.BossBar
import no.sandramoen.prideart2022.ui.ControllerMessage
import no.sandramoen.prideart2022.ui.ExperienceBar
import no.sandramoen.prideart2022.ui.HealthBar
import no.sandramoen.prideart2022.utils.*

open class BaseLevel : BaseScreen() {
    protected lateinit var player: Player
    protected lateinit var tilemap: TilemapActor
    protected lateinit var enemySpawner: BaseActor
    protected lateinit var mainLabel: Label
    protected lateinit var experienceBar: ExperienceBar
    protected lateinit var bossBar: BossBar
    protected lateinit var healthBar: HealthBar
    protected lateinit var controllerMessage: ControllerMessage
    protected lateinit var fleetAdmiral: FleetAdmiral
    protected lateinit var fleetAdmiralSubtitles: Label

    protected var isGameOver = false

    override fun initialize() {
        Vignette(uiStage)
        uiSetup()
    }

    override fun update(dt: Float) {
        if (isGameOver) return
        handleEnemies()
        handlePickups()
        handleDestructibles(player)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode != Input.Keys.ESCAPE && dtModifier == 0f) resume()

        if (keycode == Input.Keys.R) BaseGame.setActiveScreen(Level1())
        else if (keycode == Input.Keys.Q) Gdx.app.exit()
        else if (keycode == Input.Keys.E) experienceBar.increment(1)
        else if (keycode == Input.Keys.NUM_1) setGameOver()
        else if (keycode == Input.Keys.NUM_2) {
            if (player.health > 0) {
                player.hit(1)
                player.health--
                healthBar.subtractHealth()
                dropHealth()
            }
        } else if (keycode == Input.Keys.NUM_3) playerExitLevel()
        else if (keycode == Input.Keys.ESCAPE) pauseOrGoMenu()
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if (buttonCode != XBoxGamepad.BUTTON_BACK && dtModifier == 0f) resume()

        if (buttonCode == XBoxGamepad.BUTTON_A)
            player.flashColor(Color.GREEN)
        else if (buttonCode == XBoxGamepad.BUTTON_B)
            player.flashColor(Color.RED)
        else if (buttonCode == XBoxGamepad.BUTTON_X)
            player.flashColor(Color.BLUE)
        else if (buttonCode == XBoxGamepad.BUTTON_Y)
            player.flashColor(Color.YELLOW)
        else if (buttonCode == XBoxGamepad.BUTTON_START)
            BaseGame.setActiveScreen(Level1())
        else if (buttonCode == XBoxGamepad.BUTTON_BACK)
            pauseOrGoMenu()

        return super.buttonDown(controller, buttonCode)
    }

    override fun connected(controller: Controller?) {
        if (controller!!.canVibrate() && BaseGame.isVibrationEnabled)
            controller!!.startVibration(1000, .2f)
        controllerMessage.showConnected()
        BaseGame.controllerConnectedSound!!.play(BaseGame.soundVolume)
        pause()
    }

    override fun disconnected(controller: Controller?) {
        controllerMessage.showDisConnected()
        BaseGame.controllerDisconnectedSound!!.play(BaseGame.soundVolume)
        pause()
    }

    override fun resume() {
        super.resume()
        if (!isGameOver) {
            dtModifier = 1f
            unpauseMainLabel()
        }
    }

    override fun pause() {
        super.pause()
        if (!isGameOver) {
            dtModifier = 0f
            setMainLabelToPaused()
        }
    }

    private fun pauseOrGoMenu() {
        if (dtModifier == 0f) {
            setMenuScreen()
        } else {
            pause()
            BaseGame.controllerDisconnectedSound!!.play(BaseGame.soundVolume)
        }
    }

    private fun setMenuScreen() {
        BaseGame.setActiveScreen(MenuScreen())
        BaseGame.levelMusic!!.stop()
    }

    fun initializePlayer() {
        val startPoint = tilemap.getRectangleList("player start")[0]
        val playerPosX = startPoint.properties.get("x") as Float * TilemapActor.unitScale
        val playerPosY = startPoint.properties.get("y") as Float * TilemapActor.unitScale

        val groundCrack = GroundCrack(0f, 0f, mainStage)
        player = Player(playerPosX, playerPosY, mainStage)
        groundCrack.centerAtActor(player)
    }

    fun initializeDestructibles() {
        for (i in 0 until 55) {
            Destructible(
                MathUtils.random(0f, BaseActor.getWorldBounds().width),
                MathUtils.random(0f, BaseActor.getWorldBounds().height),
                mainStage,
                player
            )
        }
    }

    private fun unpauseMainLabel() {
        mainLabel.isVisible = false
        mainLabel.clearActions()
        mainLabel.color.a = 1f
    }

    private fun setMainLabelToPaused() {
        mainLabel.setText(BaseGame.myBundle!!.get("paused"))
        GameUtils.pulseWidget(mainLabel)
        mainLabel.isVisible = true
    }

    private fun playerExitLevel() {
        BeamOut(player.x, player.y, mainStage, player)
        player.isPlaying = false
        player.addAction(
            Actions.parallel(
                Actions.scaleTo(.1f, 3f, BeamOut.animationDuration),
                Actions.moveBy(0f, 100f, BeamOut.animationDuration, Interpolation.circleIn),
                Actions.fadeOut(BeamOut.animationDuration, Interpolation.circleIn)
            )
        )
    }

    private fun handleEnemies() {
        for (enemy: BaseActor in BaseActor.getList(mainStage, Boss0::class.java.canonicalName)) {
            enemy as Boss0
            enemyCollidedWithPlayer(enemy, false, player.health)
            handleDestructibles(enemy)
            if (bossBar.complete && !enemy.dying) {
                enemy.death()
                bossDeath()
            }
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Charger::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shooter::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Shot::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, true, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, Beam::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in BaseActor.getList(mainStage, GhostFreed::class.java.canonicalName)) {
            player.preventOverlap(enemy)
        }
    }

    private fun bossDeath() {
        experienceBar.level++
        bossBar.isVisible = false
        fadeFleetAdmiralInAndOut("Godt jobba! Han kan ikke plage noen mere nå")
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.delay(5f),
                Actions.run {
                    fadeFleetAdmiralInAndOut(
                        "Artefakten er ikke her. Vi trenger å gå dypere inn i riksen...",
                        5f
                    )
                },
                Actions.delay(5f),
                Actions.run { playerExitLevel() }
            ))
    }

    private fun handlePickups() {
        for (experience: BaseActor in BaseActor.getList(
            mainStage,
            Experience::class.java.canonicalName
        )) {
            if (player.overlaps(experience as Experience)) {
                val isLevelUp = experienceBar.increment(experience.amount)
                experience.pickup()
                if (
                    experienceBar.level == 2 &&
                    BaseActor.count(mainStage, Boss0::class.java.canonicalName) == 0
                ) {
                    Boss0(player.x + 20f, player.y + 20f, mainStage, player)
                    bossBar.countDown()
                    enemySpawner.clearActions()
                    fadeFleetAdmiralInAndOut(
                        "Kim Alexander Tønseth!\nDenne fascisten gjorde mye vold mot transfolk!",
                        5f
                    )
                } else if (isLevelUp)
                    fadeFleetAdmiralInAndOut("Ja! Fortsett å provosere dem")
            }
        }
        for (healthDrop: BaseActor in BaseActor.getList(
            mainStage,
            HealthDrop::class.java.canonicalName
        )) {
            if (player.overlaps(healthDrop as HealthDrop)) {
                if (healthBar.addHealth()) {
                    player.healthBack()
                    healthDrop.pickup(true)
                }
                healthDrop.pickup(false)
            }
        }
    }

    private fun handleDestructibles(baseActor: BaseActor) {
        for (destructible: BaseActor in BaseActor.getList(
            mainStage,
            Destructible::class.java.canonicalName
        )) {
            destructible as Destructible
            if (baseActor.overlaps(destructible)) {
                destructible.destroy()
            }
        }
    }

    fun spawnEnemies() {
        enemySpawner = BaseActor(0f, 0f, mainStage)
        enemySpawner.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(2.4f),
                    Actions.run {
                        val range = 30f
                        val xPos =
                            if (MathUtils.randomBoolean()) player.x - range else player.x + range
                        val yPos =
                            if (MathUtils.randomBoolean()) player.y - range else player.y + range
                        Charger(xPos, yPos, mainStage, player)
                        Shooter(xPos, yPos, mainStage, player)
                    }
                )))
    }

    private fun pauseGameForDuration(duration: Float = .05f) {
        val temp = dtModifier
        dtModifier = 0f
        BaseActor(0f, 0f, uiStage).addAction(
            Actions.sequence(
                Actions.delay(duration),
                Actions.run { dtModifier = temp }
            ))
    }

    private fun enemyCollidedWithPlayer(enemy: BaseActor, remove: Boolean, damageAmount: Int) {
        player.preventOverlap(enemy)
        if (player.overlaps(enemy) && !isGameOver) {
            player.hit(damageAmount)
            if (player.health <= 0) setGameOver()
            else {
                dropHealth()
                pauseGameForDuration()
            }
            BaseGame.playerDeathSound!!.play(BaseGame.soundVolume, 1.5f, 0f)
            healthBar.subtractHealth()
            if (remove) enemy.death()
        }
    }

    private fun setGameOver() {
        isGameOver = true
        mainLabel.isVisible = true
        mainLabel.setText(BaseGame.myBundle!!.get("gameOver"))
        dtModifier = .125f
        player.death()
        BaseGame.groundCrackSound!!.play(BaseGame.soundVolume)
        fadeFleetAdmiralInAndOut("Det er for farlig! kom tilbake!")
    }

    private fun dropHealth() {
        fadeFleetAdmiralInAndOut("Jeg droppa helse til deg!")
        HealthDrop(
            MathUtils.random(10f, BaseActor.getWorldBounds().width - 10f),
            MathUtils.random(10f, BaseActor.getWorldBounds().height - 10f),
            mainStage,
            player
        )
    }

    private fun fadeFleetAdmiralInAndOut(subtitles: String, talkDuration: Float = 3f) {
        fleetAdmiral.clearActions()
        fleetAdmiral.fadeIn()
        fleetAdmiral.talk()
        fleetAdmiralSubtitles.addAction(Actions.fadeIn(1f))
        fleetAdmiralSubtitles.setText(subtitles)
        fleetAdmiral.addAction(
            Actions.sequence(
                Actions.delay(talkDuration),
                Actions.run {
                    fleetAdmiral.stopTalking()
                    fleetAdmiral.fadeOut()
                    fleetAdmiralSubtitles.addAction(Actions.fadeOut(1f))
                }
            ))
    }

    fun uiSetup() {
        experienceBar = ExperienceBar(0f, Gdx.graphics.height.toFloat(), uiStage)
        bossBar = BossBar(0f, Gdx.graphics.height.toFloat(), uiStage)

        healthBar = HealthBar()
        val padding = Gdx.graphics.height * .05f
        uiTable.add(healthBar).padTop(padding).padBottom(-healthBar.prefHeight - padding)
            .row()

        fleetAdmiral = FleetAdmiral(0f, 0f, uiStage)
        fleetAdmiral.scaleBy(100f)
        fleetAdmiral.setPosition(
            Gdx.graphics.width * .1f,
            Gdx.graphics.height - fleetAdmiral.height * 100f
        )

        fleetAdmiralSubtitles = Label("", BaseGame.smallLabelStyle)
        fleetAdmiralSubtitles.setPosition(fleetAdmiral.x - 50f, fleetAdmiral.y - 150)
        uiStage.addActor(fleetAdmiralSubtitles)

        mainLabel = Label("", BaseGame.bigLabelStyle)
        mainLabel.isVisible = false
        uiTable.add(mainLabel).expandY().row()

        controllerMessage = ControllerMessage()
        uiTable.add(controllerMessage)
            .padTop(-controllerMessage.prefHeight - Gdx.graphics.height * .1f)
            .padBottom(Gdx.graphics.height * .1f)
    }
}
