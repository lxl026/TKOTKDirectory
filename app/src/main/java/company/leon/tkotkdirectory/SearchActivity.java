package company.leon.tkotkdirectory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import org.litepal.crud.DataSupport;

import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class SearchActivity extends AppCompatActivity {

    SearchView mSearchView;



    TextView ResultLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //返回图标
        ImageView back = (ImageView) findViewById(R.id.searchBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //搜索结果提示
        ResultLog = (TextView)findViewById(R.id.ResultLog);


        mSearchView = (SearchView) findViewById(R.id.searchView);




        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                //RecyclerView初始化
                final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.searchResult);
                LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(layoutManager);

                final List<Hero> Heros = DataSupport.where("name like ? or nationality like ? or sex like ? or origin like ? or birth like ? ",
                        "%" + query+ "%","%" + query+ "%","%" + query+ "%","%" + query+ "%","%" + query+ "%").find(Hero.class);

                final BaseRecyclerAdapter heroAdapter = new BaseRecyclerAdapter<Hero>(SearchActivity.this,Heros,R.layout.hero_list){
                    @Override
                    public void convert(BaseRecyclerHolder holder, Hero hero) {
                        ImageView n = holder.getView(R.id.HeroPicture);
                        if(hero.getId()<11){
                            RequestManager glideRequest;
                            glideRequest = Glide.with(getContext());
                            glideRequest.load(hero.getPicture())
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.drawable.ic_error_36pt_3x)
                                    .crossFade()
                                    .transform(new CenterCrop(getContext())
                                            ,new GlideRoundTransform(getContext(), 10))
                                    .into(n);
                        }else {
                            RequestManager glideRequest;
                            glideRequest = Glide.with(getContext());
                            glideRequest.load(hero.getPictureSource())
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.drawable.ic_error_36pt_3x)
                                    .crossFade()
                                    .transform(new CenterCrop(getContext())
                                            ,new GlideRoundTransform(getContext(), 10))
                                    .into(n);
                        }
                        TextView t = holder.getView(R.id.HeroName);
                        t.setText(hero.getName());
                    }
                };
                heroAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onClick( int position) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        int id = Heros.get(position).getId();
                        //TODO:跳转到详情界面
                        Intent intent1 = new Intent(SearchActivity.this,DetailActivity.class);
                        intent1.putExtra("id",id);
                        startActivity(intent1);
                        //Toast.makeText(getContext(),"点击了"+position,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongClick(final int position) {
                        //final int[] choice = {0};
                        AlertDialog.Builder alterDialog = new AlertDialog.Builder(SearchActivity.this);
                        alterDialog.setCancelable(true);

                        alterDialog.setTitle("删除")
                                .setMessage("要移除" + Heros.get(position).getName() + "吗?\n取消请按返回键")
                                .setNegativeButton("仅从列表删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, final int p) {
                                        heroAdapter.delete(position);
                                    }
                                })
                                .setPositiveButton("彻底删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, final int p) {
                                        DataSupport.delete(Hero.class, Heros.get(position).getId());
                                        heroAdapter.delete(position);
                                    }
                                })
                                .create()
                                .show();
                        //Toast.makeText(getContext(),"点击了"+position,Toast.LENGTH_LONG).show();
                    }
                });
                recyclerView.setAdapter(heroAdapter);
                recyclerView.setVisibility(View.VISIBLE);
                ResultLog.setVisibility(View.VISIBLE);
                if(Heros.size() == 0)ResultLog.setText("无匹配结果！");
                ResultLog.setText("共搜索到"+Heros.size()+"个结果");
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        //设置搜索关闭监听
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });
    }
}
