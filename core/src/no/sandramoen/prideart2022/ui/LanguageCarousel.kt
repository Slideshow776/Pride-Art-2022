package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.I18NBundleLoader
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.I18NBundle
import no.sandramoen.prideart2022.screens.shell.OptionsScreen
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils
import no.sandramoen.prideart2022.utils.GameUtils.Companion.isTouchDownEvent
import java.util.*

class LanguageCarousel : Table() {
    val chooseLanguageLabel =
        Label("${BaseGame.myBundle!!.get("chooseLanguage")}", BaseGame.smallLabelStyle)

    init {
        GameUtils.addWidgetEnterExitEffect(chooseLanguageLabel)
        chooseLanguageLabel.setFontScale(1.2f)

        val languageLabel =
            Label(BaseGame.myBundle!!.get("language"), BaseGame.smallLabelStyle)
        languageLabel.setFontScale(1.1f)

        add(chooseLanguageLabel).colspan(3).padBottom(Gdx.graphics.height * .02f).row()
        add(arrowButton("<<")).right().fill().expand()
        add(languageLabel)
        add(arrowButton(">>")).left().fill().expand()
    }

    fun changeLanguage() {
        BaseGame.clickSound!!.play(BaseGame.soundVolume)
        if (BaseGame.currentLocale == "en") changeLocale("no")
        else if (BaseGame.currentLocale == "no") changeLocale("en")
    }

    private fun arrowButton(arrowLabel: String): TextButton {
        val button = TextButton(arrowLabel, BaseGame.textButtonStyle)
        button.label.setFontScale(.5f)
        button.label.color = BaseGame.lightPink
        GameUtils.pulseWidget(button, .9f, 2f)
        button.addListener { e: Event ->
            if (isTouchDownEvent(e)) changeLanguage()
            false
        }
        return button
    }

    private fun changeLocale(locale: String) {
        BaseGame.assetManager.unload("i18n/MyBundle")
        BaseGame.assetManager.load(
            "i18n/MyBundle",
            I18NBundle::class.java,
            I18NBundleLoader.I18NBundleParameter(Locale(locale))
        )
        BaseGame.assetManager.finishLoading()
        BaseGame.myBundle = BaseGame.assetManager["i18n/MyBundle", I18NBundle::class.java]
        BaseGame.currentLocale = locale
        GameUtils.saveGameState()
        BaseGame.setActiveScreen(OptionsScreen())
    }
}