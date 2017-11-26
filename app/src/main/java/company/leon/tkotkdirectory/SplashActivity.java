package company.leon.tkotkdirectory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.litepal.tablemanager.Connector;

/**
 * Created by Leon on 2017/11/20.
 */

public class SplashActivity extends Activity {
    //延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //在启动界面，通过子线程实现数据的预处理,判断是不是第一次运行，如果是就初始化数据
        if(isFirstRun() == true){
            new Thread(new Runnable() {
                @Override
                public void run(){
                    initData();
                }
            }).start();
        }
        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, SPLASH_DELAY_MILLIS);
    }
    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    boolean isFirstRun(){
        SharedPreferences read = getSharedPreferences("data",MODE_PRIVATE);
        boolean isFirstRun = read.getBoolean("isFirstRun",true);
        return isFirstRun;
    }

    //初始化程序需要的数据
    void initData(){

        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("isFirstRun",false);
        editor.putBoolean("bgm",true);
        editor.putInt("HeroNum",10);//一开始只有10个人物
        editor.apply();

        Log.d("DataBase","Initial");
        Connector.getDatabase();
        Hero GuanYu=new Hero("蜀","关羽");
        GuanYu.setSex("男");
        GuanYu.setOrigin("河东郡解县");
        GuanYu.setBirth("？ - 220");
        GuanYu.setPicture(R.drawable.guanyu);
        GuanYu.setExperience("    因当地势豪倚势凌人，杀之而逃难江湖，改名性关。与刘备、张飞桃园结义，关羽居其次。虎牢关温酒斩华雄，屯土山降汉不降曹。为报恩斩颜良、诛文丑，解曹操白马之围。后得知刘备音信，过五关斩六将，千里寻兄。刘备平定益州后，封关羽为五虎大将之首，督荆州事。关羽起军攻曹，水淹七军，威震华夏。围樊城右臂中箭，幸得华佗医治，刮骨疗伤。但因大意，关羽父子败走麦城，不屈遭害。");
        GuanYu.save();

        Hero LiuBei=new Hero("蜀","刘备");
        LiuBei.setSex("男");
        LiuBei.setBirth("161 - 223");
        LiuBei.setPicture(R.drawable.liubei);
        LiuBei.setOrigin("幽州涿县");
        LiuBei.setExperience("     刘备少年时拜卢植为师；早年颠沛流离，备尝艰辛，投靠过多个诸侯，曾参与镇压黄巾起义。先后率军救援北海相孔融、徐州牧陶谦等。陶谦病亡后，将徐州让与刘备。赤壁之战时，刘备与孙权联盟击败曹操，趁势夺取荆州。而后进取益州。于章武元年（221年）在成都称帝，国号汉，史称蜀或蜀汉。《三国志》评刘备的机权干略不及曹操，但其弘毅宽厚，知人待士，百折不挠，终成帝业。刘备也称自己做事“每与操反，事乃成尔”。");
        LiuBei.save();

        Hero ZhangFei=new Hero("蜀","张飞");
        ZhangFei.setOrigin("幽州涿县");
        ZhangFei.setPicture(R.drawable.zhangfei);
        ZhangFei.setBirth("？ - 221");
        ZhangFei.setSex("男");
        ZhangFei.setExperience("    三国时期蜀汉名将。刘备长坂坡败退，张飞仅率二十骑断后，据水断桥，曹军没人敢逼近；与诸葛亮、赵云扫荡西川时，于江州义释严颜；汉中之战时又于宕渠击败张郃，对蜀汉贡献极大，官至车骑将军、领司隶校尉，封西乡侯，后被范强、张达刺杀。后主时代追谥为“桓侯”。在中国传统文化中，张飞以其勇猛、鲁莽、嫉恶如仇而著称，虽然此形象主要来源于小说和戏剧等民间艺术，但已深入人心。");
        ZhangFei.save();

        Hero CsiYan=new Hero("魏","蔡琰");
        CsiYan.setBirth("？ - ？");
        CsiYan.setSex("女");
        CsiYan.setOrigin("陈留郡圉县");
        CsiYan.setPicture(R.drawable.caiwenji);
        CsiYan.setExperience("    字文姬，汉末女诗人，蔡邕之女。初嫁河东卫仲道。后为北方虏走，归南匈奴左贤王，居匈奴十二年。期间生二子，作《胡笳十八拍》。曹操念蔡邕无后，以千金赎回，再嫁董祀。");
        CsiYan.save();

        Hero FaZheng=new Hero("蜀","法正");
        FaZheng.setOrigin("扶风郿");
        FaZheng.setPicture(R.drawable.fazheng);
        FaZheng.setSex("男");
        FaZheng.setBirth("176 - 220");
        FaZheng.setExperience("    东汉末年刘备帐下谋士，名士法真之孙。原为刘璋部下，刘备围成都时劝说刘璋投降，而后又与刘备进取汉中，献计将曹操大将夏侯渊斩首。法正善奇谋，深受刘备信任和敬重。建安二十四年（219年），刘备进位汉中王，封法正为尚书令、护军将军。次年，法正去世，终年四十五岁。法正之死令刘备十分感伤，连哭数日。被追谥为翼侯，是刘备时代唯一一位有谥号的大臣。法正善于奇谋，被陈寿称赞为可比曹操帐下的程昱和郭嘉。");
        FaZheng.save();

        Hero SunCe=new Hero("吴","孙策");
        SunCe.setBirth("175 - 200");
        SunCe.setOrigin("吴郡富春");
        SunCe.setPicture(R.drawable.sunce);
        SunCe.setSex("男");
        SunCe.setExperience("    破虏将军孙坚长子、吴大帝孙权长兄。东汉末年割据江东一带的军阀，汉末群雄之一，三国时期孙吴的奠基者之一。《三国演义》称其武勇犹如霸王项羽，绰号“小霸王”。孙策为将，有智有勇，英姿勃发，其治军严整，军纪严明。但在征战中由于年轻气盛，难免出现处事不慎、好勇斗狠的弱点，这为其结怨和遇刺种下了祸根。建安五年（200年）4月，正当孙策准备发兵北上之时，在丹徒狩猎中为刺客所伤，不久后身亡，年仅二十六岁。");
        SunCe.save();

        Hero SunShangxiang=new Hero("蜀","孙尚香");
        SunShangxiang.setBirth("？ - ？");
        SunShangxiang.setOrigin("吴郡富春");
        SunShangxiang.setSex("女");
        SunShangxiang.setPicture(R.drawable.sunshangxiang);
        SunShangxiang.setExperience("    孙夫人， 孙权之妹，曾为刘备之妻。《三国志》称之为孙夫人。为巩固孙刘联盟，孙夫人嫁给刘备三年，后来大归回吴，之后事迹不详。");
        SunShangxiang.save();

        Hero LvMeng=new Hero("吴","吕蒙");
        LvMeng.setOrigin("汝南富陂");
        LvMeng.setPicture(R.drawable.lvmeng);
        LvMeng.setBirth("179 - 220");
        LvMeng.setSex("男");
        LvMeng.setExperience("    随孙策为将，以胆气称。鲁肃去世后，代守陆口，设计袭取荆州，击败蜀汉名将关羽，立下大功。不久后因病去世，享年四十二岁。吕蒙发愤勤学的事迹，成为了中国古代将领勤补拙、笃志力学的代表。");
        LvMeng.save();

        Hero SimaYi=new Hero("魏","司马懿");
        SimaYi.setOrigin("河内郡温县");
        SimaYi.setSex("男");
        SimaYi.setBirth("179 - 251");
        SimaYi.setPicture(R.drawable.simayi);
        SimaYi.setExperience("    司马懿曾任曹魏的大都督、大将军、太尉、太傅，是辅佐了魏国三代的托孤辅政之重臣，后期成为掌控魏国朝政的权臣。善谋奇策，多次征伐有功，其中最显著的功绩是两次率大军成功抵御诸葛亮北伐和远征平定辽东。对屯田、水利等农耕经济发展有重要贡献。73岁去世，辞郡公和殊礼，葬于首阳山。谥号宣文；次子司马昭封晋王后，追谥司马懿为宣王；司马炎称帝后，追尊司马懿为宣皇帝，庙号高祖。");
        SimaYi.save();

        Hero Caocao=new Hero("魏","曹操");
        Caocao.setBirth("155 - 220");
        Caocao.setOrigin("沛国谯县");
        Caocao.setPicture(R.drawable.caocao);
        Caocao.setSex("男");
        Caocao.setExperience("    西园八校尉之一，曾只身行刺董卓。官渡之战中打败袁绍，最后统一了北方。但是在南下讨伐江东的战役中，曹操在赤壁惨败。后来在汉中争夺战中，曹操再次无功而返。曹操一生未称帝，他病死后，曹丕继位称帝，追封曹操为魏武皇帝。");
        Caocao.save();
    }
}