package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.*
import com.badlogic.gdx.Input.Keys
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
import java.util.*
import kotlin.system.measureTimeMillis


abstract class BaseGame(appLocale: String) : Game(), AssetErrorListener {
    private val appLocale = appLocale
    init { game = this }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator
        const val WORLD_WIDTH = 200f
        const val WORLD_HEIGHT = 200f
        const val isCustomShadersEnabled = true // debugging purposes
        const val isVibrationEnabled = false // debugging purposes
        var isControllerChecked = false // debugging purposes
        val lightPink = Color(0.875f, 0.518f, 0.647f, 1f)

        // game assets
        var smallLabelStyle: LabelStyle? = null
        var bigLabelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var levelMusic: Music? = null
        var menuMusic: Music? = null
        var cinematic1Music: Music? = null
        var cinematic2Music: Music? = null
        var cinematic3Music: Music? = null
        var level2IntroMusic: Music? = null
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
        var clickSound: Sound? = null
        var hoverOverEnterSound: Sound? = null
        var spaceStationBeamSound: Sound? = null
        var fleetAdmiralSound: Sound? = null
        var healthPickupSuccessSound: Sound? = null
        var healthPickupFailSound: Sound? = null
        var beamChargeSound: Sound? = null
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
        var crystalPickupSound: Sound? = null
        var intro1VoiceSound: Sound? = null
        var level1: TiledMap? = null
        var level2: TiledMap? = null
        var defaultShader: String? = null
        var glowShader: String? = null
        var shockwaveShader: String? = null
        var waveShader: String? = null

        // game state
        var prefs: Preferences? = null
        var loadPersonalParameters = false
        var voiceVolume = 1f
        var soundVolume = .75f
        var musicVolume = .5f
        var currentLocale: String? = null
        var myBundle: I18NBundle? = null

