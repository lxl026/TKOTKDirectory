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
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.litepal.crud.DataSupport;

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

    RecyclerView recyclerView;//人物recycler View
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
        if(isHerosVisiable){
            recyclerView.setVisibility(View.INVISIBLE);
            isHerosVisiable = false;
        }
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


    //初始化toolbar
    void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//设置菜单图片
            actionBar.setHomeAsUpIndicator(R.drawable.ic_toc_white_18dp);
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

                    case R.id.copyright:
                        AlertDialog.Builder copyrightalert;
                        copyrightalert = new AlertDialog.Builder(MainActivity.this);
                        copyrightalert.setTitle("版权说明：").setIcon(R.drawable.ic_copyright_black_36dp).setMessage("本应用图片音乐资源来源于网络，如有侵权，请联系我们删除。");
                        copyrightalert.create();
                        copyrightalert.show();
                        break;

                    case R.id.restore:
                        AlertDialog.Builder restorealert;
                        restorealert = new AlertDialog.Builder(MainActivity.this);
                        restorealert.setTitle("清理缓存").setIcon(R.drawable.ic_restore_black_36dp).setMessage("应用将删除拍摄的照片与您创建的人物，请谨慎选择");
                        restorealert.setCancelable(true);
                        restorealert .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int p) {

                            }
                        });
                        restorealert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, final int p) {
                                    restoreData();
                                }
                            });
                        restorealert.create();
                        restorealert.show();
                        break;

                    case R.id.reference_website:
                        Intent webIntent = new Intent(MainActivity.this,ReferenceWebSiteActivity.class);
                        startActivityForResult(webIntent,2);

                        break;
                    case R.id.aboutus:
                        AlertDialog.Builder alert;
                        alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("关于我们").setIcon(R.drawable.ic_people_black_36dp).setMessage("开发团队：刘亦爽、柳晓龙、潘洲\n中山大学数据科学与计算机学院移动信息工程");
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
                        .priority( Priority.HIGH )
                        .error(R.drawable.ic_error_36pt_3x)
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
        NationRecyclerView.setAdapter(nationAdapter);
    }

    private void showHeroRecyclerView(int position){

        recyclerView = (RecyclerView) findViewById(R.id.HeroRecyclerView);
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
                    if(isHerosVisiable){
                        recyclerView.setVisibility(View.INVISIBLE);
                        isHerosVisiable = false;
                    }
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
                                    DataBaseTest();
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
            isHerosVisiable = true;
        }
    }
    void addHeroDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View view = layoutInflater.inflate(R.layout.addheros,null);

        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("增加人物");
        alertDialog.setIcon(R.drawable.ic_person_add_black_36dp);
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
                EditText hero_experience = view.findViewById(R.id.WholeLife);
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
                String experience = hero_experience.getText().toString();
                //增加人物并且设置属性
                Hero new_hero = new Hero(country,name);
                new_hero.setSex(sex);
                new_hero.setOrigin(origin);
                new_hero.setBirth(birth_death);
                new_hero.setPictureSource(ImagePath);
                new_hero.setExperience(experience);
                Log.d("ImagePath",ImagePath);
                SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
                int id = read.getInt("HeroNum",10);
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putInt("HeroNum",id+1);//增加人物数量
                editor.apply();
                new_hero.save();
                DataBaseTest();
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
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

                    popupWindow.dismiss();
                }
                else{
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
            case 2:
                if (grantResults.length>0&&grantResults[0]==PackageManager.
                        PERMISSION_GRANTED){
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
                        int id = read.getInt("HeroNum",10) +1;
                        saveBitmap(bitmap,id);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(this,"照片存储失败，请重拍，否则无法显示",Toast.LENGTH_SHORT);
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

    private void DataBaseTest(){
        List<Hero> test = DataSupport.findAll(Hero.class);
        for(int i=0;i<test.size();i++){
            Hero x=test.get(i);
            System.out.println(x.getName()+"  "+x.getId()+"  "+x.getBirth()+"  "+x.getNationality()+"  "+x.getOrigin()+"  "+x.getSex()+"  "+x.getPictureSource());
        }
    }

    //清理图片
    void restoreData(){
        //删除拍摄的图片
        List<Hero> heros = DataSupport.where("id > ?","10").find(Hero.class);
        for(int i=0;i<heros.size();i++){
            int id = heros.get(i).getId();
            if(id>10){
                System.out.println("删除图片："+id);
                String s = heros.get(i).getPictureSource();
                String t = "/sdcard/DCIM/Camera/"+id+".png";
                if(s.indexOf(t)!=-1 )  {
                    System.out.println("删除了："+s);
                    deleteFile(s);
                }
                DataSupport.delete(Hero.class,id);
            }
        }
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

}
