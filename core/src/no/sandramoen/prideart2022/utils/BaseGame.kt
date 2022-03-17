package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.*
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.I18NBundle
import kotlin.system.measureTimeMillis

abstract class BaseGame : Game(), AssetErrorListener {
    init { game = this }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator
        const val WORLD_WIDTH = 200f
        const val WORLD_HEIGHT = 200f
        val lightPink = Color(1f, .816f, .94f, 1f)
        var enableCustomShaders = true // debugging purposes

        // game assets
        var smallLabelStyle: LabelStyle? = null
        var bigLabelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var levelMusic: Music? = null
        var enemyChargeSound: Sound? = null
        var enemyChargeupSound: Sound? = null
        var enemyDeathSound: Sound? = null
        var enemyShootSound: Sound? = null
        var experiencePickupSound: Sound? = null
        var playerDeathSound: Sound? = null
        var playerLevelUpSound: Sound? = null

        // game state
        var prefs: Preferences? = null
        var loadPersonalParameters = false
        var soundVolume = .75f
        var musicVolume = .5f
        var currentLocale: String? = null
        var myBundle: I18NBundle? = null

        fun setActiveScreen(screen: BaseScreen) {
            game?.setScreen(screen)
        }
    }

    override fun create() {
        Gdx.input.setCatchKey(Keys.BACK, true) // so that android doesn't exit game on back button
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input


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

            // sounds
            assetManager.load("audio/sound/enemyCharge.wav", Sound::class.java)
            assetManager.load("audio/sound/enemyChargeup.wav", Sound::class.java)
            assetManager.load("audio/sound/enemyDeath.wav", Sound::class.java)
            assetManager.load("audio/sound/enemyShoot.wav", Sound::class.java)
            assetManager.load("audio/sound/experiencePickup.wav", Sound::class.java)
            assetManager.load("audio/sound/playerDeath.wav", Sound::class.java)
            assetManager.load("audio/sound/playerLevelUp.wav", Sound::class.java)

            // fonts
            val resolver = InternalFileHandleResolver()
            assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
            assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

            // skins
            // assetManager.load("skins/arcade/arcade.json", Skin::class.java)

            // i18n
            // assetManager.load("i18n/MyBundle", I18NBundle::class.java, I18NBundleParameter(Locale(currentLocale)))

            // shaders
            // assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))

            assetManager.finishLoading()

            textureAtlas = assetManager.get("images/included/packed/images.pack.atlas") // all images are found in this global static variable

            // audio
            levelMusic = assetManager.get("audio/music/384468__frankum__vintage-elecro-pop-loop.mp3", Music::class.java)

            enemyChargeSound = assetManager.get("audio/sound/enemyCharge.wav", Sound::class.java)
            enemyChargeupSound = assetManager.get("audio/sound/enemyChargeup.wav", Sound::class.java)
            enemyDeathSound = assetManager.get("audio/sound/enemyDeath.wav", Sound::class.java)
            enemyShootSound = assetManager.get("audio/sound/enemyShoot.wav", Sound::class.java)
            experiencePickupSound = assetManager.get("audio/sound/experiencePickup.wav", Sound::class.java)
            playerDeathSound = assetManager.get("audio/sound/playerDeath.wav", Sound::class.java)
            playerLevelUpSound = assetManager.get("audio/sound/playerLevelUp.wav", Sound::class.java)

            // text files
            // defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()

            // skin
            // skin = assetManager.get("skins/arcade/arcade.json", Skin::class.java)

            // i18n
            // myBundle = assetManager["i18n/MyBundle", I18NBundle::class.java]

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
