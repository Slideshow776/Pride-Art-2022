package no.sandramoen.transagentx.utils

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.I18NBundleLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.I18NBundle
import com.codedisaster.steamworks.*
import java.util.*
import kotlin.system.measureTimeMillis


abstract class BaseGame(appLocale: String) : Game(), AssetErrorListener, SteamUserStatsCallback {
    private val appLocale = appLocale.toLowerCase()

    init {
        game = this
    }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator
        lateinit var spookyFontGenerator: FreeTypeFontGenerator
        const val WORLD_WIDTH = 200f
        const val WORLD_HEIGHT = 200f
        const val isCustomShadersEnabled = true // debugging purposes
        const val isVibrationEnabled = true // debugging purposes
        var isControllerChecked = false // debugging purposes
        val lightPink = Color(0.875f, 0.518f, 0.647f, 1f)
        val lightBlue = Color(0.31f, 0.561f, 0.729f, 1f)

        // game assets
        var smallLabelStyle: LabelStyle? = null
        var mediumLabelStyle: LabelStyle? = null
        var bigLabelStyle: LabelStyle? = null
        var spookySmallLabelStyle: LabelStyle? = null
        var spookyBigLabelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var level1Music: Music? = null
        var level5Music: Music? = null
        var cinematic1Music: Music? = null
        var cinematic2Music: Music? = null
        var cinematic3Music: Music? = null
        var level2IntroMusic: Music? = null
        var level2Music: Music? = null
        var level3Music: Music? = null
        var bossMusic: Music? = null
        var rainMusic: Music? = null
        var windAmbianceMusic: Music? = null

        var enemyChargeSound: Sound? = null
        var enemyChargeUpSound: Sound? = null
        var enemyDeathSound: Sound? = null
        var enemyShootSound: Sound? = null
        var experiencePickupSound: Sound? = null
        var playerDeathSound: Sound? = null
        var playerLevelUpSound: Sound? = null
        var beamInSound: Sound? = null
        var healthUpSound: Sound? = null
        var groundCrackSound: Sound? = null
        var explosionSound: Sound? = null
        var controllerConnectedSound: Sound? = null
        var controllerDisconnectedSound: Sound? = null
        var click1Sound: Sound? = null
        var click2Sound: Sound? = null
        var hoverOverEnterSound: Sound? = null
        var spaceStationBeamSound: Sound? = null
        var fleetAdmiralSound: Sound? = null
        var healthPickupSuccessSound: Sound? = null
        var healthPickupFailSound: Sound? = null
        var beamChargeSound: Sound? = null
        var beamCharge2Sound: Sound? = null
        var barrelDestroyedSound: Sound? = null
        var vaseDestroyedSound: Sound? = null
        var bottleDestroyedSound: Sound? = null
        var chairDestroyedSound: Sound? = null
        var skeletonDestroyedSound: Sound? = null
        var skullsDestroyedSound: Sound? = null
        var skullDestroyedSound: Sound? = null
        var smallBushDestroyedSound: Sound? = null
        var shieldInSound: Sound? = null
        var shieldOutSound: Sound? = null
        var lostPickupSound: Sound? = null
        var scream1Sound: Sound? = null
        var scream2Sound: Sound? = null
        var tentacleChargeUpSound: Sound? = null
        var tentacleWhipSound: Sound? = null
        var thunderSound: Sound? = null
        var rainbowSound: Sound? = null
        var portalSound: Sound? = null
        var chainSound: Sound? = null
        var lostDeathSound: Sound? = null
        var artifactPickUpSound: Sound? = null
        var demonWhispersSound: Sound? = null
        var intro1VoiceSound: Sound? = null

        var level1: TiledMap? = null
        var level2: TiledMap? = null
        var level3: TiledMap? = null
        var level4: TiledMap? = null
        var level5: TiledMap? = null
        var defaultShader: String? = null
        var glowShader: String? = null
        var shockwaveShader: String? = null
        var waveShader: String? = null

        // game state
        var prefs: Preferences? = null
        var loadPersonalParameters = false
        var voiceVolume = 1f
        var soundVolume = .3f
        var musicVolume = .7f
        var currentLocale: String? = null
        var lastPlayedLevel: String? = null
        var myBundle: I18NBundle? = null
        private var steamUserStats: SteamUserStats? = null

        fun setActiveScreen(screen: BaseScreen) {
            screen.initialize()
            game?.setScreen(screen)
        }

