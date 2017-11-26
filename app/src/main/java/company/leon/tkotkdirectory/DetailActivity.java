package company.leon.tkotkdirectory;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.util.List;

import company.leon.tkotkdirectory.fragment.PieChartFragment;

public class DetailActivity extends AppCompatActivity {
    private ImageView img;
    private int imgid;
    private String heroorigin;
    private String herobirth;
    private String herosex;
    private String heroname;
    private String heronationality;
    private String experience;
    private int id;//英雄的id
    private PieChartFragment pieChartFragment;
    private FragmentTransaction fragmentTransaction;
    private FrameLayout chartFragments;
    private Toolbar toolbar;
    private Hero temp;
    private TextView heroExperience;//用这个view来设生平信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_detail);requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏*/
        setContentView(R.layout.activity_detail);
        View v = findViewById(R.id.detail);
        v.getBackground().setAlpha(200);
        img = (ImageView) findViewById(R.id.img);
        heroExperience = (TextView)findViewById(R.id.Affairs);
        //设置最下面的文字可以滚动
        heroExperience.setMovementMethod(new ScrollingMovementMethod());

        /*heroname = (TextView) findViewById(R.id.heroname);
        //backbutton = (ImageButton) findViewById(R.id.backbutton);
        heronationality = (EditText) findViewById(R.id.heronationality);*/
        //modify = (ImageButton) findViewById(R.id.modify) ;
        toolbar = (Toolbar)findViewById(R.id.toolbar2);
        //设置不可更改

        //设置toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //接收来自FirstActivity的信息，呈现相应的界面
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            id = (int) extras.get("id");
            String IDString = ""+id;
            List<Hero> HeroList = DataSupport.where("id=?",IDString).find(Hero.class);
            temp = HeroList.get(0);
            imgid=temp.getPicture();
            int Hero_ID = temp.getId();
            img.setImageResource(temp.getPicture());

            //根据id判断是否是新增加的人物，如果是，那么图片源在bitmap中取,新增人物的图片时另外发送的
            if (Hero_ID <= 10){
                Glide.with(this)
                        .load(temp.getPicture())
                        .error(R.drawable.ic_error_36pt_3x)
                        .into(img);

            } else {
                Glide.with(this)
                        .load(temp.getPictureSource())
                        .error(R.drawable.ic_error_36pt_3x)
                        .into(img);
            }
            heroname=temp.getName();//英雄的名字
            heronationality=temp.getNationality();//英雄的国家
            herosex=temp.getSex();//英雄的性别
            herobirth=temp.getBirth();//英雄的生卒年
            heroorigin=temp.getOrigin();//英雄的籍贯
            experience=temp.getExperience();//人物的生平介绍

