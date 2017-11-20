package company.leon.tkotkdirectory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends AppCompatActivity {

    private final String WUGUO =  "https://wx3.sinaimg.cn/mw690/006L5CY2ly1fers1iga5yg30an05zkjl.gif";
    private final String SHUGUO = "https://wx3.sinaimg.cn/mw690/006L5CY2ly1fers1ddfo5g30an05z4qq.gif";
    private final String WEIGUO = "https://wx3.sinaimg.cn/mw690/006L5CY2ly1fers1gbm2mg30an05zb2a.gif";

    private boolean isHerosVisiable = false;
    private DrawerLayout mDrawerLayout;//滑动布局
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //初始化三个国家的图案，包括点击事件
        initNationRecyclerView();

        //toolBar初始化
        initToolbar();

        //初始化mDrawerLayout
        initDrawerLayout();

        //判断是不是第一次运行，如果是就初始化数据库
        if(isFirstRun()) initDB();

    }

    //ToolBar后续工作
    //导入menu，和toolbar进行绑定
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    //对toolBar中的item进行点击监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(searchIntent,1);
                break;
            case R.id.addhero:
                Toast.makeText(this,"TEST",Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    //判断是不是第一次运行
    boolean isFirstRun(){
        SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
        boolean isFirstRun = read.getBoolean("isFirstRun",true);
        if(isFirstRun){
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("isFirstRun",false);
            editor.apply();
        }
        System.out.println(isFirstRun);
        return isFirstRun;
    }

    //初始化数据库,同时记录当前数据库中有多少人物
    void initDB(){
        Log.d("DataBase","Initial");
        Connector.getDatabase();
        Hero GuanYu=new Hero("蜀","关羽");
        GuanYu.setSex("男");
        GuanYu.setOrigin("河东郡解县(山西运城市临猗县)");
        GuanYu.setBirth("？ - 220");
        GuanYu.setPicture(R.drawable.guanyu);
        GuanYu.setId(0);
        GuanYu.save();

        Hero LiuBei=new Hero("蜀","刘备");
        LiuBei.setSex("男");
        LiuBei.setBirth("161 - 223");
        LiuBei.setPicture(R.drawable.liubei);
        LiuBei.setOrigin("幽州涿县（今河北省涿州市）");
        LiuBei.setId(1);
        LiuBei.save();

        Hero ZhangFei=new Hero("蜀","张飞");
        ZhangFei.setOrigin("幽州涿县（今河北省涿州市）");
        ZhangFei.setPicture(R.drawable.zhangfei);
        ZhangFei.setBirth("？ - 221");
        ZhangFei.setSex("男");
        ZhangFei.setId(2);
        ZhangFei.save();

        Hero CsiYan=new Hero("魏","蔡琰");
        CsiYan.setBirth("？ - ？");
        CsiYan.setSex("女");
        CsiYan.setOrigin("陈留郡圉县（今河南开封杞县）");
        CsiYan.setPicture(R.drawable.caiwenji);
        CsiYan.setId(3);
        CsiYan.save();

        Hero FaZheng=new Hero("蜀","法正");
        FaZheng.setOrigin("扶风郿（今陕西省眉县）");
        FaZheng.setPicture(R.drawable.fazheng);
        FaZheng.setSex("男");
        FaZheng.setBirth("176 - 220");
        FaZheng.setId(4);
        FaZheng.save();

        Hero SunCe=new Hero("吴","孙策");
        SunCe.setBirth("175 - 200");
        SunCe.setOrigin("吴郡富春（今浙江富阳）");
        SunCe.setPicture(R.drawable.sunce);
        SunCe.setSex("男");
        SunCe.setId(5);
        SunCe.save();

        Hero SunShangxiang=new Hero("蜀","孙尚香");
        SunShangxiang.setBirth("？ - ？");
        SunShangxiang.setOrigin("吴郡富春（今浙江富阳）");
        SunShangxiang.setSex("女");
        SunShangxiang.setPicture(R.drawable.sunshangxiang);
        SunShangxiang.setId(6);
        SunShangxiang.save();

        Hero LvMeng=new Hero("吴","吕蒙");
        LvMeng.setOrigin("汝南富陂（今安徽阜南吕家岗）");
        LvMeng.setPicture(R.drawable.lvmeng);
        LvMeng.setBirth("179 - 220");
        LvMeng.setSex("男");
        LvMeng.setId(7);
        LvMeng.save();

        Hero SimaYi=new Hero("魏","司马懿");
        SimaYi.setOrigin("河内郡温县（今河南省焦作市）");
        SimaYi.setSex("男");
        SimaYi.setBirth("179 - 251");
        SimaYi.setPicture(R.drawable.simayi);
        SimaYi.setId(8);
        SimaYi.save();

        Hero Caocao=new Hero("魏","曹操");
        Caocao.setBirth("155 - 220");
        Caocao.setOrigin("沛国谯县（今安徽亳州）");
        Caocao.setPicture(R.drawable.caocao);
        Caocao.setSex("男");
        Caocao.setId(9);
        Caocao.save();

        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putInt("HeroNum",10);
        editor.apply();
    }

    //初始化toolbar
    void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//设置菜单图片
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
    }

    //初始化DrawerLayout
    void initDrawerLayout(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    //初始化三个国家的图案，包括点击事件
    void initNationRecyclerView(){
        List<String> NationImageResource = new ArrayList<>();
        NationImageResource.add(WEIGUO);
        NationImageResource.add(SHUGUO);
        NationImageResource.add(WUGUO);
        BaseRecyclerAdapter nationAdapter;
        nationAdapter = new BaseRecyclerAdapter<String>(this,NationImageResource,R.layout.nation_list) {
            @Override
            public void convert(BaseRecyclerHolder holder, String nation) {
                ImageView n = holder.getView(R.id.Nation);
                Glide.with(getContext())
                        .load(nation)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .crossFade()
                        .centerCrop()
                        .into(new GlideDrawableImageViewTarget(n,1));
            }
        };
        nationAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick( int position) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.HeroRecyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(layoutManager);
                if(isHerosVisiable){
                    recyclerView.setVisibility(View.INVISIBLE);
                    isHerosVisiable = false;
                }
                else {
                    List<Hero> Heros;
                    switch (position){
                        case 0: Heros = DataSupport.where("nationality = ?","魏").find(Hero.class);break;
                        case 1: Heros = DataSupport.where("nationality = ?","蜀").find(Hero.class);break;
                        case 2: Heros = DataSupport.where("nationality = ?","吴").find(Hero.class);break;
                        default:Heros = DataSupport.findAll(Hero.class);break;
                    }
                    HeroAdapter heroAdapter = new HeroAdapter(Heros);
                    recyclerView.setAdapter(heroAdapter);
                    recyclerView.addItemDecoration(new HeroAdapter.SpacesItemDecoration(1));
                    recyclerView.setVisibility(View.VISIBLE);
                    isHerosVisiable = true;
                }
                //Toast.makeText(getContext(),"点击了"+position,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onLongClick(int position) {
                Toast.makeText(getContext(),"点击了"+position,Toast.LENGTH_LONG).show();
            }
        });
        RecyclerView NationRecyclerView = (RecyclerView) findViewById(R.id.NationRecyclerView);
        NationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //BaseRecyclerAdapter nAdapter = new NationAdapter(NationImageResource);
        NationRecyclerView.setAdapter(nationAdapter);
    }


}
