package com.aidan.inventoryworkplatform.Model;

import com.aidan.inventoryworkplatform.Database.AgentDAO;
import com.aidan.inventoryworkplatform.Database.DBHelper;
import com.aidan.inventoryworkplatform.Entity.Agent;
import com.aidan.inventoryworkplatform.Entity.Department;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2016/11/20.
 */

public class AgentSingleton {
    static AgentSingleton agentSingleton;
    private List<Agent> agentList = new ArrayList<>();
    public static AgentSingleton getInstance(){
        if(agentSingleton == null){
            synchronized (AgentSingleton.class){
                if(agentSingleton == null){
                    agentSingleton = new AgentSingleton();
                }
            }
        }
        return agentSingleton;
    }

    public List<Agent> getAgentList() {
        return agentList;
    }
    public void loadFromDB(){
        agentList = AgentDAO.getInstance().getAll();
    }
    public void saveToDB() {
        try {
            DBHelper.getDatabase().beginTransaction();
            for (Agent agent : agentList) {
                saveAgent(agent);
            }
            DBHelper.getDatabase().setTransactionSuccessful();
            DBHelper.getDatabase().endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveAgent(Agent agent){
        if (!AgentDAO.getInstance().update(agent))
            AgentDAO.getInstance().insert(agent);
    }
}
