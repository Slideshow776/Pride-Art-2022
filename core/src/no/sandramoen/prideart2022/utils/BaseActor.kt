package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.scenes.scene2d.Group
import no.sandramoen.prideart2022.actors.TilemapActor
import kotlin.math.abs

open class BaseActor(x: Float, y: Float, s: Stage) : Group() {
    private var animation: Animation<TextureAtlas.AtlasRegion>?
    private var animationTime: Float = 0f
    private var animationPaused: Boolean = false

    private var velocityVec: Vector2 = Vector2(0f, 0f)
    private var accelerationVec: Vector2 = Vector2(0f, 0f)
    private var acceleration: Float = 0f
    private var maxSpeed: Float = 1000f
    private var deceleration: Float = 0f
    private var boundaryPolygon: Polygon? = null

    var isFacingRight = true
    var pause = false
    var animationWidth = width
    var animationHeight = height
    var isCollisionEnabled = true
    var shakyCamIntensity = .5f
    var isShakyCam = false

    init {
        this.x = x
        this.y = y
        s.addActor(this)
        animation = null
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        setAnimationSize(width, height)
    }

    override fun act(dt: Float) {
        if (!pause)
            super.act(dt)

        if (!animationPaused)
            animationTime += dt

        if (isShakyCam)
            shakeCamera()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        //  apply color tint effect
        val c: Color = color
        batch.setColor(c.r, c.g, c.b, c.a)

        if (animation != null && isVisible) {
            if (isFacingRight)
                batch.draw(
                    animation!!.getKeyFrame(animationTime),
                    x + abs(width - animationWidth) / 2,
                    y + abs(height - animationHeight) / 2,
                    originX,
                    originY,
                    animationWidth,
                    animationHeight,
                    scaleX,
                    scaleY,
                    rotation
                )
            else
                batch.draw(
                    animation!!.getKeyFrame(animationTime),
                    x + width,
                    y,
                    originX - width,
                    originY,
                    -width,
                    height,
                    scaleX,
                    scaleY,
                    rotation
                )
        }
        super.draw(batch, parentAlpha)
    }

    // Graphics ---------------------------------------------------------------------------------------------------
    fun setAnimation(anim: Animation<TextureAtlas.AtlasRegion>) {
        animation = anim
        animationTime = 0f

        val tr: TextureRegion = animation!!.getKeyFrame(0.toFloat())
        val w: Float = tr.regionWidth.toFloat() * TilemapActor.unitScale
        val h: Float = tr.regionHeight.toFloat() * TilemapActor.unitScale
        setSize(w, h)
        setOrigin(w / 2, h / 2)

        if (boundaryPolygon == null)
            setBoundaryRectangle()
    }

    fun setAnimationSize(width: Float, height: Float) {
        animationWidth = width
        animationHeight = height
    }

    fun flip() { isFacingRight = !isFacingRight }
    fun setAnimationPaused(pause: Boolean) { animationPaused = pause }
    fun isAnimationFinished(): Boolean { return animation!!.isAnimationFinished(animationTime) }
    open fun death() {}

    fun loadTexture(fileName: String): Animation<TextureRegion> {
        val fileNames: Array<String> = Array(1)
        fileNames.add(fileName)
        return loadAnimationFromFiles(fileNames, 1f, true)
    }

    private fun loadAnimationFromFiles(
        fileNames: Array<String>, frameDuration: Float, loop: Boolean,
        textureFilter: TextureFilter = TextureFilter.Linear
    ): Animation<TextureRegion> {
        val textureArray: Array<TextureRegion> = Array()

        for (i in 0 until fileNames.size) {
            val texture = Texture(Gdx.files.internal(fileNames[i]))
            texture.setFilter(textureFilter, textureFilter)
            textureArray.add(TextureRegion(texture))
        }

        val anim: Animation<TextureRegion> = Animation(frameDuration, textureArray)

        if (loop)
            anim.playMode = Animation.PlayMode.LOOP
        else
            anim.playMode = Animation.PlayMode.NORMAL

        if (animation == null)
            setAnimation(anim as Animation<TextureAtlas.AtlasRegion>)

        return anim
    }

    fun loadImage(name: String) {
        val region = BaseGame.textureAtlas!!.findRegion(name)
        setAnimation(Animation(1f, region))
    }

