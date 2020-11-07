package com.aidan.inventoryworkplatform.ui.search

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.aidan.inventoryworkplatform.BaseFragmentManager
import com.aidan.inventoryworkplatform.DatePicker.TimePickerView
import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter
import com.aidan.inventoryworkplatform.Dialog.SearchItemDialog
import com.aidan.inventoryworkplatform.Dialog.SearchableItem
import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment
import com.aidan.inventoryworkplatform.ItemListPage.ItemListPresenter
import com.aidan.inventoryworkplatform.KeyConstants
import com.aidan.inventoryworkplatform.R
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

/**
 * Created by Aidan on 2017/1/8.
 */
class SearchFragment : DialogFragment() {
    lateinit var presenter: SearchPresenter
    var baseFragmentManager: BaseFragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ViewModelProviders.of(this).get(SearchPresenter::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewClick(view)
        presenter.searchResultLiveEvent.observe(this, androidx.lifecycle.Observer {
            showFragmentWithResult(it)
        })
    }

    fun setViewClick(view: View) {
        view.apply {
            minDateTextView.setOnClickListener { v: View? -> minDateTextViewClick() }
            maxDateTextView.setOnClickListener { v: View? -> maxDateTextViewClick() }
            clearTextView.setOnClickListener { v: View? ->
                presenter.clearAll()
                clearViews()
            }
            searchTextView.setOnClickListener { v: View? ->
                var id = ""
                id += c1EditText.text.toString()
                id += c2EditText.text.toString()
                id += c3EditText.text.toString()
                id += c4EditText.text.toString()
                val name = nameEditText.text.toString()
                presenter.searchTextViewClick(name, id, serialMinNumberEditText.text.toString(), serialMaxNumberEditText.text.toString())
            }
            c1EditText.addTextChangedListener(getNextTextWatcher(3, c2EditText))
            c2EditText.addTextChangedListener(getNextTextWatcher(4, c3EditText))
            c3EditText.addTextChangedListener(getNextTextWatcher(6, c4EditText))
            c4EditText.addTextChangedListener(getNextTextWatcher(3, serialMinNumberEditText))
            serialMinNumberEditText.addTextChangedListener(getNextTextWatcher(7, serialMaxNumberEditText))
        }
    }

    fun minDateTextViewClick() {
        val minCalendar = presenter.minCalendar
        val maxCalendar = presenter.maxCalendar
        showDatePicker(minCalendar, Runnable {
            maxCalendar[minCalendar[Calendar.YEAR], minCalendar[Calendar.MONTH]] = minCalendar[Calendar.DAY_OF_MONTH]
            setMinDateTextView(minCalendar)
            setMaxDateTextView(maxCalendar)
        })
    }

    fun maxDateTextViewClick() {
        val maxCalendar = presenter.maxCalendar
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
        serialMinNumberEditText.setText("")
        serialMaxNumberEditText.setText("")
        minDateTextView.text = "請點選起始日期"
        maxDateTextView.text = "請點選最後日期"
        nameEditText.setText("")
    }

    fun showToast(msg: String) {
        view?.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    fun showFragmentWithResult(items: List<Item>) {
        val presenter = ViewModelProviders.of(this).get(ItemListPresenter::class.java)
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