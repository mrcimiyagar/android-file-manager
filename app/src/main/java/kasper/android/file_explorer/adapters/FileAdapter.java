package kasper.android.file_explorer.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnAdapterRequestListener;
import kasper.android.file_explorer.listeners.OnCbrdTransactListener;
import kasper.android.file_explorer.listeners.OnFileRenameListener;
import kasper.android.file_explorer.models.Action;
import kasper.android.file_explorer.models.App;
import kasper.android.file_explorer.utils.AppUtils;
import kasper.android.file_explorer.utils.CbrdUtils;
import kasper.android.file_explorer.utils.DialogUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.HWareUtils;
import kasper.android.file_explorer.utils.ResUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.activities.FileExplorerActivity;
import kasper.android.file_explorer.adapters.base.BaseAdapter;

import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;

public class FileAdapter extends BaseAdapter implements SectionTitleProvider {

    private final String shortcutExtension = ".KasperLauncherShortcut";

    private ArrayList<String> foldersPaths;
    private ArrayList<String> docsPaths;

    private OnAdapterRequestListener adapterRequestListener;

    private HashSet<Holder> visibleHolders;

    public FileAdapter(ArrayList<String> folders, ArrayList<String> docs, OnAdapterRequestListener adapterRequestListener) {

        this.foldersPaths = new ArrayList<>(folders);
        this.docsPaths = new ArrayList<>(docs);

        this.adapterRequestListener = adapterRequestListener;

        this.visibleHolders = new HashSet<>();

        this.notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<String> folders, ArrayList<String> docs, boolean keepScroll) {

        try {

            HashSet<String> currentFoldersSet = new HashSet<>(foldersPaths);
            HashSet<String> updateFoldersSet = new HashSet<>(folders);

            HashSet<String> currentDocsSet = new HashSet<>(docsPaths);
            HashSet<String> updateDocsSet = new HashSet<>(docs);

            ArrayList<String> currentFoldersList = foldersPaths;
            ArrayList<String> currentDocsList = docsPaths;

            this.foldersPaths = new ArrayList<>(folders);
            this.docsPaths = new ArrayList<>(docs);

            int counter = 0;
            for (String currentFolderTemp : currentFoldersList) {

                try {
                    if (!updateFoldersSet.contains(currentFolderTemp)) {
                        notifyItemRemoved(counter);
                    } else {
                        counter++;
                    }
                } catch (Exception ignored) {
                }
            }

            for (String currentDocTemp : currentDocsList) {

                try {
                    if (!updateDocsSet.contains(currentDocTemp)) {
                        notifyItemRemoved(counter);
                    } else {
                        counter++;
                    }
                } catch (Exception ignored) {
                }
            }

            counter = 0;

            for (String updateFolderTemp : folders) {

                try {
                    if (!currentFoldersSet.contains(updateFolderTemp)) {
                        notifyItemInserted(counter);
                    } else {
                        notifyItemChanged(counter);
                    }

                    counter++;
                } catch (Exception ignored) {
                }
            }

            for (String updateDocTemp : docs) {

                try {
                    if (!currentDocsSet.contains(updateDocTemp)) {
                        notifyItemInserted(counter);
                    } else {
                        notifyItemChanged(counter);
                    }

                    counter++;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        if (!keepScroll) {
            adapterRequestListener.scrollToTop();
        }
    }

    public void cleanAdapter() {

        this.foldersPaths = new ArrayList<>();
        this.docsPaths = new ArrayList<>();
        this.notifyDataSetChanged();
    }

    public void updateSelectionStatus() {

        for (Holder holder : visibleHolders) {

            if (holder.getPath() != null) {

                if (!CbrdUtils.isPathSelected(holder.getPath())) {
                    holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    holder.mainLayout.setBackgroundColor(Core.getInstance().getResources().getColor(R.color.colorAccent2));
                }
            }
        }
    }

    public void addFolder(String path) {

        String title = path.substring(path.lastIndexOf("/") + 1);

        int counter = 0;

        if (foldersPaths.size() > 0) {

            ListIterator<String> iterator = this.foldersPaths.listIterator();

            boolean added = false;

            while (iterator.hasNext()) {

                String tempPath = iterator.next();
                String tempTitle = tempPath.substring(tempPath.lastIndexOf("/") + 1);

                if (tempTitle.compareToIgnoreCase(title) >= 0) {
                    iterator.previous();
                    iterator.add(path);
                    added = true;
                    break;
                }

                counter++;
            }

            if (!added) {

                foldersPaths.add(path);
            }
        }
        else {
            foldersPaths.add(path);
        }

        notifyItemInserted(counter);
    }

    public void addDocument(String path) {

        String title = path.substring(path.lastIndexOf("/") + 1);

        int counter = 0;

        if (docsPaths.size() > 0) {

            ListIterator<String> iterator = this.docsPaths.listIterator();

            boolean added = false;

            while (iterator.hasNext()) {

                String tempPath = iterator.next();
                String tempTitle = tempPath.substring(tempPath.lastIndexOf("/") + 1);

                if (tempTitle.compareToIgnoreCase(title) >= 0) {
                    iterator.previous();
                    iterator.add(path);
                    added = true;
                    break;
                }

                counter++;
            }

            if (!added) {

                docsPaths.add(path);
            }
        }
        else {
            docsPaths.add(path);
        }

        notifyItemInserted(counter + foldersPaths.size());
    }

    public void removeFolder(String path) {

        int index = foldersPaths.indexOf(path);

        if (index >= 0) {

            foldersPaths.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeDocument(String path) {

        int index = docsPaths.indexOf(path);

        if (index >= 0) {

            docsPaths.remove(index);
            notifyItemRemoved(index + foldersPaths.size());
        }
    }

    public void renameFolder(String oldPath, String newPath) {

        int counter = 0;

        boolean found = false;

        for (String folderPath : foldersPaths) {

            if (folderPath.equals(oldPath)) {

                found = true;

                break;
            }

            counter++;
        }

        if (found) {

            foldersPaths.set(counter, newPath);

            this.notifyItemChanged(counter);
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                Glide.get(Core.getInstance()).clearDiskCache();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                Glide.get(Core.getInstance()).clearMemory();
            }
        }.execute();
    }

    public void renameDocument(String oldPath, String newPath) {

        int counter = 0;

        boolean found = false;

        for (String docPath : docsPaths) {

            if (docPath.equals(oldPath)) {

                found = true;

                break;
            }

            counter++;
        }

        if (found) {

            docsPaths.set(counter, newPath);

            this.notifyItemChanged(counter + foldersPaths.size());
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                Glide.get(Core.getInstance()).clearDiskCache();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                Glide.get(Core.getInstance()).clearMemory();
            }
        }.execute();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_file, parent, false));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        visibleHolders.remove(holder);

        ((Holder)holder).cleanup();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        try {

            final String path = position < foldersPaths.size() ? foldersPaths.get(position) : docsPaths.get(position - foldersPaths.size());

            ((Holder)holder).setPath(path);

            visibleHolders.add(((Holder)holder));

            new HolderLoader(((Holder)holder), path).execute();
        }
        catch (Exception ignored) { }
    }

    @Override
    public int getItemCount() { return this.foldersPaths.size() + this.docsPaths.size(); }

    @Override
    public String getSectionTitle(int position) {

        String path;

        if (position < this.foldersPaths.size()) {

            path = this.foldersPaths.get(position);
        }
        else {
            if (docsPaths.size() > position - foldersPaths.size()) {
                path = this.docsPaths.get(position - foldersPaths.size());
            }
            else {
                path = "";
            }
        }

        String title = "";
        int index = path.lastIndexOf("/");
        if (index > 0 && index < path.length() - 1) {
            title = path.substring(index + 1);
        }
        return title.length() > 0 ? title.substring(0, 1).toUpperCase() : "";
    }

    private void imageLoaded(Holder holder) {

        holder.iconIV.setPadding(0, 0, 0, 0);
        int layoutPadding = (int) (4 * HWareUtils.getScreenDensity());
        holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);
    }

