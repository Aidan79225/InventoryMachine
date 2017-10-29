package com.aidan.inventoryworkplatform.SearchPage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Agent;
import com.aidan.inventoryworkplatform.Entity.Department;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Entity.SortCategory;
import com.aidan.inventoryworkplatform.Entity.TagContent;
import com.aidan.inventoryworkplatform.Model.AgentSingleton;
import com.aidan.inventoryworkplatform.Model.BarCodeCreator;
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.Model.LocationSingleton;
import com.aidan.inventoryworkplatform.Printer.TagCreator;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.google.zxing.BarcodeFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Aidan on 2017/1/8.
 */

public class SearchPresenter implements SearchContract.presenter {
    SearchContract.view view;
    String[] locationStrings = {};
    String[] agentStrings = {};
    String[] departmentStrings = {};
    Location location;
    Agent agent;
    Department department;
    Agent user;
    Department useGroup;
    TagContent selectTagContent = null;
    SortCategory sortCategory = null;
    Calendar minCalendar;
    Calendar maxCalendar;
    Date minDate = null;
    Date maxDate = null;

    SearchPresenter(SearchContract.view view) {
        this.view = view;
        minCalendar = Calendar.getInstance();
        minCalendar.set(Calendar.HOUR_OF_DAY, 0);
        minCalendar.set(Calendar.MINUTE, 0);
        maxCalendar = Calendar.getInstance();
        maxCalendar.set(Calendar.HOUR_OF_DAY, 23);
        maxCalendar.set(Calendar.MINUTE, 59);
    }

    @Override
    public void start() {
        init();
        view.findView();
        view.setViewClick();
    }

    private void init() {
        List<Location> locationList = LocationSingleton.getInstance().getLocationList();
        List<String> locationStringList = new ArrayList<>();
        for (Location location : locationList) {
            locationStringList.add(location.name);
        }
        locationStrings = locationStringList.toArray(locationStrings);

        List<Agent> agentList = AgentSingleton.getInstance().getAgentList();
        List<String> agentStringList = new ArrayList<>();
        for (Agent agent : agentList) {
            agentStringList.add(agent.name);
        }
        agentStrings = agentStringList.toArray(agentStrings);


        List<Department> departmentList = DepartmentSingleton.getInstance().getDepartmentList();
        List<String> departmentStringList = new ArrayList<>();
        for (Department department : departmentList) {
            departmentStringList.add(department.name);
        }
        departmentStrings = departmentStringList.toArray(departmentStrings);
    }

