package no.sandramoen.prideart2022.actors.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.Explosion
import no.sandramoen.prideart2022.actors.particles.RunningSmokeEffect
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.XBoxGamepad

class Player(x: Float, y: Float, stage: Stage) : BaseActor(0f, 0f, stage) {
    private lateinit var runWESAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runWENAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runNAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var runSAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var deathAnimation: Animation<TextureAtlas.AtlasRegion>
    private var wobbleAction: RepeatAction? = null
    private var runningSmokeAction: RepeatAction? = null
    private var isPlaying = true
    private var state = State.Idle

    var movementSpeed = 26f
    private var movementAcceleration = movementSpeed * 8f
    var health = 2

    init {
        loadAnimation()
        centerAtPosition(x, y)
        playEnterAnimation()

        setAcceleration(movementAcceleration)
        setMaxSpeed(movementSpeed)
        setDeceleration(movementAcceleration)

        alignCamera()
        zoomCamera(.5f)

        setBoundaryPolygon(8)
        setOrigin(Align.center)

        pantingAnimation()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (!isPlaying) return

        movementPolling()
        applyPhysics(dt)
        setMovementAnimation()

        boundToWorld()
        alignCamera(lerp = .1f)
    }

    override fun death() {
        clearActions()
        addAction(
            Actions.parallel(
                Actions.color(Color.WHITE, 0f),
                Actions.scaleTo(1f, 1f, 0f),
                Actions.moveBy(0f, 100f, BeamOut.animationDuration, Interpolation.circleIn),
                Actions.fadeOut(BeamOut.animationDuration, Interpolation.circleIn),
                Actions.rotateBy(40f, BeamOut.animationDuration),
                Actions.run { isShakyCam = false }
            )
        )
        BeamOut(x, y - 2, stage, this)
        setAnimation(deathAnimation)
        isPlaying = false
        GroundCrack(x - width / 2, y - height / 1, stage)
    }

