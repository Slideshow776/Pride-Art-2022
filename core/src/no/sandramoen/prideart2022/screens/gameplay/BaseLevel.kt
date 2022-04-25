package no.sandramoen.prideart2022.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.actors.*
import no.sandramoen.prideart2022.actors.characters.FleetAdmiral
import no.sandramoen.prideart2022.actors.characters.enemies.*
import no.sandramoen.prideart2022.actors.characters.player.BeamOut
import no.sandramoen.prideart2022.actors.GroundCrack
import no.sandramoen.prideart2022.actors.characters.player.Player
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.ui.*
import no.sandramoen.prideart2022.utils.*
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseActor.Companion.getList

open class BaseLevel : BaseScreen() {
    protected lateinit var player: Player
    protected lateinit var tilemap: TilemapActor
    protected lateinit var mainLabel: Label
    protected lateinit var crystalLabel: CrystalLabel
    protected lateinit var experienceBar: ExperienceBar
    protected lateinit var bossBar: BossBar
    protected lateinit var healthBar: HealthBar
    protected lateinit var controllerMessage: ControllerMessage
    protected lateinit var fleetAdmiral: FleetAdmiral
    protected lateinit var fleetAdmiralSubtitles: Label

    protected var isGameOver = false
    protected var enemySpawner1 = BaseActor(0f, 0f, mainStage)
    protected var enemySpawner2 = BaseActor(0f, 0f, mainStage)

    override fun initialize() {
        Vignette(uiStage)
        uiSetup()
    }

    override fun update(dt: Float) {
        if (isGameOver) return
        handleEnemies()
        handlePickups()
        handleDestructibles(player)
        handleImpassables(player)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode != Input.Keys.ESCAPE && dtModifier == 0f) resume()

        if (keycode == Input.Keys.ESCAPE) pauseOrGoToMenu()

        // TODO: for debugging, remove on launch -------------
        else if (keycode == Input.Keys.R) BaseGame.setActiveScreen(Level2())
        else if (keycode == Input.Keys.Q) Gdx.app.exit()
        else if (keycode == Input.Keys.E) experienceBar.increment(1)
        else if (keycode == Input.Keys.NUM_1) setGameOver()
        else if (keycode == Input.Keys.NUM_2) {
            if (player.health > 0) {
                player.isHurt(1)
                player.health--
                healthBar.subtractHealth()
                dropHealth()
            }
        } else if (keycode == Input.Keys.NUM_3) playerExitLevel()
        else if (keycode == Input.Keys.NUM_2) {
            if (player.health > 0) {
                player.isHurt(1)
                player.health--
                healthBar.subtractHealth()
                dropHealth()
            }
        } else if (keycode == Input.Keys.NUM_4) dropShield()
        // ----------------------------------------------------
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
            pauseOrGoToMenu()

