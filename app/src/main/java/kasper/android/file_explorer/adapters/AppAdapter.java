package kasper.android.file_explorer.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;
import java.util.HashSet;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.models.Action;
import kasper.android.file_explorer.models.App;
import kasper.android.file_explorer.utils.AppUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.adapters.base.BaseAdapter;

public class AppAdapter extends BaseAdapter implements SectionTitleProvider {

    private ArrayList<App> appsList;

    public AppAdapter(ArrayList<App> appsList) {

        this.appsList = new ArrayList<>(appsList);

        this.notifyDataSetChanged();
    }

    public void update(ArrayList<App> apps) {

        try {

            HashSet<App> currentAppsSet = new HashSet<>(appsList);
            HashSet<App> updateAppsSet = new HashSet<>(apps);

            ArrayList<App> currentFoldersList = appsList;

            this.appsList = new ArrayList<>(apps);

            int counter = 0;
            for (App currentAppTemp : currentFoldersList) {

                if (!updateAppsSet.contains(currentAppTemp)) {
                    notifyItemRemoved(counter);
                }
                else {
                    counter++;
                }
            }
            counter = 0;

            for (App updateAppTemp : apps) {

                if (!currentAppsSet.contains(updateAppTemp)) {
                    notifyItemInserted(counter);
                }
                else {
                    notifyItemChanged(counter);
                }

                counter++;
            }
        }
        catch (Exception ignored) { }
    }

    public void addApp(App app) {


    }

    public void deleteApp(App app) {


    }

    @Override
    public AppAdapter.AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new AppAdapter.AppHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_app, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ((AppAdapter.AppHolder)holder).iconIV.setImageDrawable(appsList.get(position).getIcon());
        ((AppAdapter.AppHolder)holder).labelTV.setText(appsList.get(position).getTitle());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeSoftKeyboard();
                AppUtils.openApplication(appsList.get(holder.getAdapterPosition()).getPackageName());
            }
        };

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int[] menuIcons = new int[3];
                String[] menuLabels = new String[3];
                Action[] menuActions = new Action[3];

                menuIcons[0] = R.drawable.info;
                menuLabels[0] = "Show Application Info";
                menuActions[0] = new Action() {
                    @Override
                    public void run() {
                        AppUtils.showAppInfo(appsList.get(holder.getAdapterPosition()).getPackageName());
                    }
                };

                menuIcons[1] = R.drawable.delete;
                menuLabels[1] = "UnInstall Application";
                menuActions[1] = new Action() {
                    @Override
                    public void run() {
                        AppUtils.uninstallApp(appsList.get(holder.getAdapterPosition()).getPackageName());
                    }
                };

                menuIcons[2] = R.drawable.shortcut;
                menuLabels[2] = "Shortcut to Desktop";
                menuActions[2] = new Action() {
                    @Override
                    public void run() {
                        FileUtils.createShortcutOf(appsList.get(position).getTitle(), appsList.get(position).getPackageName());
                    }
                };

                UIUtils.showCxtMenu(view, menuIcons, menuLabels, menuActions);

                return true;
            }
        };

        ((AppAdapter.AppHolder)holder).mainLayout.setOnClickListener(clickListener);
        ((AppAdapter.AppHolder)holder).iconIV.setOnClickListener(clickListener);
        ((AppAdapter.AppHolder)holder).labelTV.setOnClickListener(clickListener);

        ((AppAdapter.AppHolder)holder).mainLayout.setOnLongClickListener(longClickListener);
        ((AppAdapter.AppHolder)holder).iconIV.setOnLongClickListener(longClickListener);
        ((AppAdapter.AppHolder)holder).labelTV.setOnLongClickListener(longClickListener);
    }

    @Override
    public int getItemCount() { return this.appsList.size(); }

    @Override
    public String getSectionTitle(int position) {

        return this.appsList.get(position).getTitle().substring(0, 1).toUpperCase();
    }

    @Override
    public void cleanAdapter() {

        this.appsList = new ArrayList<>();
        this.notifyDataSetChanged();
    }

    class AppHolder extends RecyclerView.ViewHolder {

        LinearLayout mainLayout;
        ImageView iconIV;
        TextView labelTV;

        AppHolder(View itemView) {

            super(itemView);

            mainLayout = (LinearLayout) itemView.findViewById(R.id.adapter_app_main_layout);
            iconIV = (ImageView) itemView.findViewById(R.id.adapter_app_icon_image_view);
            labelTV = (TextView) itemView.findViewById(R.id.adapter_app_title_text_view);
        }
    }
}