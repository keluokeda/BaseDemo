package com.ufind.basedemo;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ufind.baselibrary.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class LazyDataActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lazy_data);

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager= (ViewPager) findViewById(R.id.container);
        int size=3;
        List<Fragment> fragments=new ArrayList<>(size);
        List<String> strings=new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            fragments.add(new LazyDataFragment());
            strings.add("index "+i);
        }

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragments,strings));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(size);
    }
}
