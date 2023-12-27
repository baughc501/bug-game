package com.example.app5

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Half.toFloat
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import java.util.Timer

class MyView : View
{

  companion object
  {
    private var instance : MyView? = null
    public fun getInstance() : MyView
    {
      return instance!!
    }
  }

  private var x1 : Float = 100.0f
  private var y1 : Float = 100.0f
  private var ball : ImageView? = null
  private var angle : SeekBar? = null
  private var cannonBase : ImageView? = null
  private var targets = ArrayList<Drawable>() //Targets
  private var cannonBarrel = Path()
  private var start : Boolean = true          ///Display the ball properly
  private var ballCoords : Rect = Rect(0,0,0,0)
  private var cannonBarrelCoords : Rect = Rect(0,0,0,0)
  private var targetCoords : Rect = Rect(0,0,0,0)
  private var ball_touched = false
  private var startBallPoint : Point = Point(0,0)
  private var offsetBall : Point = Point(0,0)
  private var totalScore : Int = 0
  private var angleChange : Int =0
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)  //One per widget

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  {
    cannonBarrelCoords.set(83,800,118,700)
    ballCoords.set(155,765,200, 810)

    instance = this
  }

  public fun setballCoords(ux : Int, uy : Int, lx : Int, ly : Int)
  {
    this.ballCoords.set(ux,uy,lx,ly)
  }
  public fun setcannonBarrelCoords(ux : Int, uy : Int, lx : Int, ly : Int)
  {
    this.cannonBarrelCoords.set(ux,uy,lx,ly)
  }
  public fun setTargetCoords(ux : Int, uy : Int, lx : Int, ly : Int)
  {
    this.targetCoords.set(ux,uy,lx,ly)
  }

  public fun getBallCoords() : Rect
  {
    return ballCoords
  }
  public fun getCannonBarrelCoords() : Rect
  {
    return cannonBarrelCoords
  }
  public fun getTargetCoords(drawable: Drawable): Rect
  {
    return targetCoords
  }

  public fun getTargets(): ArrayList<Drawable>
  {
    return targets
  }

  public fun setCoords(x1 : Float, y1 : Float)
  {
    this.x1 = x1
    this.y1 = y1
  }

  public fun getX1() : Float
  {
    return x1
  }
  public fun getY1() : Float
  {
    return y1
  }

  override fun onDraw(canvas: Canvas)
  {
    super.onDraw(canvas)

    paint.setColor(Color.BLACK)

    ball = MainActivity.getInstance().findViewById<ImageView>(R.id.ball)
    angle = MainActivity.getInstance().findViewById<SeekBar>(R.id.angle)
    angleChange = angle!!.progress.toInt()
    cannonBase = MainActivity.getInstance().findViewById<ImageView>(R.id.cannonBase)

    //draw the barrel
    paint.setColor(Color.DKGRAY)
    paint.setStrokeWidth(40.0f)
    canvas.drawLine(cannonBarrelCoords.left.toFloat(),cannonBarrelCoords.bottom.toFloat(),
      cannonBarrelCoords.left.toFloat()+90 -angleChange,cannonBarrelCoords.top.toFloat()+90-angleChange,paint)


//Draw the targets normally –
    for (i in 0..targets.size - 1)
    {
      var drawable = targets.get(i)
      drawable.draw(canvas)
    }

    ball?.setLeftTopRightBottom(ballCoords.left,ballCoords.top, ballCoords.right, ballCoords.bottom)
    //ball?.setLeftTopRightBottom(cannonBarrelCoords.left,cannonBarrelCoords.top, cannonBarrelCoords.right, cannonBarrelCoords.bottom)
    //ballCoords.set(cannonBarrelCoords.left,cannonBarrelCoords.top, cannonBarrelCoords.right, cannonBarrelCoords.bottom)

    if (start)
    {
      start = false
    }
    else
    {
      //ball?.setX(ballCoords.left.toFloat())
      //ball?.setY(ballCoords.top.toFloat())
      ball?.setLeftTopRightBottom(ballCoords.left,ballCoords.top, ballCoords.right, ballCoords.bottom)
      //ball?.setLeftTopRightBottom(cannonBarrelCoords.left,cannonBarrelCoords.top, cannonBarrelCoords.right, cannonBarrelCoords.bottom)
      //ballCoords.set(cannonBarrelCoords.left,cannonBarrelCoords.top, cannonBarrelCoords.right, cannonBarrelCoords.bottom)
    }

  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
  {
    super.onSizeChanged(w, h, oldw, oldh)
    var width = this.getWidth()
    var height = this.getHeight()
    //Run only once per class
    var startX = (.5 * width).toInt()
    var startY = (.1 * height).toInt()
    for (i in 0..48)
    {
        var x = startX
        var y = startY
        var imageView = ImageView(MainActivity.getInstance())
        imageView.setImageResource(R.drawable.untouched)
        var drawable = imageView.getDrawable()
        drawable.setBounds(x, y, x + (.045 * width).toInt(), y + (.045 * width).toInt()) //Sets the dimensions
        setTargetCoords(x, y, x + (.045 * width).toInt(), y + (.045 * width).toInt())
        targets.add(drawable)  //stores away the image
        startX = startX + (.045 * width).toInt()
        if (targets.size%7==0)
        {
          startX = (.5 * width).toInt()
          startY = startY + (.045 * width).toInt()
        }
    }
    //ball = MainActivity.getInstance().findViewById<ImageView>(R.id.ball)

    cannonBase = MainActivity.getInstance().findViewById<ImageView>(R.id.cannonBase)
    cannonBarrelCoords.set((.03 * width).toInt() , (.74 * height).toInt(), (.04 * width).toInt(), (.84 * height).toInt())
    ballCoords.set((.06 * width).toInt(), (.8 * height).toInt(), (.1 * width).toInt(), (.85 * height).toInt())
    //ballCoords.set(cannonBarrelCoords.left,cannonBarrelCoords.top, cannonBarrelCoords.right, cannonBarrelCoords.bottom)

    var handler = Handler1()

    //maybe use this for fire button
    var fire =  MainActivity.getInstance().findViewById<Button>(R.id.fireButton)
    fire.setOnClickListener(handler)

  }

  //need to modify
  inner  class Handler1 : View.OnClickListener
  {
    var shotCounter = MainActivity.getInstance().findViewById<TextView>(R.id.shotCounter)
    private var shotCount : Int = 0

    //private var counter : Int = 0
    override fun onClick(v: View?)
    {
      var timer = MainActivity.getInstance().fire()
      shotCount++
      shotCounter.setText(shotCount.toString())

    }
  }
}