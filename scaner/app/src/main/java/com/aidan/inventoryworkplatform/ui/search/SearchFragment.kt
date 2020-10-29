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
import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter
import com.aidan.inventoryworkplatform.Dialog.SearchItemDialog
import com.aidan.inventoryworkplatform.Dialog.SearchableItem
import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment
import com.aidan.inventoryworkplatform.ItemListPage.ItemListPresenter
import com.aidan.inventoryworkplatform.KeyConstants
import com.aidan.inventoryworkplatform.R
import java.util.*

/**
 * Created by Aidan on 2017/1/8.
 */
class SearchFragment : DialogFragment(), SearchContract.view {
    var presenter: SearchContract.presenter? = null
    var serialMinNumberEditText: EditText? = null
    var serialMaxNumberEditText: EditText? = null
    var locationTextView: TextView? = null
    var agentTextView: TextView? = null
    var departmentTextView: TextView? = null
    var searchTextView: TextView? = null
    var clearTextView: TextView? = null
    var printTextView: TextView? = null
    var useGroupTextView: TextView? = null
    var userTextView: TextView? = null
    var c1EditText: EditText? = null
    var c2EditText: EditText? = null
    var c3EditText: EditText? = null
    var c4EditText: EditText? = null
    var c5EditText: EditText? = null
    var baseFragmentManager: BaseFragmentManager? = null
    var tagContentTextView: TextView? = null
    var sortTextView: TextView? = null
    var minDateTextView: TextView? = null
    var maxDateTextView: TextView? = null
    var printLittleTagTextView: TextView? = null
    var nameEditText: EditText? = null
    var nicknameEditText: EditText? = null
    var mProgressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ViewModelProviders.of(this).get(SearchPresenter::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        setViewClick()
    }

    fun findView(rootView: View) {
        c1EditText = rootView.findViewById<View>(R.id.c1EditText) as EditText
        c2EditText = rootView.findViewById<View>(R.id.c2EditText) as EditText
        c3EditText = rootView.findViewById<View>(R.id.c3EditText) as EditText
        c4EditText = rootView.findViewById<View>(R.id.c4EditText) as EditText
        c5EditText = rootView.findViewById<View>(R.id.c5EditText) as EditText
        serialMinNumberEditText = rootView.findViewById<View>(R.id.serialMinNumberEditText) as EditText
        serialMaxNumberEditText = rootView.findViewById<View>(R.id.serialMaxNumberEditText) as EditText
        locationTextView = rootView.findViewById<View>(R.id.locationTextView) as TextView
        agentTextView = rootView.findViewById<View>(R.id.agentTextView) as TextView
        departmentTextView = rootView.findViewById<View>(R.id.departmentTextView) as TextView
        searchTextView = rootView.findViewById<View>(R.id.searchTextView) as TextView
        clearTextView = rootView.findViewById<View>(R.id.clearTextView) as TextView
        printTextView = rootView.findViewById<View>(R.id.printTextView) as TextView
        printLittleTagTextView = rootView.findViewById<View>(R.id.printLittleTagTextView) as TextView
        useGroupTextView = rootView.findViewById<View>(R.id.useGroupTextView) as TextView
        userTextView = rootView.findViewById<View>(R.id.userTextView) as TextView
        tagContentTextView = rootView.findViewById<View>(R.id.tagContentTextView) as TextView
        sortTextView = rootView.findViewById<View>(R.id.sortTextView) as TextView
        minDateTextView = rootView.findViewById<View>(R.id.minDateTextView) as TextView
        maxDateTextView = rootView.findViewById<View>(R.id.maxDateTextView) as TextView
        nameEditText = rootView.findViewById<View>(R.id.nameEditText) as EditText
        nicknameEditText = rootView.findViewById<View>(R.id.nicknameEditText) as EditText
    }

