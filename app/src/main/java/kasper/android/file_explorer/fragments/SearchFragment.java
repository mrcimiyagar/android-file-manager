package kasper.android.file_explorer.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.ArrayList;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.listeners.OnAdapterRequestListener;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.HWareUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.adapters.FileAdapter;
import kasper.android.file_explorer.ui_utils.FileDecoration;

public class SearchFragment extends Fragment {

    private String dirPath;

    private String searchText;

    private ImageButton backBTN;
    private EditText searchEntryET;
    private RecyclerView itemsRV;
    private FastScroller itemsFS;

    private ArrayList<String> foldersPaths;
    private ArrayList<String> docsPaths;

    private FileAdapter fileAdapter;

    int gridSpanCount = 1;

    Handler searchHandler;
    Runnable searchRunnable;

    public SearchFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dirPath = (String) getArguments().getSerializable("dir_path");

        initView(view);
        initListeners();
        initDecoration();
        initData();

        return view;
    }

    private void initView(View view) {

        backBTN = (ImageButton) view.findViewById(R.id.activity_main_search_back_floating_action_button);
        searchEntryET = (EditText) view.findViewById(R.id.activity_main_search_edit_text);
        itemsRV = (RecyclerView) view.findViewById(R.id.activity_main_search_items_recycler_view);
        itemsFS = (FastScroller) view.findViewById(R.id.fragment_files_search_items_files_fast_scroller);

        backBTN.bringToFront();
    }

    private void initListeners() {

        searchHandler = new Handler();
        searchRunnable = new Runnable() {
            @Override
            public void run() {

                searchText = searchEntryET.getText().toString().toLowerCase();

                if (searchText.length() == 0) {

                    fileAdapter.updateAdapter(foldersPaths, docsPaths, false);
                }
                else {

                    ArrayList<String> dirsList1 = new ArrayList<>();
                    ArrayList<String> dirsList2 = new ArrayList<>();

                    for (String folderPath : foldersPaths) {
                        if (folderPath.substring(folderPath.lastIndexOf("/") + 1).toLowerCase().startsWith(searchText)) {
                            dirsList1.add(folderPath);
                        }
                        else if (folderPath.substring(folderPath.lastIndexOf("/") + 1).toLowerCase().contains(searchText)) {
                            dirsList2.add(folderPath);
                        }
                    }

                    ArrayList<String> docsList1 = new ArrayList<>();
                    ArrayList<String> docsList2 = new ArrayList<>();

                    for (String docPath : docsPaths) {
                        if (docPath.substring(docPath.lastIndexOf("/") + 1).toLowerCase().startsWith(searchText)) {
                            docsList1.add(docPath);
                        }
                        else if (docPath.substring(docPath.lastIndexOf("/") + 1).toLowerCase().contains(searchText)) {
                            docsList2.add(docPath);
                        }
                    }

                    ArrayList<String> dirResult = new ArrayList<>(dirsList1);
                    dirResult.addAll(dirsList2);
                    ArrayList<String> docResult = new ArrayList<>(docsList1);
                    docResult.addAll(docsList2);

                    fileAdapter.updateAdapter(dirResult, docResult, false);
                }
            }
        };

        searchEntryET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchHandler.post(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });
    }

    private void initDecoration() {

        foldersPaths = new ArrayList<>();
        docsPaths = new ArrayList<>();

        this.gridSpanCount = (int)(HWareUtils.getScreenSizeX() / (104 * HWareUtils.getScreenDensity()));
        itemsRV.setLayoutManager(new GridLayoutManager(getActivity(), gridSpanCount, LinearLayoutManager.VERTICAL, false));
        itemsRV.addItemDecoration(new FileDecoration(gridSpanCount));

        fileAdapter = new FileAdapter(foldersPaths, docsPaths, new OnAdapterRequestListener() {
            @Override
            public void scrollToTop() {
                itemsRV.scrollToPosition(0);
            }
        });
        itemsRV.setAdapter(fileAdapter);
        itemsFS.setRecyclerView(itemsRV);
    }

    private void initData() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final Pair<ArrayList<String>, ArrayList<String>> files = FileUtils.getFilesOfFolder(dirPath);
                if (files != null) {
                    foldersPaths = files.first;
                    docsPaths = files.second;
                }
                else {
                    foldersPaths = new ArrayList<>();
                    docsPaths = new ArrayList<>();
                }

                fileAdapter.updateAdapter(foldersPaths, docsPaths, false);

                UIUtils.openSoftKeyboard();

                searchEntryET.requestFocus();
            }
        });
    }

    public void notifyFolderDeleted(String path) {

        if (fileAdapter != null) {
            fileAdapter.removeFolder(path);
        }
    }

    public void notifyFolderInserted(String path) {

        if (fileAdapter != null) {
            if (searchText == null || searchText.length() == 0) {
                fileAdapter.addFolder(path);
            }
            else {
                String title = FileUtils.getTitleOfPath(path);
                if (title.startsWith(searchText) || title.contains(searchText)) {
                    fileAdapter.addFolder(path);
                }
            }
        }
    }

    public void notifyDocDeleted(String path) {

        if (fileAdapter != null) {
            fileAdapter.removeDocument(path);
        }
    }

    public void notifyDocInserted(String path) {

        if (fileAdapter != null) {
            if (searchText == null || searchText.length() == 0) {
                fileAdapter.addDocument(path);
            }
            else {
                String title = FileUtils.getTitleOfPath(path);
                if (title.startsWith(searchText) || title.contains(searchText)) {
                    fileAdapter.addDocument(path);
                }
            }
        }
    }
}