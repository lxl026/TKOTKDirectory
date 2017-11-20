package company.leon.tkotkdirectory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.List;

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
                RecyclerView searchRecler = (RecyclerView)findViewById(R.id.searchResult);
                LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                searchRecler.setLayoutManager(layoutManager);

                List<Hero> SearchItems = DataSupport.where("name like ? or nationality like ? or sex like ? or origin like ? or birth like ? ",
                        "%" + query+ "%","%" + query+ "%","%" + query+ "%","%" + query+ "%","%" + query+ "%").find(Hero.class);

                HeroAdapter heroAdapter = new HeroAdapter(SearchItems);
                searchRecler.setAdapter(heroAdapter);
                searchRecler.setVisibility(View.VISIBLE);
                ResultLog.setVisibility(View.VISIBLE);
                if(SearchItems.size() == 0)ResultLog.setText("无匹配结果！");
                ResultLog.setText("共搜索到"+SearchItems.size()+"个结果");

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
