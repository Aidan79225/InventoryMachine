package com.aidan.inventoryworkplatform.ItemDetailPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchItemDialog;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment;
import com.aidan.inventoryworkplatform.KeyConstants;
import com.aidan.inventoryworkplatform.Printer.PrintItemLittleTagDialog;
import com.aidan.inventoryworkplatform.Printer.PrinterItemDialog;
import com.aidan.inventoryworkplatform.R;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;


/**
 * Created by s352431 on 2016/11/22.
 */
public class ItemDetailFragment extends DialogFragment implements ItemDetailContract.view {
    ItemDetailContract.presenter presenter;
    ViewGroup rootView;
    TextView yearsTextView, buyDateTextView, brandTextView,
            typeTextView, locationTextView,
            nameTextView, itemIdTextView,
            custodyGroupTextView, custodianTextView,
            useGroupTextView, userTextView,
            deleteTextView,printTextView,nickNameTextView,tagContentTextView;
    Button confirmButton,printButton , cancelButton, printLittleButton, photoButton;
    ItemListFragment.RefreshItems refreshItems;

    public static ItemDetailFragment newInstance(Item item, ItemListFragment.RefreshItems refreshItems) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.presenter = new ItemDetailPresenter(fragment, item);
        fragment.refreshItems = refreshItems;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_item_detail, container, false);
        presenter.start();
        return rootView;
    }

    @Override
    public void findView() {
        confirmButton = rootView.findViewById(R.id.confirmButton);
        cancelButton = rootView.findViewById(R.id.cancelButton);
        printButton= rootView.findViewById(R.id.printButton);
        yearsTextView = rootView.findViewById(R.id.yearsTextView);
        buyDateTextView = rootView.findViewById(R.id.buyDateTextView);
        brandTextView = rootView.findViewById(R.id.brandTextView);
        typeTextView = rootView.findViewById(R.id.typeTextView);

        locationTextView = rootView.findViewById(R.id.locationTextView);
        nameTextView = rootView.findViewById(R.id.nameTextView);
        itemIdTextView = rootView.findViewById(R.id.itemIdTextView);

        custodyGroupTextView = rootView.findViewById(R.id.custodyGroupTextView);
        custodianTextView = rootView.findViewById(R.id.custodianTextView);
        useGroupTextView = rootView.findViewById(R.id.useGroupTextView);
        userTextView = rootView.findViewById(R.id.userTextView);
        deleteTextView = rootView.findViewById(R.id.deleteTextView);
        printTextView = rootView.findViewById(R.id.printTextView);
        nickNameTextView = rootView.findViewById(R.id.nickNameTextView);
        tagContentTextView= rootView.findViewById(R.id.tagContentTextView);
        printLittleButton = rootView.findViewById(R.id.printLittleButton);
        photoButton = rootView.findViewById(R.id.photoButton);
    }

    @Override
    public void setViewValue(Item item) {
        yearsTextView.setText(item.getYears());
        buyDateTextView.setText(item.ADtoCal() +", " +item.getPurchaseDate());
        brandTextView.setText(item.getBrand());
        typeTextView.setText(item.getType());
        custodianTextView.setText(item.getCustodian().name);
        custodyGroupTextView.setText(item.getCustodyGroup().name);
        userTextView.setText(item.getUser().name);
        useGroupTextView.setText(item.getUseGroup().name);
        locationTextView.setText(item.getLocation().name);
        nameTextView.setText(item.getName());
        nickNameTextView.setText(item.getNickName());
        itemIdTextView.setText(item.getIdNumber());
        deleteTextView.setText(item.isDelete() ? "Y" : "N");
        printTextView.setText(item.isPrint() ? "Y" : "N");
        if(item.getTagContent() != null){
            tagContentTextView.setText(item.getTagContent().getName());
        }
        printButton.setVisibility(KeyConstants.showPrint ? View.VISIBLE : View.GONE);
        printLittleButton.setVisibility(KeyConstants.showPrintLittleTag ? View.VISIBLE : View.GONE);
        photoButton.setOnClickListener(v -> startPhotoActivity(item.getIdNumber().replace("-","")));

    }

    private void startPhotoActivity(String fileName) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/欣華盤點系統/照片";
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        int count = 1;
        File file = new File(dir, fileName + count + ".jpg");
        while (file.exists()) {
            count++;
            file = new File(dir, fileName+ count + ".jpg");
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file));
        startActivity(intent);
    }

    @Override
    public void setViewClick() {
        confirmButton.setOnClickListener(v -> {
            presenter.saveItemToChecked(true);
            refreshItems.refresh();
            dismiss();
        });
        cancelButton.setOnClickListener(v -> {
            presenter.saveItemToChecked(false);
            refreshItems.refresh();
            dismiss();
        });
        locationTextView.setOnClickListener(v -> presenter.locationTextViewClick());
        custodianTextView.setOnClickListener(v -> presenter.agentTextViewClick());
        custodyGroupTextView.setOnClickListener(v -> presenter.departmentTextViewClick());
        userTextView.setOnClickListener(v -> presenter.userTextViewClick());
        useGroupTextView.setOnClickListener(v -> presenter.useGroupTextViewClick());
        deleteTextView.setOnClickListener(v -> presenter.deleteTextViewClick());
        printTextView.setOnClickListener(v -> presenter.printTextViewClick());
        printButton.setOnClickListener(v -> presenter.printButtonClick());
        printLittleButton.setOnClickListener(v -> presenter.printLittleButtonClick());
        tagContentTextView.setOnClickListener(v -> presenter.tagContentTextViewClick());
    }
    @Override
    public void showSetDialog(DialogInterface.OnClickListener clickListener, String title, final String[] temp){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setItems(temp, clickListener);
        dialog.create().show();
    }
    @Override
    public void showSetDialog(SearchItemAdapter.OnClickListener clickListener, String title, List<SearchableItem> dataList){
        SearchItemDialog dialog = new SearchItemDialog(getActivity(),dataList);
        dialog.setTitle(title);
        dialog.setOnClickListener(clickListener);
        dialog.show();
    }

    @Override
    public void showPrintDialog(Item item) {
        PrinterItemDialog dialog = new PrinterItemDialog(getActivity());
        dialog.setItem(item);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void showLittlePrintDialog(Item item) {
        PrintItemLittleTagDialog dialog = new PrintItemLittleTagDialog(getActivity());
        dialog.setItem(item);
        dialog.setCancelable(false);
        dialog.show();
    }
}
