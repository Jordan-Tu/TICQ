package com.tu.curriculumdesign.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.android.material.navigation.NavigationView;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.FragmentAdapter;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.fragment.MessageFragment;
import com.tu.curriculumdesign.fragment.MineFragment;
import com.tu.curriculumdesign.fragment.TopicFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.tu.curriculumdesign.R.drawable.background1;
import static com.tu.curriculumdesign.R.drawable.background2;
import static com.tu.curriculumdesign.R.drawable.background3;
import static com.tu.curriculumdesign.R.drawable.background4;
import static com.tu.curriculumdesign.R.drawable.background5;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bottom_navigation_bar) BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.viewPager) ViewPager2 viewPager;
    @BindView(R.id.drawer_layout) DrawerLayout drawer_layout;
    @BindView(R.id.navigation_view) NavigationView navigation_view;
    @BindView(R.id.tv_top_name) TextView tv_top_name;
    @BindView(R.id.civ_name) CircleImageView civ_name;

    @OnClick(R.id.fab) void toPost(){
        startActivity(new Intent(MainActivity.this,PostActivity.class));
    }

    @OnClick(R.id.civ_name) void openDrawerLayout(){
        drawer_layout.openDrawer(GravityCompat.START);
    }


    private List<Fragment> fragmentList = new ArrayList<>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPager();
        List<Integer> background = new ArrayList<>();
        background.add(background1);
        background.add(background2);
        background.add(background3);
        background.add(background4);
        background.add(background5);
        civ_name.setImageResource(background.get(new Random().nextInt(5)));
        tv_top_name.setText(MyApplication.getCurrentUser().getName().substring(0,1));

        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setBarBackgroundColor(R.color.bar_background)
                .setActiveColor(R.color.bar_active)
                .setInActiveColor(R.color.bar_inactive)
                .addItem(new BottomNavigationItem(R.mipmap.ic_topic, "话题"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_message,"聊天"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_mine, "我的"))
                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                viewPager.setCurrentItem(position);
            }
        });

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer_layout.closeDrawer(Gravity.START);
                System.out.println(item.getTitle().toString());
                System.out.println(item.getItemId());
                if(item.getTitle().toString().equals("我的")){
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",MyApplication.getCurrentUser());
                    intent.putExtra("flag", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if(item.getTitle().toString().equals("邮箱")){
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                }else if(item.getTitle().toString().equals("测试")){
                    startActivity(new Intent(MainActivity.this, TestActivity.class));
                }
                return true;
            }
        });

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                TextView tv_userName = drawerView.findViewById(R.id.tv_userName);
                TextView tv_userEmail = drawerView.findViewById(R.id.tv_userEmail);
                tv_userName.setText(MyApplication.getCurrentUser().getName());
                tv_userEmail.setText(MyApplication.getCurrentUser().getEmail());
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });



    }
    private void initPager(){
        fragmentList.add(new TopicFragment());
        fragmentList.add(new MessageFragment());
        fragmentList.add(new MineFragment());
        FragmentAdapter adapter = new FragmentAdapter(this,fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationBar.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


}
