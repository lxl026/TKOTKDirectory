package company.leon.tkotkdirectory;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by Leon on 2017/11/19.
 */

public class NationAdapter extends RecyclerView.Adapter<NationAdapter.ViewHolder> {
    private List<String> NationList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView NationImage;

        public ViewHolder(View view){
            super(view);
            NationImage = (ImageView) view.findViewById(R.id.Nation);
        }
    }

    public NationAdapter(List<String> SL){
        NationList = SL;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nation_list,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String nation = NationList.get(position);
        Glide.with(getContext())
                .load(nation)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .centerCrop()
                .into(new GlideDrawableImageViewTarget(holder.NationImage));
    }

    @Override
    public int getItemCount() {
        return NationList.size();
    }
    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }
}

