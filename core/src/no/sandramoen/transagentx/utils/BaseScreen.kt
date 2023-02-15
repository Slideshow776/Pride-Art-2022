package no.sandramoen.transagentx.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.codedisaster.steamworks.*

abstract class BaseScreen : Screen, InputProcessor, ControllerListener, SteamFriendsCallback {
    protected var mainStage: Stage
    protected var uiStage: Stage
    protected var uiTable: Table
    protected var dtModifier = 1f
    protected var isPause = false

    init {
        mainStage = Stage()
        mainStage.viewport = ExtendViewport(100f, 100f)

        uiTable = Table()
        uiTable.setFillParent(true)
        uiStage = Stage()
        uiStage.addActor(uiTable)
        uiStage.viewport = ScreenViewport()

        SteamFriends(this)
    }

    abstract fun initialize()
    abstract fun update(dt: Float)

    override fun render(dt: Float) {
        uiStage.act(dt)
        if (!isPause) {
            mainStage.act(dt * dtModifier)
            update(dt * dtModifier)
        }

        if (SteamAPI.isSteamRunning())
            SteamAPI.runCallbacks()

        Gdx.gl.glClearColor(0.035f, 0.039f, 0.078f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mainStage.viewport.apply()
        mainStage.draw()

        uiStage.viewport.apply()
        uiStage.draw()
    }

    override fun show() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        im.addProcessor(this)
        im.addProcessor(uiStage)
        im.addProcessor(mainStage)
        Controllers.addListener(this)
    }

    override fun hide() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        im.removeProcessor(this)
        im.removeProcessor(uiStage)
        im.removeProcessor(mainStage)
        Controllers.removeListener(this)
    }

    override fun resize(width: Int, height: Int) {
        mainStage.viewport.update(width, height)
        uiStage.viewport.update(width, height, true)
    }

    override fun pause() {
        isPause = true
    }

    override fun resume() {
        isPause = false
    }

    override fun dispose() {}

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        return false
    }

    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        return false
    }

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        return false
    }

    override fun connected(controller: Controller?) {}
    override fun disconnected(controller: Controller?) {}


    override fun onGameOverlayActivated(active: Boolean) {
        if (active) pause()
        else resume()
    }

    override fun onSetPersonaNameResponse(
        success: Boolean,
        localSuccess: Boolean,
        result: SteamResult?
    ) {
    }

    override fun onPersonaStateChange(steamID: SteamID?, change: SteamFriends.PersonaChange?) {}
    override fun onGameLobbyJoinRequested(steamIDLobby: SteamID?, steamIDFriend: SteamID?) {}
    override fun onAvatarImageLoaded(steamID: SteamID?, image: Int, width: Int, height: Int) {}
    override fun onFriendRichPresenceUpdate(steamIDFriend: SteamID?, appID: Int) {}
    override fun onGameRichPresenceJoinRequested(steamIDFriend: SteamID?, connect: String?) {}
    override fun onGameServerChangeRequested(server: String?, password: String?) {}
}
