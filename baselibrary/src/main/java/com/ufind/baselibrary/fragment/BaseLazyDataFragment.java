package com.ufind.baselibrary.fragment;


import android.os.Bundle;
import android.os.Parcelable;

public abstract class BaseLazyDataFragment<T extends Parcelable> extends BaseDataFragment<T> {

    private boolean isLazyLoadDone = false;//懒加载是否完成
    private boolean isCreateViewDone = false;//view是否初始化完成

    @Override
    protected boolean refreshWhenCreateView() {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //此方法 执行与 onCreateView之前
        super.setUserVisibleHint(isVisibleToUser);
        onLazyLoad();
    }

    /**
     * 当view 初始化完成 并且当前 fragment可见 并且懒加载没完成 执行此方法 加载数据
     */
    protected void onLazyLoad() {
        if (isCreateViewDone && getUserVisibleHint() && (!isLazyLoadDone)) {
            beginRefresh();
            isLazyLoadDone = true;
        }
    }

    @Override
    protected void afterCreateView(Bundle bundle) {
        super.afterCreateView(bundle);
        isCreateViewDone = true;
        onLazyLoad();
    }

    @Override
    protected boolean needSaveInstance() {
        return false;
    }
}
