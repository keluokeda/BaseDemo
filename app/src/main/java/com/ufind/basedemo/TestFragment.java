package com.ufind.basedemo;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ufind.baselibrary.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TestFragment extends BaseFragment<TestEntity> {
    private ViewHolder mViewHolder;


    @Override
    protected boolean loadingMoreEnable() {
        return false;
    }

    @Override
    protected boolean pullToRefreshEnable() {
        return false;
    }

    @Override
    protected Observable<TestEntity> getData() {
        return Observable.create(new Observable.OnSubscribe<TestEntity>() {
            @Override
            public void call(Subscriber<? super TestEntity> subscriber) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TestEntity testEntity = new TestEntity();
                testEntity.setSize(10);
                testEntity.setName("hankuke");
                subscriber.onNext(testEntity);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void bindData(TestEntity testEntity) {
        Logger.d(testEntity);
        mViewHolder.mTv00.setText("size= "+testEntity.getSize());
        mViewHolder.mTv01.setText(testEntity.getName());
    }

    @Override
    protected void afterCreateView(Bundle bundle) {
        mViewHolder = new ViewHolder(getRootView());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    static class ViewHolder {
        @BindView(R.id.tv_00)
        TextView mTv00;
        @BindView(R.id.tv_01)
        TextView mTv01;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
