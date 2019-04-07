package com.aidan.inventoryworkplatform.Utils;

import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Aidan on 2017/10/29.
 */

public class ReadExcel {
    private ProgressAction progressAction;

    public void readName(final String inputFile) {
        new Thread(() -> {
            if(progressAction != null){
                progressAction.showProgress("讀取名稱中");
            }
            File inputWorkbook = new File(inputFile);
            Workbook w;
            try {
                w = Workbook.getWorkbook(inputWorkbook);
                loadAndSetName(w);
            } catch (BiffException e) {
                progressAction.showToast("檔案格式錯誤");
                e.printStackTrace();
            } catch (IOException iOException) {
                progressAction.showToast("檔案格式錯誤");
                iOException.printStackTrace();
            }finally {
                if(progressAction != null){
                    progressAction.hideProgress();
                }
            }
        }).start();
    }

    public void loadAndSetName(Workbook w) {
        Sheet sheet = w.getSheet(0);
        List<Item> itemList = ItemSingleton.getInstance().getItemList();
        Map<String,List<Item>> itemMap = new HashMap<>();
        for(Item item : itemList){
            List<Item> list = itemMap.get(item.getNumber());
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(item);
            itemMap.put(item.getNumber(),list);
        }
        for (int i = 0; i < sheet.getRows(); i++) {
            if (i == 0) continue;
            String id = "";
            id += sheet.getCell(0, i).getContents();
            id += sheet.getCell(1, i).getContents();
            id += sheet.getCell(2, i).getContents();
            id += sheet.getCell(3, i).getContents();
            id += sheet.getCell(4, i).getContents();
            String name = sheet.getCell(5, i).getContents();

            List<Item> list = itemMap.get(id);
            if(list != null){
                for(Item item : list){
                    item.setNAME(name);
                }
            }
            if(progressAction != null){
                progressAction.updateProgress((i+1)*100/sheet.getRows());
            }
        }
        ItemSingleton.getInstance().saveToDB();
    }

    public void readPurchaseDate(final String inputFile) {
        new Thread(() -> {
            if(progressAction != null){
                progressAction.showProgress("讀取名稱中");
            }
            File inputWorkbook = new File(inputFile);
            Workbook w;
            try {
                w = Workbook.getWorkbook(inputWorkbook);
                loadAndSetPurchaseDate(w);
            } catch (BiffException e) {
                progressAction.showToast("檔案格式錯誤");
                e.printStackTrace();
            } catch (IOException iOException) {
                progressAction.showToast("檔案格式錯誤");
                iOException.printStackTrace();
            }finally {
                if(progressAction != null){
                    progressAction.hideProgress();
                }
            }
        }).start();
    }

    public void loadAndSetPurchaseDate(Workbook w) {
        Sheet sheet = w.getSheet(0);
        List<Item> itemList = ItemSingleton.getInstance().getItemList();
        Map<String,List<Item>> itemMap = new HashMap<>();
        for(Item item : itemList){
            List<Item> list = itemMap.get(item.getNumber());
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(item);
            itemMap.put(item.getNumber(),list);
        }
        for (int i = 0; i < sheet.getRows(); i++) {
            if (i == 0) continue;
            String number = sheet.getCell(0, i).getContents().split("-")[0];
            String id = sheet.getCell(0, i).getContents().split("-")[1];


            List<Item> list = itemMap.get(number);
            if(list != null){
                for(Item item : list){
                    if (item.getSerialNumber().equals(id)) {
                        item.setPurchaseDate(sheet.getCell(1, i).getContents());
                    }
                }
            }
            if(progressAction != null){
                progressAction.updateProgress((i+1)*100/sheet.getRows());
            }
        }
        ItemSingleton.getInstance().saveToDB();
    }

    public void setProgressAction(ProgressAction progressAction){
        this.progressAction = progressAction;
    }
    public interface ProgressAction{
        void showProgress(String msg);
        void hideProgress();
        void updateProgress(int value);
        void showToast(String msg);
    }
}