    // Physics ---------------------------------------------------------------------------------------------------
    fun setSpeed(speed: Float) {
        // If length is zero, then assume motion angle is zero degrees
        if (velocityVec.len() == 0f)
            velocityVec.set(speed, 0f)
        else
            velocityVec.setLength(speed)
    }

    fun getSpeed() = velocityVec.len()
    fun setMotionAngle(angle: Float) = velocityVec.setAngleDeg(angle)
    fun getMotionAngle() = velocityVec.angleDeg()
    fun getVelocity() = velocityVec
    fun setVelocity(vel: Vector2) { velocityVec = vel }
    fun isMoving() = getSpeed() > 0
    fun setAcceleration(acc: Float) { acceleration = acc }
    fun accelerateAtAngle(angle: Float) = accelerationVec.add(Vector2(acceleration, 0f).setAngle(angle))
    fun accelerateForward() = accelerateAtAngle(rotation)
    fun setMaxSpeed(ms: Float) { maxSpeed = ms }
    fun setDeceleration(dec: Float) { deceleration = dec }

    fun applyPhysics(dt: Float) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt)

        var speed = getSpeed()

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0f)
            speed -= deceleration * dt

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0f, maxSpeed)

        // update velocity
        setSpeed(speed)

        // apply velocity
        moveBy(velocityVec.x * dt, velocityVec.y * dt)

        // reset acceleration
        accelerationVec.set(0f, 0f)
    }

    // camera ---------------------------------------------------------------------------------------------------
    fun zoomCamera(zoom: Float) {
        if (this.stage != null) {
            val camera = this.stage.camera as OrthographicCamera
            camera.zoom = zoom

            bindCameraToWorld(camera)
            camera.update()
        }
    }

    fun alignCamera(target: Vector2 = Vector2(x, y), lerp: Float = 1f) {
        if (this.stage != null) {
            val camera = this.stage.viewport.camera as OrthographicCamera

            // center camera on actor
            camera.position.set(Vector3(
                camera.position.x + (target.x + originX - camera.position.x) * lerp,
                camera.position.y + (target.y + originY - camera.position.y) * lerp,
                0f
            ))

            bindCameraToWorld(camera)
            camera.update()
        }
    }

    fun averageBetweenTargetsCamera(targetA: Vector2, targetB: Vector2, lerp: Float = 1f) {
        if (this.stage != null) {
            val camera = this.stage.camera as OrthographicCamera
            val avgX = (targetA.x + targetB.x) / 2
            val avgY = (targetA.y + targetB.y) / 2
            val position = camera.position

            position.x = camera.position.x + (avgX - camera.position.x) * lerp
            position.y = camera.position.y + (avgY - camera.position.y) * lerp
            camera.position.set(position)

            bindCameraToWorld(camera)
            camera.update()
        }
    }

    fun searchFocalPoints(focalPoints: Array<Vector2>, target: Vector2, threshold: Float, lerp: Float = .1f): Boolean {
        if (this.stage != null) {
            val camera = this.stage.camera as OrthographicCamera
            for (point in focalPoints) {
                if (target.dst(point) < threshold) {
                    alignCamera(point, lerp)
                    return true
                }
            }
        }
        return false
    }

    private fun bindCameraToWorld(camera: OrthographicCamera) {
        val minX = (camera.viewportWidth * camera.zoom) / 2
        val maxX = worldBounds.width - (camera.viewportWidth * camera.zoom) / 2
        if (minX <= maxX)
            camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX)
        else
            camera.position.x = (camera.viewportWidth * camera.zoom) / 2 - ((camera.viewportWidth * camera.zoom) - worldBounds.width) / 2

        val minY = (camera.viewportHeight * camera.zoom) / 2
        val maxY = worldBounds.height - (camera.viewportHeight * camera.zoom) / 2
        if (minY <= maxY)
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY)
        else
            camera.position.y = (camera.viewportHeight * camera.zoom) / 2 - ((camera.viewportHeight * camera.zoom) - worldBounds.height) / 2
    }

    private fun shakeCamera() {
        this.stage.camera.position.set(Vector3(
            this.stage.viewport.camera.position.x + MathUtils.random(-shakyCamIntensity, shakyCamIntensity),
            this.stage.viewport.camera.position.y + MathUtils.random(-shakyCamIntensity, shakyCamIntensity),
            0f
        ))
        bindCameraToWorld(this.stage.camera as OrthographicCamera)
    }

    // Collision detection --------------------------------------------------------------------------------------
    fun setBoundaryRectangle() {
        val w: Float = width
        val h: Float = height
        val vertices: FloatArray = floatArrayOf(0f, 0f, w, 0f, w, h, 0f, h)
        boundaryPolygon = Polygon(vertices)
    }

    fun setBoundaryPolygon(numSides: Int) {
        val w: Float = width
        val h: Float = height

        val vertices = FloatArray(2 * numSides)
        for (i in 0 until numSides) {
            val angle: Float = i * MathUtils.PI2 / numSides
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2    // x-coordinates
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2  // y-coordinates
        }
        boundaryPolygon = Polygon(vertices)
    }

    fun getBoundaryPolygon(): Polygon {
        boundaryPolygon!!.setPosition(x, y)
        boundaryPolygon!!.setOrigin(originX, originY)
        boundaryPolygon!!.rotation = rotation
        boundaryPolygon!!.setScale(scaleX, scaleY)
        return boundaryPolygon as Polygon
    }

    fun overlaps(other: BaseActor): Boolean {
        if (!isCollisionEnabled || !other.isCollisionEnabled) return false
        val poly1: Polygon = this.getBoundaryPolygon()
        val poly2: Polygon = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle))
            return false
        return Intersector.overlapConvexPolygons(poly1, poly2)
    }

    fun preventOverlap(other: BaseActor): Vector2? {
        if (!isCollisionEnabled || !other.isCollisionEnabled) return null
        val poly1: Polygon = this.getBoundaryPolygon()
        val poly2: Polygon = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle))
            return null

        val mtv = Intersector.MinimumTranslationVector()
        val polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv)

        if (!polygonOverlap)
            return null

        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth)
        return mtv.normal
    }

    // miscellaneous -------------------------------------------------------------------------------------------
    fun centerAtPosition(x: Float, y: Float) = setPosition(x - width / 2, y - height / 2)
    fun centerAtActor(other: BaseActor) = centerAtPosition(other.x + other.width / 2, other.y + other.height / 2)
    fun setOpacity(opacity: Float) { this.color.a = opacity }
    fun getAngleTowardActor(other: BaseActor) = (MathUtils.atan2(y - other.y, x - other.x) * MathUtils.radiansToDegrees) + 180

    fun outOfBounds(): Boolean {
        return x < 0f - width || x > worldBounds.width + width ||
                y < 0f - height || y > worldBounds.height + height
    }

    fun boundToWorld() {
        if (x < 0)
            x = 0f
        else if (x + width > worldBounds.width)
            x = worldBounds.width - width

        if (y < 0)
            y = 0f
        else if (y + height > worldBounds.height)
            y = worldBounds.height - height
    }

    fun isWithinDistance(distance: Float, other: BaseActor) : Boolean {
        val poly1 = this.getBoundaryPolygon()
        val scaleX = (this.width + 2 * distance) / this.width
        val scaleY = (this.height + 2 * distance) / this.height
        poly1.setScale(scaleX, scaleY)

        val poly2 = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle))
            return false
        return Intersector.overlapConvexPolygons(poly1, poly2)
    }

    companion object {
        private val token = "BaseActor.kt - companion: "
        private var worldBounds = Rectangle(0f, 0f, BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT)

        fun setWorldBounds(width: Float, height: Float) { worldBounds = Rectangle(0f, 0f, width, height) }
        fun setWorldBounds(ba: BaseActor) = setWorldBounds(ba.width, ba.height)
        fun getWorldBounds() = worldBounds
        fun count(stage: Stage, className: String): Int = getList(stage, className).size // HTML incompatible

        fun getList(stage: Stage, className: String): ArrayList<BaseActor> { // HTML incompatible
            var list: ArrayList<BaseActor> = ArrayList()

            var theClass: Class<*>? = null
            try { theClass = Class.forName(className) }
            catch (error: Exception) { error.printStackTrace() }

            for (actor in stage.actors)
                if (theClass!!.isInstance(actor))
                    list.add(actor as BaseActor)

            return list
        }
    }
}