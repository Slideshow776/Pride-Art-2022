package no.sandramoen.prideart2022.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.actors.ShockwaveBackground
import no.sandramoen.prideart2022.screens.LevelScreen
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen

class SplashScreen : BaseScreen() {
    private lateinit var shockwaveBackground: ShockwaveBackground
    private lateinit var blackOverlay: BaseActor

    override fun initialize() {
        shockwaveBackground = ShockwaveBackground("images/excluded/splash.jpg", uiStage)
        blackOverlayAnimation()
    }

    override fun update(dt: Float) {}

    override fun dispose() {
        super.dispose()
        shockwaveBackground.shaderProgram.dispose()
        shockwaveBackground.remove()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE || keycode == Keys.Q) {
            BaseGame.clickSound!!.play(BaseGame.soundVolume)
            blackOverlay.clearActions()
            blackOverlay.addAction(Actions.sequence(
                Actions.fadeIn(.45f),
                Actions.delay(.05f),
                Actions.run {
                    super.dispose()
                    Gdx.app.exit()
                }
            ))
        }
        return false
    }

    private fun blackOverlayAnimation() {
        blackOverlayInitialization()
        blackOverlayFadeInAndOut()
        disposeAndSetActiveScreen()
    }

    private fun blackOverlayInitialization() {
        blackOverlay = BaseActor(0f, 0f, uiStage)
        blackOverlay.loadImage("whitePixel")
        blackOverlay.color = Color(0f, 0f, 0f, 1f)
        blackOverlay.touchable = Touchable.childrenOnly
        blackOverlay.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    private fun blackOverlayFadeInAndOut() {
        val totalDurationInSeconds = 4f
        blackOverlay.addAction(
            Actions.sequence(
                Actions.fadeOut(totalDurationInSeconds / 3),
                /*Actions.run { googlePlayServicesSignIn() },*/
                Actions.delay(totalDurationInSeconds / 3),
                Actions.fadeIn(totalDurationInSeconds / 3)
            )
        )
    }

    /*private fun googlePlayServicesSignIn() {
        if (
            Gdx.app.type == Application.ApplicationType.Android &&
            BaseGame.isGPS &&
            BaseGame.gps != null
        )
            BaseGame.gps!!.signIn()
    }*/

    private fun disposeAndSetActiveScreen() {
        blackOverlay.addAction(Actions.after(Actions.run {
            dispose()
            BaseGame.setActiveScreen(LevelScreen())
        }))
    }
}