        fun setSteamAchievement(id: String) {
            if (SteamAPI.isSteamRunning()) {
                steamUserStats!!.setAchievement(id)
                steamUserStats!!.storeStats()
            }
        }

        fun incrementSteamLostSoulsStat() {
            if (SteamAPI.isSteamRunning()) {
                var numLostSouls = steamUserStats!!.getStatI("num_lost_souls", 0)
                if (numLostSouls >= 7) {
                    setSteamAchievement("ACHIEVEMENT_LOST")
                } else {
                    steamUserStats!!.setStatI("num_lost_souls", ++numLostSouls)
                    steamUserStats!!.storeStats()
                }
            }
        }

        fun clearSteamAchievements() { // for debugging purposes
            if (SteamAPI.isSteamRunning()) {
                steamUserStats!!.clearAchievement("ACHIEVEMENT_LEVEL_1")
                steamUserStats!!.clearAchievement("ACHIEVEMENT_LEVEL_2")
                steamUserStats!!.clearAchievement("ACHIEVEMENT_LEVEL_3")
                steamUserStats!!.clearAchievement("ACHIEVEMENT_LEVEL_4")
                steamUserStats!!.clearAchievement("ACHIEVEMENT_LEVEL_5")
                steamUserStats!!.clearAchievement("ACHIEVEMENT_COLOUR")
                steamUserStats!!.clearAchievement("ACHIEVEMENT_LOST")
                steamUserStats!!.storeStats()
            }
        }
    }

    override fun create() {
        Gdx.input.setCatchKey(Keys.BACK, true) // so that android doesn't exit game on back button
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input
        currentLocale = appLocale.toLowerCase()

        connectToSteam()
        setGameState()
        loadSkin()
        loadAssets()
    }

    private fun connectToSteam() {
        try {
            SteamAPI.loadLibraries()
            if (!SteamAPI.init())
                Gdx.app.error(
                    javaClass.simpleName,
                    "Steamworks initialization error: Steam client not running"
                )
            else {
                steamUserStats = SteamUserStats(this)
                steamUserStats!!.requestCurrentStats()
            }

        } catch (e: SteamException) {
            Gdx.app.error(
                javaClass.simpleName,
                "SteamException: Error extracting or loading native libraries"
            )
        }
    }

    private fun setGameState() {
        GameUtils.loadGameState()
        if (!loadPersonalParameters) {
            currentLocale = appLocale.toLowerCase()
            soundVolume = .3f
            musicVolume = .7f
            voiceVolume = 1f
            lastPlayedLevel = "Level1"
        }
    }

    private fun loadSkin() {
        try {
            skin = Skin(Gdx.files.internal("skins/default/uiskin.json"))
        } catch (error: Throwable) {
            Gdx.app.error(javaClass.simpleName, "Error: Could not load skin: $error")
        }
    }

    private fun loadAssets() {
        val time = measureTimeMillis {
            assetManager = AssetManager()
            assetManager.setErrorListener(this)
            assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas::class.java)

            // music
            assetManager.load(
                "audio/music/384468__frankum__vintage-elecro-pop-loop.mp3",
                Music::class.java
            )
            assetManager.load("audio/music/530376__andrewkn__pad-ambient.wav", Music::class.java)
            assetManager.load("audio/music/341652__devern__cinematic-build.wav", Music::class.java)
            assetManager.load(
                "audio/music/553418__eminyildirim__cinematic-boom-impact-hit-2021.wav",
                Music::class.java
            )
            assetManager.load(
                "audio/music/236894__chimerical__cinematic-suspense.wav",
                Music::class.java
            )
            assetManager.load(
                "audio/music/316821__pearcewilsonking__space-horror-atmosphere-loop.wav",
                Music::class.java
            )
            assetManager.load(
                "audio/music/348834__darkgamer364__creepy-eerie-horror-loop (1).wav",
                Music::class.java
            )
            assetManager.load("audio/music/234475__pcruzn__basslines-1.wav", Music::class.java)
            assetManager.load(
                "audio/music/531947__straget__the-rain-falls-against-the-parasol.wav",
                Music::class.java
            )
            assetManager.load(
                "audio/music/585571__frankum__arp-v-frankum-frankumjay.mp3",
                Music::class.java
            )
            assetManager.load(
                "audio/music/159509__mistersherlock__halloween-graveyard-at-night-howling-wind.wav",
                Music::class.java
            )

            // sounds
            assetManager.load("audio/sound/enemyCharge.wav", Sound::class.java)
            assetManager.load("audio/sound/enemyChargeup.wav", Sound::class.java)
            assetManager.load("audio/sound/enemyDeath.wav", Sound::class.java)
            assetManager.load("audio/sound/enemyShoot.wav", Sound::class.java)
            assetManager.load("audio/sound/experiencePickup.wav", Sound::class.java)
            assetManager.load("audio/sound/playerDeath.wav", Sound::class.java)
            assetManager.load("audio/sound/playerLevelUp.wav", Sound::class.java)
            assetManager.load("audio/sound/beamIn.wav", Sound::class.java)
            assetManager.load("audio/sound/healthUp.wav", Sound::class.java)
            assetManager.load("audio/sound/groundCrack.wav", Sound::class.java)
            assetManager.load("audio/sound/Explosion7.wav", Sound::class.java)
            assetManager.load("audio/sound/controllerConnected.wav", Sound::class.java)
            assetManager.load("audio/sound/controllerDisconnected.wav", Sound::class.java)
            assetManager.load("audio/sound/click1.wav", Sound::class.java)
            assetManager.load("audio/sound/click2.wav", Sound::class.java)
            assetManager.load("audio/sound/hoverOverEnter.wav", Sound::class.java)
            assetManager.load("audio/sound/spaceStationBeam.wav", Sound::class.java)
            assetManager.load("audio/sound/fleetAdmiralSound.wav", Sound::class.java)
            assetManager.load("audio/sound/healthPickupSuccess.wav", Sound::class.java)
            assetManager.load("audio/sound/healthPickupFail.wav", Sound::class.java)
            assetManager.load("audio/sound/beamChargeSound.wav", Sound::class.java)
            assetManager.load("audio/sound/beamChargeSound2.wav", Sound::class.java)
            assetManager.load("audio/sound/barrelDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/vaseDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/bottleDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/chairDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/skeletonDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/skullsDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/skullDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/smallBushDestroyed.wav", Sound::class.java)
            assetManager.load("audio/sound/shieldIn.wav", Sound::class.java)
            assetManager.load("audio/sound/shieldOut.wav", Sound::class.java)
            assetManager.load("audio/sound/lostPickup.wav", Sound::class.java)
            assetManager.load("audio/sound/scream1.wav", Sound::class.java)
            assetManager.load("audio/sound/scream2.wav", Sound::class.java)
            assetManager.load("audio/sound/tentacleChargeUp.wav", Sound::class.java)
            assetManager.load("audio/sound/tentacleWhip.wav", Sound::class.java)
            assetManager.load("audio/sound/thunder.wav", Sound::class.java)
            assetManager.load("audio/sound/Pickup_Coin2.wav", Sound::class.java)
            assetManager.load("audio/sound/portalSound.wav", Sound::class.java)
            assetManager.load("audio/sound/chainSound.wav", Sound::class.java)
            assetManager.load("audio/sound/lostDeathSound.wav", Sound::class.java)
            assetManager.load("audio/sound/artifactPickUpSound.wav", Sound::class.java)
            assetManager.load(
                "audio/sound/438989__magnesus__demonic-whisper.mp3",
                Sound::class.java
            )
            assetManager.load("audio/voice/intro1.wav", Sound::class.java)

            // fonts
            val resolver = InternalFileHandleResolver()
            assetManager.setLoader(
                FreeTypeFontGenerator::class.java,
                FreeTypeFontGeneratorLoader(resolver)
            )
            assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

            // skins
            // assetManager.load("skins/arcade/arcade.json", Skin::class.java)

            // i18n
            assetManager.load(
                "i18n/MyBundle", I18NBundle::class.java, I18NBundleLoader.I18NBundleParameter(
                    Locale(currentLocale)
                )
            )

            // shaders
            assetManager.load(
                AssetDescriptor(
                    "shaders/default.vs",
                    Text::class.java,
                    TextLoader.TextParameter()
                )
            )
            assetManager.load(
                AssetDescriptor(
                    "shaders/glow-pulse.fs",
                    Text::class.java,
                    TextLoader.TextParameter()
                )
            )
            assetManager.load(
                AssetDescriptor(
                    "shaders/shockwave.fs",
                    Text::class.java,
                    TextLoader.TextParameter()
                )
            )
            assetManager.load(
                AssetDescriptor(
                    "shaders/wave.fs",
                    Text::class.java,
                    TextLoader.TextParameter()
                )
            )

            // tiled maps
            assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
            assetManager.load("maps/level1.tmx", TiledMap::class.java)
            assetManager.load("maps/level2.tmx", TiledMap::class.java)
            assetManager.load("maps/level3.tmx", TiledMap::class.java)
            assetManager.load("maps/level4.tmx", TiledMap::class.java)

            assetManager.finishLoading()

            textureAtlas =
                assetManager.get("images/included/packed/images.pack.atlas") // all images are found in this global static variable

            // audio
            level1Music = assetManager.get(
                "audio/music/384468__frankum__vintage-elecro-pop-loop.mp3",
                Music::class.java
            )
            level5Music =
                assetManager.get("audio/music/530376__andrewkn__pad-ambient.wav", Music::class.java)
            cinematic1Music = assetManager.get(
                "audio/music/341652__devern__cinematic-build.wav",
                Music::class.java
            )
            cinematic2Music = assetManager.get(
                "audio/music/553418__eminyildirim__cinematic-boom-impact-hit-2021.wav",
                Music::class.java
            )
            cinematic3Music = assetManager.get(
                "audio/music/236894__chimerical__cinematic-suspense.wav",
                Music::class.java
            )
            level2IntroMusic = assetManager.get(
                "audio/music/316821__pearcewilsonking__space-horror-atmosphere-loop.wav",
                Music::class.java
            )
            level2Music = assetManager.get(
                "audio/music/348834__darkgamer364__creepy-eerie-horror-loop (1).wav",
                Music::class.java
            )
            level3Music = assetManager.get(
                "audio/music/585571__frankum__arp-v-frankum-frankumjay.mp3",
                Music::class.java
            )
            bossMusic =
                assetManager.get("audio/music/234475__pcruzn__basslines-1.wav", Music::class.java)
            rainMusic = assetManager.get(
                "audio/music/531947__straget__the-rain-falls-against-the-parasol.wav",
                Music::class.java
            )
            windAmbianceMusic = assetManager.get(
                "audio/music/159509__mistersherlock__halloween-graveyard-at-night-howling-wind.wav",
                Music::class.java
            )

            enemyChargeSound = assetManager.get("audio/sound/enemyCharge.wav", Sound::class.java)
            enemyChargeUpSound =
                assetManager.get("audio/sound/enemyChargeup.wav", Sound::class.java)
            enemyDeathSound = assetManager.get("audio/sound/enemyDeath.wav", Sound::class.java)
            enemyShootSound = assetManager.get("audio/sound/enemyShoot.wav", Sound::class.java)
            experiencePickupSound =
                assetManager.get("audio/sound/experiencePickup.wav", Sound::class.java)
            playerDeathSound = assetManager.get("audio/sound/playerDeath.wav", Sound::class.java)
            playerLevelUpSound =
                assetManager.get("audio/sound/playerLevelUp.wav", Sound::class.java)
            beamInSound = assetManager.get("audio/sound/beamIn.wav", Sound::class.java)
            healthUpSound = assetManager.get("audio/sound/healthUp.wav", Sound::class.java)
            groundCrackSound = assetManager.get("audio/sound/groundCrack.wav", Sound::class.java)
            explosionSound = assetManager.get("audio/sound/Explosion7.wav", Sound::class.java)
            controllerConnectedSound =
                assetManager.get("audio/sound/controllerConnected.wav", Sound::class.java)
            controllerDisconnectedSound =
                assetManager.get("audio/sound/controllerDisconnected.wav", Sound::class.java)
            click1Sound = assetManager.get("audio/sound/click1.wav", Sound::class.java)
            click2Sound = assetManager.get("audio/sound/click2.wav", Sound::class.java)
            hoverOverEnterSound =
                assetManager.get("audio/sound/hoverOverEnter.wav", Sound::class.java)
            spaceStationBeamSound =
                assetManager.get("audio/sound/spaceStationBeam.wav", Sound::class.java)
            healthPickupSuccessSound =
                assetManager.get("audio/sound/healthPickupSuccess.wav", Sound::class.java)
            healthPickupFailSound =
                assetManager.get("audio/sound/healthPickupFail.wav", Sound::class.java)
            fleetAdmiralSound =
                assetManager.get("audio/sound/fleetAdmiralSound.wav", Sound::class.java)
            beamChargeSound = assetManager.get("audio/sound/beamChargeSound.wav", Sound::class.java)
            beamCharge2Sound =
                assetManager.get("audio/sound/beamChargeSound2.wav", Sound::class.java)
            barrelDestroyedSound =
                assetManager.get("audio/sound/barrelDestroyed.wav", Sound::class.java)
            vaseDestroyedSound =
                assetManager.get("audio/sound/vaseDestroyed.wav", Sound::class.java)
            bottleDestroyedSound =
                assetManager.get("audio/sound/bottleDestroyed.wav", Sound::class.java)
            chairDestroyedSound =
                assetManager.get("audio/sound/chairDestroyed.wav", Sound::class.java)
            skeletonDestroyedSound =
                assetManager.get("audio/sound/skeletonDestroyed.wav", Sound::class.java)
            skullsDestroyedSound =
                assetManager.get("audio/sound/skullsDestroyed.wav", Sound::class.java)
            skullDestroyedSound =
                assetManager.get("audio/sound/skullDestroyed.wav", Sound::class.java)
            smallBushDestroyedSound =
                assetManager.get("audio/sound/smallBushDestroyed.wav", Sound::class.java)
            shieldInSound = assetManager.get("audio/sound/shieldIn.wav", Sound::class.java)
            shieldOutSound = assetManager.get("audio/sound/shieldOut.wav", Sound::class.java)
            lostPickupSound = assetManager.get("audio/sound/lostPickup.wav", Sound::class.java)
            scream1Sound = assetManager.get("audio/sound/scream1.wav", Sound::class.java)
            scream2Sound = assetManager.get("audio/sound/scream2.wav", Sound::class.java)
            tentacleChargeUpSound =
                assetManager.get("audio/sound/tentacleChargeUp.wav", Sound::class.java)
            tentacleWhipSound = assetManager.get("audio/sound/tentacleWhip.wav", Sound::class.java)
            thunderSound = assetManager.get("audio/sound/thunder.wav", Sound::class.java)
            rainbowSound = assetManager.get("audio/sound/Pickup_Coin2.wav", Sound::class.java)
            portalSound = assetManager.get("audio/sound/portalSound.wav", Sound::class.java)
            chainSound = assetManager.get("audio/sound/chainSound.wav", Sound::class.java)
            lostDeathSound = assetManager.get("audio/sound/lostDeathSound.wav", Sound::class.java)
            artifactPickUpSound =
                assetManager.get("audio/sound/artifactPickUpSound.wav", Sound::class.java)
            demonWhispersSound = assetManager.get(
                "audio/sound/438989__magnesus__demonic-whisper.mp3",
                Sound::class.java
            )
            intro1VoiceSound = assetManager.get("audio/voice/intro1.wav", Sound::class.java)

            // text files
            defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
            glowShader = assetManager.get("shaders/glow-pulse.fs", Text::class.java).getString()
            shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()
            waveShader = assetManager.get("shaders/wave.fs", Text::class.java).getString()

            // skin
            // skin = assetManager.get("skins/arcade/arcade.json", Skin::class.java)

            // i18n
            myBundle = assetManager.get("i18n/MyBundle", I18NBundle::class.java)

            // tiled map
            level1 = assetManager.get("maps/level1.tmx", TiledMap::class.java)
            level2 = assetManager.get("maps/level2.tmx", TiledMap::class.java)
            level3 = assetManager.get("maps/level3.tmx", TiledMap::class.java)
            level4 = assetManager.get("maps/level4.tmx", TiledMap::class.java)
            level5 = assetManager.get("maps/level4.tmx", TiledMap::class.java)

            // fonts
            FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
            fontGenerator =
                FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans.ttf")) // hemi-head-426.rg-bolditalic
            val fontParameters = FreeTypeFontParameter()
            fontParameters.size =
                (.038f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
            if (fontParameters.size > 230)
                fontParameters.size = 230
            fontParameters.color = Color.WHITE
            fontParameters.borderWidth = 2f
            fontParameters.shadowColor = Color(0f, 0f, 0f, .25f)
            fontParameters.shadowOffsetX = 2
            fontParameters.shadowOffsetY = 2
            fontParameters.borderColor = Color.BLACK
            fontParameters.borderStraight = true
            fontParameters.minFilter = TextureFilter.Linear
            fontParameters.magFilter = TextureFilter.Linear
            val fontSmall = fontGenerator.generateFont(fontParameters)
            fontParameters.size =
                (.116f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
            if (fontParameters.size > 230)
                fontParameters.size = 230
            val fontMedium = fontGenerator.generateFont(fontParameters)
            fontParameters.size =
                (.2f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
            if (fontParameters.size > 230)
                fontParameters.size = 230
            val fontBig = fontGenerator.generateFont(fontParameters)

            val buttonFontParameters = FreeTypeFontParameter()
            buttonFontParameters.size =
                (.08f * Gdx.graphics.height).toInt() // If the resolutions height is 1440 then the font size becomes 86
            buttonFontParameters.color = Color.WHITE
            buttonFontParameters.borderWidth = 2f
            buttonFontParameters.borderColor = Color.BLACK
            buttonFontParameters.borderStraight = true
            buttonFontParameters.minFilter = TextureFilter.Linear
            buttonFontParameters.magFilter = TextureFilter.Linear
            val buttonCustomFont = fontGenerator.generateFont(buttonFontParameters)

            smallLabelStyle = LabelStyle()
            smallLabelStyle!!.font = fontSmall
            mediumLabelStyle = LabelStyle()
            mediumLabelStyle!!.font = fontMedium
            bigLabelStyle = LabelStyle()
            bigLabelStyle!!.font = fontBig

            textButtonStyle = TextButtonStyle()
            textButtonStyle!!.font = buttonCustomFont
            textButtonStyle!!.fontColor = Color.WHITE

            spookyFont()
        }
        Gdx.app.log(javaClass.simpleName, "Asset manager took $time ms to load all game assets.")
    }

    private fun spookyFont() {
        FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
        spookyFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/SHLOP___.ttf"))
        val fontParameters = FreeTypeFontParameter()
        fontParameters.size =
            (.06f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
        if (fontParameters.size > 128)
            fontParameters.size = 90
        fontParameters.color = Color.WHITE
        fontParameters.borderWidth = 4f
        fontParameters.shadowColor = Color(0f, 0f, 0f, .25f)
        fontParameters.shadowOffsetX = 2
        fontParameters.shadowOffsetY = 2
        fontParameters.borderColor = Color.BLACK
        fontParameters.borderStraight = true
        fontParameters.minFilter = TextureFilter.Linear
        fontParameters.magFilter = TextureFilter.Linear
        val fontSmall = spookyFontGenerator.generateFont(fontParameters)
        fontParameters.size =
            (.3f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
        if (fontParameters.size > 150)
            fontParameters.size = 100
        val fontBig = spookyFontGenerator.generateFont(fontParameters)

        spookySmallLabelStyle = LabelStyle()
        spookySmallLabelStyle!!.font = fontSmall
        spookyBigLabelStyle = LabelStyle()
        spookyBigLabelStyle!!.font = fontBig
    }

    override fun dispose() {
        super.dispose()
        try { // TODO: uncomment this when development is done
            SteamAPI.shutdown()
            assetManager.dispose()
            fontGenerator.dispose()
        } catch (error: UninitializedPropertyAccessException) {
            Gdx.app.error(javaClass.simpleName, "Error disposing => $error")
        }
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        Gdx.app.error(javaClass.simpleName, "Could not load asset: " + asset.fileName, throwable)
    }


    override fun onLeaderboardScoresDownloaded(
        leaderboard: SteamLeaderboardHandle?,
        entries: SteamLeaderboardEntriesHandle?,
        numEntries: Int
    ) {
    }

    override fun onLeaderboardScoreUploaded(
        success: Boolean,
        leaderboard: SteamLeaderboardHandle?,
        score: Int,
        scoreChanged: Boolean,
        globalRankNew: Int,
        globalRankPrevious: Int
    ) {
    }

    override fun onUserAchievementStored(
        gameId: Long,
        isGroupAchievement: Boolean,
        achievementName: String?,
        curProgress: Int,
        maxProgress: Int
    ) {
    }

    override fun onUserStatsReceived(gameId: Long, steamIDUser: SteamID?, result: SteamResult?) {}
    override fun onUserStatsStored(gameId: Long, result: SteamResult?) {}
    override fun onUserStatsUnloaded(steamIDUser: SteamID?) {}
    override fun onLeaderboardFindResult(leaderboard: SteamLeaderboardHandle?, found: Boolean) {}
    override fun onNumberOfCurrentPlayersReceived(success: Boolean, players: Int) {}
    override fun onGlobalStatsReceived(gameId: Long, result: SteamResult?) {}
}
