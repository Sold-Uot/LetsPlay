package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragCreateEventRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.FreeHoursRedesAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CreateEventRedesFrag : DialogFragment() {

    private var _binding: FragCreateEventRedesBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<CreateEventRedesFragArgs>()
    private lateinit var viewModel: CreateEventViewModel
    private val months = arrayListOf(
        "января",
        "февраля",
        "марта",
        "апреля",
        "мая",
        "июня",
        "июля",
        "августа",
        "сентября",
        "октября",
        "ноября",
        "декабря"
    )
    private var month = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    // TODO
    fun getDisabledDays(arrayList: List<String>, currentPageDate: Calendar): ArrayList<Calendar> {
        val disabledDays = ArrayList<Calendar>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, currentPageDate.get(Calendar.YEAR))
        calendar.set(Calendar.MONDAY, currentPageDate.get(Calendar.MONDAY))
        for (ii in arrayList) {
            if (ii != null) {
                val i = ii.split("-")
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, i[2].toInt())
                cal.set(Calendar.MONDAY, i[1].toInt() - 1)
                cal.set(Calendar.DAY_OF_MONTH, i[0].toInt())
                disabledDays.add(cal)
            }
        }
        return disabledDays
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragCreateEventRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        onBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeLayout.setOnRefreshListener {
            settingView()
        }
        settingView()
    }

    private fun settingView() {
        binding.enterBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        val recyclerView = binding.freeHoursRv
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = FreeHoursRedesAdapter {
            viewModel.saveBeginTime(month)
            viewModel.saveStartAndEnd(it)
        }
        val dateInString: String = SimpleDateFormat("dd-MM-yyyy").format(Date())
        month = "${dateInString}"
        viewModel.fetchHoursFree(args.id.toInt(), dateInString)
        recyclerView.adapter = adapter
        viewModel.calendar.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                adapter.submitList(null)
                binding.emptyListTv.visible()
            } else {
                binding.emptyListTv.gone()
                val originalList: List<ru.radixit.letsplay.data.model.Calendar> = it
// заполнение originalList

// заполнение originalList
                val pairedList: MutableList<Pair<ru.radixit.letsplay.data.model.Calendar, ru.radixit.letsplay.data.model.Calendar?>> =
                    mutableListOf()

                var i = 0
                while (i < originalList.size) {

                    // проверяем, что текущий и следующий элементы есть
                    if (i < originalList.size - 1) {
                        pairedList.add(Pair(originalList[i], originalList[i + 1]))
                    } else { // если только один элемент остался в списке
                        pairedList.add(Pair(originalList[i], null))
                    }
                    i += 2
                }
                adapter.submitList(pairedList)
            }
        }

        val cal = Calendar.getInstance()
        viewModel.fetchDisabledDays(
            args.id.toInt(),
            cal.get(Calendar.MONTH).plus(1).toString(),
            cal.get(Calendar.YEAR).toString()
        )
        binding.datePicker.apply {
            val today = Calendar.getInstance()
            setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
                override fun onChange() {
                    viewModel.fetchDisabledDays(
                        args.id.toInt(),
                        binding.datePicker.currentPageDate.get(Calendar.MONTH).plus(1).toString(),
                        binding.datePicker.currentPageDate.get(Calendar.YEAR).toString()
                    )
                }
            })
            setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
                override fun onChange() {
                    viewModel.fetchDisabledDays(
                        args.id.toInt(),
                        binding.datePicker.currentPageDate.get(Calendar.MONTH).plus(1).toString(),
                        binding.datePicker.currentPageDate.get(Calendar.YEAR).toString()
                    )
                }
            })
            setMinimumDate(today)
            setDate(today)
            viewModel.disabledDays.observe(viewLifecycleOwner) {
                setDisabledDays(getDisabledDays(it, cal))
            }
            setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    val time = eventDay.calendar
                    val monthLocal = time.get(Calendar.MONTH) + 1
                    val date: String
                    if (monthLocal < 10) {
                        date = "${time.get(Calendar.DAY_OF_MONTH)}" +
                                "-0${time.get(Calendar.MONTH) + 1}" +
                                "-${time.get(Calendar.YEAR)}"
                    } else {
                        date = "${time.get(Calendar.DAY_OF_MONTH)}" +
                                "-${time.get(Calendar.MONTH) + 1}" +
                                "-${time.get(Calendar.YEAR)}"
                    }
                    viewModel.fetchHoursFree(args.id.toInt(), date)
                    viewModel.saveDate(date)
                    month = "${date}"
                }
            })
        }
        if (binding.swipeLayout.isRefreshing) {
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}