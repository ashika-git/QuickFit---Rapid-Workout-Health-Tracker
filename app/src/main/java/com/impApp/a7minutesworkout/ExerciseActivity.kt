package com.impApp.a7minutesworkout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.impApp.a7minutesworkout.databinding.ActivityExerciseBinding
import com.impApp.a7minutesworkout.databinding.ActivityMainBinding
import com.impApp.a7minutesworkout.databinding.DialogCustomBackBinding
import java.lang.Exception
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private  var  binding: ActivityExerciseBinding? = null

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Long = 1

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long = 1


    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    //text to speech
    private var tts : TextToSpeech? = null
    //we need a media player obj so that once the exercise done we will know that
    private var player:MediaPlayer? = null

    private var exerciseAdapter :ExerciseStatusAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        //from this you get a back button on a toolbar
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener{
            //onBackPressed()
            customDialogForBackButton()
        }
        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this,this)
        setupRestView()
        setUpExerciseStatusRecyclerView()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)

        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()

        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }


    private fun setupRestView(){

        try {
            val soundURI = Uri.parse("android.resource://com.impApp.a7minutesworkout/" +R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e:Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility =View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility =View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpComingExerciseName?.visibility =View.VISIBLE

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        //what is gone a be next exercise is mention here
        binding?.tvUpComingExerciseName?.text =
            exerciseList !! [currentExercisePosition +1].getName()


        setRestProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object :CountDownTimer(restTimerDuration * 1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text =(10 - restProgress).toString()

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                currentExercisePosition++
                //here color of the recycler view is going to change
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }
    private fun setupExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility =View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility =View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpComingExerciseName?.visibility =View.INVISIBLE

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList !! [currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList !! [currentExercisePosition].getName()


        setExerciseProgressBar()
    }


    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object :CountDownTimer(exerciseTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text =(30 - exerciseProgress).toString()

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {

                if(currentExercisePosition < exerciseList?.size !! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else{
//                    Toast.makeText(
//                        this@ExerciseActivity,
//                        "Congratulations! you have completed the 7 minutes workout.",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        //shutting down the text to speech feature when activity is destroyed
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        //media player distroy to prevent the leakage of audio file
        if(player != null){
            player?.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            //set us english language
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The language specified is not supported!")
            }
        }else
        {
            Log.e("TTS" , "Initialization Failed")
        }
    }

    private fun speakOut(text :String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null , "")
    }
    private fun setUpExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter

    }
}