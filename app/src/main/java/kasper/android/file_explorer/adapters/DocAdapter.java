package kasper.android.file_explorer.adapters;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnAdapterRequestListener;
import kasper.android.file_explorer.models.Action;
import kasper.android.file_explorer.models.files.Doc;
import kasper.android.file_explorer.models.files.DocTypes;
import kasper.android.file_explorer.utils.DialogUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.HWareUtils;
import kasper.android.file_explorer.utils.ResUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.adapters.base.BaseAdapter;

import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;

public class DocAdapter extends BaseAdapter implements SectionTitleProvider {

    final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private ArrayList<Doc> docs;

    private OnAdapterRequestListener adapterRequestListener;

    public DocAdapter(ArrayList<Doc> docs, OnAdapterRequestListener adapterRequestListener) {

        this.docs = new ArrayList<>(docs);

        this.adapterRequestListener = adapterRequestListener;

        this.notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Doc> docs) {

        try {

            HashSet<Doc> currentDocsSet = new HashSet<>(this.docs);
            HashSet<Doc> updateDocsSet = new HashSet<>(docs);

            ArrayList<Doc> currentDocsList = this.docs;

            this.docs = new ArrayList<>(docs);

            int counter = 0;

            for (Doc currentDocTemp : currentDocsList) {

                if (!updateDocsSet.contains(currentDocTemp)) {
                    notifyItemRemoved(counter);
                }
                else {
                    counter++;
                }
            }

            counter = 0;

            for (Doc updateDocTemp : docs) {

                if (!currentDocsSet.contains(updateDocTemp)) {
                    notifyItemInserted(counter);
                } else {
                    notifyItemChanged(counter);
                }

                counter++;
                adapterRequestListener.scrollToTop();
            }
        }
        catch (Exception ignored) { }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_doc, parent, false));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        ((DocAdapter.Holder)holder).cleanup();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Doc doc = docs.get(position);

        if (doc.getDocType() == DocTypes.Photo) {

            Glide.with(Core.getInstance()).load(new File(doc.getPath())).listener(new RequestListener<File, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {

                    imageNotLoaded(((DocAdapter.Holder)holder), DocTypes.Photo);

                    return true;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                    ((DocAdapter.Holder)holder).iconIV.setImageDrawable(resource);

                    imageLoaded(((DocAdapter.Holder)holder));

                    return true;
                }
            }).fitCenter().crossFade().into(((DocAdapter.Holder)holder).iconIV);
        }
        else if (doc.getDocType() == DocTypes.Music) {

            Glide.with(Core.getInstance()).load(getCoverPath(doc.getTag())).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {

                    imageNotLoaded(((DocAdapter.Holder)holder), DocTypes.Music);

                    return true;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                    ((DocAdapter.Holder)holder).iconIV.setImageDrawable(resource);

                    imageLoaded(((DocAdapter.Holder)holder));

                    return true;
                }
            }).fitCenter().crossFade().into(((DocAdapter.Holder)holder).iconIV);
        }
        else {
            new DocImageLoader().execute(holder, doc);
        }

        ((DocAdapter.Holder)holder).titleTV.setText(doc.getTitle());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.openDocument(doc.getPath());
            }
        };

        ((DocAdapter.Holder)holder).mainLayout.setOnClickListener(clickListener);
        //((DocAdapter.Holder)holder).iconLayout.setOnClickListener(clickListener);
        ((DocAdapter.Holder)holder).iconIV.setOnClickListener(clickListener);
        ((DocAdapter.Holder)holder).titleTV.setOnClickListener(clickListener);

        final int[] menuIcons;
        final String[] menuLabels;
        final Action[] menuActions;

        if (doc.getDocType() == DocTypes.Photo) {
            menuIcons = new int[4];
            menuLabels = new String[4];
            menuActions = new Action[4];
        }
        else {
            menuIcons = new int[3];
            menuLabels = new String[3];
            menuActions = new Action[3];
        }

        menuIcons[0] = R.drawable.open_with;
        menuLabels[0] = "Open with...";
        menuActions[0] = new Action() {
            @Override
            public void run() {
                FileUtils.openDocumentWith(doc.getPath());
            }
        };

        menuIcons[1] = R.drawable.shortcut;
        menuLabels[1] = "Shortcut to desktop";
        menuActions[1] = new Action() {
            @Override
            public void run() {
                FileUtils.createShortcutOf(doc.getTitle(), doc.getPath());
            }
        };

        menuIcons[2] = R.drawable.open_location;
        menuLabels[2] = "Open Location";
        menuActions[2] = new Action() {
            @Override
            public void run() {
                String parentPath = doc.getPath().substring(0, doc.getPath().lastIndexOf("/"));
                FileUtils.openDocument(parentPath);
            }
        };

        if (doc.getDocType() == DocTypes.Photo) {
            menuIcons[3] = R.drawable.wallpaper;
            menuLabels[3] = "Set as wallpaper";
            menuActions[3] = new Action() {
                @Override
                public void run() {

                    ResUtils.setNewWallpaper(doc.getPath());
                    UIUtils.showDialogBox(DialogUtils.getPendingTaskDialogView(UIUtils.getLayoutInflater(), "Changing wallpaper..."));
                }
            };
        }

        ((Holder) holder).cxtMenuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UIUtils.showCxtMenu(((Holder) holder).mainLayout, menuIcons, menuLabels, menuActions);
            }
        });
    }

    private Uri getCoverPath(long albumId) {

        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public void imageLoaded(Holder holder) {

        holder.iconIV.setPadding(0, 0, 0, 0);
        //holder.iconLayout.setPadding(0, 0, 0, 0);
        holder.iconIV.setScaleType(ImageView.ScaleType.FIT_XY);

        /*holder.iconIV.setPadding(0, 0, 0, 0);
        int layoutPadding = (int) (4 * HWareUtils.getScreenDensity());
        holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);*/
    }

    public void imageNotLoaded(Holder holder, DocTypes docType) {

        int padding = (int) (12 * HWareUtils.getScreenDensity());
        holder.iconIV.setPadding(padding, padding, padding, padding);
        int layoutPadding = (int) (8 * HWareUtils.getScreenDensity());
        //holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);
        holder.iconIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //holder.iconIV.setImageResource(R.drawable.audio_dark);

        /*int padding = (int) (12 * HWareUtils.getScreenDensity());
        holder.iconIV.setPadding(padding, padding, padding, padding);
        int layoutPadding = (int) (8 * HWareUtils.getScreenDensity());
        holder.iconLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);*/

        holder.iconIV.setImageResource(docType == DocTypes.Photo ? R.drawable.photo_dark : (docType
                == DocTypes.Music ? R.drawable.music_dark : R.drawable.audio_dark));
    }

    @Override
    public int getItemCount() { return this.docs.size(); }

    @Override
    public String getSectionTitle(int position) {

        Doc doc = docs.get(position);
        String title = "";
        int index = doc.getPath().lastIndexOf("/");
        if (index > 0 && index < doc.getPath().length() - 1) {
            title = doc.getPath().substring(index + 1);
        }
        return title.length() > 0 ? title.substring(0, 1).toUpperCase() : "";
    }

    @Override
    public void cleanAdapter() {

        this.docs = new ArrayList<>();
        this.notifyDataSetChanged();
    }

    class DocImageLoader extends AsyncTask<Object, Void, Bitmap> {

        DocAdapter.Holder holder;
        Doc doc;

        @Override
        protected Bitmap doInBackground(Object... params) {

            holder = (Holder) params[0];
            doc = (Doc) params[1];

            holder.imageTask = this;

            Bitmap result = null;

            if (doc.getDocType() == DocTypes.Movie) {

                result = ThumbnailUtils.createVideoThumbnail(doc.getPath(), MICRO_KIND);
            }

            holder.imageTask = null;

            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {

                holder.iconIV.setImageBitmap(bitmap);

                imageLoaded(holder);
            }
            else if (doc.getDocType() == DocTypes.Movie) {

                imageNotLoaded(holder, doc.getDocType());
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        RelativeLayout mainLayout;
        //RelativeLayout iconLayout;
        TextView titleTV;
        ImageView iconIV;
        ImageView cxtMenuIV;

        DocImageLoader imageTask;

        Holder(View itemView) {

            super(itemView);

            mainLayout = itemView.findViewById(R.id.adapter_doc_main_layout);
            //iconLayout = itemView.findViewById(R.id.adapter_doc_icon_layout);
            iconIV = itemView.findViewById(R.id.adapter_doc_icon_image_view);
            titleTV = itemView.findViewById(R.id.adapter_doc_title_text_view);
            cxtMenuIV = itemView.findViewById(R.id.adapter_doc_context_menu_image_view);
        }

        void cleanup() {

            if (imageTask != null) {
                imageTask.cancel(true);
            }
            else {
                Glide.clear(this.iconIV);
            }
        }
    }
}