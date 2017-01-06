package com.ufind.basedemo;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;


public class FragmentAdapter extends FragmentPagerAdapter {
    private List<? extends Fragment> fragmentList;
    private List<String> pageTitleList;//如果需要和 LabLayout结合使用，要为此对象赋值，不需要就null

    public FragmentAdapter(@NonNull FragmentManager fm, @NonNull List<? extends Fragment> fragmentList, @NonNull List<String> pageTitleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.pageTitleList = pageTitleList;

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (pageTitleList == null) {
            return null;
        }
        return pageTitleList.get(position);


    }
}