    @Override
    public void locationTextViewClick(final TextView locationTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll(LocationSingleton.getInstance().getLocationList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                locationTextView.setText(item.getName());
                location = (Location) item;
            }
        }, "地點列表", temp);
    }

    @Override
    public void departmentTextViewClick(final TextView departmentTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll(DepartmentSingleton.getInstance().getDepartmentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                departmentTextView.setText(item.getName());
                department = (Department) item;
            }
        }, "保管部門列表", temp);
    }

    @Override
    public void agentTextViewClick(final TextView agentTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll(AgentSingleton.getInstance().getAgentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                agentTextView.setText(item.getName());
                agent = (Agent) item;
            }
        }, "保管人列表", temp);
    }

    @Override
    public void useGroupTextViewClick(final TextView useGroupTextVie) {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll(DepartmentSingleton.getInstance().getDepartmentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                useGroupTextVie.setText(item.getName());
                useGroup = (Department) item;
            }
        }, "使用部門列表", temp);
    }

    @Override
    public void userTextViewClick(final TextView userTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll(AgentSingleton.getInstance().getAgentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                userTextView.setText(item.getName());
                user = (Agent) item;
            }
        }, "使用人列表", temp);
    }

    @Override
    public void tagContentTextViewClick(final TextView tagContentTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        for (TagContent tagContent : TagContent.values()) {
            temp.add(tagContent);
        }
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                selectTagContent = (TagContent) item;
                tagContentTextView.setText(item.getName());
            }
        }, "標籤內容", temp);
    }

    @Override
    public void sortTextViewClick(final TextView sortTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        for (SortCategory sortCategory : SortCategory.values()) {
            temp.add(sortCategory);
        }
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                sortCategory = (SortCategory) item;
                sortTextView.setText(item.getName());
            }
        }, "排序條件", temp);
    }

    @Override
    public void minDateTextViewClick(TextView minDateTextView) {
        showDatePicker(minCalendar, new Runnable() {
            @Override
            public void run() {
                minDate = minCalendar.getTime();
            }
        }, minDateTextView);
    }

    @Override
    public void maxDateTextViewClick(TextView maxDateTextView) {
        showDatePicker(maxCalendar, new Runnable() {
            @Override
            public void run() {
                maxDate = maxCalendar.getTime();
            }
        }, maxDateTextView);
    }

    public void showDatePicker(final Calendar c, final Runnable callback, final TextView textView) {
        new DatePickerDialog(textView.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                c.set(year, month, day);
                textView.setText(String.valueOf(c.get(Calendar.YEAR) - 1911) + "/" + String.valueOf(c.get(Calendar.MONTH)) + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
                callback.run();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void searchTextViewClick(String name, String number, String serialMinNumber, String serialMaxNumber) {
        int minSerialNumber = serialMinNumber.length() > 0 ? Integer.valueOf(serialMinNumber) : 0;
        int maxSerialNumber = serialMaxNumber.length() > 0 ? Integer.valueOf(serialMaxNumber) : Integer.MAX_VALUE;
        List<Item> itemList = getItemListWithCondition(name, number, minSerialNumber, maxSerialNumber);
        view.showFragmentWithResult(itemList);
    }

    @Override
    public void printTextViewClick(Context context, String name, String number, String serialMinNumber, String serialMaxNumber) {
        int minSerialNumber = serialMinNumber.length() > 0 ? Integer.valueOf(serialMinNumber) : 0;
        int maxSerialNumber = serialMaxNumber.length() > 0 ? Integer.valueOf(serialMaxNumber) : Integer.MAX_VALUE;
        final List<Item> itemList = getItemListWithCondition(name, number, minSerialNumber, maxSerialNumber);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("列印").
                setMessage("將會列印 " + itemList.size() + " 個項目，您確定要列印嗎？").
                setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        print(itemList);
                        view.showToast("列印中請稍後");
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public List<Item> getItemListWithCondition(String name, String number, int minSerialNumber, int maxSerialNumber) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : ItemSingleton.getInstance().getItemList()) {
            if (!item.getName().contains(name)) {
                continue;
            }
            if (location != null && !item.getLocation().number.equals(location.number)) {
                continue;
            }
            if (agent != null && !item.getCustodian().number.equals(agent.number)) {
                continue;
            }
            if (department != null && !item.getCustodyGroup().number.equals(department.number)) {
                continue;
            }
            if (user != null && !item.getUser().number.equals(user.number)) {
                continue;
            }
            if (useGroup != null && !item.getUseGroup().number.equals(useGroup.number)) {
                continue;
            }
            if (number.length() > 1 && !item.getNumber().equals(number)) {
                continue;
            }
            int serialNumber = Integer.valueOf(item.getSerialNumber());
            if (serialNumber < minSerialNumber || serialNumber > maxSerialNumber) {
                continue;
            }
            if (minDate != null && minDate.getTime() > item.getDate().getTime()) {
                continue;
            }
            if (maxDate != null && maxDate.getTime() < item.getDate().getTime()) {
                continue;
            }
            item.setTagContent(selectTagContent);
            itemList.add(item);
        }
        if (sortCategory != null) {
            switch (sortCategory) {
                case Agent:
                    Collections.sort(itemList, new Comparator<Item>() {
                        @Override
                        public int compare(Item o1, Item o2) {
                            return o1.getCustodian().getName().compareTo(o2.getCustodian().getName());
                        }
                    });
                    break;
                case Group:
                    Collections.sort(itemList, new Comparator<Item>() {
                        @Override
                        public int compare(Item o1, Item o2) {
                            return o1.getCustodyGroup().getName().compareTo(o2.getCustodyGroup().getName());
                        }
                    });
                    break;
                case Location:
                    Collections.sort(itemList, new Comparator<Item>() {
                        @Override
                        public int compare(Item o1, Item o2) {
                            return o1.getLocation().getName().compareTo(o2.getLocation().getName());
                        }
                    });
                    break;
            }
        }
        return itemList;
    }

    @Override
    public void clearAll() {
        location = null;
        agent = null;
        department = null;
        user = null;
        useGroup = null;
        selectTagContent = null;
        sortCategory = null;
        minDate = null;
        maxDate = null;
        minCalendar = Calendar.getInstance();
        minCalendar.set(Calendar.HOUR_OF_DAY, 0);
        minCalendar.set(Calendar.MINUTE, 0);
        maxCalendar = Calendar.getInstance();
        maxCalendar.set(Calendar.HOUR_OF_DAY, 23);
        maxCalendar.set(Calendar.MINUTE, 59);
        view.clearViews();
    }


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int dpToPix(int dp) {
        return (int) Resources.getSystem().getDisplayMetrics().density * dp;
    }

    public void print(final List<Item> itemList) {
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/欣華盤點系統/圖片暫存";
                List<File> fileList = new ArrayList<>();
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                int width = getScreenWidth();
                int height = getScreenWidth() * 3 / 7;
                for (Item item : itemList) {
//                    Bitmap bitmap = TagCreator.transStringToImage(item.getTagContentString(), width, height, height / 10 - dpToPix(2) * 2, dpToPix(2));
                    Bitmap bitmap = TagCreator.transStringToImage(item.getTagContentString(), width, height, height / 10, 0);
                    try {
                        bitmap = TagCreator.mergeBitmap(bitmap, BarCodeCreator.encodeAsBitmap(item.getBarcodeNumber(), BarcodeFormat.CODE_128, width, height / 5), dpToPix(2));
                        String fileName = item.getNumber() + item.getSerialNumber() + ".png";
                        File file = new File(dir, fileName);
                        if (file.exists()) {
                            file.delete();
                            file = new File(dir, fileName);
                        }
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        fileList.add(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                Printer printer = new Printer();
                NetPrinter[] netPrinters = printer.getNetPrinters("PT-P900W");
                if (netPrinters == null || netPrinters.length == 0) {
                    view.showToast("列印失敗,找不到機器");
                } else {
                    PrinterInfo printInfo = new PrinterInfo();
                    printInfo.printerModel = PrinterInfo.Model.PT_P900W;
                    printInfo.port = PrinterInfo.Port.NET;
                    printInfo.ipAddress = netPrinters[0].ipAddress;
                    printInfo.macAddress = netPrinters[0].macAddress;
                    printInfo.labelNameIndex = LabelInfo.PT.W36.ordinal();
                    printInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;
                    printInfo.align = PrinterInfo.Align.CENTER;
                    printInfo.isAutoCut = false;
                    printInfo.isCutAtEnd = true;
                    printInfo.isHalfCut = true;
                    printer.setPrinterInfo(printInfo);
                    printer.startCommunication();
                    int count = 0;
                    for(File file : fileList){
                        PrinterStatus status = printer.printFile(file.getAbsolutePath());
                        if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                            view.showToast("列印失敗,找不到機器");
                            break;
                        }
                        if(count > 0)break;
                        count++ ;
                    }
                    printer.endCommunication();
                    view.showToast("列印成功");
                }
            }
        });
        trd.start();
    }
}
