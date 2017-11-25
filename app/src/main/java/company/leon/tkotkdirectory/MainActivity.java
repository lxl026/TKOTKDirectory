package company.leon.tkotkdirectory;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends AppCompatActivity {

    //图片地址
    private final String WUGUO =  "https://wx3.sinaimg.cn/mw690/006L5CY2ly1fers1iga5yg30an05zkjl.gif";
    private final String SHUGUO = "https://wx3.sinaimg.cn/mw690/006L5CY2ly1fers1ddfo5g30an05z4qq.gif";
    private final String WEIGUO = "https://wx3.sinaimg.cn/mw690/006L5CY2ly1fers1gbm2mg30an05zb2a.gif";

    //人物recycler View是否可见
    private boolean isHerosVisiable = false;
    private DrawerLayout mDrawerLayout;//滑动布局


    //弹出的对话框（修改界面）中的图片
    private ImageView hero_image;
    //调用相机时的一些量的定义
    public static final int TAKE_PHOTO = 1;//启用相机的标志
    private static final int CHOOSE_PHOTO =2;//启用相册的标志
    private Uri imageUri;//启用相机拍照之后图片的存储路径，通过此路径来找到图片

    private String ImagePath;//拍照存储或选择相册得到的文件路径

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
        List<Hero> test =DataSupport.findAll(Hero.class);
        for(int i=0;i<test.size();i++)
        {
            System.out.println(test.get(i).getName()+"  "+test.get(i).getId()+"*********************");
        }

        SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
        boolean bgm = read.getBoolean("bgm",false);
        if(bgm){
            Intent intent = new Intent(MainActivity.this,BackgroundMusicService.class);
            startService(intent);
            //item.setIcon(R.drawable.volumehigh);
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("bgm",true);
            editor.apply();
        }

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
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(searchIntent, 1);
                break;
            case R.id.addhero:
                addHeroDialog();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    //判断是不是第一次运行,同时记录当前数据库中有多少人物
    boolean isFirstRun(){
        SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
        boolean isFirstRun = read.getBoolean("isFirstRun",true);
        if(isFirstRun){
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("isFirstRun",false);
            editor.putBoolean("bgm",true);
            editor.putInt("HeroNum",10);//初始10个人物
            editor.apply();
        }
        System.out.println(isFirstRun);
        return isFirstRun;
    }

    //初始化数据库
    void initDB(){
        Log.d("DataBase","Initial");
        Connector.getDatabase();
        Hero GuanYu=new Hero("蜀","关羽");
        GuanYu.setSex("男");
        GuanYu.setOrigin("河东郡解县");
        GuanYu.setBirth("？ - 220");
        GuanYu.setPicture(R.drawable.guanyu);
        GuanYu.setExperience("    因当地势豪倚势凌人，杀之而逃难江湖，改名性关。与刘备、张飞桃园结义，关羽居其次。虎牢关温酒斩华雄，屯土山降汉不降曹。为报恩斩颜良、诛文丑，解曹操白马之围。后得知刘备音信，过五关斩六将，千里寻兄。刘备平定益州后，封关羽为五虎大将之首，督荆州事。关羽起军攻曹，水淹七军，威震华夏。围樊城右臂中箭，幸得华佗医治，刮骨疗伤。但因大意，关羽父子败走麦城，不屈遭害。");
        GuanYu.setId(0);

        GuanYu.save();

        Hero LiuBei=new Hero("蜀","刘备");
        LiuBei.setSex("男");
        LiuBei.setBirth("161 - 223");
        LiuBei.setPicture(R.drawable.liubei);
        LiuBei.setOrigin("幽州涿县");
        LiuBei.setId(1);
        LiuBei.setExperience("     刘备少年时拜卢植为师；早年颠沛流离，备尝艰辛，投靠过多个诸侯，曾参与镇压黄巾起义。先后率军救援北海相孔融、徐州牧陶谦等。陶谦病亡后，将徐州让与刘备。赤壁之战时，刘备与孙权联盟击败曹操，趁势夺取荆州。而后进取益州。于章武元年（221年）在成都称帝，国号汉，史称蜀或蜀汉。《三国志》评刘备的机权干略不及曹操，但其弘毅宽厚，知人待士，百折不挠，终成帝业。刘备也称自己做事“每与操反，事乃成尔”。");
        LiuBei.save();

        Hero ZhangFei=new Hero("蜀","张飞");
        ZhangFei.setOrigin("幽州涿县");
        ZhangFei.setPicture(R.drawable.zhangfei);
        ZhangFei.setBirth("？ - 221");
        ZhangFei.setSex("男");
        ZhangFei.setExperience("    三国时期蜀汉名将。刘备长坂坡败退，张飞仅率二十骑断后，据水断桥，曹军没人敢逼近；与诸葛亮、赵云扫荡西川时，于江州义释严颜；汉中之战时又于宕渠击败张郃，对蜀汉贡献极大，官至车骑将军、领司隶校尉，封西乡侯，后被范强、张达刺杀。后主时代追谥为“桓侯”。在中国传统文化中，张飞以其勇猛、鲁莽、嫉恶如仇而著称，虽然此形象主要来源于小说和戏剧等民间艺术，但已深入人心。");
        ZhangFei.setId(2);

        ZhangFei.save();

        Hero CsiYan=new Hero("魏","蔡琰");
        CsiYan.setBirth("？ - ？");
        CsiYan.setSex("女");
        CsiYan.setOrigin("陈留郡圉县");
        CsiYan.setPicture(R.drawable.caiwenji);
        CsiYan.setId(3);
        CsiYan.setExperience("    字文姬，汉末女诗人，蔡邕之女。初嫁河东卫仲道。后为北方虏走，归南匈奴左贤王，居匈奴十二年。期间生二子，作《胡笳十八拍》。曹操念蔡邕无后，以千金赎回，再嫁董祀。");
        CsiYan.save();

        Hero FaZheng=new Hero("蜀","法正");
        FaZheng.setOrigin("扶风郿");
        FaZheng.setPicture(R.drawable.fazheng);
        FaZheng.setSex("男");
        FaZheng.setBirth("176 - 220");
        FaZheng.setId(4);
        FaZheng.setExperience("    东汉末年刘备帐下谋士，名士法真之孙。原为刘璋部下，刘备围成都时劝说刘璋投降，而后又与刘备进取汉中，献计将曹操大将夏侯渊斩首。法正善奇谋，深受刘备信任和敬重。建安二十四年（219年），刘备进位汉中王，封法正为尚书令、护军将军。次年，法正去世，终年四十五岁。法正之死令刘备十分感伤，连哭数日。被追谥为翼侯，是刘备时代唯一一位有谥号的大臣。法正善于奇谋，被陈寿称赞为可比曹操帐下的程昱和郭嘉。");
        FaZheng.save();

        Hero SunCe=new Hero("吴","孙策");
        SunCe.setBirth("175 - 200");
        SunCe.setOrigin("吴郡富春");
        SunCe.setPicture(R.drawable.sunce);
        SunCe.setSex("男");
        SunCe.setId(5);
        SunCe.setExperience("    破虏将军孙坚长子、吴大帝孙权长兄。东汉末年割据江东一带的军阀，汉末群雄之一，三国时期孙吴的奠基者之一。《三国演义》称其武勇犹如霸王项羽，绰号“小霸王”。孙策为将，有智有勇，英姿勃发，其治军严整，军纪严明。但在征战中由于年轻气盛，难免出现处事不慎、好勇斗狠的弱点，这为其结怨和遇刺种下了祸根。建安五年（200年）4月，正当孙策准备发兵北上之时，在丹徒狩猎中为刺客所伤，不久后身亡，年仅二十六岁。");
        SunCe.save();

        Hero SunShangxiang=new Hero("蜀","孙尚香");
        SunShangxiang.setBirth("？ - ？");
        SunShangxiang.setOrigin("吴郡富春");
        SunShangxiang.setSex("女");
        SunShangxiang.setPicture(R.drawable.sunshangxiang);
        SunShangxiang.setId(6);
        SunShangxiang.setExperience("    孙夫人， 孙权之妹，曾为刘备之妻。《三国志》称之为孙夫人。为巩固孙刘联盟，孙夫人嫁给刘备三年，后来大归回吴，之后事迹不详。");
        SunShangxiang.save();

        Hero LvMeng=new Hero("吴","吕蒙");
        LvMeng.setOrigin("汝南富陂");
        LvMeng.setPicture(R.drawable.lvmeng);
        LvMeng.setBirth("179 - 220");
        LvMeng.setSex("男");
        LvMeng.setId(7);
        LvMeng.setExperience("    随孙策为将，以胆气称。鲁肃去世后，代守陆口，设计袭取荆州，击败蜀汉名将关羽，立下大功。不久后因病去世，享年四十二岁。吕蒙发愤勤学的事迹，成为了中国古代将领勤补拙、笃志力学的代表。");
        LvMeng.save();

        Hero SimaYi=new Hero("魏","司马懿");
        SimaYi.setOrigin("河内郡温县");
        SimaYi.setSex("男");
        SimaYi.setBirth("179 - 251");
        SimaYi.setPicture(R.drawable.simayi);
        SimaYi.setId(8);
        SimaYi.setExperience("    司马懿曾任曹魏的大都督、大将军、太尉、太傅，是辅佐了魏国三代的托孤辅政之重臣，后期成为掌控魏国朝政的权臣。善谋奇策，多次征伐有功，其中最显著的功绩是两次率大军成功抵御诸葛亮北伐和远征平定辽东。对屯田、水利等农耕经济发展有重要贡献。73岁去世，辞郡公和殊礼，葬于首阳山。谥号宣文；次子司马昭封晋王后，追谥司马懿为宣王；司马炎称帝后，追尊司马懿为宣皇帝，庙号高祖。");
        SimaYi.save();

        Hero Caocao=new Hero("魏","曹操");
        Caocao.setBirth("155 - 220");
        Caocao.setOrigin("沛国谯县");
        Caocao.setPicture(R.drawable.caocao);
        Caocao.setSex("男");
        Caocao.setId(9);
        Caocao.setExperience("    西园八校尉之一，曾只身行刺董卓。官渡之战中打败袁绍，最后统一了北方。但是在南下讨伐江东的战役中，曹操在赤壁惨败。后来在汉中争夺战中，曹操再次无功而返。曹操一生未称帝，他病死后，曹丕继位称帝，追封曹操为魏武皇帝。");
        Caocao.save();
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
                int id = item.getItemId();
                switch (id){
                    case R.id.bgm:
                        SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
                        boolean bgm = read.getBoolean("bgm",false);
                        if(bgm == false){
                            Intent intent = new Intent(MainActivity.this,BackgroundMusicService.class);
                            startService(intent);
                            item.setIcon(R.drawable.volumehigh);
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putBoolean("bgm",true);
                            editor.apply();
                        }else {
                            Intent intent = new Intent(MainActivity.this,BackgroundMusicService.class);
                            stopService(intent);
                            item.setIcon(R.drawable.volumemute2);
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putBoolean("bgm",false);
                            editor.apply();
                        }
                        break;

                    case R.id.clean:

                        break;

                    case R.id.restore:

                        break;

                    case R.id.reference_website:
                        Intent webIntent = new Intent(MainActivity.this,ReferenceWebSiteActivity.class);
                        startActivityForResult(webIntent,2);

                        break;
                    case R.id.aboutus:
                        AlertDialog.Builder alert;
                        alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("关于我们").setIcon(R.drawable.aboutus).setMessage("开发者：刘亦爽、柳晓龙、潘洲");
                        alert.create();
                        alert.show();
                        break;
                }
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
                showHeroRecyclerView(position);
            }

            @Override
            public void onLongClick(int position) {
                //Toast.makeText(getContext(),"点击了"+position,Toast.LENGTH_LONG).show();
            }
        });
        RecyclerView NationRecyclerView = (RecyclerView) findViewById(R.id.NationRecyclerView);
        NationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //BaseRecyclerAdapter nAdapter = new NationAdapter(NationImageResource);
        NationRecyclerView.setAdapter(nationAdapter);
    }

    private void showHeroRecyclerView(int position){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.HeroRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        if(isHerosVisiable){
            recyclerView.setVisibility(View.INVISIBLE);
            isHerosVisiable = false;
        }
        else {
            final List<Hero> Heros;
            switch (position){
                case 0: Heros = DataSupport.where("nationality = ?","魏").find(Hero.class);break;
                case 1: Heros = DataSupport.where("nationality = ?","蜀").find(Hero.class);break;
                case 2: Heros = DataSupport.where("nationality = ?","吴").find(Hero.class);break;
                default:Heros = DataSupport.findAll(Hero.class);break;
            }
            final BaseRecyclerAdapter heroAdapter = new BaseRecyclerAdapter<Hero>(this,Heros,R.layout.hero_list){
                @Override
                public void convert(BaseRecyclerHolder holder, Hero hero) {
                    ImageView n = holder.getView(R.id.HeroPicture);
                    if(hero.getId()<11){
                        RequestManager glideRequest;
                        glideRequest = Glide.with(getContext());
                        glideRequest.load(hero.getPicture())
                                .placeholder(R.mipmap.ic_launcher)
                                .crossFade()
                                .transform(new CenterCrop(getContext())
                                        ,new GlideRoundTransform(getContext(), 10))
                                .into(n);
                    }else {
                        RequestManager glideRequest;
                        glideRequest = Glide.with(getContext());
                        glideRequest.load(hero.getPictureSource())
                                .placeholder(R.mipmap.ic_launcher)
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
                    int id = Heros.get(position).getId();
                    //TODO:跳转到详情界面
                    Intent intent1 = new Intent(MainActivity.this,DetailActivity.class);
                    intent1.putExtra("id",id);
                    startActivity(intent1);
                    //Toast.makeText(getContext(),"点击了"+position,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClick(final int position) {
                    //final int[] choice = {0};
                    AlertDialog.Builder alterDialog = new AlertDialog.Builder(MainActivity.this);
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
            //recyclerView.addItemDecoration(new HeroAdapter.SpacesItemDecoration(1));
            recyclerView.setVisibility(View.VISIBLE);
            isHerosVisiable = true;
        }
    }
    void addHeroDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View view = layoutInflater.inflate(R.layout.addheros,null);

        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("增加人物");
        alertDialog.setIcon(R.drawable.liubei);
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO:点击确定即增加英雄到英雄列表中并且进行更新adapter
                Bitmap bitmap = ((BitmapDrawable)hero_image.getDrawable()).getBitmap();

                EditText hero_name = view.findViewById(R.id.name);//名字

                RadioGroup hero_country = view.findViewById(R.id.country);//国家组合选项
                int selected = hero_country.getCheckedRadioButtonId();//获得选中的国家的id

                EditText hero_sex = view.findViewById(R.id.sex);//性别
                EditText hero_origin = view.findViewById(R.id.hometown);//籍贯
                EditText hero_birth = view.findViewById(R.id.birth_death);//生卒年
                String name = hero_name.getText().toString();
                String country = "";//国家，势力
                switch (selected){
                    case R.id.wei:
                        country = "魏";
                        break;

                    case R.id.shu:
                        country = "蜀";
                        break;

                    case R.id.wu:
                        country = "吴";
                        break;
                }

                String sex = hero_sex.getText().toString();
                String origin = hero_origin.getText().toString();
                String birth_death = hero_birth.getText().toString();
                //增加人物并且设置属性
                Hero new_hero = new Hero(country,name);
                new_hero.setSex(sex);
                new_hero.setOrigin(origin);
                new_hero.setBirth(birth_death);
                new_hero.setPictureSource(ImagePath);
                Log.d("ImagePath",ImagePath);
                SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
                int id = read.getInt("HeroNum",10);
                new_hero.setId(id);
                System.out.println("id:"+id);
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putInt("HeroNum",id+1);//增加人物数量
                editor.apply();
                new_hero.save();
            }
        });
        alertDialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消，不进行任何处理
            }
        });

        //设置对“增加图片”的点击事件，点击之后弹出popupwindow，选择拍照或者是从相册中选择
        hero_image = (ImageView)view.findViewById(R.id.hero_image);
        hero_image.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPopueWindow(view);//设置浮于对话框的上方
            }
        });
        alertDialog.show();
    }

    private void showPopueWindow(View view){
        View popView = View.inflate(MainActivity.this,R.layout.popuewindow,null);
        Button bt_album = popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancel = popView.findViewById(R.id.btn_pop_cancel);

        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setAnimationStyle(R.style.Widget_AppCompat_PopupMenu);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                    popupWindow.dismiss();
                }
                else{
                    openAlbum();
                    popupWindow.dismiss();
                }
            }
        });

        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建File对象，用于存储拍照后的照片
                File outputImage = new File(getExternalCacheDir(),
                        "output_image.jpg");
                try{
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e){
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(MainActivity.this,
                            "company.leon.tkotkdirectory.fileprovider",outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);

                popupWindow.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明,对话框也变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);

        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,50);//传入一个对话框的view，pop在对话框之上


    }

    ////////////TODO:下面是一系列的函数，和打开相机有关和相册有关,细节可以忽略，还有对图片进行压缩的函数

    //用于打开相册的函数
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    //手机回应是否有权限访问
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.
                        PERMISSION_GRANTED){
                    openAlbum();
                } else {
                    Toast.makeText(MainActivity.this,"You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    //处理拍照或从相册中取到的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("SET","YES*****************************!!!!!!!!!!!!!!!");
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片压缩之后显示出来
//                    Bitmap bitmap = getSmallBitmap(getImagePath(imageUri,null),300,300);
//                    hero_image.setImageBitmap(bitmap);
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        hero_image.setImageBitmap(bitmap);
                        SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
                        int id = read.getInt("HeroNum",10);
                        saveBitmap(bitmap,id);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                }
                break;//一定要记得加break语句，否则case语句就失效了

            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){

                        handleImageOnKitKat(data);
                    } else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;//一定要记得加break语句，否则case语句就失效了

            default:
                break;
        }

    }

    //不同的版本用不同的处理方法，因为API19以上的对uri有封装
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(MainActivity.this,uri)){
            //如果是document类型的URI，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果content类型的Uri,则使用普通处理方式处理
            imagePath = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型则直接获取图片路径即可
            imagePath = uri.getPath();
        }
        ImagePath = imagePath;
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    //获取图片的路径
    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        //如果cursor为空则说明不是在相册中取照片而是通过拍照取照片，直接getPath获得拍照的图片的路径
        if (cursor == null){
            path = uri.getPath();
        }
        else {
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                        Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }
    //将来自相册的图像进行压缩并将图片显示出来
    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = getSmallBitmap(imagePath,300,300);
            hero_image.setImageBitmap(bitmap);
        }else{
            Toast.makeText(MainActivity.this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }


    //TODO:下面是几个压缩图片的函数，当获取到图片之后便调用下面的函数进行图片的压缩

    private int calculateInSampleSize(BitmapFactory.Options options, int reqW, int reqH){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqH || width > reqH){
            final int heightRatio = Math.round((float)height/(float)reqH);
            final int widthRatio = Math.round((float)width/(float)reqW);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);

        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath,options);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void saveBitmap(Bitmap bitmap,int bitName)
    {
        File file = new File("/sdcard/DCIM/Camera/"+bitName+".png");
        ImagePath = file.getPath();
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 1, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
