package company.leon.tkotkdirectory;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by Leon on 2017/11/20.
 */

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.ViewHolder> {
    private List<Hero> HerosList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View HeroView;
        ImageView HeroPicture;
        TextView HeroName;

        public ViewHolder(View view){
            super(view);
            HeroView = view;
            HeroPicture = (ImageView) view.findViewById(R.id.HeroPicture);
            HeroName = (TextView) view.findViewById(R.id.HeroName);
        }
    }

    public HeroAdapter(List<Hero> HL){
        HerosList = HL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hero_list,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.HeroView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Hero hero = HerosList.get(position);
                Toast.makeText(v.getContext(),"点击了"+hero.getName()+"，应该实现弹出详情界面\n到HeroAdapter.java53行实现",Toast.LENGTH_LONG).show();
            }
        });
        holder.HeroView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                Hero hero = HerosList.get(position);
                Toast.makeText(v.getContext(),"长按了"+hero.getName()+"，应该实现弹出删除提示\n到HeroAdapter.java61行实现",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hero hero = HerosList.get(position);
        holder.HeroName.setText(hero.getName());
        if(hero.getId()>10){
            RequestManager glideRequest;
            glideRequest = Glide.with(getContext());
            glideRequest.load(hero.getPictureSource())
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .transform(new CenterCrop(getContext())
                            ,new GlideRoundTransform(getContext(), 10))
                    .into(holder.HeroPicture);
        }else {
            RequestManager glideRequest;
            glideRequest = Glide.with(getContext());
            glideRequest.load(hero.getPicture())
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .transform(new CenterCrop(getContext())
                            ,new GlideRoundTransform(getContext(), 10))
                    .into(holder.HeroPicture);
        }
    }

    @Override
    public int getItemCount() {
        return HerosList.size();
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
