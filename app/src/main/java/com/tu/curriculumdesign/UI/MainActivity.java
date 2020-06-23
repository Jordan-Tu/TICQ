package com.tu.curriculumdesign.UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSON;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.android.material.navigation.NavigationView;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.FragmentAdapter;
import com.tu.curriculumdesign.application.ActivityController;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.fragment.MessageFragment;
import com.tu.curriculumdesign.fragment.MineFragment;
import com.tu.curriculumdesign.fragment.TopicFragment;
import com.tu.curriculumdesign.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity {
    @BindView(R.id.bottom_navigation_bar) BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.viewPager) ViewPager2 viewPager;
    @BindView(R.id.drawer_layout) DrawerLayout drawer_layout;
    @BindView(R.id.navigation_view) NavigationView navigation_view;
    @BindView(R.id.tv_top_name) TextView tv_top_name;
    @BindView(R.id.civ_name) CircleImageView civ_name;
    @BindView(R.id.civ_search)CircleImageView civ_search;

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
        civ_name.setImageResource(R.drawable.background);
        tv_top_name.setText(MyApplication.getCurrentUser().getName().substring(0,1));

        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setBarBackgroundColor(R.color.bar_background)
                .setActiveColor(R.color.bar_active)
                .setInActiveColor(R.color.bar_inactive)
                .addItem(new BottomNavigationItem(R.mipmap.ic_topic, "话题"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_message,"聊天"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_mine, "测试"))
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
                }else if(item.getTitle().toString().equals("消息通知")){
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                }else if(item.getTitle().toString().equals("测试")){
                    startActivity(new Intent(MainActivity.this, TestActivity.class));
                }else if(item.getTitle().toString().equals("退出登录")){
                    ActivityController.finishAll();
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
                if(navigation_view.getCheckedItem()!=null){
                    navigation_view.getCheckedItem().setChecked(false);
                }
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

    @OnClick(R.id.civ_search) void search(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText edit = new EditText(this);
        edit.setHint("email@ticq.com");
        builder.setTitle("请输入账号：").setView(edit)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(edit.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "账号不能为空哦！", Toast.LENGTH_SHORT).show();
                        }else{
                            dialog.cancel();
                            getUser(Integer.parseInt(edit.getText().toString()));
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        builder.show();
    }

    private void getUser(Integer id){
        String controller = "/displayUserInfo";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id.toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(MainActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String string = response.body().string();
                User user = null;
                try {
                    user = JSON.parseObject(string).toJavaObject(User.class);
                }catch (Exception e){
                    Log.d(controller,"onFailure:"+e.getMessage());
                }
                if (user!=null) {
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "找到该用户了！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "很抱歉未找到该用户！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });


    }


}
