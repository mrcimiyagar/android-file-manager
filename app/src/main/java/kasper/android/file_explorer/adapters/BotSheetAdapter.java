package kasper.android.file_explorer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.models.Action;

public class BotSheetAdapter extends RecyclerView.Adapter<BotSheetAdapter.BotSheetHolder> {

    int[] icons;
    String[] labels;
    Action[] actions;
    Runnable onItemClickCallback;

    public BotSheetAdapter(int[] icons, String[] labels, Action[] actions, Runnable onItemClickCallback) {
        this.icons = icons;
        this.labels = labels;
        this.actions = actions;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public BotSheetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BotSheetHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bottom_sheet, parent, false));
    }

    @Override
    public void onBindViewHolder(BotSheetHolder holder, final int position) {
        holder.titleTV.setText(labels[position]);
        holder.iconIV.setImageResource(icons[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.run();
                actions[position].run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return labels.length;
    }


    class BotSheetHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView iconIV;
        TextView titleTV;

        BotSheetHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.iconIV = (ImageView) itemView.findViewById(R.id.adapter_bottom_sheet_icon_image_view);
            this.titleTV = (TextView) itemView.findViewById(R.id.adapter_bottom_sheet_label_text_view);
        }
    }
}