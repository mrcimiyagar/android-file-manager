package kasper.android.file_explorer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.blocks.base.BlockTypes;
import kasper.android.file_explorer.blocks.drivers.AppsDriver;
import kasper.android.file_explorer.blocks.drivers.DeskDriver;
import kasper.android.file_explorer.blocks.drivers.MusicsDriver;
import kasper.android.file_explorer.blocks.drivers.PStorageDriver;
import kasper.android.file_explorer.blocks.drivers.PhotosDriver;
import kasper.android.file_explorer.blocks.drivers.SStorageDriver;
import kasper.android.file_explorer.blocks.drivers.VideosDriver;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockHolder> {

    Context context;

    LayoutInflater inflater;

    ArrayList<BlockTypes> blocksList;

    public BlockAdapter(Context context, LayoutInflater inflater, ArrayList<BlockTypes> blocksList) {

        this.context = context;
        this.inflater = inflater;
        this.blocksList = blocksList;

        this.notifyDataSetChanged();
    }

    public void addBlock(BlockTypes block) {

        this.blocksList.add(block);

        if (this.blocksList.size() > 1) {
            this.notifyItemChanged(this.blocksList.size() - 2);
        }

        this.notifyItemInserted(this.blocksList.size() - 1);
    }

    @Override
    public BlockHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new BlockHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_block, parent, false));
    }

    @Override
    public void onBindViewHolder(BlockHolder holder, int position) {

        BlockTypes block = blocksList.get(position);

        switch (block) {
            case AppsBlock: {

                new AppsDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
            case PhotosBlock: {

                new PhotosDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
            case MusicsBlock: {

                new MusicsDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
            case VideosBlock: {

                new VideosDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
            case PStorageBlock: {

                new PStorageDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
            case SStorageBlock: {

                new SStorageDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
            case DeskBlock: {

                new DeskDriver().fillData(holder.blockView, holder.blockIconIV, holder.dataPart1TV, holder.dataPart2TV);

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return blocksList.size();
    }

    public class BlockHolder extends RecyclerView.ViewHolder {

        View blockView;
        ImageView blockIconIV;
        TextView dataPart1TV;
        TextView dataPart2TV;

        public BlockHolder(View itemView) {

            super(itemView);

            blockView = itemView;
            blockIconIV = (ImageView) itemView.findViewById(R.id.adapter_block_icon_image_view);
            dataPart1TV = (TextView) itemView.findViewById(R.id.adapter_block_data_part_1_text_view);
            dataPart2TV = (TextView) itemView.findViewById(R.id.adapter_block_data_part_2_text_view);
        }
    }
}