    fun setViewClick() {
        locationTextView!!.setOnClickListener { v: View? -> presenter!!.locationTextViewClick(locationTextView) }
        agentTextView!!.setOnClickListener { v: View? -> presenter!!.agentTextViewClick(agentTextView) }
        departmentTextView!!.setOnClickListener { v: View? -> presenter!!.departmentTextViewClick(departmentTextView) }
        userTextView!!.setOnClickListener { v: View? -> presenter!!.userTextViewClick(userTextView) }
        useGroupTextView!!.setOnClickListener { v: View? -> presenter!!.useGroupTextViewClick(useGroupTextView) }
        tagContentTextView!!.setOnClickListener { v: View? -> presenter!!.tagContentTextViewClick(tagContentTextView) }
        sortTextView!!.setOnClickListener { v: View? -> presenter!!.sortTextViewClick(sortTextView) }
        minDateTextView!!.setOnClickListener { v: View? -> presenter!!.minDateTextViewClick(activity) }
        maxDateTextView!!.setOnClickListener { v: View? -> presenter!!.maxDateTextViewClick(activity) }
        clearTextView!!.setOnClickListener { v: View? -> presenter!!.clearAll() }
        searchTextView!!.setOnClickListener { v: View? ->
            var id = ""
            id += c1EditText!!.text.toString()
            id += c2EditText!!.text.toString()
            id += c3EditText!!.text.toString()
            id += c4EditText!!.text.toString()
            id += c5EditText!!.text.toString()
            val name = nameEditText!!.text.toString()
            val nickname = nicknameEditText!!.text.toString()
            presenter!!.searchTextViewClick(name, nickname, id, serialMinNumberEditText!!.text.toString(), serialMaxNumberEditText!!.text.toString())
        }
        printTextView!!.setOnClickListener { v: View? ->
            var id = ""
            id += c1EditText!!.text.toString()
            id += c2EditText!!.text.toString()
            id += c3EditText!!.text.toString()
            id += c4EditText!!.text.toString()
            id += c5EditText!!.text.toString()
            val name = nameEditText!!.text.toString()
            val nickname = nicknameEditText!!.text.toString()
            presenter!!.printTextViewClick(context, name, nickname, id, serialMinNumberEditText!!.text.toString(), serialMaxNumberEditText!!.text.toString())
        }
        printLittleTagTextView!!.setOnClickListener { v: View? ->
            var id = ""
            id += c1EditText!!.text.toString()
            id += c2EditText!!.text.toString()
            id += c3EditText!!.text.toString()
            id += c4EditText!!.text.toString()
            id += c5EditText!!.text.toString()
            val name = nameEditText!!.text.toString()
            val nickname = nicknameEditText!!.text.toString()
            presenter!!.printLittleTextViewClick(context, name, nickname, id, serialMinNumberEditText!!.text.toString(), serialMaxNumberEditText!!.text.toString())
        }
        c1EditText!!.addTextChangedListener(getNextTextWatcher(1, c2EditText))
        c2EditText!!.addTextChangedListener(getNextTextWatcher(2, c3EditText))
        c3EditText!!.addTextChangedListener(getNextTextWatcher(2, c4EditText))
        c4EditText!!.addTextChangedListener(getNextTextWatcher(2, c5EditText))
        c5EditText!!.addTextChangedListener(getNextTextWatcher(4, serialMinNumberEditText))
        serialMinNumberEditText!!.addTextChangedListener(getNextTextWatcher(7, serialMaxNumberEditText))
        printTextView!!.visibility = if (KeyConstants.showPrint) View.VISIBLE else View.GONE
        printLittleTagTextView!!.visibility = if (KeyConstants.showPrintLittleTag) View.VISIBLE else View.GONE
    }

    private fun getNextTextWatcher(length: Int, next: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == length) {
                    next!!.requestFocus()
                }
            }
        }
    }

    override fun clearViews() {
        c1EditText!!.setText("")
        c2EditText!!.setText("")
        c3EditText!!.setText("")
        c4EditText!!.setText("")
        c5EditText!!.setText("")
        serialMinNumberEditText!!.setText("")
        serialMaxNumberEditText!!.setText("")
        locationTextView!!.text = "請點選存置地點"
        agentTextView!!.text = "請點選保管人"
        departmentTextView!!.text = "請點選保管單位"
        tagContentTextView!!.text = "請點選標籤內容"
        sortTextView!!.text = "請點選排序條件"
        minDateTextView!!.text = "請點選起始日期"
        maxDateTextView!!.text = "請點選最後日期"
        nameEditText!!.setText("")
        nicknameEditText!!.setText("")
    }

    override fun showToast(msg: String) {
        view!!.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun showSetDialog(clickListener: SearchItemAdapter.OnClickListener, title: String, dataList: List<SearchableItem>) {
        val dialog = SearchItemDialog(activity, dataList)
        dialog.setTitle(title)
        dialog.setOnClickListener(clickListener)
        dialog.show()
    }

    override fun showFragmentWithResult(items: List<Item>) {
        val presenter = ViewModelProviders.of(this).get(ItemListPresenter::class.java)
        presenter.itemList = items
        val fragment: Fragment = ItemListFragment.newInstance(true)
        baseFragmentManager!!.loadFragment(fragment)
    }

    override fun showProgress(title: String) {
        view!!.post {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.setTitle(title)
            mProgressDialog!!.setMessage("正在處理請稍後...")
            mProgressDialog!!.max = 100
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            mProgressDialog!!.show()
        }
    }

    override fun hideProgress() {
        view!!.post { mProgressDialog!!.dismiss() }
    }

    override fun updateProgress(value: Int) {
        view!!.post { mProgressDialog!!.progress = value }
    }

    override fun setMinDateTextView(c: Calendar) {
        minDateTextView!!.text = (c[Calendar.YEAR] - 1911).toString() + "/" + (c[Calendar.MONTH] + 1).toString() + "/" + c[Calendar.DAY_OF_MONTH].toString()
    }

    override fun setMaxDateTextView(c: Calendar) {
        maxDateTextView!!.text = (c[Calendar.YEAR] - 1911).toString() + "/" + (c[Calendar.MONTH] + 1).toString() + "/" + c[Calendar.DAY_OF_MONTH].toString()
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