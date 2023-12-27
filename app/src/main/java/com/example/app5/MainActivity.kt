package com.example.app5

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.Timer
import java.util.TimerTask

class HelperThread : Runnable
{
  override fun run()
  {
    MainActivity.getInstance().update()
  }
}


class TimerObject : TimerTask()
{
  override fun run()
  {
    var helper = HelperThread()

    MainActivity.getInstance().runOnUiThread(helper)
  }
}

class MainActivity : AppCompatActivity()
{
  //Data Fields for controlling the view
  private var speed = 5
  private var ball_dir = 1
  private var timer = Timer()
  private var shotCount : Int = 0
  private var angleValue = 0
  private var velocityValue = 0
  private var targets = ArrayList<Drawable>() //Targets
  var totalScore : Int = 0
  var x = 0
  var y = 0
  private var counter : Int = 0
  private var GRAVITY : Double = 9.8
  private var alpha : Double = 0.0
  private var PI = 3.141592
  private var t : Double = 0.0
  private var total : Int = 0

  public fun getTimer(): Timer
  {
    return timer
  }

  public fun fire()
  {
    timer = Timer()
    var timerTask = TimerObject()
    timer.schedule(timerTask,50,25)
  }

  companion object
  {
    private var instance : MainActivity? = null
    public fun getInstance() : MainActivity
    {
      return instance!!
    }
  }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    instance = this
    setContentView(R.layout.activity_main)

    var handler = Handler()
    var fire = findViewById<Button>(R.id.fireButton)
    fire.setOnClickListener(handler)
    //slider
    var angleSlider = findViewById<SeekBar>(R.id.angle)
    angleSlider.setOnSeekBarChangeListener(handler)

    //to handle second slider
    var handler2 = Handler2()
    var velocitySlider = findViewById<SeekBar>(R.id.velocity)
    velocitySlider.setOnSeekBarChangeListener(handler2)

  }

  public fun update()
  {
    var myView = findViewById<MyView>(R.id.myView)
    var ball = findViewById<ImageView>(R.id.ball)
    var cannonBase = findViewById<ImageView>(R.id.cannonBase)
    targets = myView.getTargets()
    //var counter : Int = 0
    //total = targets.size
    //var t : Int = 0


    //Synchronize with the view getting setup
    if (myView.getWidth() > 1)
    {
      //println(myView.getWidth() )
      //println(myView.getHeight())

      ball.left += ((velocityValue*(cos(alpha) *t)) + 1).toInt()
      ball.right += ((velocityValue*(cos(alpha) *t)) + 1).toInt()
      ball.top += ((-velocityValue*(sin(alpha) *t) + 0.5*GRAVITY*t*t) + 1).toInt()
      ball.bottom += ((-velocityValue*(sin(alpha) *t) + 0.5*GRAVITY*t*t) + 1).toInt()
      t = t + .025
      //Check for edges
      if ( (ball.right < 0) || (ball.left > myView.getWidth()) || (ball.bottom < 0)
        || (ball.top > myView.getHeight()))
      {
        //ball_dir *= -1
        println("out of bounds")
        var timer = MainActivity.getInstance().getTimer()
        t = 0.0
        timer.cancel()
      }

      myView.setballCoords(ball.left, ball.top, ball.right, ball.bottom)
      //myView.setballCoords(myView.getCannonBarrelCoords().left,myView.getCannonBarrelCoords().top, myView.getCannonBarrelCoords().right, myView.getCannonBarrelCoords().bottom)
      //myView.setcannonBarrelCoords(0,myView.getHeight(), 20, (.1 * myView.getHeight()).toInt())

      //*********NEW STUFF************
      var counter : Int = 0
      total = targets.size

      for (i in 0..<total-1)
      {
        var target = targets.get(i)
        if ( (ball?.left!! > target.getBounds().left) && (ball?.top!! > target.getBounds().top) &&
          (ball?.left!! < target.getBounds().right) && (ball?.top!! < target.getBounds().bottom))
        {
          totalScore+=1
          println(totalScore)
          var score = MainActivity.getInstance().findViewById<TextView>(R.id.score)
          score.setText(totalScore.toString())
          targets.removeAt(i)
          if (totalScore ==48)
          {
            successDialog()
          }
          myView.invalidate()
          //counter++
          total = total -1

        }
      }

      myView.invalidate()
    }

  }

  fun successDialog()
  {
    var dialogBuilder = AlertDialog.Builder(MainActivity.getInstance())
    var handler = Handler()
    //dialogBuilder.setPositiveButton("OK", handler)
    val alert1 = dialogBuilder.create()
    alert1.setTitle("Game Over!")
    alert1.show()
  }

  //modify to handle velocity button
  inner class Handler : View.OnClickListener, SeekBar.OnSeekBarChangeListener
  {
    override fun onClick(v: View?)
    {
      var text = (v as Button).getText()
      if (text == "FIRE")
      {

        speed++
      }

      else if (text == "-")
        speed--
      if (speed < 0)
        speed = 0
    }

    //slider
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
    {
      println("Inside of onProgressChanged")
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?)
    {
      println("start")
    }
    override fun onStopTrackingTouch(seekBar: SeekBar?)
    {
      angleValue = 0
      angleValue = seekBar!!.progress.toInt()
      println(angleValue)
      var angle = MainActivity.getInstance().findViewById<TextView>(R.id.angleOut)
      angle.setText(angleValue.toString())
      alpha = angleValue * PI / 180.0
      var myView = findViewById<MyView>(R.id.myView)
      myView.invalidate()
    }

    fun onClick(dialog: DialogInterface?, which: Int)
    {

      if (which == DialogInterface.BUTTON_NEGATIVE)
      {
        println("negative")
      }
      else if (which == DialogInterface.BUTTON_POSITIVE)
      {
        println("positive")
      }
    }

  }

  inner class Handler2 : SeekBar.OnSeekBarChangeListener
  {
    //slider
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
    {
      println("Inside of onProgressChanged")
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?)
    {
      println("start")
    }
    override fun onStopTrackingTouch(seekBar: SeekBar?)
    {
      velocityValue = seekBar!!.progress.toInt()/2
      println(velocityValue)
      var velocity = MainActivity.getInstance().findViewById<TextView>(R.id.velocityOut)
      velocity.setText(velocityValue.toString())
      var myView = findViewById<MyView>(R.id.myView)
      myView.invalidate()
    }

  }

}
