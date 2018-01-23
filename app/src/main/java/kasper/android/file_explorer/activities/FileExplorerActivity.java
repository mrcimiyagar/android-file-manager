package kasper.android.file_explorer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnAdapterRequestListener;
import kasper.android.file_explorer.listeners.OnAddressViewRequestListener;
import kasper.android.file_explorer.listeners.OnCbrdTransactListener;
import kasper.android.file_explorer.listeners.OnFileToUIListener;
import kasper.android.file_explorer.listeners.OnFilesChangeListener;
import kasper.android.file_explorer.listeners.OnSelectionUpdate;
import kasper.android.file_explorer.listeners.OnUITransactListener;
import kasper.android.file_explorer.models.Action;
import kasper.android.file_explorer.models.App;
import kasper.android.file_explorer.models.RRAction;
import kasper.android.file_explorer.models.files.Doc;
import kasper.android.file_explorer.models.files.DocTypes;
import kasper.android.file_explorer.utils.AppUtils;
import kasper.android.file_explorer.utils.CbrdUtils;
import kasper.android.file_explorer.utils.DialogUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.HWareUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.adapters.AppAdapter;
import kasper.android.file_explorer.adapters.BotSheetAdapter;
import kasper.android.file_explorer.adapters.DocAdapter;
import kasper.android.file_explorer.adapters.FileAdapter;
import kasper.android.file_explorer.adapters.base.BaseAdapter;
import kasper.android.file_explorer.components.AddressView;
import kasper.android.file_explorer.fragments.RocketFragment;
import kasper.android.file_explorer.fragments.SearchFragment;
import kasper.android.file_explorer.fragments.SettingsFragment;
import kasper.android.file_explorer.ui_utils.AppDecoration;
import kasper.android.file_explorer.ui_utils.DocDecoration;
import kasper.android.file_explorer.ui_utils.FileDecoration;

public class FileExplorerActivity extends AppCompatActivity {

    FrameLayout shadowLayout;
    View dialogView;

    Stack<String> pagesPathsStack;
    Stack<Integer> pagesScrollStack;

    CoordinatorLayout mainLayout;

    private Hashtable<Integer, RRAction> actionsOnResults = new Hashtable<>();
    private int requestCounter = 0;

    String moveAct = "";
    RelativeLayout headerLayout;
    RelativeLayout actionLayout;
    RelativeLayout detailsLayout;
    ImageButton rocketFAB;
    FloatingActionButton selectedItemsFAB;
    ImageButton contextMenuBTN;
    AddressView pagePathAV;

    FloatingActionButton pageBackFAB;
    ImageButton pageToParentBTN;
    //RelativeLayout lmChooserLayout;
    //ImageView lmChooserPointerIV;

    ImageButton pageSearchBTN;
    TextView pageItemsCountTV;

    RecyclerView itemsRV;
    FastScroller itemsFS;

    private FrameLayout emptySignLayout;

    boolean gridMode = true;

    private ArrayList<String> foldersPaths;
    private ArrayList<String> docsPaths;
    private ArrayList<Doc> docs;
    private ArrayList<App> apps;

    // ***

    BaseAdapter itemsAdapter;
    RecyclerView.ItemDecoration itemsDecoration;
    int gridSpanCount = 0;

    int currentItemsRVPose = 0;

    OnCbrdTransactListener cbrdTransactListener;

    final int fileItemWidth = 104;
    final int docItemWidth = 165;

    FrameLayout fragmentContainer;

    boolean dialogBoxTouched = false;

    @SuppressLint("StaticFieldLeak")
    private static FileExplorerActivity instance;
    public static FileExplorerActivity getInstance() {
        return instance;
    }

    // ***

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        try {
            pagesPathsStack = (Stack<String>) savedInstanceState.getSerializable("pages_paths_stack");
            pagesScrollStack = (Stack<Integer>) savedInstanceState.getSerializable("pages_scroll_stack");
        } catch (Exception ignored) {
        } finally {
            if (pagesPathsStack == null) {
                pagesPathsStack = new Stack<>();
                pagesScrollStack = new Stack<>();
                String reservedPath = null;
                try {
                    reservedPath = getIntent().getExtras().getString("dir_path");
                } catch (Exception ignored) {
                } finally {
                    if (reservedPath == null) {
                        reservedPath = FileUtils.getPrimaryStoragePath();
                    }
                    pagesPathsStack.push(reservedPath);
                }
            }
        }

