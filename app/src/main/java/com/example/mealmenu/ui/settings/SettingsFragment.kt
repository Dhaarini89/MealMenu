package com.example.mealmenu.ui.settings


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mealmenu.MainActivity.Companion.Daysvalue

import com.example.mealmenu.ui.MenuBroadcastReciever
import com.example.mealmenu.ui.PlannerBroadcastReciever
import com.example.mealmenu.ui.viewmodel.GenericViewModel
import com.example.mealmenu.R
import com.example.mealmenu.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {
    var selectedDay: String? = null
    var lastselectedplandayposition:Int?=0
    private lateinit var sharedPref: SharedPreferences
    private var _binding: FragmentSettingsBinding? = null
    private val viewModel: GenericViewModel by viewModels()
    private  var plannerpendingIntent :PendingIntent?=null
    private var setplannerpendingIntent :PendingIntent?=null
    private val binding get() = _binding!!
    var timeArray = emptyArray<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         val plannerintent = Intent(requireContext(), MenuBroadcastReciever::class.java)
        val setplannerintent = Intent(requireContext(), PlannerBroadcastReciever::class.java)
        plannerpendingIntent = PendingIntent.getBroadcast(requireContext(), 200, plannerintent, PendingIntent.FLAG_MUTABLE)
        setplannerpendingIntent = PendingIntent.getBroadcast(requireContext(), 20, setplannerintent, PendingIntent.FLAG_IMMUTABLE)

        var counter :Int=0
        for (i in resources.getStringArray(R.array.week)) {
            Daysvalue[i] = counter +1
            counter +=1
        }
        counter =0
        timeArray +="Never"
        for (i in 0..9) {
            timeArray += "0${i}:00"
        }

        for (i in 10..23) {
            timeArray += "${i}:00"
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.notifyMenuTime.adapter = adapter
        val startday=sharedPref.getString("startDay", null)
        startday?.let { value ->
            val position = (binding.days.adapter as ArrayAdapter<String>).getPosition(value)
            binding.days.setSelection(position)
        }
        binding.days.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDay = parent.getItemAtPosition(position) as String
                val editor = sharedPref.edit()
                editor.putString("startDay", selectedDay)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        lastselectedplandayposition=sharedPref.getInt("selectPlanDayPosition", 0)
        binding.notifyPlanDays.setSelection(lastselectedplandayposition!!)
        binding.notifyPlanDays.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedPlanDay = parent.getItemAtPosition(position) as String
                    val editor = sharedPref.edit()
                    editor.putInt("selectPlanDayPosition", position)
                    editor.apply()
                    Log.d("lastposition",position.toString())
                    if (lastselectedplandayposition != position) {
                        if (selectedPlanDay != "Never") {
                            setPlannerNotification(position)
                            lastselectedplandayposition=position
                        }
                        else {
                            val setplanneralarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            setplanneralarmManager.cancel(plannerpendingIntent)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        val selectedMenuTime=sharedPref.getString("selectedMenuTime", null)
        selectedMenuTime?.let { value ->
            val position = (binding.notifyMenuTime.adapter as ArrayAdapter<String>).getPosition(value)
            binding.notifyMenuTime.setSelection(position)
        }
        val lastselectedMenuTimePosition=sharedPref.getInt("selectedMenuTimePosition",0)
        binding.notifyMenuTime.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedMenuTime = parent.getItemAtPosition(position) as String
                    val editor = sharedPref.edit()
                    editor.putString("selectedMenuTime", selectedMenuTime)
                    editor.putInt("selectedMenuTimePosition",position)
                    editor.apply()
                   if ( position != lastselectedMenuTimePosition )
                   {
                   if (position!= 0) {
                       binding.alertMessage.text="Device with version Android 12 and above,Please set ALLOW NOTIFICATIONS for the app in Settings"
                       binding.alertMessage.visibility =View.VISIBLE
                       val selectedMenuTime=sharedPref.getString("selectedMenuTime","8:00")
                        setMenuNotification(selectedMenuTime!!)
                     }
                    else
                    {
                        val planneralarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        planneralarmManager.cancel(plannerpendingIntent)
                        binding.alertMessage.visibility=View.INVISIBLE
                    }

                   }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }


        return root
    }

    fun setPlannerNotification(value :Int) {
        val setplannercalendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, value)
            set(Calendar.HOUR_OF_DAY, 9) // Set the desired hour of the day
            set(Calendar.MINUTE, 0) // Set the desired minute of the hour
            set(Calendar.SECOND, 0)
        }
        if (setplannercalendar.timeInMillis < Calendar.getInstance().timeInMillis)
        {
            setplannercalendar.add(Calendar.WEEK_OF_YEAR,1)

        }
        val setplanneralarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setplanneralarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            setplannercalendar.timeInMillis, AlarmManager.INTERVAL_DAY*7,
            setplannerpendingIntent
        )
    }
    fun setMenuNotification(value: String)
    {
        Log.d("MenuNotificationset","Yes")
         val timevalue =value.split(":")[0].toInt()
         val plannercalendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, timevalue) // Set the desired hour of the day
            set(Calendar.MINUTE, 0) // Set the desired minute of the hour
            set(Calendar.SECOND, 0)
        }
        if (plannercalendar.timeInMillis < Calendar.getInstance().timeInMillis)
        {
            plannercalendar.add(Calendar.DAY_OF_MONTH,1)
            plannercalendar.set(Calendar.HOUR_OF_DAY,timevalue)
            plannercalendar.set(Calendar.MINUTE,0)
            plannercalendar.set(Calendar.SECOND,0)
        }
        val planneralarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        planneralarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            plannercalendar.timeInMillis, AlarmManager.INTERVAL_DAY,
            plannerpendingIntent
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}