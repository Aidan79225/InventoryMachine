package com.aidan.inventoryworkplatform.ui.file

import android.os.Environment
import com.aidan.inventoryworkplatform.Constants
import com.aidan.inventoryworkplatform.Entity.*
import com.aidan.inventoryworkplatform.Model.*
import com.aidan.inventoryworkplatform.ScannerPage.ScannerItemManager
import com.aidan.inventoryworkplatform.Singleton
import com.aidan.inventoryworkplatform.Utils.ReadExcel
import com.aidan.inventoryworkplatform.Utils.ReadExcel.ProgressAction
import com.aidan.inventoryworkplatform.base.BaseViewModel
import com.aidan.inventoryworkplatform.database.AgentDAO
import com.aidan.inventoryworkplatform.database.DepartmentDAO
import com.aidan.inventoryworkplatform.database.ItemDAO
import com.aidan.inventoryworkplatform.database.LocationDAO
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.*

/**
 * Created by Aidan on 2016/11/20.
 */
class FilePresenter : BaseViewModel(), FileContract.presenter {
    val showProgress = SingleLiveEvent<String>()
    val hideProgress = SingleLiveEvent<String>()
    val updateProgress = SingleLiveEvent<Int>()

    override fun readTxtButtonClick(fileDescriptor: FileDescriptor) {
        Thread(Runnable {
            val allowType: MutableSet<String> = HashSet()
            allowType.add("0")
            allowType.add("1")
            allowType.add("2")
            allowType.add("3")
            allowType.add("4")
            allowType.add("5")
            loadData(fileDescriptor, "讀取財產中", allowType, Constants.PREFERENCE_PROPERTY_KEY, SelectableItem.Type.property)
        }).start()
    }

