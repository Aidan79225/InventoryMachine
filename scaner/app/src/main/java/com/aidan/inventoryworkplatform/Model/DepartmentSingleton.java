package com.aidan.inventoryworkplatform.Model;

import android.content.Context;

import com.aidan.inventoryworkplatform.Database.DBHelper;
import com.aidan.inventoryworkplatform.Database.DepartmentDAO;
import com.aidan.inventoryworkplatform.Entity.Department;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Entity.SelectableItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2016/11/20.
 */

public class DepartmentSingleton {
    static DepartmentSingleton departmentSingleton;
    private List<Department> departmentList = new ArrayList<>();
    public static DepartmentSingleton getInstance(){
        if(departmentSingleton == null){
            synchronized (AgentSingleton.class){
                if(departmentSingleton == null){
                    departmentSingleton = new DepartmentSingleton();
                }
            }
        }
        return departmentSingleton;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public List<Department> getDepartmentList(SelectableItem.Type type) {
        List<Department> temp = new ArrayList<>();
        for (Department department : departmentList) {
            if (department.getType() == type) {
                temp.add(department);
            }
        }
        return temp;
    }

    public void saveToDB() {
        try {
            DBHelper.getDatabase().beginTransaction();
            for (Department department : departmentList) {
                saveDepartment(department);
            }
            DBHelper.getDatabase().setTransactionSuccessful();
            DBHelper.getDatabase().endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveDepartment(Department department){
        if (!DepartmentDAO.getInstance().update(department))
            DepartmentDAO.getInstance().insert(department);
    }
    public void loadFromDB(){
        departmentList = DepartmentDAO.getInstance().getAll();
    }
}