        fun setActiveScreen(screen: BaseScreen) {
            screen.initialize()
            game?.setScreen(screen)
        }
    }

    override fun create() {
        Gdx.input.setCatchKey(Keys.BACK, true) // so that android doesn't exit game on back button
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input

        currentLocale = appLocale
        GameUtils.loadGameState()
        if (!loadPersonalParameters) {
            currentLocale = appLocale
            soundVolume = .75f
            musicVolume = .25f
        }

        try {
            skin = Skin(Gdx.files.internal("skins/default/uiskin.json"))
        } catch (error: Throwable) {
            Gdx.app.error(javaClass.simpleName, "Error: Could not load skin: $error")
        }

        // asset manager
        val time = measureTimeMillis {
            assetManager = AssetManager()
            assetManager.setErrorListener(this)
            assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas::class.java)

            // music
            assetManager.load("audio/music/384468__frankum__vintage-elecro-pop-loop.mp3", Music::class.java)
            assetManager.load("audio/music/530376__andrewkn__pad-ambient.wav", Music::class.java)
            assetManager.load("audio/music/341652__devern__cinematic-build.wav", Music::class.java)
            assetManager.load("audio/music/553418__eminyildirim__cinematic-boom-impact-hit-2021.wav", Music::class.java)
            assetManager.load("audio/music/236894__chimerical__cinematic-suspense.wav", Music::class.java)
            assetManager.load("audio/music/564618__bloodpixelhero__horror-atmospheric-loop.wav", Music::class.java)

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
            assetManager.load("audio/sound/hoverOverEnter.wav", Sound::class.java)
            assetManager.load("audio/sound/spaceStationBeam.wav", Sound::class.java)
            assetManager.load("audio/sound/fleetAdmiralSound.wav", Sound::class.java)
            assetManager.load("audio/sound/healthPickupSuccess.wav", Sound::class.java)
            assetManager.load("audio/sound/healthPickupFail.wav", Sound::class.java)
            assetManager.load("audio/sound/beamChargeSound.wav", Sound::class.java)
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
            assetManager.load("audio/sound/crystalPickup.wav", Sound::class.java)
            assetManager.load("audio/voice/intro1.wav", Sound::class.java)

            // fonts
            val resolver = InternalFileHandleResolver()
            assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
            assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

            // skins
            // assetManager.load("skins/arcade/arcade.json", Skin::class.java)

            // i18n
            assetManager.load("i18n/MyBundle", I18NBundle::class.java, I18NBundleLoader.I18NBundleParameter(Locale(currentLocale)))

            // shaders
            assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/glow-pulse.fs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/shockwave.fs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/wave.fs", Text::class.java, TextLoader.TextParameter()))

            // tiled maps
            assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
            assetManager.load("map/level1.tmx", TiledMap::class.java)
            assetManager.load("map/level2.tmx", TiledMap::class.java)

            assetManager.finishLoading()

            textureAtlas = assetManager.get("images/included/packed/images.pack.atlas") // all images are found in this global static variable

            // audio
            levelMusic = assetManager.get("audio/music/384468__frankum__vintage-elecro-pop-loop.mp3", Music::class.java)
            menuMusic = assetManager.get("audio/music/530376__andrewkn__pad-ambient.wav", Music::class.java)
            cinematic1Music = assetManager.get("audio/music/341652__devern__cinematic-build.wav", Music::class.java)
            cinematic2Music = assetManager.get("audio/music/553418__eminyildirim__cinematic-boom-impact-hit-2021.wav", Music::class.java)
            cinematic3Music = assetManager.get("audio/music/236894__chimerical__cinematic-suspense.wav", Music::class.java)
            level2IntroMusic = assetManager.get("audio/music/564618__bloodpixelhero__horror-atmospheric-loop.wav", Music::class.java)

            enemyChargeSound = assetManager.get("audio/sound/enemyCharge.wav", Sound::class.java)
            enemyChargeUpSound = assetManager.get("audio/sound/enemyChargeup.wav", Sound::class.java)
            enemyDeathSound = assetManager.get("audio/sound/enemyDeath.wav", Sound::class.java)
            enemyShootSound = assetManager.get("audio/sound/enemyShoot.wav", Sound::class.java)
            experiencePickupSound = assetManager.get("audio/sound/experiencePickup.wav", Sound::class.java)
            playerDeathSound = assetManager.get("audio/sound/playerDeath.wav", Sound::class.java)
            playerLevelUpSound = assetManager.get("audio/sound/playerLevelUp.wav", Sound::class.java)
            beamInSound = assetManager.get("audio/sound/beamIn.wav", Sound::class.java)
            healthUpSound = assetManager.get("audio/sound/healthUp.wav", Sound::class.java)
            groundCrackSound = assetManager.get("audio/sound/groundCrack.wav", Sound::class.java)
            explosionSound = assetManager.get("audio/sound/Explosion7.wav", Sound::class.java)
            controllerConnectedSound = assetManager.get("audio/sound/controllerConnected.wav", Sound::class.java)
            controllerDisconnectedSound = assetManager.get("audio/sound/controllerDisconnected.wav", Sound::class.java)
            clickSound = assetManager.get("audio/sound/click1.wav", Sound::class.java)
            hoverOverEnterSound = assetManager.get("audio/sound/hoverOverEnter.wav", Sound::class.java)
            spaceStationBeamSound = assetManager.get("audio/sound/spaceStationBeam.wav", Sound::class.java)
            healthPickupSuccessSound = assetManager.get("audio/sound/healthPickupSuccess.wav", Sound::class.java)
            healthPickupFailSound = assetManager.get("audio/sound/healthPickupFail.wav", Sound::class.java)
            fleetAdmiralSound = assetManager.get("audio/sound/fleetAdmiralSound.wav", Sound::class.java)
            beamChargeSound = assetManager.get("audio/sound/beamChargeSound.wav", Sound::class.java)
            barrelDestroyedSound = assetManager.get("audio/sound/barrelDestroyed.wav", Sound::class.java)
            vaseDestroyedSound = assetManager.get("audio/sound/vaseDestroyed.wav", Sound::class.java)
            bottleDestroyedSound = assetManager.get("audio/sound/bottleDestroyed.wav", Sound::class.java)
            chairDestroyedSound = assetManager.get("audio/sound/chairDestroyed.wav", Sound::class.java)
            skeletonDestroyedSound = assetManager.get("audio/sound/skeletonDestroyed.wav", Sound::class.java)
            skullsDestroyedSound = assetManager.get("audio/sound/skullsDestroyed.wav", Sound::class.java)
            skullDestroyedSound = assetManager.get("audio/sound/skullDestroyed.wav", Sound::class.java)
            smallBushDestroyedSound = assetManager.get("audio/sound/smallBushDestroyed.wav", Sound::class.java)
            shieldInSound = assetManager.get("audio/sound/shieldIn.wav", Sound::class.java)
            shieldOutSound = assetManager.get("audio/sound/shieldOut.wav", Sound::class.java)
            crystalPickupSound = assetManager.get("audio/sound/crystalPickup.wav", Sound::class.java)
            intro1VoiceSound = assetManager.get("audio/voice/intro1.wav", Sound::class.java)

            // text files
            defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
            glowShader = assetManager.get("shaders/glow-pulse.fs", Text::class.java).getString()
            shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()
            waveShader = assetManager.get("shaders/wave.fs", Text::class.java).getString()

            // skin
            // skin = assetManager.get("skins/arcade/arcade.json", Skin::class.java)

            // i18n
            myBundle = assetManager["i18n/MyBundle", I18NBundle::class.java]

            // tiled map
            level1 = assetManager.get("map/level1.tmx", TiledMap::class.java)
            level2 = assetManager.get("map/level2.tmx", TiledMap::class.java)

            // fonts
            FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
            fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans.ttf"))
            val fontParameters = FreeTypeFontParameter()
            fontParameters.size = (.038f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
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
            fontParameters.size = (.2f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
            val fontBig = fontGenerator.generateFont(fontParameters)

            val buttonFontParameters = FreeTypeFontParameter()
            buttonFontParameters.size = (.08f * Gdx.graphics.height).toInt() // If the resolutions height is 1440 then the font size becomes 86
            buttonFontParameters.color = Color.WHITE
            buttonFontParameters.borderWidth = 2f
            buttonFontParameters.borderColor = Color.BLACK
            buttonFontParameters.borderStraight = true
            buttonFontParameters.minFilter = TextureFilter.Linear
            buttonFontParameters.magFilter = TextureFilter.Linear
            val buttonCustomFont = fontGenerator.generateFont(buttonFontParameters)

            smallLabelStyle = LabelStyle()
            smallLabelStyle!!.font = fontSmall
            bigLabelStyle = LabelStyle()
            bigLabelStyle!!.font = fontBig

            textButtonStyle = TextButtonStyle()
            textButtonStyle!!.font = buttonCustomFont
            textButtonStyle!!.fontColor = Color.WHITE
        }
        Gdx.app.error(javaClass.simpleName, "Asset manager took $time ms to load all game assets.")
    }

    override fun dispose() {
        super.dispose()
        try { // TODO: uncomment this when development is done
            assetManager.dispose()
            fontGenerator.dispose()
        } catch (error: UninitializedPropertyAccessException) {
            Gdx.app.error(javaClass.simpleName, "$error")
        }
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        Gdx.app.error(javaClass.simpleName, "Could not load asset: " + asset.fileName, throwable)
    }
}
