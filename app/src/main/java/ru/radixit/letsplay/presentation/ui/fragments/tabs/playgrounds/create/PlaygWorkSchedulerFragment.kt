package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.create

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Schedule
import ru.radixit.letsplay.databinding.WorkScheduleRedesFragBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible
import java.util.*

@AndroidEntryPoint
class PlaygWorkSchedulerFragment : DialogFragment() {

    private var _binding: WorkScheduleRedesFragBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreatePlaygroundsViewModel
    private var _timePickerDialog: TimePickerDialog? = null
    private val timePickerDialog get() = _timePickerDialog!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WorkScheduleRedesFragBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreatePlaygroundsViewModel::class.java]
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar2.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            withTimeEd1.setText(if (viewModel.getMondayStart() != null) viewModel.getMondayStart() else "")
            withTimeEd2.setText(if (viewModel.getTuesdayStart() != null) viewModel.getTuesdayStart() else "")
            withTimeEd3.setText(if (viewModel.getWednesdayStart() != null) viewModel.getWednesdayStart() else "")
            withTimeEd4.setText(if (viewModel.getThursdayStart() != null) viewModel.getThursdayStart() else "")
            withTimeEd5.setText(if (viewModel.getFridayStart() != null) viewModel.getFridayStart() else "")
            withTimeEd6.setText(if (viewModel.getSaturdayStart() != null) viewModel.getSaturdayStart() else "")
            withTimeEd7.setText(if (viewModel.getSundayStart() != null) viewModel.getSundayStart() else "")

            byField1.setText(if (viewModel.getMondayEnd() != null) viewModel.getMondayEnd() else "")
            byField2.setText(if (viewModel.getTuesdayEnd() != null) viewModel.getTuesdayEnd() else "")
            byField3.setText(if (viewModel.getWednesdayEnd() != null) viewModel.getWednesdayEnd() else "")
            byField4.setText(if (viewModel.getThursdayEnd() != null) viewModel.getThursdayEnd() else "")
            byField5.setText(if (viewModel.getFridayEnd() != null) viewModel.getFridayEnd() else "")
            byField6.setText(if (viewModel.getSaturdayEnd() != null) viewModel.getSaturdayEnd() else "")
            byField7.setText(if (viewModel.getSundayEnd() != null) viewModel.getSundayEnd() else "")
            byDayMatBtn.setOnClickListener {
                constByWeek1.visible()
                constByWeek2.gone()
                byWeekMatBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                byDayMatBtn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                byWeekMatBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )
                byDayMatBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                byDayMatBtn.tag = "1"
                byWeekMatBtn.tag = "0"
            }
            byWeekMatBtn.setOnClickListener {
                if (binding.week1Rb.isChecked && !binding.week2Rb.isChecked) {
                    allDayWorking()
                } else {
                    allDayWeekend()
                }
                binding.week1Rbg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rGroup, checkedId ->
                    val radioButtonID: Int = rGroup.checkedRadioButtonId
                    val radioButton: View = rGroup.findViewById(radioButtonID)
                    val idx: Int = rGroup.indexOfChild(radioButton)

                    if (idx == 0) {
                        binding.week1Rb.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.main_orange
                            )
                        )
                        binding.week2Rb.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.main_gray
                            )
                        )
                    } else if (idx == 1) {
                        binding.week2Rb.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.main_orange
                            )
                        )
                        binding.week1Rb.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.main_gray
                            )
                        )
                    }
                })
                constByWeek1.gone()
                constByWeek2.visible()
                byWeekMatBtn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                byDayMatBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                byWeekMatBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                byDayMatBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )
                byDayMatBtn.tag = "0"
                byWeekMatBtn.tag = "1"
            }
            continueButton.setOnClickListener {
                val list = mutableListOf<Schedule>()
                if (viewModel.getMondayStart() != null && viewModel.getMondayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 0,
                            timeStart = viewModel.getMondayStart(),
                            timeEnd = viewModel.getMondayEnd()
                        )
                    )
                if (viewModel.getTuesdayStart() != null && viewModel.getTuesdayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 1,
                            timeStart = viewModel.getTuesdayStart(),
                            timeEnd = viewModel.getTuesdayEnd()
                        )
                    )
                if (viewModel.getWednesdayStart() != null && viewModel.getWednesdayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 2,
                            timeStart = viewModel.getWednesdayStart(),
                            timeEnd = viewModel.getWednesdayEnd()
                        )
                    )
                if (viewModel.getThursdayStart() != null && viewModel.getThursdayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 3,
                            timeStart = viewModel.getThursdayStart(),
                            timeEnd = viewModel.getThursdayEnd()
                        )
                    )
                if (viewModel.getFridayStart() != null && viewModel.getFridayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 4,
                            timeStart = viewModel.getFridayStart(),
                            timeEnd = viewModel.getFridayEnd()
                        )
                    )
                if (viewModel.getSaturdayStart() != null && viewModel.getSaturdayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 5,
                            timeStart = viewModel.getSaturdayStart(),
                            timeEnd = viewModel.getSaturdayEnd()
                        )
                    )
                if (viewModel.getSundayStart() != null && viewModel.getSundayEnd() != null)
                    list.add(
                        Schedule(
                            weekDay = 6,
                            timeStart = viewModel.getSundayStart(),
                            timeEnd = viewModel.getSundayEnd()
                        )
                    )
                viewModel.saveScheduler(list)
                requireContext().showToast("Сохранено")
                findNavController().popBackStack()
            }
            val weekends = listOf(
                day1Rbg,
                day2Rbg,
                day3Rbg,
                day4Rbg,
                day5Rbg,
                day6Rbg,
                day7Rbg
            )
            val allDays = listOf(
                allDayField1,
                allDayField2,
                allDayField3,
                allDayField4,
                allDayField5,
                allDayField6,
                allDayField7
            )
            val starts = listOf(
                withTimeEd1,
                withTimeEd2,
                withTimeEd3,
                withTimeEd4,
                withTimeEd5,
                withTimeEd6,
                withTimeEd7
            )
            val ends = listOf(
                byField1,
                byField2,
                byField3,
                byField4,
                byField5,
                byField6,
                byField7
            )

            for (i in weekends.indices) {
                weekends[i].setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rGroup, checkedId ->
                    val radioButton: RadioButton = rGroup.findViewById(checkedId)
                    val idx: Int = rGroup.indexOfChild(radioButton)

                    val count: Int = rGroup.getChildCount()
                    for (i in 0 until count) {
                        val view: View = rGroup.getChildAt(i)
                        if (view is RadioButton) {
                            val radioButtonId = view.id
                            if (radioButtonId == checkedId) {
                                radioButton.buttonTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.main_orange
                                    )
                                )
                            } else {
                                val radioButtonOther: RadioButton = rGroup.findViewById(radioButtonId)
                                radioButtonOther.buttonTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.main_gray
                                    )
                                )
                            }
                        }
                    }
                    if (idx == 0) {
                        allDays[i].isEnabled = true
                        starts[i].isEnabled = true
                        ends[i].isEnabled = true
                        allDays[i].setBackgroundColor(
                            resources.getColor(
                                R.color.background_for_edittext,
                                resources.newTheme()
                            )
                        )
                        starts[i].setBackgroundColor(
                            resources.getColor(
                                R.color.background_for_edittext,
                                resources.newTheme()
                            )
                        )
                        ends[i].setBackgroundColor(
                            resources.getColor(
                                R.color.background_for_edittext,
                                resources.newTheme()
                            )
                        )
                        when (i) {
                            0 -> {
                                viewModel.saveMondayStart(null)
                                viewModel.saveMondayEnd(null)
                            }
                            1 -> {
                                viewModel.saveTuesdayStart(null)
                                viewModel.saveTuesdayEnd(null)
                            }
                            2 -> {
                                viewModel.saveWednesdayStart(null)
                                viewModel.saveWednesdayEnd(null)
                            }
                            3 -> {
                                viewModel.saveThursdayStart(null)
                                viewModel.saveThursdayEnd(null)
                            }
                            4 -> {
                                viewModel.saveFridayStart(null)
                                viewModel.saveFridayEnd(null)
                            }
                            5 -> {
                                viewModel.saveSaturdayStart(null)
                                viewModel.saveSaturdayEnd(null)
                            }
                            6 -> {
                                viewModel.saveSundayStart(null)
                                viewModel.saveSundayEnd(null)
                            }
                        }
                    } else if (idx == 1) {
                        allDays[i].isEnabled = false
                        starts[i].isEnabled = false
                        ends[i].isEnabled = false
                        allDays[i].setBackgroundColor(
                            resources.getColor(
                                R.color.weekend_color,
                                resources.newTheme()
                            )
                        )
                        starts[i].setBackgroundColor(
                            resources.getColor(
                                R.color.weekend_color,
                                resources.newTheme()
                            )
                        )
                        ends[i].setBackgroundColor(
                            resources.getColor(
                                R.color.weekend_color,
                                resources.newTheme()
                            )
                        )
                        when (i) {
                            0 -> {
                                viewModel.saveMondayStart("")
                                viewModel.saveMondayEnd("")
                            }
                            1 -> {
                                viewModel.saveTuesdayStart("")
                                viewModel.saveTuesdayEnd("")
                            }
                            2 -> {
                                viewModel.saveWednesdayStart("")
                                viewModel.saveWednesdayEnd("")
                            }
                            3 -> {
                                viewModel.saveThursdayStart("")
                                viewModel.saveThursdayEnd("")
                            }
                            4 -> {
                                viewModel.saveFridayStart("")
                                viewModel.saveFridayEnd("")
                            }
                            5 -> {
                                viewModel.saveSaturdayStart("")
                                viewModel.saveSaturdayEnd("")
                            }
                            6 -> {
                                viewModel.saveSundayStart("")
                                viewModel.saveSundayEnd("")
                            }
                        }
                    }
                })
                allDays[i].setOnClickListener {
                    allDays[i].setBackgroundColor(
                        resources.getColor(
                            R.color.main_orange,
                            resources.newTheme()
                        )
                    )
                    starts[i].setBackgroundColor(
                        resources.getColor(
                            R.color.weekend_color,
                            resources.newTheme()
                        )
                    )
                    ends[i].setBackgroundColor(
                        resources.getColor(
                            R.color.weekend_color,
                            resources.newTheme()
                        )
                    )
                    when (i) {
                        0 -> {
                            viewModel.saveMondayStart("")
                            viewModel.saveMondayEnd("")
                        }
                        1 -> {
                            viewModel.saveTuesdayStart("")
                            viewModel.saveTuesdayEnd("")
                        }
                        2 -> {
                            viewModel.saveWednesdayStart("")
                            viewModel.saveWednesdayEnd("")
                        }
                        3 -> {
                            viewModel.saveThursdayStart("")
                            viewModel.saveThursdayEnd("")
                        }
                        4 -> {
                            viewModel.saveFridayStart("")
                            viewModel.saveFridayEnd("")
                        }
                        5 -> {
                            viewModel.saveSaturdayStart("")
                            viewModel.saveSaturdayEnd("")
                        }
                        6 -> {
                            viewModel.saveSundayStart("")
                            viewModel.saveSundayEnd("")
                        }
                    }
                }
            }

            val now: Calendar = Calendar.getInstance()
            for (i in starts.indices) {
                starts[i].setOnClickListener {
                    starts[i].setBackgroundColor(
                        resources.getColor(
                            R.color.background_black,
                            resources.newTheme()
                        )
                    )
                    ends[i].setBackgroundColor(
                        resources.getColor(
                            R.color.background_black,
                            resources.newTheme()
                        )
                    )
                    val timeListener =
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
                            var min = "$minute"
                            if (minute == 0) {
                                min += "0"
                            }
                            starts[i].setText("$hourOfDay:$min")
                            when (i) {
                                0 -> {
                                    viewModel.saveMondayStart("$hourOfDay:$min")
                                }
                                1 -> {
                                    viewModel.saveTuesdayStart("$hourOfDay:$min")
                                }
                                2 -> {
                                    viewModel.saveWednesdayStart("$hourOfDay:$min")
                                }
                                3 -> {
                                    viewModel.saveThursdayStart("$hourOfDay:$min")
                                }
                                4 -> {
                                    viewModel.saveFridayStart("$hourOfDay:$min")
                                }
                                5 -> {
                                    viewModel.saveSaturdayStart("$hourOfDay:$min")
                                }
                                6 -> {
                                    viewModel.saveSundayStart("$hourOfDay:$min")
                                }
                            }
                        }
                    _timePickerDialog = TimePickerDialog.newInstance(
                        timeListener,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ) as TimePickerDialog
                    timePickerDialog.isThemeDark = true
                    timePickerDialog.title = "Выберите время"
                    timePickerDialog.setTimeInterval(1, 30, 60)
                    timePickerDialog.accentColor = Color.parseColor("#FF8A2F")
                    timePickerDialog.vibrate(true)
                    timePickerDialog.show(childFragmentManager, "Timepickerdialog")
                }
            }

            for (i in ends.indices) {
                ends[i].setOnClickListener {
                    starts[i].setBackgroundColor(
                        resources.getColor(
                            R.color.background_black,
                            resources.newTheme()
                        )
                    )
                    ends[i].setBackgroundColor(
                        resources.getColor(
                            R.color.background_black,
                            resources.newTheme()
                        )
                    )
                    val timeListener =
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
                            var min = "$minute"
                            if (minute == 0) {
                                min += "0"
                            }
                            ends[i].isCursorVisible = true
                            ends[i].isFocusable = true
                            ends[i].setText("$hourOfDay:$min")
                            ends[i].isCursorVisible = false
                            ends[i].isFocusable = false
                            when (i) {
                                0 -> {
                                    viewModel.saveMondayEnd("$hourOfDay:$min")
                                }
                                1 -> {
                                    viewModel.saveTuesdayEnd("$hourOfDay:$min")
                                }
                                2 -> {
                                    viewModel.saveWednesdayEnd("$hourOfDay:$min")
                                }
                                3 -> {
                                    viewModel.saveThursdayEnd("$hourOfDay:$min")
                                }
                                4 -> {
                                    viewModel.saveFridayEnd("$hourOfDay:$min")
                                }
                                5 -> {
                                    viewModel.saveSaturdayEnd("$hourOfDay:$min")
                                }
                                6 -> {
                                    viewModel.saveSundayEnd("$hourOfDay:$min")
                                }
                            }
                        }
                    _timePickerDialog = TimePickerDialog.newInstance(
                        timeListener,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ) as TimePickerDialog
                    timePickerDialog.isThemeDark = true
                    timePickerDialog.title = "Выберите время"
                    timePickerDialog.setTimeInterval(1, 30, 60)
                    timePickerDialog.accentColor = Color.parseColor("#FF8A2F")
                    timePickerDialog.vibrate(true)
                    timePickerDialog.show(childFragmentManager, "Timepickerdialog")
                }
            }
        }


    }
    private fun allDayWorking() {
        with(binding) {
            binding.withTimeEdWeek.isEnabled = true
            binding.byFieldWeek.isEnabled = true
            binding.allDayFieldWeek.isEnabled = true
        }
        viewModel.saveMondayStart(null)
        viewModel.saveMondayEnd(null)
        viewModel.saveTuesdayStart(null)
        viewModel.saveTuesdayEnd(null)
        viewModel.saveWednesdayStart(null)
        viewModel.saveWednesdayEnd(null)
        viewModel.saveThursdayStart(null)
        viewModel.saveThursdayEnd(null)
        viewModel.saveFridayStart(null)
        viewModel.saveFridayEnd(null)
        viewModel.saveSaturdayStart(null)
        viewModel.saveSaturdayEnd(null)
        viewModel.saveSundayStart(null)
        viewModel.saveSundayEnd(null)
    }

    private fun allDayWeekend() {
        with(binding) {
            binding.withTimeEdWeek.isEnabled = false
            binding.byFieldWeek.isEnabled = false
            binding.allDayFieldWeek.isEnabled = false
        }
        viewModel.saveMondayStart("")
        viewModel.saveMondayEnd("")
        viewModel.saveTuesdayStart("")
        viewModel.saveTuesdayEnd("")
        viewModel.saveWednesdayStart("")
        viewModel.saveWednesdayEnd("")
        viewModel.saveThursdayStart("")
        viewModel.saveThursdayEnd("")
        viewModel.saveFridayStart("")
        viewModel.saveFridayEnd("")
        viewModel.saveSaturdayStart("")
        viewModel.saveSaturdayEnd("")
        viewModel.saveSundayStart("")
        viewModel.saveSundayEnd("")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
