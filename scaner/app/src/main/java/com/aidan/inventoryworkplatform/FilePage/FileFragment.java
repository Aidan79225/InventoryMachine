package com.aidan.inventoryworkplatform.FilePage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.Constants;
import com.aidan.inventoryworkplatform.R;
import com.aidan.inventoryworkplatform.Singleton;
import com.aidan.inventoryworkplatform.Utils.ReadExcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Aidan on 2016/11/20.
 */

public class FileFragment extends DialogFragment implements FileContract.view, ReadExcel.ProgressAction {
    ViewGroup rootView;
    FileContract.presenter presenter;
    TextView inputTextView, outputTextView, readNameTextView;
    TextView outputItemTextView, inputItemTextView;
    ArrayList<String> filePaths = new ArrayList<>();
    ArrayList<String> docPaths = new ArrayList<>();
    Runnable fileRunnable;
    ProgressDialog mProgressDialog;
    TextView clearTextView;
    TextView readPurchaseDateTextView;
    int type = 0;
    private static final int readTxtType = 19;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int FILE_SELECT_CODE = 100;
    private static final int FILE_SELECT_NAME_CODE = 2;
    private static final int FILE_SELECT_ITEM_CODE = 3;
    private static final int FILE_SELECT_PURCHASE_DATE_CODE = 4;

    @Override
    public void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        //未取得權限，向使用者要求允許權限
        else {
            //已有權限，可進行檔案存取
            if (fileRunnable != null) {
                fileRunnable.run();
            }
        }

    }

    @Override
    public void showToast(final String msg) {
        rootView.post(() -> Toast.makeText(rootView.getContext(), msg, Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限，進行檔案存取
                    if (fileRunnable != null) {
                        fileRunnable.run();
                    }
                }
                return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_input_file, container, false);
        presenter = new FilePresenter(this);
        presenter.start();
        return rootView;
    }

    @Override
    public void findView() {
        inputTextView = rootView.findViewById(R.id.inputTextView);
        outputTextView = rootView.findViewById(R.id.outputTextView);
        readNameTextView = rootView.findViewById(R.id.readNameTextView);
        clearTextView = rootView.findViewById(R.id.clearTextView);
        outputItemTextView = rootView.findViewById(R.id.outputItemTextView);
        inputItemTextView = rootView.findViewById(R.id.inputItemTextView);
        readPurchaseDateTextView = rootView.findViewById(R.id.readPurchaseDateTextView);
    }

    @Override
    public void setViewClick() {
        inputTextView.setOnClickListener(v -> {
            fileRunnable = () -> showFileChooser(FILE_SELECT_CODE);
            checkPermission();
        });
        outputTextView.setOnClickListener(v -> {
            fileRunnable = () -> {
                Set<String> allowType = new HashSet<>();
                allowType.add("0");
                allowType.add("1");
                allowType.add("2");
                allowType.add("3");
                allowType.add("4");
                allowType.add("5");
                showFileNameDialog("請輸入財產檔名", Constants.PREFERENCE_PROPERTY_KEY, allowType);
            };
            checkPermission();
        });
        outputItemTextView.setOnClickListener(v -> {
            fileRunnable = () -> {
                Set<String> allowType = new HashSet<>();
                allowType.add("6");
                showFileNameDialog("請輸入物品檔名", Constants.PREFERENCE_ITEM_KEY, allowType);
            };
            checkPermission();
        });
        readNameTextView.setOnClickListener(v -> {
            fileRunnable = () -> showFileChooser(FILE_SELECT_NAME_CODE);
            checkPermission();
        });
        readPurchaseDateTextView.setOnClickListener(v -> {
            fileRunnable = () -> showFileChooser(FILE_SELECT_PURCHASE_DATE_CODE);
            checkPermission();
        });
        clearTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
            builder.setTitle(R.string.clear_data).
                    setMessage(R.string.clear_data_msg).
                    setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss()).
                    setPositiveButton(R.string.confirm, (dialog, which) -> {
                        presenter.clearData();
                        dialog.dismiss();
                    }).show();
        });
        inputItemTextView.setOnClickListener(v -> {
            fileRunnable = () -> showFileChooser(FILE_SELECT_ITEM_CODE);
            checkPermission();
        });


    }

    @Override
    public void showProgress(final String title) {
        rootView.post(() -> {
            mProgressDialog = new ProgressDialog(rootView.getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage("正在處理請稍後...");
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        });
    }

    @Override
    public void hideProgress() {
        rootView.post(() -> mProgressDialog.dismiss());
    }

    @Override
    public void updateProgress(final int value) {
        rootView.post(() -> mProgressDialog.setProgress(value));
    }

    public void showFileNameDialog(String title, final String preferencesKey, final Set<String> allowType) {
        final AlertDialog.Builder editDialog = new AlertDialog.Builder(getActivity());
        editDialog.setTitle(title);

        final EditText editText = new EditText(getActivity());
        SimpleDateFormat parse_DateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date();
        String id = "";
        try {
            String MS = Singleton.preferences.getString(preferencesKey, "");
            JSONObject jsonObject = new JSONObject(MS);
            id = jsonObject.getString(Constants.MS01);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editText.setText("PD" + id + parse_DateFormatter.format(date));
        editText.setSelection(editText.getText().length());
        editDialog.setView(editText);

        // do something when the button is clicked
        editDialog.setPositiveButton("OK", (arg0, arg1) -> {
            presenter.saveFile(editText.getText().toString(), preferencesKey, allowType);
            arg0.dismiss();
        });
        // do something when the button is clicked
        editDialog.setNegativeButton("Cancel", (arg0, arg1) -> arg0.dismiss());
        editDialog.show();
    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        presenter.readTxtButtonClick(readTextFromUri(uri));
                    } catch (Exception e) {
                    }
                }
                break;
            case FILE_SELECT_NAME_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        presenter.readNameTextViewClick(readTextFromUri(uri));
                    } catch (Exception e) {
                    }
                }
                break;
            case FILE_SELECT_ITEM_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        presenter.inputItemTextViewClick(readTextFromUri(uri));
                    } catch (Exception e) {
                    }
                }
                break;

            case FILE_SELECT_PURCHASE_DATE_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        presenter.readPurchaseDateTextViewClick(readTextFromUri(uri));
                    } catch (Exception e) {
                    }
                }
                break;
        }

    }

    private FileDescriptor readTextFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContext().getContentResolver().openFileDescriptor(uri, "r");
        return parcelFileDescriptor.getFileDescriptor();
    }
}
