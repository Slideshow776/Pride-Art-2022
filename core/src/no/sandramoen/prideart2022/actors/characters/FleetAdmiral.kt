package no.sandramoen.prideart2022.actors.characters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class FleetAdmiral(x: Float, y: Float, stage: Stage) : BaseActor(x, y, stage) {
    private lateinit var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    private lateinit var talkAnimation: Animation<TextureAtlas.AtlasRegion>
    private var talk = false
    private val talkFrequency = .35f
    private var talkCounter = 0f

    init {
        initializeAnimations()
        color.a = 0f
    }

    override fun act(dt: Float) {
        super.act(dt)
        talkSounds(dt)
    }

    fun talk() {
        setAnimation(talkAnimation)
        setSize(Gdx.graphics.width * .105f, Gdx.graphics.height * .22f)
        talk = true
    }

    fun stopTalking() {
        talk = false
        setAnimation(idleAnimation)
        setSize(Gdx.graphics.width * .105f, Gdx.graphics.height * .22f)
    }

    fun fadeFleetAdmiralInAndOut(talkDuration: Float = 3f) {
        clearActions()
        fadeIn()
        talk()
        addAction(
            Actions.sequence(
                Actions.delay(talkDuration),
                Actions.run {
                    stopTalking()
                    fadeOut()
                }
            ))
    }

    fun idle() = setAnimation(idleAnimation)
    fun fadeIn() = addAction(Actions.fadeIn(1f))
    fun fadeOut() = addAction(Actions.fadeOut(1f))

    private fun talkSounds(dt: Float) {
        if (talk) {
            if (talkCounter >= talkFrequency) {
                BaseGame.fleetAdmiralSound!!.play(
                    BaseGame.soundVolume,
                    MathUtils.random(.9f, 1.1f),
                    0f
                )
                talkCounter = MathUtils.random(0f, .28f)
            }
            talkCounter += dt
        }
    }

    private fun initializeAnimations() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        for (i in 1..20)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("fleet admiral/idle1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("fleet admiral/idle2"))
        idleAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..10) {
            animationImages.add(BaseGame.textureAtlas!!.findRegion("fleet admiral/talk1"))
            animationImages.add(BaseGame.textureAtlas!!.findRegion("fleet admiral/talk2"))
        }
        animationImages.add(BaseGame.textureAtlas!!.findRegion("fleet admiral/idle2"))
        talkAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        setAnimation(idleAnimation)
        setSize(Gdx.graphics.width * .105f, Gdx.graphics.height * .22f)
    }
}
