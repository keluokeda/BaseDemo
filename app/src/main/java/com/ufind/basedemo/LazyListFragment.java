package com.ufind.basedemo;


import com.chad.library.adapter.base.BaseViewHolder;
import com.ufind.baselibrary.fragment.BaseLazyListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


public class LazyListFragment extends BaseLazyListFragment<TestEntity> {


    @Override
    protected boolean pullToRefreshEnable() {
        return true;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return true;
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_size_name;
    }

    @Override
    protected void convertData(BaseViewHolder baseViewHolder, TestEntity testEntity) {
        baseViewHolder.setText(R.id.tv_size, "size = " + testEntity.getSize());
        baseViewHolder.setText(R.id.tv_name, testEntity.getName());
    }

    @Override
    protected Observable<List<TestEntity>> getDataList(int currentIndex) {
        List<TestEntity> list = new ArrayList<>(currentIndex);
        for (int i = 0; i < currentIndex; i++) {
            TestEntity testEntity=new TestEntity();
            testEntity.setSize(currentIndex*i);
            testEntity.setName(UUID.randomUUID().toString());
            list.add(testEntity);
        }
        return Observable.just(list).delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread());
    }
}