            heroExperience.setText(experience);//根据数据库的生平来来设置可滚动的信息
        }
        initView();
    }

    //导入menu，和toolbar进行绑定
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            case R.id.modify_information:

                Snackbar.make(toolbar, "请修改", Snackbar.LENGTH_SHORT).show();


                //弹出对话框,并且进行人物信息的修改
                addHeroDialog();



                break;
        }


        return true;
    }

    //修改信息时弹出的对话框
    void addHeroDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
        final View view = layoutInflater.inflate(R.layout.addheros,null);

        final ImageView hero_image = view.findViewById(R.id.hero_image);
        final EditText hero_name = view.findViewById(R.id.name);//名字
        final RadioGroup hero_country = view.findViewById(R.id.country);//国家组合选项
        final EditText hero_sex = view.findViewById(R.id.sex);//性别
        final EditText hero_origin = view.findViewById(R.id.hometown);//籍贯
        final EditText hero_birth = view.findViewById(R.id.birth_death);//生卒年
        final EditText hero_experience = view.findViewById(R.id.WholeLife);
        final RadioButton wei = view.findViewById(R.id.wei);
        final RadioButton shu = view.findViewById(R.id.shu);
        final RadioButton wu = view.findViewById(R.id.wu);

        //在弹出对话框之前设置好人物的信息，这样逻辑更加合理
        hero_name.setText(heroname);//填入人物名字
        hero_sex.setText(herosex);//填入人物性别
        hero_origin.setText(heroorigin);//填入人物籍贯
        hero_birth.setText(herobirth);//填入人物生卒年
        hero_experience.setText(experience);//填入人物生平

        switch (heronationality){//设置单选按钮落点的位置 魏 蜀 吴

            case "魏":
                wei.setChecked(true);
                shu.setChecked(false);
                wu.setChecked(false);
                break;

            case "蜀":
                wei.setChecked(false);
                shu.setChecked(true);
                wu.setChecked(false);
                break;

            case "吴":
                wei.setChecked(false);
                shu.setChecked(false);
                wu.setChecked(true);
                break;

            default:
                break;

        }
        String IDString = ""+id;
        List<Hero> HeroList = DataSupport.where("id=?",IDString).find(Hero.class);
        temp = HeroList.get(0);
        imgid=temp.getPicture();
        int Hero_ID = temp.getId();
        img.setImageResource(temp.getPicture());

        //根据id判断是否是新增加的人物，如果是，那么图片源在bitmap中取,新增人物的图片时另外发送的
        if (Hero_ID <= 10){
            Glide.with(this)
                    .load(temp.getPicture())
                    .error(R.drawable.ic_error_36pt_3x)
                    .into(hero_image);

        } else {
            Glide.with(this)
                    .load(temp.getPictureSource())
                    .error(R.drawable.ic_error_36pt_3x)
                    .into(hero_image);
        }


        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(DetailActivity.this);
        alertDialog.setTitle("修改人物信息");
        alertDialog.setIcon(R.drawable.ic_mode_edit_black_36dp);
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {


                int selected = hero_country.getCheckedRadioButtonId();//获得选中的国家的id
                //TODO 下面用TODO标注的都是修改之后获取到的字符串
                heroname = hero_name.getText().toString();//TODO 修改之后的名字
                heronationality = "";//国家，势力，TODO 修改之后的国家势力
                switch (selected){
                    case R.id.wei:
                        heronationality = "魏";
                        break;

                    case R.id.shu:
                        heronationality = "蜀";
                        break;

                    case R.id.wu:
                        heronationality = "吴";
                        break;
                }


                herosex = hero_sex.getText().toString();//TODO 修改之后的性别
                heroorigin = hero_origin.getText().toString();//TODO 修改之后的籍贯
                herobirth = hero_birth.getText().toString();//TODO 修改之后的生卒年
                experience = hero_experience.getText().toString();//TODO 生平
                //TODO 亦爽也可以根据上面的字符串进行更新详情界面，注意更新生平
                heroExperience.setText(experience);//TODO 去掉注释则修改人物生平


                temp.setName(heroname);
                temp.setNationality(heronationality);
                temp.setSex(herosex);
                temp.setOrigin(heroorigin);
                temp.setBirth(herobirth);
                temp.setExperience(experience);
                temp.save();
                initView();
            }
        });
        alertDialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消，不进行任何处理
            }
        });

        //图片在这里就不修改了
        //设置对“增加图片”的点击事件，点击之后弹出popupwindow，选择拍照或者是从相册中选择
        /*hero_image = (ImageView)view.findViewById(R.id.hero_image);
        hero_image.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPopueWindow(view);//设置浮于对话框的上方
            }
        });*/
        //将对话框显示出来
        alertDialog.show();
    }

    private void initView(){
        chartFragments = (FrameLayout) findViewById(R.id.chart_fragments);

        FragmentManager manager=getSupportFragmentManager();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        pieChartFragment = (PieChartFragment)manager.findFragmentByTag("Tag4");
        if (pieChartFragment == null){
            pieChartFragment = new company.leon.tkotkdirectory.fragment.PieChartFragment();
            pieChartFragment.setNation(heronationality);
            String temp[] ={herosex,herobirth,heroname,heroorigin};
            pieChartFragment.setInfo(temp);
            fragmentTransaction.add(R.id.chart_fragments,pieChartFragment,"Tag4");
            fragmentTransaction.show(pieChartFragment);
        }else {
            pieChartFragment = new company.leon.tkotkdirectory.fragment.PieChartFragment();
            pieChartFragment.setNation(heronationality);
            String temp[] ={herosex,herobirth,heroname,heroorigin};
            pieChartFragment.setInfo(temp);
            fragmentTransaction.replace(R.id.chart_fragments,pieChartFragment,"Tag4");
            fragmentTransaction.show(pieChartFragment);
        }

        fragmentTransaction.commit();//提交事务


    }

    private void hideFragment(FragmentTransaction fragmentTransaction){

        if (pieChartFragment != null){
            fragmentTransaction.hide(pieChartFragment);
        }
    }


}