    fun hit() {
        health--
        addAction(Actions.moveBy(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f), .1f))
        hurtAnimation()
        Explosion(this, stage)
        reduceMovementSpeedBy(20)
    }

    fun flashColor(color: Color) {
        val duration = .25f
        addAction(
            Actions.sequence(
                Actions.color(color, duration / 2),
                Actions.color(Color.WHITE, duration / 2)
            )
        )
    }

    private fun pantingAnimation() {
        addAction(
            Actions.sequence(
                Actions.delay(8f),
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(1.025f, .975f, .5f),
                        Actions.scaleTo(.975f, 1.025f, .5f)
                    )
                )
            )
        )
    }

    private fun hurtAnimation() {
        isCollisionEnabled = false
        val colourDuration = 1.25f
        setAnimation(deathAnimation)
        isShakyCam = true
        addAction(
            Actions.sequence(
                Actions.color(Color.BLACK, colourDuration / 2),
                Actions.run { isCollisionEnabled = true },
                Actions.color(Color.WHITE, colourDuration / 2),
                Actions.run {
                    state = State.Idle
                    setMovementAnimation()
                    isShakyCam = false
                }
            )
        )
    }

    private fun addRunningSmokeAction() {
        if (runningSmokeAction == null) {
            runningSmokeAction = Actions.forever(Actions.sequence(
                Actions.delay(.1f),
                Actions.run {
                    val effect = RunningSmokeEffect()
                    effect.setScale(.015f)
                    effect.setPosition(x + width / 2, y + height / 8)
                    stage.addActor(effect)
                    effect.zIndex = 2
                    effect.start()
                }
            ))
            addAction(runningSmokeAction)
        }
    }

    private fun removeRunningSmokeAction() {
        removeAction(runningSmokeAction)
        runningSmokeAction = null
    }

    private fun reduceMovementSpeedBy(percent: Int) {
        movementSpeed *= (100 - percent) / 100f
        setMaxSpeed(movementSpeed)
    }

    private fun movementPolling() {
        if (Controllers.getControllers().size > 0)
            controllerPolling()
        keyboardPolling()
    }

    private fun controllerPolling() {
        val controller = Controllers.getControllers()[0] // TODO: Warning => this is dangerous
        controllerAxisPolling(controller)
        controllerDirectionalPadPolling(controller)
    }

    private fun controllerAxisPolling(controller: Controller) {
        val direction = Vector2(
            controller.getAxis(XBoxGamepad.AXIS_LEFT_Y),
            -controller.getAxis(XBoxGamepad.AXIS_LEFT_X)
        )

        val length = direction.len()
        val deadZone = .1f
        if (length > deadZone) {
            setSpeed(length * 100f)
            setMotionAngle(direction.angleDeg())
        }
    }

    private fun controllerDirectionalPadPolling(controller: Controller) {
        if (controller.getButton(XBoxGamepad.DPAD_UP))
            accelerateAtAngle(90f)
        if (controller.getButton(XBoxGamepad.DPAD_LEFT))
            accelerateAtAngle(180f)
        if (controller.getButton(XBoxGamepad.DPAD_DOWN))
            accelerateAtAngle(270f)
        if (controller.getButton(XBoxGamepad.DPAD_RIGHT))
            accelerateAtAngle(0f)
    }

    private fun keyboardPolling() {
        if (Gdx.input.isKeyPressed(Keys.W))
            accelerateAtAngle(90f)
        if (Gdx.input.isKeyPressed(Keys.A))
            accelerateAtAngle(180f)
        if (Gdx.input.isKeyPressed(Keys.S))
            accelerateAtAngle(270f)
        if (Gdx.input.isKeyPressed(Keys.D))
            accelerateAtAngle(0f)
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        for (i in 1..20)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("player/idle1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/idle2"))
        idleAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runWES1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runWES2"))
        runWESAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runWEN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runWEN2"))
        runWENAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runN2"))
        runNAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/runS2"))
        runSAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/death1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/death2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/death3"))
        deathAnimation = Animation(.2f, animationImages)
        animationImages.clear()

        setAnimation(idleAnimation)
    }

    private fun setMovementAnimation() {
        setAnimation()
        flipPlayer()
    }

    private fun addWobbleAction() {
        if (wobbleAction == null) {
            val rotation = 8f
            val duration = .18f
            wobbleAction = Actions.forever(
                Actions.sequence(
                    Actions.rotateTo(rotation, duration),
                    Actions.rotateTo(-rotation, duration * 2),
                    Actions.rotateTo(0f, duration)
                )
            )
            addAction(wobbleAction)
        }
    }

    private fun removeWobbleAction() {
        removeAction(wobbleAction)
        rotation = 0f
        wobbleAction = null
    }

    private fun setAnimation() {
        if (!isMoving() && !isState(State.Idle)) {
            setAnimationAndState(idleAnimation, State.Idle)
            removeWobbleAction()
            removeRunningSmokeAction()
        } else if (isMoving() && !isState(State.RunningN) && (getMotionAngle() in 70f..110f)) {
            setAnimationAndState(runNAnimation, State.RunningN)
            addWobbleAction()
            addRunningSmokeAction()
        } else if (isMoving() && !isState(State.RunningWEN) && ((getMotionAngle() > 45 && getMotionAngle() < 70f) || (getMotionAngle() > 110f && getMotionAngle() < 135f))) {
            setAnimationAndState(runWENAnimation, State.RunningWEN)
            addWobbleAction()
            addRunningSmokeAction()
        } else if (isMoving() && !isState(State.RunningWES) && ((getMotionAngle() <= 45 || getMotionAngle() > 290) || (getMotionAngle() < 250f && getMotionAngle() >= 135))) {
            setAnimationAndState(runWESAnimation, State.RunningWES)
            addWobbleAction()
            addRunningSmokeAction()
        } else if (isMoving() && !isState(State.RunningS) && (getMotionAngle() in 250f..290f)) {
            setAnimationAndState(runSAnimation, State.RunningS)
            addWobbleAction()
            addRunningSmokeAction()
        }
    }

    private fun isState(state: State): Boolean {
        return this.state == state
    }

    private fun setAnimationAndState(animation: Animation<TextureAtlas.AtlasRegion>, state: State) {
        setAnimation(animation)
        this.state = state
    }

    private fun flipPlayer() {
        if (getSpeed() > 0 && (getMotionAngle() <= 90 || getMotionAngle() >= 270) && !isFacingRight)
            cardboardFlipAnimation()
        else if (getSpeed() > 0 && (getMotionAngle() > 90 && getMotionAngle() < 270) && isFacingRight)
            cardboardFlipAnimation()
    }

    private fun cardboardFlipAnimation() {
        flip()
        val duration = .15f
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(.025f, 1f, duration),
                    Actions.color(Color.BLACK, duration)
                ),
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, duration),
                    Actions.color(Color.WHITE, duration)
                )
            )
        )
    }

    private enum class State {
        Idle, RunningWES, RunningWEN, RunningN, RunningS
    }

    private fun playEnterAnimation() {
        BeamIn(x, y, stage, this)
        revealAnimation(BeamIn.animationDuration)
    }

    private fun revealAnimation(beamDuration: Float) {
        setScale(0f, 0f)
        color.a = 0f
        addAction(
            Actions.sequence(
                Actions.delay(beamDuration / 3),
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, 1f, Interpolation.bounceOut),
                    Actions.fadeIn(1f, Interpolation.bounceOut)
                )
            )
        )
    }
}