    private void imageNotLoaded(Holder holder, String path) {

        int padding = (int) (12 * HWareUtils.getScreenDensity());
        holder.iconIV.setPadding(padding, padding, padding, padding);
        int layoutPadding = (int) (8 * HWareUtils.getScreenDensity());
        holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);

        String mimeType = FileUtils.getMimeType(path);
        if (mimeType != null) {
            holder.iconIV.setImageResource(mimeType.startsWith("image") ? R.drawable.photo_dark : (mimeType.startsWith("audio") ? R.drawable.music_dark : R.drawable.audio_dark));
        }
        else {
            holder.iconIV.setImageResource(R.drawable.document_dark);
        }
        //holder.iconIV.setBackgroundResource(R.drawable.primary_circle);
    }

    class Holder extends RecyclerView.ViewHolder {

        RelativeLayout mainLayout;
        RelativeLayout iconLayout;
        ImageView iconIV;
        ImageView shortcutIV;
        TextView titleTV;
        TextView detailsTV;
        ImageView cxtMenuIV;

        private String path;
        public String getPath() { return this.path; }
        public void setPath(String path) { this.path = path; }

        private HolderLoader holderLoader;
        void setHolderLoader(HolderLoader holderLoader) { this.holderLoader = holderLoader; }

        Holder(View itemView) {

            super(itemView);

            mainLayout = itemView.findViewById(R.id.adapter_file_directory_main_layout);
            iconLayout = itemView.findViewById(R.id.adapter_file_icon_layout);
            iconIV = itemView.findViewById(R.id.adapter_file_directory_icon_image_view);
            shortcutIV = itemView.findViewById(R.id.adapter_file_directory_shortcut_image_view);
            titleTV = itemView.findViewById(R.id.adapter_file_directory_title_text_view);
            detailsTV = itemView.findViewById(R.id.adapter_file_directory_details_text_view);
            cxtMenuIV = itemView.findViewById(R.id.adapter_file_context_menu_image_view);
        }

        void cleanup() {

            if (holderLoader != null) {
                holderLoader.cancel(true);
            }

            Glide.clear(iconIV);

            mainLayout.clearAnimation();
        }
    }

    private class HolderLoader extends AsyncTask {

        Holder holder;
        String path;

        String title;
        String detail;
        String targetPath;
        boolean isFolder;
        String mimeType;
        Bitmap icon;

        HolderLoader(Holder holder, String path) {

            this.holder = holder;
            this.path = path;
        }

        @Override
        public Void doInBackground(final Object... params) {

            holder.setHolderLoader(this);

            try {

                int index = path.lastIndexOf("/");

                if (index > 0 && index < path.length() - 1) {
                    String titleWithExt = path.substring(index + 1);
                    if (titleWithExt.endsWith(shortcutExtension)) {
                        titleWithExt = titleWithExt.substring(0, titleWithExt.length() - shortcutExtension.length());
                    }
                    title = titleWithExt;
                } else {
                    title = "unknown";
                }

                // ***

                final File file = new File(path);
                //@SuppressLint("SimpleDateFormat")
                //String lastModifiedDate = new SimpleDateFormat("dd MMM yyyy hh:mm aa").format(new Date(file.lastModified()));

                if (path.endsWith(shortcutExtension)) {
                    detail = android.text.format.Formatter.formatFileSize(Core.getInstance(), file.length());
                    String tempTargetPath = (String) FileUtils.readObjectFromFile(file);
                    if (tempTargetPath == null) {
                        tempTargetPath = "";
                    }
                    targetPath = tempTargetPath;
                    mimeType = FileUtils.getMimeType(targetPath);
                    icon = loadIconOf(targetPath);
                    isFolder = new File(targetPath).isDirectory();
                } else {
                    targetPath = "";
                    if (file.isDirectory()) {
                        isFolder = true;
                        File[] files = file.listFiles();
                        int foldersCount = 0, docsCount = 0;
                        if (files != null) {
                            for (File childFile : files) {
                                if (childFile.isDirectory()) {
                                    foldersCount++;
                                } else {
                                    docsCount++;
                                }
                            }
                        }
                        detail = (foldersCount + docsCount) + " items";
                    } else {
                        isFolder = false;
                        detail = android.text.format.Formatter.formatFileSize(Core.getInstance(), file.length());
                    }

                    if (!isFolder) {
                        mimeType = FileUtils.getMimeType(path);
                        icon = loadIconOf(path);
                    } else {
                        icon = null;
                        mimeType = "folder";
                    }
                }
            }
            catch (Exception ignored) { }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            holder.titleTV.setText(title);
            holder.detailsTV.setText(detail);
            holder.shortcutIV.setVisibility(targetPath.length() > 0 ? View.VISIBLE : View.GONE);

            if (targetPath.length() > 0 && !targetPath.contains("/")) {
                App app = AppUtils.getAppsDict().get(targetPath);
                holder.iconIV.setImageDrawable(app.getIcon());
                holder.iconIV.setBackgroundColor(Color.TRANSPARENT);
                int padding = (int) (2 * HWareUtils.getScreenDensity());
                holder.iconIV.setPadding(padding, padding, padding, padding);
                int layoutPadding = (int) (8 * HWareUtils.getScreenDensity());
                holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);
            }
            else {
                if (mimeType != null && mimeType.startsWith("image")) {

                    Glide.with(Core.getInstance()).load(new File(targetPath.length() > 0 ? targetPath : path))/*.signature(new StringSignature("0"))*/.listener(new RequestListener<File, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {

                            imageNotLoaded(holder, path);

                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            holder.iconIV.setImageDrawable(resource);

                            imageLoaded(holder);

                            return true;
                        }
                    }).fitCenter().crossFade().into(holder.iconIV);
                }

                if (icon != null || (mimeType != null && mimeType.startsWith("image"))) {
                    if (mimeType != null && !mimeType.startsWith("image")) {
                        holder.iconIV.setImageBitmap(icon);
                    }
                    holder.iconIV.setBackgroundColor(Color.TRANSPARENT);
                    holder.iconIV.setPadding(0, 0, 0, 0);
                    int layoutPadding = (int) (4 * HWareUtils.getScreenDensity());
                    holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);
                } else {
                    //holder.iconIV.setBackgroundResource(R.drawable.gray_circle);
                    if (isFolder) {
                        holder.iconIV.setImageResource(R.drawable.folder_dark);
                    } else if (mimeType != null && mimeType.startsWith("video")) {
                        holder.iconIV.setImageResource(R.drawable.audio_dark);
                    } else if (mimeType != null && mimeType.startsWith("audio")) {
                        holder.iconIV.setImageResource(R.drawable.music_dark);
                    } else {
                        holder.iconIV.setImageResource(R.drawable.document_dark);
                    }
                    int padding = (int) (12 * HWareUtils.getScreenDensity());
                    holder.iconIV.setPadding(padding, padding, padding, padding);
                    int layoutPadding = (int) (8 * HWareUtils.getScreenDensity());
                    //holder.iconIV.setBackgroundResource(R.drawable.primary_circle);
                    holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);
                }
            }

            if (!CbrdUtils.isPathSelected(path)) {
                holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.mainLayout.setBackgroundColor(Core.getInstance().getResources().getColor(R.color.colorAccent2));
            }

            View.OnClickListener clickListener;
            View.OnLongClickListener longClickListener;

            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (targetPath.length() == 0) {
                        if (!CbrdUtils.isPathSelected(path)) {
                            FileUtils.openDocument(path);
                        }
                        else {
                            CbrdUtils.deselectPath(path);
                            holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    else {
                        if (targetPath.contains("/")) {
                            if (!CbrdUtils.isPathSelected(path)) {
                                FileUtils.openDocument(targetPath);
                            }
                            else {
                                CbrdUtils.deselectPath(path);
                                holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                            }
                        } else {
                            if (!CbrdUtils.isPathSelected(path)) {
                                AppUtils.openApplication(targetPath);
                            }
                            else {
                                CbrdUtils.deselectPath(path);
                                holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                    }
                }
            };

            final int[] menuIcons;
            final String[] menuLabels;
            final Action[] menuActions;

            if (isFolder) { // folder

                menuIcons = new int[9];
                menuLabels = new String[9];
                menuActions = new Action[9];
            }
            else { // file

                if (mimeType != null && (mimeType.startsWith("image") || mimeType.startsWith("audio") || path.endsWith(".zip") || path.endsWith(".zip".concat(shortcutExtension)))) {
                    menuIcons = new int[11];
                    menuLabels = new String[11];
                    menuActions = new Action[11];
                }
                else { // not image or sound
                    menuIcons = new int[10];
                    menuLabels = new String[10];
                    menuActions = new Action[10];
                }
            }

            if (isFolder) {
                menuIcons[0] = R.drawable.new_tab;
                menuLabels[0] = "Open in new tab";
                menuActions[0] = new Action() {
                    @Override
                    public void run() {

                        UIUtils.openNewTab(path);
                    }
                };
            }
            else {
                menuIcons[0] = R.drawable.open_with;
                menuLabels[0] = "Open file with...";
                menuActions[0] = new Action() {
                    @Override
                    public void run() {
                        FileUtils.openDocumentWith(path);
                    }
                };
            }

            menuIcons[1] = R.drawable.shortcut;
            menuLabels[1] = "Shortcut to desktop";
            menuActions[1] = new Action() {
                @Override
                public void run() {
                    FileUtils.createShortcutOf(title, path);
                }
            };

            menuIcons[2] = R.drawable.copy;
            menuLabels[2] = "Copy file";
            menuActions[2] = new Action() {
                @Override
                public void run() {
                    CbrdUtils.copy(path);
                    FileAdapter.this.updateSelectionStatus();
                }
            };

            menuIcons[3] = R.drawable.cut;
            menuLabels[3] = "Cut file";
            menuActions[3] = new Action() {
                @Override
                public void run() {
                    CbrdUtils.cut(path);
                    FileAdapter.this.updateSelectionStatus();
                }
            };

            menuIcons[4] = R.drawable.put;
            menuLabels[4] = "Move file";
            menuActions[4] = new Action() {
                @Override
                public void run() {
                    CbrdUtils.cut(path);
                    Intent intent = new Intent(Core.getInstance(), FileExplorerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("move_act", "true");
                    Core.getInstance().startActivity(intent);
                }
            };

            menuIcons[5] = R.drawable.delete;
            menuLabels[5] = "Delete file";
            menuActions[5] = new Action() {
                @Override
                public void run() {
                    CbrdUtils.delete(path, new OnCbrdTransactListener() {
                        @Override
                        public void fileTransacting(String path, int progress) {

                            DialogUtils.setFileTransactionMessage(path);
                            DialogUtils.setFileTransactionProgress(progress);
                        }

                        @Override
                        public void workingQueueDone() {

                            UIUtils.closeDialogBox();
                        }
                    });
                }
            };

            menuIcons[6] = R.drawable.rename;
            menuLabels[6] = "Rename file";
            menuActions[6] = new Action() {
                @Override
                public void run() {

                    UIUtils.showDialogBox(DialogUtils.getRenameDialogView(UIUtils.getLayoutInflater(), path, new OnFileRenameListener() {
                        @Override
                        public void renameFileTo(String oldPath, String newPath) {

                            if (isFolder) {
                                FileAdapter.this.renameFolder(oldPath, newPath);
                            }
                            else {
                                FileAdapter.this.renameDocument(oldPath, newPath);
                            }
                        }
                    }));
                }
            };

            menuIcons[7] = R.drawable.info;
            menuLabels[7] = "Properties";
            menuActions[7] = new Action() {
                @Override
                public void run() {

                    UIUtils.showDialogBox(DialogUtils.getPropertiesDialogView(UIUtils.getLayoutInflater(), path));
                }
            };

            menuIcons[8] = R.drawable.zip;
            menuLabels[8] = "Zip to desktop";
            menuActions[8] = new Action() {
                @Override
                public void run() {

                    FileUtils.zipFile(path, new File(new File(FileUtils.getPrimaryRootFolder(), "Desktop"), title.concat(".zip")).getPath());
                }
            };

            if (!isFolder) {

                menuIcons[9] = R.drawable.share;
                menuLabels[9] = "Share through...";
                menuActions[9] = new Action() {
                    @Override
                    public void run() {

                        if (targetPath.length() > 0) {
                            if (new File(targetPath).isDirectory()) {
                                return;
                            }
                        }

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri screenshotUri;

                        try {

                            if (targetPath.length() > 0) {

                                screenshotUri = Uri.fromFile(new File(targetPath));
                            }
                            else {
                                screenshotUri = Uri.fromFile(new File(path));
                            }

                            sharingIntent.setType(mimeType != null ? mimeType : "*/*");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                            Core.getInstance().startActivity(Intent.createChooser(sharingIntent, "Share...")
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        catch (Exception ignored) {

                            if (targetPath.length() > 0) {

                                screenshotUri = Uri.fromFile(new File(targetPath));
                            }
                            else {
                                screenshotUri = Uri.fromFile(new File(path));
                            }

                            sharingIntent.setType("*/*");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                            Core.getInstance().startActivity(Intent.createChooser(sharingIntent, "Share...")
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    }
                };

                if (mimeType != null && mimeType.startsWith("image")) {

                    menuIcons[10] = R.drawable.wallpaper;
                    menuLabels[10] = "Set as wallpaper";
                    menuActions[10] = new Action() {
                        @Override
                        public void run() {

                            ResUtils.setNewWallpaper(path);
                            UIUtils.showDialogBox(DialogUtils.getPendingTaskDialogView(UIUtils.getLayoutInflater(), "Changing wallpaper..."));
                        }
                    };
                }
                else if (mimeType != null && mimeType.startsWith("audio")) {

                    menuIcons[10] = R.drawable.ringtone;
                    menuLabels[10] = "Set as ringtone";
                    menuActions[10] = new Action() {
                        @Override
                        public void run() {

                            ResUtils.setNewRingtone(path);
                        }
                    };
                }
                else if (path.endsWith(".zip") || path.endsWith(".zip".concat(shortcutExtension))) {

                    menuIcons[10] = R.drawable.extract;
                    menuLabels[10] = "Extract zip file";
                    menuActions[10] = new Action() {
                        @Override
                        public void run() {

                            FileUtils.unzipFile(targetPath.length() > 0 ? targetPath : path, new File(FileUtils.getPrimaryRootFolder(), "Desktop").getAbsolutePath());
                        }
                    };
                }
            }

            holder.cxtMenuIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIUtils.showCxtMenu(holder.cxtMenuIV, menuIcons, menuLabels, menuActions);
                }
            });

            longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (CbrdUtils.isPathSelected(path)) {
                        CbrdUtils.deselectPath(path);
                        holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        CbrdUtils.selectPath(path);
                        holder.mainLayout.setBackgroundColor(Core.getInstance().getResources().getColor(R.color.colorAccent2));
                    }

                    return true;
                }
            };

            View.OnClickListener multiClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean anyThingSelected = false;

                    for (String path : foldersPaths) {
                        if (CbrdUtils.isPathSelected(path)) {
                            anyThingSelected = true;
                            break;
                        }
                    }
                    for (String path : docsPaths) {
                        if (CbrdUtils.isPathSelected(path)) {
                            anyThingSelected = true;
                            break;
                        }
                    }

                    if (anyThingSelected) {
                        if (CbrdUtils.isPathSelected(path)) {
                            CbrdUtils.deselectPath(path);
                            holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                        } else {
                            CbrdUtils.selectPath(path);
                            holder.mainLayout.setBackgroundColor(Core.getInstance().getResources().getColor(R.color.colorAccent2));
                        }
                    }
                    else {
                        if (targetPath.length() == 0) {
                            if (!CbrdUtils.isPathSelected(path)) {
                                FileUtils.openDocument(path);
                            }
                            else {
                                CbrdUtils.deselectPath(path);
                                holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                        else {
                            if (targetPath.contains("/")) {
                                if (!CbrdUtils.isPathSelected(path)) {
                                    FileUtils.openDocument(targetPath);
                                }
                                else {
                                    CbrdUtils.deselectPath(path);
                                    holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                                }
                            } else {
                                if (!CbrdUtils.isPathSelected(path)) {
                                    AppUtils.openApplication(targetPath);
                                }
                                else {
                                    CbrdUtils.deselectPath(path);
                                    holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
                                }
                            }
                        }
                    }
                }
            };

            holder.mainLayout.setOnClickListener(multiClickListener);

            holder.titleTV.setOnClickListener(clickListener);
            holder.detailsTV.setOnClickListener(clickListener);

            holder.iconIV.setOnClickListener(multiClickListener);
            holder.shortcutIV.setOnClickListener(multiClickListener);

            holder.mainLayout.setOnLongClickListener(longClickListener);
            holder.titleTV.setOnLongClickListener(longClickListener);
            holder.detailsTV.setOnLongClickListener(longClickListener);
            holder.iconIV.setOnLongClickListener(longClickListener);
            holder.shortcutIV.setOnLongClickListener(longClickListener);
        }

        private Bitmap loadIconOf(String path) {

            Bitmap icon = null;

            String mimeType = null;

            try {
                mimeType = FileUtils.getMimeType(path);
            }
            catch(Exception ignored) { }

            if (mimeType == null) {
                mimeType = "document";
            }

            if (mimeType.startsWith("video")) {
                icon = ThumbnailUtils.createVideoThumbnail(path, MICRO_KIND);
            }
            else if (mimeType.startsWith("audio")) {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(path);
                byte[] art = metaRetriever.getEmbeddedPicture();
                if (art != null) {
                    icon = BitmapFactory.decodeByteArray(art, 0, art.length);
                }
                else {
                    icon = null;
                }
            }

            return icon;
        }
    }
}