        return super.buttonDown(controller, buttonCode)
    }

    override fun connected(controller: Controller?) {
        if (controller!!.canVibrate() && BaseGame.isVibrationEnabled)
            controller.startVibration(1000, .2f)
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

    protected fun initializePlayer() {
        val startPoint = tilemap.getRectangleList("player start")[0]
        val playerPosX = startPoint.properties.get("x") as Float * TilemapActor.unitScale
        val playerPosY = startPoint.properties.get("y") as Float * TilemapActor.unitScale

        val groundCrack = GroundCrack(0f, 0f, mainStage)
        player = Player(playerPosX, playerPosY, mainStage)
        groundCrack.centerAtActor(player)
    }

    protected fun initializeDestructibles() {
        for (i in 0 until 55) {
            Destructible(
                MathUtils.random(0f, BaseActor.getWorldBounds().width - 5),
                MathUtils.random(0f, BaseActor.getWorldBounds().height - 5),
                mainStage,
                player
            )
        }
    }

    protected fun initializeImpassables() {
        for (obj in tilemap.getRectangleList("impassable")) {
            val props = obj.properties
            val xPos = props.get("x") as Float * TilemapActor.unitScale
            val yPos = props.get("y") as Float * TilemapActor.unitScale
            val width = props.get("width") as Float * TilemapActor.unitScale
            val height = props.get("height") as Float * TilemapActor.unitScale
            Impassable(xPos, yPos, width, height, mainStage)
        }
    }

    private fun pauseOrGoToMenu() {
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

    fun playerExitLevel() {
        BeamOut(player.x, player.y, mainStage, player)
        player.exitLevel()
    }

    private fun handleEnemies() {
        for (enemy: BaseActor in getList(mainStage, Follower::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, true, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, Charger::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, Shooter::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, Shot::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, true, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, Beam::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, GhostFreed::class.java.canonicalName)) {
            player.preventOverlap(enemy)
        }
    }

    private fun handlePickups() {
        handleExperiencePickup()
        handleHealthPickup()
        handleShieldPickup()
        handleCrystals()
    }

    private fun handleCrystals() {
        for (crystal: BaseActor in getList(mainStage, Crystal::class.java.canonicalName)) {
            if (player.overlaps(crystal as Crystal)) {
                crystal.pickup()
            }
        }
    }

    private fun handleHealthPickup() {
        for (healthDrop: BaseActor in getList(mainStage, HealthDrop::class.java.canonicalName)) {
            if (player.overlaps(healthDrop as HealthDrop)) {
                if (healthBar.addHealth()) {
                    player.healthBack()
                    healthDrop.pickup(true)
                }
                healthDrop.pickup(false)
            }
        }
    }

    private fun handleShieldPickup() {
        for (shieldDrop: BaseActor in getList(mainStage, ShieldDrop::class.java.canonicalName)) {
            if (player.overlaps(shieldDrop as ShieldDrop)) {
                shieldDrop.pickup()
                player.activateShield()
            }
        }
    }

    private fun handleExperiencePickup() {
        for (experience: BaseActor in getList(mainStage, Experience::class.java.canonicalName)) {
            if (player.overlaps(experience as Experience)) {
                player.speedBoost()
                val isLevelUp = experienceBar.increment(experience.amount)
                experience.pickup()
                if (isLevelUp && fleetAdmiral.actions.size == 0)
                    fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral3"))
            }
        }
    }

    fun handleDestructibles(baseActor: BaseActor) {
        for (destructible: BaseActor in getList(
            mainStage,
            Destructible::class.java.canonicalName
        )) {
            destructible as Destructible
            if (baseActor.overlaps(destructible)) {
                destructible.destroy()
            }
        }
    }

    private fun handleImpassables(baseActor: BaseActor, isRemovable: Boolean = false) {
        for (impassable: BaseActor in getList(mainStage, Impassable::class.java.canonicalName)) {
            if (isRemovable && baseActor.overlaps(impassable))
                baseActor.death()
            baseActor.preventOverlap(impassable)
        }
    }

    fun spawnAroundPlayer(offset: Float): Vector2 {
        var x: Float
        var y: Float
        if (MathUtils.randomBoolean()) { // horizontal
            x = MathUtils.random(player.x - offset, player.x + offset)
            if (MathUtils.randomBoolean())
                y = player.y + offset
            else
                y = player.y - offset
        } else { // vertical
            if (MathUtils.randomBoolean())
                x = player.x + offset
            else
                x = player.x - offset
            y = MathUtils.random(player.y - offset, player.y + offset)
        }
        return Vector2(x, y)
    }


    fun spawnAtEdgesOfMap(offset: Float): Vector2 {
        var x: Float
        var y: Float
        if (MathUtils.randomBoolean()) { // horizontal
            x = MathUtils.random(offset, BaseActor.getWorldBounds().width - offset)
            if (MathUtils.randomBoolean())
                y = offset
            else
                y = BaseActor.getWorldBounds().height - offset
        } else { // vertical
            if (MathUtils.randomBoolean())
                x = offset
            else
                x = BaseActor.getWorldBounds().width - offset
            y = MathUtils.random(offset, BaseActor.getWorldBounds().height - offset)
        }
        return Vector2(x, y)
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

    fun enemyCollidedWithPlayer(enemy: BaseActor, remove: Boolean, damageAmount: Int) {
        player.preventOverlap(enemy)
        if (player.overlaps(enemy) && !isGameOver) {
            if (remove) enemy.death()
            if (player.isHurt(damageAmount)) {
                BaseGame.playerDeathSound!!.play(BaseGame.soundVolume, 1.5f, 0f)
                if (player.health <= 0)
                    setGameOver()
                else {
                    dropHealth()
                    pauseGameForDuration()
                }
                healthBar.subtractHealth()
            }
        }
    }

    private fun setGameOver() {
        isGameOver = true
        mainLabel.isVisible = true
        mainLabel.setText(BaseGame.myBundle!!.get("gameOver"))
        dtModifier = .125f
        player.death()
        BaseGame.groundCrackSound!!.play(BaseGame.soundVolume)
        fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral1"))
    }

    private fun dropShield() {
        if (fleetAdmiral.actions.size == 0)
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral7"))
        ShieldDrop(
            MathUtils.random(10f, BaseActor.getWorldBounds().width - 10f),
            MathUtils.random(10f, BaseActor.getWorldBounds().height - 10f),
            mainStage,
            player
        )
    }

    private fun dropHealth() {
        if (fleetAdmiral.actions.size == 0)
            fadeFleetAdmiralInAndOut(BaseGame.myBundle!!.get("fleetAdmiral2"))
        HealthDrop(
            MathUtils.random(10f, BaseActor.getWorldBounds().width - 10f),
            MathUtils.random(10f, BaseActor.getWorldBounds().height - 10f),
            mainStage,
            player
        )
    }

    fun fadeFleetAdmiralInAndOut(subtitles: String, talkDuration: Float = 3f) {
        fleetAdmiral.fadeFleetAdmiralInAndOut(talkDuration)
        fleetAdmiralSubtitles.clearActions()
        fleetAdmiralSubtitles.addAction(Actions.fadeIn(1f))
        fleetAdmiralSubtitles.setText(subtitles)
        fleetAdmiralSubtitles.addAction(
            Actions.sequence(
                Actions.delay(talkDuration),
                Actions.fadeOut(1f)
            )
        )
    }

    private fun uiSetup() {
        experienceBar = ExperienceBar(0f, Gdx.graphics.height.toFloat(), uiStage)
        bossBar = BossBar(0f, Gdx.graphics.height.toFloat(), uiStage)

        healthBar = HealthBar()
        uiTable.add(healthBar).padTop(experienceBar.height)
            .padBottom(-healthBar.prefHeight - experienceBar.height)
            .row()

        crystalLabel = CrystalLabel()
        uiTable.add(crystalLabel).padTop(experienceBar.height + healthBar.prefHeight)
            .padBottom(-crystalLabel.prefHeight).row()

        fleetAdmiralSetup()

        mainLabel = Label("", BaseGame.bigLabelStyle)
        mainLabel.isVisible = false
        uiTable.add(mainLabel).expandY().row()

        controllerMessage = ControllerMessage()
        uiTable.add(controllerMessage)
            .padTop(-controllerMessage.prefHeight - Gdx.graphics.height * .1f)
            .padBottom(Gdx.graphics.height * .1f)
    }

    private fun fleetAdmiralSetup() {
        fleetAdmiral = FleetAdmiral(0f, 0f, uiStage)
        fleetAdmiral.scaleBy(100f)
        fleetAdmiral.setPosition(
            Gdx.graphics.width * .1f,
            Gdx.graphics.height - fleetAdmiral.height * 100f
        )

        fleetAdmiralSubtitles = Label("", BaseGame.smallLabelStyle)
        fleetAdmiralSubtitles.setPosition(fleetAdmiral.x - 50f, fleetAdmiral.y - 150)
        uiStage.addActor(fleetAdmiralSubtitles)
    }
}
