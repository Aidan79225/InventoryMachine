package com.aidan.inventoryworkplatform.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.aidan.inventoryworkplatform.BaseFragmentManager
import com.aidan.inventoryworkplatform.DatePicker.TimePickerView
import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.ui.item.list.ItemListFragment
import com.aidan.inventoryworkplatform.ui.item.list.ItemListPresenter
import com.aidan.inventoryworkplatform.R
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

/**
 * Created by Aidan on 2017/1/8.
 */
class SearchFragment : DialogFragment() {
    lateinit var viewModel: SearchViewModel
    var baseFragmentManager: BaseFragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewClick(view)
        viewModel.searchResultLiveEvent.observe(this, androidx.lifecycle.Observer {
            showFragmentWithResult(it)
        })
    }

    fun setViewClick(view: View) {
        fun getValue(string: String, default: Int): Int {
            return try {
                Integer.valueOf(string)
            } catch (t: Throwable) {
                default
            }
        }
        view.apply {
            minDateTextView.setOnClickListener { v: View? -> minDateTextViewClick() }
            maxDateTextView.setOnClickListener { v: View? -> maxDateTextViewClick() }
            clearTextView.setOnClickListener { v: View? ->
                viewModel.clearAll()
                clearViews()
            }
            searchTextView.setOnClickListener { v: View? ->
                viewModel.searchTextViewClick(
                        SearchViewModel.Number(
                                getValue(c1EditText.text.toString(), 0),
                                getValue(c2EditText.text.toString(), 0),
                                getValue(c3EditText.text.toString(), 0),
                                getValue(c4EditText.text.toString(), 0)
                        ),
                        SearchViewModel.Number(
                                getValue(c11EditText.text.toString(), 999999),
                                getValue(c12EditText.text.toString(), 999999),
                                getValue(c13EditText.text.toString(), 999999),
                                getValue(c14EditText.text.toString(), 999999)
                        ),
                        nameEditText.text.toString(),
                        stockTypeEditText.text.toString(),
                        eventNumberEditText.text.toString(),
                        eventReasonEditText.text.toString(),
                        userEditText.text.toString(),
                        locationEditText.text.toString(),
                        noteEditText.text.toString()
                )
            }
            c1EditText.addTextChangedListener(getNextTextWatcher(3, c2EditText))
            c2EditText.addTextChangedListener(getNextTextWatcher(4, c3EditText))
            c3EditText.addTextChangedListener(getNextTextWatcher(6, c4EditText))
            c4EditText.addTextChangedListener(getNextTextWatcher(3, c11EditText))
            c11EditText.addTextChangedListener(getNextTextWatcher(3, c12EditText))
            c12EditText.addTextChangedListener(getNextTextWatcher(4, c13EditText))
            c13EditText.addTextChangedListener(getNextTextWatcher(6, c14EditText))
            c14EditText.addTextChangedListener(getNextTextWatcher(3, c14EditText))
        }
    }

    fun minDateTextViewClick() {
        val minCalendar = viewModel.minCalendar
        val maxCalendar = viewModel.maxCalendar
        showDatePicker(minCalendar, Runnable {
            maxCalendar[minCalendar[Calendar.YEAR], minCalendar[Calendar.MONTH]] = minCalendar[Calendar.DAY_OF_MONTH]
            setMinDateTextView(minCalendar)
            setMaxDateTextView(maxCalendar)
        })
    }

    fun maxDateTextViewClick() {
        val maxCalendar = viewModel.maxCalendar
        showDatePicker(maxCalendar, Runnable {
            setMaxDateTextView(maxCalendar)
        })
    }

    fun showDatePicker(c: Calendar, callback: Runnable) {
        val pvTime = TimePickerView.Builder(context) { date, v -> //选中事件回调
            val temp = Calendar.getInstance()
            temp.time = date
            c[temp[Calendar.YEAR], temp[Calendar.MONTH]] = temp[Calendar.DAY_OF_MONTH]
            callback.run()
        }.setType(booleanArrayOf(true, true, true, false, false, false)).build()
        pvTime.setDate(c)
        pvTime.show()
    }

    private fun getNextTextWatcher(length: Int, next: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == length) {
                    next.requestFocus()
                }
            }
        }
    }

    fun clearViews() {
        c1EditText.setText("")
        c2EditText.setText("")
        c3EditText.setText("")
        c4EditText.setText("")
        minDateTextView.text = "請點選起始日期"
        maxDateTextView.text = "請點選最後日期"
        nameEditText.setText("")
    }

    fun showToast(msg: String) {
        view?.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    fun showFragmentWithResult(items: List<Item>) {
        val presenter = ViewModelProviders.of(requireActivity()).get(ItemListPresenter::class.java)
        presenter.itemList = items
        val fragment: Fragment = ItemListFragment.newInstance(false)
        baseFragmentManager?.loadFragment(fragment)
    }

    fun setMinDateTextView(c: Calendar) {
        minDateTextView.text = (c[Calendar.YEAR] - 1911).toString() + "/" + (c[Calendar.MONTH] + 1).toString() + "/" + c[Calendar.DAY_OF_MONTH].toString()
    }

    fun setMaxDateTextView(c: Calendar) {
        maxDateTextView.text = (c[Calendar.YEAR] - 1911).toString() + "/" + (c[Calendar.MONTH] + 1).toString() + "/" + c[Calendar.DAY_OF_MONTH].toString()
    }

    companion object {
        @JvmStatic
        fun newInstance(baseFragmentManager: BaseFragmentManager?): SearchFragment {
            val fragment = SearchFragment()
            fragment.baseFragmentManager = baseFragmentManager
            return fragment
        }
    }
}