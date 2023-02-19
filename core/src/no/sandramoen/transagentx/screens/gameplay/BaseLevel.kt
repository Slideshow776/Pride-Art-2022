package no.sandramoen.transagentx.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TypingLabel
import no.sandramoen.transagentx.actors.*
import no.sandramoen.transagentx.actors.characters.FleetAdmiral
import no.sandramoen.transagentx.actors.characters.enemies.*
import no.sandramoen.transagentx.actors.characters.lost.BaseLost
import no.sandramoen.transagentx.actors.characters.player.BeamOut
import no.sandramoen.transagentx.actors.characters.player.Player
import no.sandramoen.transagentx.screens.shell.MenuScreen
import no.sandramoen.transagentx.ui.*
import no.sandramoen.transagentx.utils.*
import no.sandramoen.transagentx.utils.BaseActor.Companion.getList
import no.sandramoen.transagentx.utils.BaseGame.Companion.myBundle

open class BaseLevel : BaseScreen() {
    protected lateinit var player: Player
    protected lateinit var tilemap: TilemapActor
    protected lateinit var mainLabel: TypingLabel
    protected lateinit var objectivesLabel: ObjectivesLabel
    protected lateinit var experienceBar: ExperienceBar
    protected lateinit var healthBar: HealthBar
    protected lateinit var controllerMessage: ControllerMessage
    protected lateinit var fleetAdmiral: FleetAdmiral
    protected lateinit var fleetAdmiralSubtitles: TypingLabel

    protected var bossBar: BossBar? = null
    protected var isGameOver = false
    protected var isRestartable = false
    protected var enemySpawner1 = BaseActor(0f, 0f, mainStage)
    protected var enemySpawner2 = BaseActor(0f, 0f, mainStage)

    override fun initialize() {
        Vignette(uiStage)
        TintOverlay(0f, 0f, mainStage)
        initializePlayer()
        initializeDestructibles()
        initializeImpassables()
        uiSetup()
        GameUtils.cancelControllerVibration()

        BaseGame.lastPlayedLevel = javaClass.simpleName
        GameUtils.saveGameState()
    }

    override fun update(dt: Float) {
        if (isGameOver) return
        handleEnemies()
        handlePickups()
        handleDestructibles(player)
        handleImpassables(player)
    }

    override fun keyDown(keycode: Int): Boolean {
        when {
            (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.SPACE) && dtModifier == 0f -> resume()
            keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE -> pauseOrGoToMenu()
            keycode == Keys.NUMPAD_0 -> player.toggleHairColor()
            keycode == Keys.NUMPAD_1 -> player.toggleSkinColor()
            keycode == Keys.NUMPAD_2 -> player.toggleHairStyle()
            keycode == Keys.NUMPAD_3 -> player.toggleBeardStyle()
        }

        // debug
        /*else if (keycode == Input.Keys.T) {
            Gdx.app.error(javaClass.simpleName, "Cleared all achievements!")
            BaseGame.clearSteamAchievements()
        }*/
        /*else if (keycode == Input.Keys.R) BaseGame.setActiveScreen(Level4())
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
        else if (keycode == Input.Keys.NUM_5) GameUtils.vibrateController()*/
        // ----------------------------------------------------
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if ((buttonCode != XBoxGamepad.BUTTON_BACK || buttonCode == XBoxGamepad.BUTTON_START) && dtModifier == 0f) {
            resume()
            return super.buttonDown(controller, buttonCode)
        }
        if (buttonCode == XBoxGamepad.BUTTON_A)
            player.toggleHairColor()
        else if (buttonCode == XBoxGamepad.BUTTON_B)
            player.toggleSkinColor()
        else if (buttonCode == XBoxGamepad.BUTTON_X)
            player.toggleHairStyle()
        else if (buttonCode == XBoxGamepad.BUTTON_Y)
            player.toggleBeardStyle()
        else if (buttonCode == XBoxGamepad.BUTTON_START) {
            if (dtModifier == 1f) {
                pause()
                BaseGame.controllerDisconnectedSound!!.play(BaseGame.soundVolume)
            }
        } else if (buttonCode == XBoxGamepad.BUTTON_BACK) {
            if (dtModifier == 0f) {
                BaseGame.click2Sound!!.play(BaseGame.soundVolume)
                setMenuScreen()
            }
        }

        return super.buttonDown(controller, buttonCode)
    }

