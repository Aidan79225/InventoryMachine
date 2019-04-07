package com.aidan.inventoryworkplatform.FilePage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import droidninja.filepicker.FilePickerActivity;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

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
    TextView outputItemTextView,inputItemTextView;
    ArrayList<String> filePaths = new ArrayList<>();
    ArrayList<String> docPaths = new ArrayList<>();
    Runnable fileRunnable;
    ProgressDialog mProgressDialog;
    TextView clearTextView;
    int type = 0;
    private static final int readTxtType = 19;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int FILE_SELECT_CODE = 0;
    private static final int FILE_SELECT_NAME_CODE = 2;
    private static final int FILE_SELECT_ITEM_CODE = 3;

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
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(rootView.getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
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
        inputTextView = (TextView) rootView.findViewById(R.id.inputTextView);
        outputTextView = (TextView) rootView.findViewById(R.id.outputTextView);
        readNameTextView = (TextView) rootView.findViewById(R.id.readNameTextView);
        clearTextView = (TextView) rootView.findViewById(R.id.clearTextView);
        outputItemTextView = (TextView) rootView.findViewById(R.id.outputItemTextView);
        inputItemTextView = (TextView) rootView.findViewById(R.id.inputItemTextView);
    }

    @Override
    public void setViewClick() {
        inputTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showFileChooser(FILE_SELECT_CODE);
                    }
                };
                checkPermission();
            }
        });
        outputTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Set<String> allowType = new HashSet<>();
                        allowType.add("0");
                        allowType.add("1");
                        allowType.add("2");
                        allowType.add("3");
                        allowType.add("4");
                        allowType.add("5");
                        showFileNameDialog("請輸入財產檔名",Constants.PREFERENCE_PROPERTY_KEY,allowType);
                    }
                };
                checkPermission();
            }
        });
        outputItemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Set<String> allowType = new HashSet<>();
                        allowType.add("6");
                        showFileNameDialog("請輸入物品檔名",Constants.PREFERENCE_ITEM_KEY,allowType);
                    }
                };
                checkPermission();
            }
        });
        readNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showFileChooser(FILE_SELECT_NAME_CODE);
                    }
                };
                checkPermission();
            }
        });
        clearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setTitle(R.string.clear_data).
                        setMessage(R.string.clear_data_msg).
                        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.clearData();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        inputItemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showFileChooser(FILE_SELECT_ITEM_CODE);
                    }
                };
                checkPermission();
            }
        });


    }

    @Override
    public void showProgress(final String title) {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = new ProgressDialog(rootView.getContext());
                mProgressDialog.setCancelable(false);
                mProgressDialog.setTitle(title);
                mProgressDialog.setMessage("正在處理請稍後...");
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void hideProgress() {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void updateProgress(final int value) {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setProgress(value);
            }
        });
    }

    public void showFileNameDialog(String title, final  String preferencesKey, final Set<String> allowType) {
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

        editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                presenter.saveFile(editText.getText().toString(),preferencesKey,allowType);
                arg0.dismiss();
            }
        });
        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        editDialog.show();
    }

    private void showFileChooser(int resultCode) {
        final String mimeType = "*/*";
        final PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            // 如果有可用的Activity
            Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
            picker.setType(mimeType);
            // 使用Intent Chooser
            Intent destIntent = Intent.createChooser(picker, "選取輸入檔案");
            startActivityForResult(destIntent, resultCode);
        } else {
            startPickerActivity();
        }
    }

    public void startPickerActivity() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme);
        type = readTxtType;
        Intent intent = new Intent(getActivity(), FilePickerActivity.class);
        Bundle bundle = new Bundle();
        intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS, filePaths);
        bundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER);
        intent.putExtras(bundle);
        startActivityForResult(intent, FilePickerConst.REQUEST_CODE_DOC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == RESULT_OK && data != null)
                    handleChooseDoc(data);
                break;
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = getPath(getActivity(), uri);
                    presenter.readTxtButtonClick(path);
                }
                break;
            case FILE_SELECT_NAME_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = getPath(getActivity(), uri);
                    presenter.readNameTextViewClick(path);
                }
                break;
            case FILE_SELECT_ITEM_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = getPath(getActivity(), uri);
                    presenter.inputItemTextViewClick(path);
                }
                break;
        }

    }

    private void handleChooseDoc(Intent data) {
        docPaths.clear();
        docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
        switch (type) {
            case readTxtType:

                if (docPaths.size() > 0) presenter.readTxtButtonClick(docPaths.get(0));
                type = 0;
                break;
        }
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

}
