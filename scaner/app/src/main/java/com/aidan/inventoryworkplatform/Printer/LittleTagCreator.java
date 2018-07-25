package com.aidan.inventoryworkplatform.Printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

public class LittleTagCreator {

    public static final int width = 900;
    public static final int height = 300;
    public static Bitmap transStringToImage(String res, int textSizePix, int lineMargin) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        TextPaint paint = new TextPaint();
        Typeface typeface = Typeface.DEFAULT;
        paint.setTypeface(typeface);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSizePix);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setColor(Color.BLACK);
        String[] strings = res.split("\n");
        int baseLine = textSizePix + lineMargin;
        for (String temp : strings) {
            String[] lines = temp.split("\t");
            int count = 0;
            int lineWidth = width / lines.length;
            for(String line : lines){
                canvas.drawText(line, lineMargin + lineWidth * count, baseLine , paint);
                count++;
            }
            baseLine += lineMargin * 2 + textSizePix;
        }
        return bmp;
    }

    public static Bitmap addQRBitmap(Bitmap backBitmap, Bitmap frontBitmap, int margin) {
        Canvas canvas = new Canvas(backBitmap);
        Rect frontRect = new Rect(backBitmap.getWidth() - frontBitmap.getWidth(), 0, backBitmap.getWidth() , frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, null, frontRect, null);
        return backBitmap;
    }



}