    override fun connected(controller: Controller?) {
        GameUtils.vibrateController()
        BaseGame.controllerConnectedSound!!.play(BaseGame.soundVolume)
        controllerMessage.showConnected()
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
            BaseGame.windAmbianceMusic!!.stop()
            dtModifier = 1f
            unpauseMainLabel()
            if (bossBar != null)
                bossBar!!.pause = false
            controllerMessage.fadeOut()
        }
    }

    override fun pause() {
        super.pause()
        if (!isGameOver) {
            dtModifier = 0f
            setMainLabelToPaused()
            if (bossBar != null)
                bossBar!!.pause = true
        }
    }

    fun setGameOver() {
        isGameOver = true
        stopMusic()
        disableInput(1.5f)
        setMainLabelToGameOver()
        dtModifier = .125f
        player.death()
        BaseGame.groundCrackSound!!.play(BaseGame.soundVolume)
        fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral1"))
        if (bossBar != null)
            bossBar!!.clearActions()
        objectivesLabel.fadeOut()
        healthBar.addAction(Actions.fadeOut(.5f))
        // continueToMenu()
    }

    fun playerExitLevel() {
        BeamOut(player.x, player.y, mainStage, player)
        player.exitLevel()
    }

    fun bossSpawn(): Vector2 {
        return if (MathUtils.randomBoolean()) Vector2(player.x - 30f, player.y + -10f)
        else Vector2(player.x + 30f, player.y - 5f)
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

    fun handleDestructibles(baseActor: BaseActor) {
        for (destructible: BaseActor in getList(
            mainStage,
            Destructible::class.java.canonicalName
        )) {
            destructible as Destructible
            if (baseActor.overlaps(destructible)) {
                try {
                    baseActor as Player
                    GameUtils.vibrateController(200, .1f)
                } catch (classCastException: ClassCastException) {
                }
                destructible.destroy()
            }
        }
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

    fun enemyCollidedWithPlayer(
        enemy: BaseActor,
        remove: Boolean,
        damageAmount: Int,
        preventOverlap: Boolean = true
    ) {
        if (preventOverlap)
            player.preventOverlap(enemy)
        if (player.overlaps(enemy) && !isGameOver) {
            if (remove) enemy.death()
            if (damageAmount > 0 && player.isHurt(damageAmount)) {
                GameUtils.vibrateController()
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

    fun dropShield() {
        if (fleetAdmiral.actions.size == 0)
            fadeFleetAdmiralInAndOut(
                "${myBundle!!.get("fleetAdmiral71")} {RAINBOW}${
                    myBundle!!.get(
                        "fleetAdmiral72"
                    )
                }{ENDRAINBOW} ${myBundle!!.get("fleetAdmiral73")}"
            )
        val position = randomWorldPosition()
        ShieldDrop(position.x, position.y, mainStage, player)
    }

    fun randomWorldPosition(offset: Float = 10f): Vector2 {
        return Vector2(
            MathUtils.random(offset, BaseActor.getWorldBounds().width - offset),
            MathUtils.random(offset, BaseActor.getWorldBounds().height - offset)
        )
    }

    fun fadeFleetAdmiralInAndOut(subtitles: String, talkDuration: Float = 3f) {
        fleetAdmiral.fadeFleetAdmiralInAndOut(talkDuration)
        fleetAdmiralSubtitles.clearActions()
        fleetAdmiralSubtitles.addAction(Actions.fadeIn(1f))
        fleetAdmiralSubtitles.restart()
        fleetAdmiralSubtitles.setText(subtitles)
        fleetAdmiralSubtitles.addAction(
            Actions.sequence(
                Actions.delay(talkDuration),
                Actions.fadeOut(1f)
            )
        )
    }

    fun isButtonCodeDpad(buttonCode: Int): Boolean {
        if (buttonCode == XBoxGamepad.DPAD_UP || buttonCode == XBoxGamepad.DPAD_RIGHT || buttonCode == XBoxGamepad.DPAD_DOWN || buttonCode == XBoxGamepad.DPAD_LEFT)
            return true
        return false
    }

    private fun setMainLabelToGameOver() {
        mainLabel.isVisible = true
        mainLabel.restart()
        mainLabel.setText("{SHAKE=.2;.2;6}${myBundle!!.get("gameOver1")} {WAIT}${myBundle!!.get("gameOver2")}")
    }

    private fun disableInput(duration: Float) {
        BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
            Actions.delay(duration),
            Actions.run { isRestartable = true }
        ))
    }

    private fun stopMusic() {
        BaseGame.level5Music!!.stop()
        BaseGame.bossMusic!!.stop()
        BaseGame.level1Music!!.stop()
        BaseGame.level2IntroMusic!!.stop()
        BaseGame.level2Music!!.stop()
        BaseGame.level3Music!!.stop()
    }

    private fun initializePlayer() {
        val startPoint = tilemap.getRectangleList("player start")[0]
        val playerPosX = startPoint.properties.get("x") as Float * TilemapActor.unitScale
        val playerPosY = startPoint.properties.get("y") as Float * TilemapActor.unitScale

        val groundCrack = GroundCrack(0f, 0f, mainStage)
        player = Player(playerPosX, playerPosY, mainStage)
        groundCrack.centerAtActor(player)
    }

    private fun initializeDestructibles() {
        for (i in 0 until 65) {
            Destructible(
                MathUtils.random(0f, BaseActor.getWorldBounds().width - 5),
                MathUtils.random(0f, BaseActor.getWorldBounds().height - 5),
                mainStage,
                player
            )
        }
    }

    private fun initializeImpassables() {
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
        if (dtModifier == 0f || isGameOver) {
            BaseGame.click2Sound!!.play(BaseGame.soundVolume)
            setMenuScreen()
        } else {
            pause()
            BaseGame.controllerDisconnectedSound!!.play(BaseGame.soundVolume)
        }
    }

    private fun setMenuScreen() {
        BaseGame.level1Music!!.stop()
        BaseGame.level2IntroMusic!!.stop()
        BaseGame.level2Music!!.stop()
        BaseGame.level3Music!!.stop()
        BaseGame.bossMusic!!.stop()
        BaseGame.rainMusic!!.stop()
        BaseGame.setActiveScreen(MenuScreen())
    }

    private fun unpauseMainLabel() {
        mainLabel.isVisible = false
        mainLabel.clearActions()
        mainLabel.color.a = 1f
    }

    private fun setMainLabelToPaused() {
        mainLabel.setText(myBundle!!.get("paused"))
        GameUtils.pulseWidget(mainLabel)
        mainLabel.isVisible = true
    }

    private fun handleEnemies() {
        for (enemy: BaseActor in getList(mainStage, Chain::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, true, 0)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, Follower::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, true, 1)
            handleDestructibles(enemy)
            for (other: BaseActor in getList(mainStage, Follower::class.java.canonicalName)) {
                if (other != enemy)
                    other.preventOverlap(enemy)
            }
        }
        for (enemy: BaseActor in getList(mainStage, Fairy::class.java.canonicalName)) {
            player.preventOverlap(enemy)
            for (other: BaseActor in getList(mainStage, Fairy::class.java.canonicalName)) {
                if (other != enemy)
                    other.preventOverlap(enemy)
            }
        }
        for (enemy: BaseActor in getList(mainStage, Tentacle::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, TeleportHazard::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1, false)
        }
        for (enemy: BaseActor in getList(mainStage, Charger::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, Beamer::class.java.canonicalName)) {
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
        for (enemy: BaseActor in getList(mainStage, BossBeam::class.java.canonicalName)) {
            enemyCollidedWithPlayer(enemy, false, 1)
            handleDestructibles(enemy)
        }
        for (enemy: BaseActor in getList(mainStage, EnemyBeam::class.java.canonicalName)) {
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
        handleLost()
    }

    private fun handleCrystals() {
        for (crystal: BaseActor in getList(mainStage, Crystal::class.java.canonicalName)) {
            if (player.overlaps(crystal as Crystal)) {
                crystal.pickup()
            }
        }
    }

    private fun handleLost() {
        for (lost: BaseActor in getList(mainStage, BaseLost::class.java.canonicalName))
            if (player.overlaps(lost as BaseLost)) {
                GameUtils.vibrateController()
                lost.pickup()

                if (this is Level5)
                    BaseGame.incrementSteamLostSoulsStat()
            }
    }

    private fun handleHealthPickup() {
        for (healthDrop: BaseActor in getList(mainStage, HealthDrop::class.java.canonicalName)) {
            if (player.overlaps(healthDrop as HealthDrop)) {
                if (healthBar.addHealth()) {
                    player.healthBack()
                    healthDrop.pickup(true)
                    GameUtils.vibrateController()
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
                GameUtils.vibrateController()
            }
        }
    }

    private fun handleExperiencePickup() {
        for (experience: BaseActor in getList(mainStage, Experience::class.java.canonicalName)) {
            if (player.overlaps(experience as Experience)) {
                /*player.speedBoost()*/
                checkIfLevelUp(experience)
            } else if (
                (experience.x < 0f || experience.x >= BaseActor.getWorldBounds().width) ||
                (experience.y < 0f || experience.y >= BaseActor.getWorldBounds().height) &&
                experience.isCollisionEnabled
            ) {
                checkIfLevelUp(experience)
            }
        }
    }

    private fun checkIfLevelUp(experience: Experience) {
        experience.pickup()
        val isLevelUp = experienceBar.increment(experience.amount)
        if (isLevelUp && fleetAdmiral.actions.size == 0) {
            GameUtils.vibrateController(duration = 500)
            fadeFleetAdmiralInAndOut(myBundle!!.get("fleetAdmiral3"))
        }
    }

    private fun handleImpassables(baseActor: BaseActor, isRemovable: Boolean = false) {
        for (impassable: BaseActor in getList(mainStage, Impassable::class.java.canonicalName)) {
            if (isRemovable && baseActor.overlaps(impassable))
                baseActor.death()
            baseActor.preventOverlap(impassable)
        }
        for (space: BaseActor in getList(mainStage, SpaceIsThePlace::class.java.canonicalName)) {
            if (isRemovable && baseActor.overlaps(space))
                baseActor.death()
            baseActor.preventOverlap(space)
        }
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

    private fun continueToMenu() {
        BaseGame.windAmbianceMusic!!.play()
        BaseGame.windAmbianceMusic!!.volume = BaseGame.musicVolume
        BaseActor(0f, 0f, uiStage).addAction(Actions.sequence(
            Actions.delay(15f),
            Actions.run {
                mainLabel.setText("{RAINBOW}${myBundle!!.get("continue")}\n5")
                BaseGame.click1Sound!!.play(BaseGame.soundVolume)
            },
            Actions.delay(1f),
            Actions.run {
                mainLabel.setText("{RAINBOW}${myBundle!!.get("continue")}\n4")
                BaseGame.click1Sound!!.play(BaseGame.soundVolume)
            },
            Actions.delay(1f),
            Actions.run {
                mainLabel.setText("{RAINBOW}${myBundle!!.get("continue")}\n3")
                BaseGame.click1Sound!!.play(BaseGame.soundVolume)
            },
            Actions.delay(1f),
            Actions.run {
                mainLabel.setText("{RAINBOW}${myBundle!!.get("continue")}\n2")
                BaseGame.click1Sound!!.play(BaseGame.soundVolume)
            },
            Actions.delay(1f),
            Actions.run {
                mainLabel.setText("{RAINBOW}${myBundle!!.get("continue")}\n1")
                BaseGame.click1Sound!!.play(BaseGame.soundVolume)
            },
            Actions.delay(1f),
            Actions.run {
                BaseGame.click2Sound!!.play(BaseGame.soundVolume)
                BaseGame.setActiveScreen(MenuScreen())
                BaseGame.windAmbianceMusic!!.stop()
            }
        ))
    }

    private fun dropHealth() {
        if (fleetAdmiral.actions.size == 0)
            fadeFleetAdmiralInAndOut(
                "${myBundle!!.get("fleetAdmiral21")} {COLOR=#c84646}${
                    myBundle!!.get(
                        "fleetAdmiral22"
                    )
                }{CLEARCOLOR} ${myBundle!!.get("fleetAdmiral23")}"
            )
        val position = randomWorldPosition()
        HealthDrop(position.x, position.y, mainStage, player)
    }

    private fun uiSetup() {
        experienceBar = ExperienceBar(0f, Gdx.graphics.height.toFloat(), uiStage)

        healthBar = HealthBar()
        uiTable.add(healthBar).padTop(experienceBar.height * 1.05f)
            .padBottom(-healthBar.prefHeight - experienceBar.height)
            .row()

        objectivesLabel = ObjectivesLabel()
        uiTable.add(objectivesLabel).padTop(experienceBar.height + healthBar.prefHeight)
            .padBottom(-objectivesLabel.prefHeight).width(Gdx.graphics.width * 98f).row()

        fleetAdmiralSetup()

        mainLabel = TypingLabel("", BaseGame.mediumLabelStyle)
        mainLabel.alignment = Align.center
        mainLabel.isVisible = false
        uiTable.add(mainLabel).expandY().width(Gdx.graphics.width * .98f).padBottom(uiTable.prefHeight * 1.5f).row()

        controllerMessage = ControllerMessage()
        uiTable.add(controllerMessage)
            .padTop(-controllerMessage.prefHeight - Gdx.graphics.height * .1f)
            .padBottom(Gdx.graphics.height * .1f)

        // uiTable.debug = true
    }

    private fun fleetAdmiralSetup() {
        fleetAdmiral = FleetAdmiral(0f, 0f, uiStage)
        fleetAdmiral.setPosition(
            Gdx.graphics.width * .03f,
            Gdx.graphics.height * .65f
        )

        fleetAdmiralSubtitles = TypingLabel("", BaseGame.smallLabelStyle)
        fleetAdmiralSubtitles.setPosition(
            Gdx.graphics.width * .07f,
            Gdx.graphics.height * .625f
        )
        uiStage.addActor(fleetAdmiralSubtitles)
    }
}
