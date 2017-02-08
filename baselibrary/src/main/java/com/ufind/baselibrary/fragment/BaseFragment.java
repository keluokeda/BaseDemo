package com.ufind.baselibrary.fragment;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ufind.baselibrary.R;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends Fragment {
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private View emptyDataView;
    private View netErrorView;
    private View customErrorView;

    private FrameLayout mRootView;

    public void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }


    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beforeCreateView(savedInstanceState);
        mRootView = (FrameLayout) inflater.inflate(R.layout.fragment_base, container, false);
        View contentView = getContentView();
        mRootView.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        afterCreateView(savedInstanceState);


        return mRootView;
    }

    public final View findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }


    /**
     * 在创建view完成之后 在设置数据之前调用
     * @param bundle
     */
    protected abstract void initViews(Bundle bundle);

    /**
     * 获取 根布局
     */
    public View getRootView() {
        return mRootView;
    }

    /**
     * 创建 view  之前
     */
    protected abstract void beforeCreateView(Bundle bundle);

    /**
     * 创建 view 之后
     */
    protected abstract void afterCreateView(Bundle bundle);

    /**
     * 获取内容 view，该方法只会在{@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}时候调用一次
     */
    protected abstract View getContentView();

    /**
     * 空数据要展示的view，背景最好不要透明
     * 此方法只会被调用一次
     */
    protected abstract View getEmptyDataView();

    protected void showEmptyDataView() {
        if (emptyDataView == null) {
            emptyDataView = getEmptyDataView();
        }
        if (emptyDataView.getParent() != null) {
            return;
        }
        mRootView.addView(emptyDataView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    protected void hideEmptyDataView() {
        if (emptyDataView != null && emptyDataView.getParent() != null) {
            mRootView.removeView(emptyDataView);
        }
    }

    /**
     * 网络链接错误时 要展示的view
     */
    protected abstract View getNetErrorView();

    protected void showNetErrorView() {
        if (netErrorView == null) {
            netErrorView = getNetErrorView();
        }
        if (netErrorView.getParent() != null) {
            return;
        }

        mRootView.addView(netErrorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    protected void hideNetErrorView() {
        if (netErrorView != null && netErrorView.getParent() != null) {
            mRootView.removeView(netErrorView);
        }
    }

    /**
     * 自定义错误的view
     */
    protected abstract View getCustomErrorView();

    protected void showCustomErrorView() {
        if (customErrorView == null) {
            customErrorView = getCustomErrorView();
        }
        if (customErrorView.getParent() != null) {
            return;
        }
        mRootView.addView(customErrorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    protected void hideCustomerrorView() {
        if (customErrorView != null && customErrorView.getParent() != null) {
            mRootView.removeView(customErrorView);
        }
    }

    /**
     * 开始刷新视图内容
     */
    public abstract void beginRefresh();


}
