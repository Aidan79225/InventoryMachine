package com.aidan.inventoryworkplatform.Printer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;


import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Model.BarCodeCreator;
import com.aidan.inventoryworkplatform.R;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.google.zxing.BarcodeFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import androidx.annotation.NonNull;

public class PrintItemLittleTagDialog extends Dialog {
    private Item item;
    private ImageView barcodeImageView;


    public PrintItemLittleTagDialog(@NonNull Context context) {
        super(context);
    }
    public void setItem(Item item) {
        this.item = item;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_print_item);
        findView();
        setContent();
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                print();
            }
        });
    }

    private void findView() {
        barcodeImageView = (ImageView) findViewById(R.id.barcodeImageView);
    }

    private void setContent() {
        Bitmap bitmap = LittleTagCreator.transStringToImage(item.getLittleTagContentString(), LittleTagCreator.height / 5 - dpToPix(2) * 2, dpToPix(1.5f));
        try {
            bitmap = LittleTagCreator.addQRBitmap(bitmap, BarCodeCreator.encodeAsBitmap(item.getBarcodeNumber(), BarcodeFormat.QR_CODE, LittleTagCreator.height , LittleTagCreator.height ), dpToPix(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        barcodeImageView.setImageBitmap(bitmap);
    }


    public int dpToPix(int dp) {
        return (int) Resources.getSystem().getDisplayMetrics().density * dp;
    }

    public int dpToPix(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp);
    }

    public void print() {
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/欣華盤點系統/圖片暫存";
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                Bitmap bitmap = LittleTagCreator.transStringToImage(item.getLittleTagContentString(), LittleTagCreator.height / 5 - dpToPix(2) * 2, dpToPix(1.5f));
                try {
                    bitmap = LittleTagCreator.addQRBitmap(bitmap, BarCodeCreator.encodeAsBitmap(item.getBarcodeNumber(), BarcodeFormat.QR_CODE, LittleTagCreator.height , LittleTagCreator.height ), dpToPix(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }



                Matrix vMatrix = new Matrix();
                vMatrix.setRotate( 90 );



                String fileName = item.getNumber() + item.getSerialNumber() + "-little.png";
                File file = new File(dir, fileName);
                if (file.exists()) {
                    file.delete();
                    file = new File(dir, fileName);
                }

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Printer printer = new Printer();
                NetPrinter[] netPrinters = printer.getNetPrinters("PT-P900W");
                if (netPrinters == null || netPrinters.length == 0) {
                    showToast("列印失敗,找不到機器");
                    dismiss();
                } else {
                    PrinterInfo printInfo = new PrinterInfo();
                    printInfo.printerModel = PrinterInfo.Model.PT_P900W;
                    printInfo.port = PrinterInfo.Port.NET;
                    printInfo.ipAddress = netPrinters[0].ipAddress;
                    printInfo.macAddress = netPrinters[0].macAddress;
                    printInfo.labelNameIndex = LabelInfo.PT.W12.ordinal();
                    printInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;
                    printInfo.align = PrinterInfo.Align.CENTER;
                    printInfo.isAutoCut = false;
                    printInfo.isCutAtEnd = false;
                    printInfo.isHalfCut = true;
                    printer.setPrinterInfo(printInfo);
                    printer.startCommunication();
                    PrinterStatus status = printer.printFile(file.getAbsolutePath());
                    printer.endCommunication();
                    if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                        showToast("列印失敗,找不到機器");
                    } else {
                        showToast("列印成功");
                    }
                    dismiss();
                }
            }
        });
        trd.start();
    }

    private void showToast(final String msg) {
        barcodeImageView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
