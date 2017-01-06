package com.ufind.basedemo;


import android.os.Bundle;
import android.widget.TextView;

import com.ufind.baselibrary.fragment.BaseLazyDataFragment;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class LazyDataFragment extends BaseLazyDataFragment<TestEntity>{
    private TextView tvInfo;
    private TextView tvName;
    @Override
    protected Observable<TestEntity> getData() {
        TestEntity testEntity=new TestEntity();
        testEntity.setName("hankuke111");
        testEntity.setSize(10);
        return Observable.just(testEntity).delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void bindData(TestEntity testEntity) {
        showEmptyDataView();
    }

    @Override
    protected void afterCreateView(Bundle bundle) {
        super.afterCreateView(bundle);
        tvInfo= (TextView) findViewById(R.id.tv_00);
        tvName= (TextView) findViewById(R.id.tv_01);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }
}