    private fun loadData(fileDescriptor: FileDescriptor, msg: String, allowType: Set<String>, key: String, type: SelectableItem.Type) {
        showProgress.postValue(msg)
        val itemList = ItemSingleton.getInstance().itemList
        val locationList = LocationSingleton.getInstance().locationList
        val agentList = AgentSingleton.getInstance().agentList
        val departmentList = DepartmentSingleton.getInstance().departmentList
        try {
            val stream = FileInputStream(fileDescriptor)
            var jsonStr = ""
            try {
                val fc = stream.channel
                val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
                jsonStr = Charset.forName("Big5").decode(bb).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stream.close()
            val jsonObj = JSONObject(jsonStr)
            val ASSETs = jsonObj.getJSONObject("ASSETs")
            val MS = jsonObj.getJSONObject(Constants.MS)
            Singleton.preferenceEditor.putString(key, MS.toString()).commit()
            getItems(ASSETs, itemList, allowType, type)
            getAgents(ASSETs, agentList, type)
            getDepartments(ASSETs, departmentList, type)
            getLocations(ASSETs, locationList, type)
            ItemSingleton.getInstance().saveToDB()
            DepartmentSingleton.getInstance().saveToDB()
            AgentSingleton.getInstance().saveToDB()
            LocationSingleton.getInstance().saveToDB()
            ItemSingleton.getInstance().loadFromDB()
            DepartmentSingleton.getInstance().loadFromDB()
            AgentSingleton.getInstance().loadFromDB()
            LocationSingleton.getInstance().loadFromDB()
        } catch (e: Exception) {
            hideProgress.postValue(null)
            showToast.postValue("檔案格式錯誤")
            e.printStackTrace()
        }
    }

    private val progressAction = object : ProgressAction {
        override fun showProgress(msg: String?) {
            showProgress.postValue(msg)
        }

        override fun hideProgress() {
            hideProgress.postValue(null)
        }

        override fun updateProgress(value: Int) {
            updateProgress.postValue(value)
        }

        override fun showToast(msg: String?) {
            showToast.postValue(msg)
        }

    }

    override fun readNameTextViewClick(fileDescriptor: FileDescriptor) {
        val readExcel = ReadExcel()
        readExcel.setProgressAction(progressAction)
        readExcel.readName(fileDescriptor)
    }

    override fun readPurchaseDateTextViewClick(fileDescriptor: FileDescriptor) {
        val readExcel = ReadExcel()
        readExcel.setProgressAction(progressAction)
        readExcel.readPurchaseDate(fileDescriptor)
    }

    private fun dropTable() {
        try {
            ItemDAO.getInstance().dropTable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            DepartmentDAO.getInstance().dropTable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            AgentDAO.getInstance().dropTable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocationDAO.getInstance().dropTable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getItems(ASSETs: JSONObject, itemList: MutableList<Item>, allowType: Set<String>, type: SelectableItem.Type) {
        try {
            val data = ASSETs.getJSONArray("PA3")
            Singleton.log("itemList size : " + data.length())
            val size = data.length()
            for (i in 0 until size) {
                val c = data.getJSONObject(i)
                val item = Item(c)
                item.itemType = type
                if (allowType.contains(item.pA3C1)) {
                    itemList.add(item)
                }
                updateProgress.postValue((i + 1) * 100 / size)
            }
            hideProgress.postValue(null)
            Singleton.log("itemList size : " + itemList.size)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getAgents(ASSETs: JSONObject, agentList: MutableList<Agent>, type: SelectableItem.Type) {
        try {
            val data = ASSETs.getJSONArray("D1")
            Singleton.log("agentList size : " + data.length())
            showProgress.postValue("讀取人名中")
            for (i in 0 until data.length()) {
                val c = data.getJSONObject(i)
                val agent = Agent(c)
                agent.setType(type)
                var isNotAdded = true
                for (mAgent in agentList) {
                    if (agent.getName() == mAgent.getName() && agent.getNumber() == mAgent.getNumber() && agent.getType() == mAgent.getType()) {
                        isNotAdded = false
                        break
                    }
                }
                if (isNotAdded) {
                    agentList.add(agent)
                }
                updateProgress.postValue((i + 1) * 100 / data.length())
            }
            Singleton.log("agentList size : " + agentList.size)
            hideProgress.postValue(null)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        agentList.sort()
    }

    private fun getDepartments(ASSETs: JSONObject, departmentList: MutableList<Department>, type: SelectableItem.Type) {
        try {
            val data = ASSETs.getJSONArray("D2")
            Singleton.log("departmentList size : " + data.length())
            showProgress.postValue("讀取部門中")
            for (i in 0 until data.length()) {
                val c = data.getJSONObject(i)
                val department = Department(c)
                department.setType(type)
                var isNotAdded = true
                for (mDepartment in departmentList) {
                    if (department.getName() == mDepartment.getName() && department.getNumber() == mDepartment.getNumber() && department.getType() == mDepartment.getType()) {
                        isNotAdded = false
                        break
                    }
                }
                if (isNotAdded) {
                    departmentList.add(department)
                }
                updateProgress.postValue((i + 1) * 100 / data.length())
            }
            Singleton.log("departmentList size : " + departmentList.size)
            hideProgress.postValue(null)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        departmentList.sort()
    }

    private fun getLocations(ASSETs: JSONObject, locationList: MutableList<Location>, type: SelectableItem.Type) {
        try {
            val data = ASSETs.getJSONArray("D3")
            Singleton.log("locationList size : " + data.length())
            showProgress.postValue("讀取地點中")
            for (i in 0 until data.length()) {
                val c = data.getJSONObject(i)
                val location = Location(c)
                location.setType(type)
                var isNotAdded = true
                for (mLocation in locationList) {
                    if (location.getName() == mLocation.getName() && location.getNumber() == mLocation.getNumber() && location.getType() == mLocation.getType()) {
                        isNotAdded = false
                        break
                    }
                }
                if (isNotAdded) {
                    locationList.add(location)
                }
                updateProgress.postValue((i + 1) * 100 / data.length())
            }
            Singleton.log("locationList size : " + locationList.size)
            hideProgress.postValue(null)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Collections.sort(locationList)
    }

    override fun saveFile(fileName: String, preferencesKey: String, allowType: Set<String>, onlyChanged: Boolean) {
        val jsonObject = getAllDataJSON(preferencesKey, allowType, onlyChanged)
        val dir = Environment.getExternalStorageDirectory().absolutePath + "/欣華盤點系統/行動裝置轉至電腦"
        val dirFile = File(dir)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        var file = File(dir, "$fileName.txt")
        if (file.exists()) {
            file.delete()
            file = File(dir, "$fileName.txt")
        }
        try {
            val bufWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file, false), "big5"))
            val source = jsonObject.toString()
            bufWriter.write(source.replace("\\/", "/"))
            bufWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Singleton.log(file.absoluteFile.toString() + "寫檔發生錯誤")
        }
    }

    override fun clearData() {
        val itemList = ItemSingleton.getInstance().itemList
        val locationList = LocationSingleton.getInstance().locationList
        val agentList = AgentSingleton.getInstance().agentList
        val departmentList = DepartmentSingleton.getInstance().departmentList
        itemList.clear()
        locationList.clear()
        agentList.clear()
        departmentList.clear()
        ScannerItemManager.getInstance().itemList.clear()
        dropTable()
    }

    override fun inputItemTextViewClick(fileDescriptor: FileDescriptor) {
        Thread(Runnable {
            val allowType: MutableSet<String> = HashSet()
            allowType.add("6")
            loadData(fileDescriptor, "讀取物品中", allowType, Constants.PREFERENCE_ITEM_KEY, SelectableItem.Type.item)
        }).start()
    }

    fun getAllDataJSON(key: String?, allowType: Set<String>, onlyChanged: Boolean): JSONObject {
        val itemList = ItemSingleton.getInstance().itemList
        val jsonObject = JSONObject()
        try {
            val MS = JSONObject(Singleton.preferences.getString(key, ""))
            jsonObject.put(Constants.MS, MS)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {
            val ASSETs = JSONObject()
            val PA3 = JSONArray()
            for (item in itemList) {
                if (onlyChanged && !item.isChanged) {
                    continue
                }
                if (allowType.contains(item.pA3C1)) {
                    PA3.put(item.toJSON())
                }
            }
            val D1 = JSONArray()
            val D2 = JSONArray()
            val D3 = JSONArray()
            ASSETs.put("D1", D1)
            ASSETs.put("D2", D2)
            ASSETs.put("D3", D3)
            ASSETs.put("PA3", PA3)
            jsonObject.put("ASSETs", ASSETs)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

}