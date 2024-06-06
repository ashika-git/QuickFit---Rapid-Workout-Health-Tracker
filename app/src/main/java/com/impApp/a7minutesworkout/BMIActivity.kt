package com.impApp.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.impApp.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object {
        private const val METRIC_UNIT_VIEW = "METRIC_UNIT_VIEW" //metric unit view
        private const val US_UNIT_VIEW = "US_UNIT_VIEW" //us unit view
    }

    private var currentVisibleView: String =
        METRIC_UNIT_VIEW

    private var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }

        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            //onBackPressed()
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId: Int ->

            if (checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleUSMetricUnitsView()
            }
        }

        binding?.btnCalculateUnit?.setOnClickListener {
            /*if (isValidateMetricUnits()) {
                val heightVal: Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100

                val weightVal: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()

                val calBMI = weightVal / (heightVal * heightVal)

                displayBMIResult(calBMI)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
             */

            calculateUnits()
        }
    }

    private fun makeVisibleMetricUnitsView() {

        currentVisibleView = METRIC_UNIT_VIEW
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE

        binding?.tilUSMetricUnitWeight?.visibility = View.GONE
        binding?.tilMetricUnitHeightFeet?.visibility = View.GONE
        binding?.tilMetricUnitHeightInch?.visibility = View.GONE

        binding?.etMetricUnitWeight?.text!!.clear()
        binding?.etMetricUnitHeight?.text!!.clear()

        binding?.LLdiplayBMIresult?.visibility = View.INVISIBLE

    }

    private fun makeVisibleUSMetricUnitsView() {

        currentVisibleView = US_UNIT_VIEW
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.tilMetricUnitWeight?.visibility = View.INVISIBLE
        binding?.tilUSMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeightInch?.visibility = View.VISIBLE

        binding?.etUSMetricUnitWeight?.text!!.clear()
        binding?.etUSMetricUnitHeightFeet?.text!!.clear()
        binding?.etUSMetricUnitHeightInch?.text!!.clear()

        binding?.LLdiplayBMIresult?.visibility = View.INVISIBLE

    }

    private fun displayBMIResult(calBMI: Float) {

        val bmiLabel: String
        val bmiDescription: String

        if (calBMI.compareTo(15f) <= 0) {
            bmiLabel = "Very Severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself !Eat more"
        } else if (calBMI.compareTo(15f) > 0 && calBMI.compareTo(16f) <= 0) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself !Eat more"

        } else if (calBMI.compareTo(16f) > 0 && calBMI.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more"

        } else if (calBMI.compareTo(18.5f) > 0 && calBMI.compareTo(25f) <= 0) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (calBMI.compareTo(25f) > 0 && calBMI.compareTo(30f) <= 0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of yourself! Workout"

        } else if (calBMI.compareTo(30f) > 0 && calBMI.compareTo(35f) <= 0) {
            bmiLabel = "Obse Class | (Moderately obse)"
            bmiDescription = "Oops! You really need to take care of yourself! Workout"

        } else if (calBMI.compareTo(35f) > 0 && calBMI.compareTo(40f) <= 0) {
            bmiLabel = "Obse Class || (Severely obse)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act Now!"
        } else {
            bmiLabel = "Obse Class ||| (Severely obse)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act Now!"
        }

        val bmiValue = BigDecimal(calBMI.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        binding?.LLdiplayBMIresult?.visibility = View.VISIBLE
        binding?.tvBmiValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBmiDescription?.text = bmiDescription

    }

    private fun isValidateMetricUnits(): Boolean {
        var isValid = true
        if (binding?.etMetricUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.etMetricUnitHeight?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    //calculate the US matrix unit
    private fun calculateUnits() {
        if (currentVisibleView == METRIC_UNIT_VIEW) {
            if (isValidateMetricUnits()) {
                val heightVal: Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100

                val weightVal: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()

                val calBMI = weightVal / (heightVal * heightVal)

                displayBMIResult(calBMI)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if (validateUsUnits()) {
                val usUnitHeightValueFeet: String =
                    binding?.etUSMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch: String =
                    binding?.etUSMetricUnitHeightInch?.text.toString()
                val usUnitWeightValue: Float =
                    binding?.etUSMetricUnitWeight?.text.toString().toFloat()

                //calculating the height feet and Inch values
                val heightValue =
                    usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                val bmi = 703 * ((usUnitWeightValue) / (heightValue * heightValue))

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun validateUsUnits(): Boolean {
        var isValid = true

        when {
            binding?.etUSMetricUnitWeight?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.etUSMetricUnitHeightFeet?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.etUSMetricUnitHeightInch?.text.toString().isEmpty() -> {
                isValid = false
            }
        }
        return isValid
    }

}