        HWareUtils.setup( this);

        initView();
        initListeners();
        initDecoration();
    }

    @Override
    protected void onStart() {

        super.onStart();

        UIUtils.setup(new OnUITransactListener() {
            @Override
            public void openSoftKeyboard() {

                FileExplorerActivity.this.openSoftKeyboard();
            }

            @Override
            public void closeSoftKeyboard() {

                FileExplorerActivity.this.closeSoftKeyboard();
            }

            @Override
            public void showCxtMenu(View view, final int[] iconsResources, final String[] labels, final Action[] actions) {

                final PopupMenu popupMenu = new PopupMenu(FileExplorerActivity.this, view);

                for (int counter = 0; counter < labels.length; counter++) {
                    popupMenu.getMenu().add(1, counter, 1, labels[counter]);
                    popupMenu.getMenu().getItem(counter).setIcon(iconsResources[counter]);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        actions[item.getItemId()].run();
                        return true;
                    }
                });

                popupMenu.show();
            }

            @Override
            public void updateTopPage() {

            }

            @Override
            public void showDialogBox(View view) {

                Log.d("KasperLogger", "test 0");

                FileExplorerActivity.this.showDialogBox(view);
            }

            @Override
            public void closeDialogBox() {

                FileExplorerActivity.this.closeDialogBox();
            }

            @Override
            public LayoutInflater getLayoutInflater() {
                return FileExplorerActivity.this.getLayoutInflater();
            }

            @Override
            public void runIntentForResult(Intent intent, RRAction action) {

                int code = requestCounter++;
                actionsOnResults.put(code, action);
                startActivityForResult(intent, code);
            }

            @Override
            public void openTab(String path) {

                startActivity(new Intent( FileExplorerActivity.this, FileExplorerActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        .putExtra("dir_path", path));
            }

            @Override
            public void openTab() {

                startActivity(new Intent( FileExplorerActivity.this, FileExplorerActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK));
            }

            @Override
            public void closeExtraUI() {

                closeSoftKeyboard();

                if (dialogView != null) {
                    closeDialogBox();
                }
                else if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SettingsFragment") != null) {
                    FileExplorerActivity.this.closeSettingsFragment();
                }
                else if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
                    FileExplorerActivity.this.closeSearchFragment();
                }
                else if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("RocketFragment") != null) {
                    FileExplorerActivity.this.closeRocketFragment();
                }
            }

        },
                new OnFilesChangeListener() {

            @Override
            public void appInserted(App app) {

            }

            @Override
            public void appDelete(App app) {

            }

            @Override
            public void docInserted(String parentPath, String path) {

                if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
                    SearchFragment settingsFragment = (SearchFragment)  FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment");
                    settingsFragment.notifyDocInserted(path);
                }

                if (pagesPathsStack.lastElement().equals(parentPath)) {
                    if (!docsPaths.contains(path)) {
                        ((FileAdapter)itemsAdapter).addDocument(path);
                    }
                }
            }

            @Override
            public void docDeleted(String parentPath, String path) {

                if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
                    SearchFragment settingsFragment = (SearchFragment)  FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment");
                    settingsFragment.notifyDocDeleted(path);
                }

                if (pagesPathsStack.lastElement().equals(parentPath)) {
                    ((FileAdapter)itemsAdapter).removeDocument(path);
                }
            }

            @Override
            public void folderInserted(String parentPath, String path) {

                if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
                    SearchFragment settingsFragment = (SearchFragment)  FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment");
                    settingsFragment.notifyFolderInserted(path);
                }

                if (pagesPathsStack.lastElement().equals(parentPath)) {
                    if (!foldersPaths.contains(path)) {
                        ((FileAdapter)itemsAdapter).addFolder(path);
                    }
                }
            }

            @Override
            public void folderDeleted(String parentPath, String path) {

                if ( FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
                    SearchFragment searchFragment = (SearchFragment)  FileExplorerActivity.this.getSupportFragmentManager().findFragmentByTag("SearchFragment");
                    searchFragment.notifyFolderDeleted(path);
                }

                if (pagesPathsStack.lastElement().equals(parentPath)) {
                    ((FileAdapter)itemsAdapter).removeFolder(path);
                }
            }

            @Override
            public void sdCardStateChanged() {

            }
        });

        FileUtils.setFileToUIListener(new OnFileToUIListener() {
            @Override
            public void openFolder(String path) {

                openPath(path);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (String permission : new String[]{Manifest.permission.SET_WALLPAPER, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}) {
                permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(Core.getInstance(), permission);
            }
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                if (Settings.System.canWrite( this)) {
                    initData(false);
                }
                else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" +  this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UIUtils.runIntentForResult(intent, new RRAction() {
                        @Override
                        public void run(int resultCode, Intent resultData) {

                            initData(false);
                        }
                    });
                }
            }
            else {
                this.requestPermissions(new String[]{Manifest.permission.SET_WALLPAPER, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else {
            initData(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putSerializable("pages_paths_stack", pagesPathsStack);
        outState.putSerializable("pages_scroll_stack", pagesScrollStack);
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentItemsRVPose = ((LinearLayoutManager)itemsRV.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;

        for(int permission : grantResults) permissionCheck = permissionCheck + permission;

        if ((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck) {

            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.System.canWrite(this)) {
                    initData(false);
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UIUtils.runIntentForResult(intent, new RRAction() {
                        @Override
                        public void run(int resultCode, Intent resultData) {

                            initData(false);
                        }
                    });
                }
            }
            else {
                initData(false);
            }
        }
        else {

            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        RRAction action = actionsOnResults.get(requestCode);

        if (action != null) {

            actionsOnResults.remove(requestCode);
            action.run(resultCode, resultData);
        }
    }

    @Override
    public void onBackPressed() {

        closeSoftKeyboard();

        if (dialogView != null) {
            closeDialogBox();
        }
        else if (getSupportFragmentManager().findFragmentByTag("SettingsFragment") != null) {
            closeSettingsFragment();
        }
        else if (getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
            closeSearchFragment();
        }
        else if (getSupportFragmentManager().findFragmentByTag("RocketFragment") != null) {
            closeRocketFragment();
        }
        else if (pagesPathsStack.size() > 1) {
            pagesPathsStack.pop();
            currentItemsRVPose = pagesScrollStack.pop();
            itemsAdapter.cleanAdapter();
            initData(false);
        }
        else if (moveAct.equals("true")) {
            super.onBackPressed();
        }
    }

    private void initView() {

        setSupportActionBar((Toolbar)  findViewById(R.id.activity_main_toolbar));

        mainLayout = (CoordinatorLayout)  findViewById(R.id.activity_main);
        fragmentContainer = (FrameLayout)  findViewById(R.id.activity_main_fragment_container);
        shadowLayout = (FrameLayout)  findViewById(R.id.activity_main_shadow_layout);

        headerLayout = (RelativeLayout)  findViewById(R.id.activity_main_header_layout);
        actionLayout = (RelativeLayout)  findViewById(R.id.activity_main_action_layout);
        detailsLayout = (RelativeLayout)  findViewById(R.id.activity_main_detail_layout);
        rocketFAB = (ImageButton)  findViewById(R.id.activity_main_files_tree_floating_action_button);
        selectedItemsFAB = (FloatingActionButton)  findViewById(R.id.activity_main_selected_items_floating_action_button);
        contextMenuBTN = (ImageButton)  findViewById(R.id.activity_main_context_menu_image_button);
        pagePathAV = (AddressView)  findViewById(R.id.activity_main_path_address_view);

        pageBackFAB = (FloatingActionButton)  findViewById(R.id.activity_main_back_floating_action_button);
        pageToParentBTN = (ImageButton)  findViewById(R.id.activity_main_to_parent_action_button);

        pageSearchBTN = (ImageButton)  findViewById(R.id.activity_main_search_floating_action_button);
        pageItemsCountTV = (TextView)  findViewById(R.id.activity_main_items_count_text_view);

        itemsRV = (RecyclerView)  findViewById(R.id.activity_main_items_recycler_view);
        itemsFS = (FastScroller)  findViewById(R.id.fragment_files_items_files_fast_scroller);

        emptySignLayout = (FrameLayout)  findViewById(R.id.activity_main_empty_sign_layout);

        if (moveAct.equals("true")) {
            actionLayout.setBackgroundColor(getResources().getColor(R.color.colorMoveHeader));
            detailsLayout.setBackgroundColor(getResources().getColor(R.color.colorMoveHeader));

        }

        // ***

        selectedItemsFAB.hide();
    }

    private void initListeners() {

        CbrdUtils.setup(new OnSelectionUpdate() {
            @Override
            public void selectionStatesChanged() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (CbrdUtils.getSelectedFilesCount() > 0) {
                            selectedItemsFAB.show();
                        }
                        else {
                            selectedItemsFAB.hide();
                        }
                    }
                });
            }
        });

        selectedItemsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cbrdTransactListener = new OnCbrdTransactListener() {

                    @Override
                    public void fileTransacting(String path, int progress) {

                        DialogUtils.setFileTransactionMessage(path);
                        DialogUtils.setFileTransactionProgress(progress);
                    }

                    @Override
                    public void workingQueueDone() {

                        UIUtils.closeDialogBox();
                    }
                };

                int[] menuIcons = new int[4];
                String[] menuLabels = new String[4];
                Action[] menuActions = new Action[4];

                menuIcons[0] = R.drawable.uncheck;
                menuLabels[0] = "Deselect All";
                menuActions[0] = new Action() {
                    @Override
                    public void run() {
                        for (String path : foldersPaths) {
                            CbrdUtils.deselectPath(path);
                        }
                        for (String path : docsPaths) {
                            CbrdUtils.deselectPath(path);
                        }

                        ((FileAdapter)itemsAdapter).updateSelectionStatus();

                        selectedItemsFAB.hide();
                    }
                };

                menuIcons[1] = R.drawable.copy;
                menuLabels[1] = "Copy Selections";
                menuActions[1] = new Action() {
                    @Override
                    public void run() {

                        CbrdUtils.copy();

                        ((FileAdapter)itemsAdapter).updateSelectionStatus();

                        selectedItemsFAB.hide();
                    }
                };

                menuIcons[2] = R.drawable.cut;
                menuLabels[2] = "Cut Selections";
                menuActions[2] = new Action() {
                    @Override
                    public void run() {

                        CbrdUtils.cut();

                        ((FileAdapter)itemsAdapter).updateSelectionStatus();

                        selectedItemsFAB.hide();
                    }
                };

                menuIcons[3] = R.drawable.delete;
                menuLabels[3] = "Delete Selections";
                menuActions[3] = new Action() {
                    @Override
                    public void run() {

                        CbrdUtils.delete(cbrdTransactListener, new Runnable() {
                            @Override
                            public void run() {

                                selectedItemsFAB.hide();
                            }
                        });
                    }
                };

                UIUtils.showCxtMenu(selectedItemsFAB, menuIcons, menuLabels, menuActions);
            }
        });

        pageBackFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        pageToParentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentPath = pagesPathsStack.lastElement();

                String newPath = currentPath.substring(0, currentPath.lastIndexOf("/"));

                File file = new File(newPath);

                if (file.exists()) {

                    openPath(newPath);
                }
            }
        });

        pageSearchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SearchFragment searchFragment = new SearchFragment();
                Bundle searchArgs = new Bundle();
                searchArgs.putSerializable("dir_path", pagesPathsStack.lastElement());
                searchFragment.setArguments(searchArgs);
                getSupportFragmentManager().beginTransaction().add(R.id.activity_main_fragment_container, searchFragment, "SearchFragment").commit();
                getSupportFragmentManager().beginTransaction().show(searchFragment).commit();
            }
        });

        rocketFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RocketFragment rocketFragment = new RocketFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.activity_main_fragment_container, rocketFragment, "RocketFragment").commit();
                getSupportFragmentManager().beginTransaction().show(rocketFragment).commit();
            }
        });

        shadowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    closeSoftKeyboard();
                    closeDialogBox();
                    return true;
                }

                return false;
            }
        });

        itemsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    selectedItemsFAB.hide();
                }
                else {
                    if (CbrdUtils.getSelectedFilesCount() > 0) {
                        selectedItemsFAB.show();
                    }
                    else {
                        selectedItemsFAB.hide();
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        pagePathAV.setInteractionInterface(new OnAddressViewRequestListener() {
            @Override
            public void openPath(String path) {
                FileExplorerActivity.this.openPath(path);
            }
        });

        rocketFAB.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void initDecoration() {

        foldersPaths = new ArrayList<>();
        docsPaths = new ArrayList<>();

        this.gridSpanCount = (int)(HWareUtils.getScreenSizeX() / (fileItemWidth * HWareUtils.getScreenDensity()));
        itemsRV.setLayoutManager(new GridLayoutManager(this, gridSpanCount, LinearLayoutManager.VERTICAL, false));
        itemsDecoration = new FileDecoration(gridSpanCount);
        itemsRV.addItemDecoration(itemsDecoration);

        itemsAdapter = new FileAdapter(foldersPaths, docsPaths, new OnAdapterRequestListener() {
            @Override
            public void scrollToTop() {
                itemsRV.scrollToPosition(0);
            }
        });
        itemsRV.setAdapter(itemsAdapter);
        itemsFS.setRecyclerView(itemsRV);
    }

    public void initData(final boolean keepScroll) {

        foldersPaths = new ArrayList<>();
        docsPaths = new ArrayList<>();
        docs = new ArrayList<>();
        apps = new ArrayList<>();

        if (pagesPathsStack.lastElement().equals("/storage/apps")) {

            contextMenuBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int[] menuIcons = new int[2];
                    String[] menuLabels = new String[2];
                    Action[] menuActions = new Action[2];

                    menuIcons[0] = R.drawable.new_tab;
                    menuLabels[0] = "New tab";
                    menuActions[0] = new Action() {
                        @Override
                        public void run() {
                            UIUtils.openNewTab();
                        }
                    };

                    menuIcons[1] = R.drawable.settings_dark;
                    menuLabels[1] = "Settings";
                    menuActions[1] = new Action() {
                        @Override
                        public void run() {

                            SettingsFragment settingsFragment = new SettingsFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.activity_main_fragment_container, settingsFragment, "SettingsFragment").commit();
                            getSupportFragmentManager().beginTransaction().show(settingsFragment).commit();
                        }
                    };

                    UIUtils.showCxtMenu(view, menuIcons, menuLabels, menuActions);
                }
            });

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {

                    apps = AppUtils.getAppsList();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    pagePathAV.setAddress(pagesPathsStack.lastElement());
                    emptySignLayout.setVisibility((apps.size() == 0) ? View.VISIBLE : View.GONE);
                    if (itemsAdapter instanceof AppAdapter) {
                        ((AppAdapter) itemsAdapter).update(apps);
                    }
                    else {
                        gridSpanCount = (int)(HWareUtils.getScreenSizeX() / (80 * HWareUtils.getScreenDensity()));
                        itemsRV.setLayoutManager(new GridLayoutManager(FileExplorerActivity.this, gridSpanCount, GridLayoutManager.VERTICAL, false));
                        if (itemsDecoration != null) {
                            itemsRV.removeItemDecoration(itemsDecoration);
                        }
                        itemsDecoration = new AppDecoration(gridSpanCount);
                        itemsRV.addItemDecoration(itemsDecoration);
                        itemsAdapter = new AppAdapter(apps);
                        itemsRV.setAdapter(itemsAdapter);
                    }

                    pageItemsCountTV.setText((apps.size()) + " items");

                    itemsRV.scrollToPosition(currentItemsRVPose);
                }
            }.execute();
        }
        else if (pagesPathsStack.lastElement().equals("/storage/photos")
                || pagesPathsStack.lastElement().equals("/storage/musics")
                || pagesPathsStack.lastElement().equals("/storage/videos")) {

            contextMenuBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int[] menuIcons = new int[2];
                    String[] menuLabels = new String[2];
                    Action[] menuActions = new Action[2];

                    menuIcons[0] = R.drawable.new_tab;
                    menuLabels[0] = "New tab";
                    menuActions[0] = new Action() {
                        @Override
                        public void run() {
                            UIUtils.openNewTab();
                        }
                    };

                    menuIcons[1] = R.drawable.settings_dark;
                    menuLabels[1] = "Settings";
                    menuActions[1] = new Action() {
                        @Override
                        public void run() {

                            SettingsFragment settingsFragment = new SettingsFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.activity_main_fragment_container, settingsFragment, "SettingsFragment").commit();
                            getSupportFragmentManager().beginTransaction().show(settingsFragment).commit();
                        }
                    };

                    UIUtils.showCxtMenu(view, menuIcons, menuLabels, menuActions);
                }
            });

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {

                    if (pagesPathsStack.lastElement().equals("/storage/photos")) {
                        try {
                            String PathOfImage;
                            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            String[] projection = {MediaStore.MediaColumns.DATA};
                            Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
                            assert cursor != null;
                            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                            while (cursor.moveToNext()) {
                                PathOfImage = cursor.getString(column_index_data);
                                if (new File(PathOfImage).exists()) {
                                    Doc doc = new Doc(PathOfImage, -1, DocTypes.Photo);
                                    docs.add(doc);
                                }
                            }
                            cursor.close();
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                    else if (pagesPathsStack.lastElement().equals("/storage/musics")) {
                        try {
                            String PathOfMusic;
                            long AlbumId;
                            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            String[] projection = {MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA};
                            Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
                            assert cursor != null;
                            int column_index_id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                            while (cursor.moveToNext()) {
                                AlbumId = cursor.getLong(column_index_id);
                                PathOfMusic = cursor.getString(column_index_data);
                                if (new File(PathOfMusic).exists()) {
                                    Doc doc = new Doc(PathOfMusic, AlbumId, DocTypes.Music);
                                    docs.add(doc);
                                }
                            }
                            cursor.close();
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                    else if (pagesPathsStack.lastElement().equals("/storage/videos")) {
                        try {
                            String PathOfVideo;
                            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            String[] projection = {MediaStore.Video.Media.DATA};
                            Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
                            assert cursor != null;
                            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                            while (cursor.moveToNext()) {
                                PathOfVideo = cursor.getString(column_index_data);
                                if (new File(PathOfVideo).exists()) {
                                    Doc doc = new Doc(PathOfVideo, -1, DocTypes.Movie);
                                    docs.add(doc);
                                }
                            }
                            cursor.close();
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    pagePathAV.setAddress(pagesPathsStack.lastElement());
                    emptySignLayout.setVisibility((docs.size() == 0) ? View.VISIBLE : View.GONE);
                    if (itemsAdapter instanceof DocAdapter) {
                        ((DocAdapter) itemsAdapter).updateAdapter(docs);
                    }
                    else {
                        gridSpanCount = (int)(HWareUtils.getScreenSizeX() / (docItemWidth * HWareUtils.getScreenDensity()));
                        itemsRV.setLayoutManager(new GridLayoutManager(FileExplorerActivity.this, gridSpanCount, GridLayoutManager.VERTICAL, false));
                        if (itemsDecoration != null) {
                            itemsRV.removeItemDecoration(itemsDecoration);
                        }
                        itemsDecoration = new DocDecoration(gridSpanCount);
                        itemsRV.addItemDecoration(itemsDecoration);
                        itemsAdapter = new DocAdapter(docs, new OnAdapterRequestListener() {
                            @Override
                            public void scrollToTop() {
                                itemsRV.getLayoutManager().scrollToPosition(0);
                            }
                        });
                        itemsRV.setAdapter(itemsAdapter);
                    }

                    pageItemsCountTV.setText((docs.size()) + " items");

                    itemsRV.scrollToPosition(currentItemsRVPose);
                }
            }.execute();
        }
        else {

            contextMenuBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cbrdTransactListener = new OnCbrdTransactListener() {

                        @Override
                        public void fileTransacting(String path, int progress) {

                            DialogUtils.setFileTransactionMessage(path);
                            DialogUtils.setFileTransactionProgress(progress);
                        }

                        @Override
                        public void workingQueueDone() {

                            UIUtils.closeDialogBox();
                        }
                    };

                    int[] menuIcons;
                    String[] menuLabels;
                    Action[] menuActions;

                    if (CbrdUtils.getCopiedFilesCount() > 0) {
                        menuIcons = new int[5];
                        menuLabels = new String[5];
                        menuActions = new Action[5];
                    }
                    else {
                        menuIcons = new int[4];
                        menuLabels = new String[4];
                        menuActions = new Action[4];
                    }

                    menuIcons[0] = R.drawable.new_tab;
                    menuLabels[0] = "New tab";
                    menuActions[0] = new Action() {
                        @Override
                        public void run() {
                            UIUtils.openNewTab();
                        }
                    };

                    menuIcons[1] = R.drawable.new_folder;
                    menuLabels[1] = "Create Folder";
                    menuActions[1] = new Action() {
                        @Override
                        public void run() {
                            UIUtils.showDialogBox(DialogUtils.getCreateFolderDialogView(getLayoutInflater(), pagesPathsStack.lastElement()));
                        }
                    };

                    menuIcons[2] = R.drawable.settings_dark;
                    menuLabels[2] = "Settings";
                    menuActions[2] = new Action() {
                        @Override
                        public void run() {

                            SettingsFragment settingsFragment = new SettingsFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.activity_main_fragment_container, settingsFragment, "SettingsFragment").commit();
                            getSupportFragmentManager().beginTransaction().show(settingsFragment).commit();
                        }
                    };

                    menuIcons[3] = R.drawable.select_all;
                    menuLabels[3] = "Select All";
                    menuActions[3] = new Action() {
                        @Override
                        public void run() {
                            for (String path : foldersPaths) {
                                CbrdUtils.selectPath(path);
                            }
                            for (String path : docsPaths) {
                                CbrdUtils.selectPath(path);
                            }
                            ((FileAdapter)itemsAdapter).updateSelectionStatus();
                        }
                    };

                    if (CbrdUtils.getCopiedFilesCount() > 0) {

                        menuIcons[4] = R.drawable.paste;
                        menuLabels[4] = "Paste files";
                        menuActions[4] = new Action() {
                            @Override
                            public void run() {

                                if (CbrdUtils.getCopiedFilesCount() > 0) {
                                    UIUtils.showDialogBox(DialogUtils.getFileTransferDialogView(getLayoutInflater(), "Pasting files..."));
                                }

                                CbrdUtils.paste(pagesPathsStack.lastElement(), cbrdTransactListener);
                            }
                        };
                    }

                    UIUtils.showCxtMenu(view, menuIcons, menuLabels, menuActions);
                }
            });

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {

                    final Pair<ArrayList<String>, ArrayList<String>> files = FileUtils.getFilesOfFolder(pagesPathsStack.lastElement());

                    if (files != null) {
                        foldersPaths = files.first;
                        docsPaths = files.second;
                    }
                    else {
                        foldersPaths = new ArrayList<>();
                        docsPaths = new ArrayList<>();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    pagePathAV.setAddress(pagesPathsStack.lastElement());
                    emptySignLayout.setVisibility((foldersPaths.size() + docsPaths.size() == 0) ? View.VISIBLE : View.GONE);
                    if (itemsAdapter instanceof FileAdapter) {
                        Log.d("KasperLogger", "hello all !");
                        ((FileAdapter) itemsAdapter).updateAdapter(foldersPaths, docsPaths, keepScroll);
                    }
                    else {
                        gridSpanCount = (int)(HWareUtils.getScreenSizeX() / (fileItemWidth * HWareUtils.getScreenDensity()));
                        itemsRV.setLayoutManager(new GridLayoutManager(FileExplorerActivity.this, gridSpanCount, GridLayoutManager.VERTICAL, false));
                        //itemsRV.setLayoutManager(new LinearLayoutManager(FileExplorerActivity.this, LinearLayoutManager.VERTICAL, false));
                        if (itemsDecoration != null) {
                            itemsRV.removeItemDecoration(itemsDecoration);
                        }
                        itemsDecoration = new FileDecoration(gridSpanCount);
                        itemsRV.addItemDecoration(itemsDecoration);
                        itemsAdapter = new FileAdapter(foldersPaths, docsPaths, new OnAdapterRequestListener() {
                            @Override
                            public void scrollToTop() {
                                itemsRV.getLayoutManager().scrollToPosition(0);
                            }
                        });
                        itemsRV.setAdapter(itemsAdapter);
                    }

                    pageItemsCountTV.setText((foldersPaths.size() + docsPaths.size()) + " items");

                    itemsRV.scrollToPosition(currentItemsRVPose);
                }
            }.execute();
        }
    }

    public void recheckEmpty() {

        final Pair<ArrayList<String>, ArrayList<String>> files = FileUtils.getFilesOfFolder(pagesPathsStack.lastElement());

        if (files != null) {
            foldersPaths = files.first;
            docsPaths = files.second;
        }
        else {
            foldersPaths = new ArrayList<>();
            docsPaths = new ArrayList<>();
        }

        if (itemsRV.getAdapter() instanceof FileAdapter) {
            emptySignLayout.setVisibility((foldersPaths.size() + docsPaths.size() == 0) ? View.VISIBLE : View.GONE);
            ((FileAdapter) itemsRV.getAdapter()).updateAdapter(foldersPaths, docsPaths, true);
        }
    }

    public void closeSearchFragment() {

        if (getSupportFragmentManager().findFragmentByTag("SearchFragment") != null) {
            Fragment searchFragment = getSupportFragmentManager().findFragmentByTag("SearchFragment");
            getSupportFragmentManager().beginTransaction().remove(searchFragment).commit();
        }
    }

    public void closeSettingsFragment() {

        if (getSupportFragmentManager().findFragmentByTag("SettingsFragment") != null) {
            Fragment settingsFragment = getSupportFragmentManager().findFragmentByTag("SettingsFragment");
            getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();
        }
    }

    public void closeRocketFragment() {

        if (getSupportFragmentManager().findFragmentByTag("RocketFragment") != null) {
            Fragment rocketFragment = getSupportFragmentManager().findFragmentByTag("RocketFragment");
            getSupportFragmentManager().beginTransaction().remove(rocketFragment).commit();
        }
    }

    public void showDialogBox(final View view) {

        if (dialogView != null) {

            dialogView.animate().y(-100 * HWareUtils.getScreenDensity()).alpha(0).setDuration(250).withEndAction(new Runnable() {
                @Override
                public void run() {
                    fragmentContainer.removeView(dialogView);
                    dialogView = null;
                    dialogBoxTouched = false;
                    attachNewDialogBox(view);
                }
            }).start();
            shadowLayout.animate().alpha(0f).setDuration(250).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialogView.setVisibility(View.GONE);
                    shadowLayout.setVisibility(View.GONE);
                }
            }, 250);
        }
        else {
            attachNewDialogBox(view);
        }
    }

    public void attachNewDialogBox(View view) {

        this.dialogView = view;

        this.dialogView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        this.dialogView.setY(-100 * HWareUtils.getScreenDensity());
        this.dialogView.setAlpha(0);
        this.dialogView.setVisibility(View.VISIBLE);

        fragmentContainer.addView(this.dialogView);

        if (Build.VERSION.SDK_INT >= 21) {
            view.setElevation(6 * HWareUtils.getScreenDensity());
        }

        this.dialogView.animate().y(0).alpha(1).setDuration(200).start();
        shadowLayout.setVisibility(View.VISIBLE);
        shadowLayout.animate().alpha(0.5f).setDuration(200).start();
    }

    public void closeDialogBox() {

        if (dialogView != null) {

            dialogView.animate().y(-100 * HWareUtils.getScreenDensity()).alpha(0).setDuration(200).withEndAction(new Runnable() {
                @Override
                public void run() {
                    fragmentContainer.removeView(dialogView);
                    dialogView = null;
                    dialogBoxTouched = false;
                }
            }).start();
            shadowLayout.animate().alpha(0f).setDuration(200).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shadowLayout.setVisibility(View.GONE);
                }
            }, 200);
        }
    }

    public void openSoftKeyboard() {

        if (!KeyboardVisibilityEvent.isKeyboardVisible(this)) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void closeSoftKeyboard() {

        if (KeyboardVisibilityEvent.isKeyboardVisible(this)) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void openPath(String path) {

        this.closeSearchFragment();

        pagesPathsStack.push(path);
        currentItemsRVPose = ((LinearLayoutManager)itemsRV.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        pagesScrollStack.push(currentItemsRVPose);
        currentItemsRVPose = 0;
        itemsAdapter.cleanAdapter();

        initData(false);
    }
}