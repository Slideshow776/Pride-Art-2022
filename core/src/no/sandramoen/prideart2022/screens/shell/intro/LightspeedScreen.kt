package no.sandramoen.prideart2022.screens.shell.intro

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.actors.characters.FleetAdmiral
import no.sandramoen.prideart2022.screens.gameplay.Level1
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen

class LightspeedScreen : BaseScreen() {
    private val camera = mainStage.camera as OrthographicCamera
    private var timeElapsed = 0f
    private val shakyCameraIntensity = .1f

    private lateinit var lightspeed0: BaseActor
    private lateinit var lightspeed1: BaseActor
    private val lightspeedSpeed = 10f
    private val horizontalSidesOfScreen = 217f

    private val label = Label("", BaseGame.smallLabelStyle)
    private lateinit var fleetAdmiral: FleetAdmiral

    override fun initialize() {
        initializeLightspeed()
        initializeBeam()
        initializeSpaceship()

        fleetAdmiral = FleetAdmiral(-50f, -27.5f, mainStage)
        fleetAdmiral.scaleBy(10f)

        BaseGame.cinematic3Music!!.play()
        BaseGame.cinematic3Music!!.volume = BaseGame.musicVolume

        uiTable.add(label).expandY().bottom().padBottom(Gdx.graphics.height * .02f)
        camera.zoom = .09f
    }

    override fun update(dt: Float) {
        timeElapsed += dt
        horizontallyScrollLightspeed(lightspeed0)
        horizontallyScrollLightspeed(lightspeed1)

        shakyCamera()
        zoomOutCamera()
    }

    override fun keyDown(keycode: Int): Boolean {
        // TODO: debug, remove before launch -------------------------
        if (keycode == Input.Keys.Q) Gdx.app.exit()
        else if (keycode == Input.Keys.R) BaseGame.setActiveScreen(Level1())
        else if (keycode == Input.Keys.W) println("time elapsed: $timeElapsed")
        // ------------------------------------------------------------
        else skipIntro()
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        skipIntro()
        return super.buttonDown(controller, buttonCode)
    }

    private fun skipIntro() {
        BaseGame.cinematic3Music!!.stop()
        BaseGame.setActiveScreen(Level1())
    }

    private fun initializeBeam() {
        val beam = BaseActor(0f, 0f, mainStage)
        beam.loadImage("beam")
        beam.rotateBy(90f)
        beam.scaleBy(10f, 2000f)
        beam.color.a = 1f
        camera.position.set(Vector3(beam.width * .5f, beam.height * .5f, 0f))
    }

    private fun initializeLightspeed() {
        lightspeed0 = BaseActor(0f, -10f, mainStage)
        lightspeed0.loadImage("lightspeed")
        lightspeed0.scaleBy(39.25f, 3.9f)

        lightspeed1 = BaseActor(horizontalSidesOfScreen, -10f, mainStage)
        lightspeed1.loadImage("lightspeed")
        lightspeed1.scaleBy(39.25f, 3.9f)
    }

    private fun horizontallyScrollLightspeed(lightspeed: BaseActor) {
        lightspeed.x -= lightspeedSpeed
        if (lightspeed.x < -horizontalSidesOfScreen)
            lightspeed.x = horizontalSidesOfScreen
    }

    private fun initializeSpaceship() {
        val spaceship = BaseActor(-horizontalSidesOfScreen, 0f, mainStage)
        spaceship.loadImage("spaceship")
        actAnimation(spaceship)
    }

    private fun actAnimation(spaceship: BaseActor) {
        spaceship.addAction(
            Actions.sequence(
                Actions.moveTo(0f, -spaceship.height / 2, 3f),
                Actions.run { monologue() },
                Actions.delay(26f),
                Actions.moveTo(horizontalSidesOfScreen, -spaceship.height / 2, 3f),
                Actions.run { BaseGame.setActiveScreen(EarthScreen()) }
            )
        )
    }

    private fun monologue() {
        BaseActor(0f, 0f, mainStage).addAction(
            Actions.sequence(
                Actions.run {
                    fleetAdmiral.fadeIn()
                    fleetAdmiral.addAction(Actions.sequence(
                        Actions.delay(1f),
                        Actions.run { fleetAdmiral.talk() }
                    ))
                    label.setText(BaseGame.myBundle!!.get("lightSpeed1"))
                },
                Actions.delay(5f),
                Actions.run { label.setText(BaseGame.myBundle!!.get("lightSpeed2")) },
                Actions.delay(5f),
                Actions.run { label.setText(BaseGame.myBundle!!.get("lightSpeed3")) },
                Actions.delay(5f),
                Actions.run { label.setText(BaseGame.myBundle!!.get("lightSpeed4")) },
                Actions.delay(5f),
                Actions.run { label.setText(BaseGame.myBundle!!.get("lightSpeed5")) },
                Actions.delay(5f),
                Actions.run { label.setText(BaseGame.myBundle!!.get("lightSpeed6")) },
                Actions.delay(2.5f),
                Actions.run {
                    label.setText("")
                    fleetAdmiral.stopTalking()
                    fleetAdmiral.fadeOut()
                }
            )
        )
    }

    private fun shakyCamera() {
        camera.position.set(
            Vector3(
                camera.position.x + MathUtils.random(-shakyCameraIntensity, shakyCameraIntensity),
                camera.position.y + MathUtils.random(-shakyCameraIntensity, shakyCameraIntensity),
                0f
            )
        )
    }

    private fun zoomOutCamera() {
        if (timeElapsed > .25f && camera.zoom + .1f <= 1f)
            camera.zoom += .0125f
    }
}
