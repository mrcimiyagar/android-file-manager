package kasper.android.file_explorer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.listeners.OnRocketRequestListener;
import kasper.android.file_explorer.utils.UIUtils;

public class RocketAdapter extends RecyclerView.Adapter<RocketAdapter.Holder> {

    OnRocketRequestListener rocketRequestListener;

    public RocketAdapter(OnRocketRequestListener rocketRequestListener) {

        this.rocketRequestListener = rocketRequestListener;
        this.notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rocket, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        switch (position) {

            case 0: {

                holder.iconIV.setImageResource(R.drawable.storage_light);
                holder.titleTV.setText("Primary");

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UIUtils.closeDialogBox();
                        rocketRequestListener.gotoPrimary();
                    }
                });

                break;
            }
            case 1: {

                holder.iconIV.setImageResource(R.drawable.storage_light);
                holder.titleTV.setText("Secondary");

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UIUtils.closeDialogBox();
                        rocketRequestListener.gotoSecondary();
                    }
                });

                break;
            }
            case 2: {

                holder.iconIV.setImageResource(R.drawable.photo_light);
                holder.titleTV.setText("Photos");

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UIUtils.closeDialogBox();
                        rocketRequestListener.gotoPhotos();
                    }
                });

                break;
            }
            case 3: {

                holder.iconIV.setImageResource(R.drawable.music_light);
                holder.titleTV.setText("Musics");

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UIUtils.closeDialogBox();
                        rocketRequestListener.gotoMusics();
                    }
                });

                break;
            }
            case 4: {

                holder.iconIV.setImageResource(R.drawable.audio_light);
                holder.titleTV.setText("Videos");

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UIUtils.closeDialogBox();
                        rocketRequestListener.gotoVideos();
                    }
                });

                break;
            }
            case 5: {

                holder.iconIV.setImageResource(R.drawable.app_light);
                holder.titleTV.setText("Apps");

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UIUtils.closeDialogBox();
                        rocketRequestListener.gotoApps();
                    }
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {

        return 6;
    }

    class Holder extends RecyclerView.ViewHolder {

        LinearLayout mainLayout;
        ImageView iconIV;
        TextView titleTV;

        Holder(View itemView) {

            super(itemView);

            mainLayout = (LinearLayout) itemView.findViewById(R.id.adapter_rocket_main_layout);
            iconIV = (ImageView) itemView.findViewById(R.id.adapter_rocket_icon_image_view);
            titleTV = (TextView) itemView.findViewById(R.id.adapter_rocket_title_text_view);
        }
    }
}