package com.ufind.baselibrary.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;
import com.ufind.baselibrary.R;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment<T> extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private BGARefreshLayout mBGARefreshLayout;
    private FrameLayout mFrameLayout;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    protected View getRootView() {
        return mBGARefreshLayout;
    }

    public View findViewById(@IdRes int id) {
        return mBGARefreshLayout.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beforeCreateView(savedInstanceState);

        mBGARefreshLayout = (BGARefreshLayout) inflater.inflate(R.layout.fragment_base, container, false);
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        mBGARefreshLayout.setDelegate(this);
        mBGARefreshLayout.setPullDownRefreshEnable(pullToRefreshEnable());
        mBGARefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity().getApplicationContext(), loadingMoreEnable()));

        mFrameLayout.addView(View.inflate(getActivity(), getLayoutId(), null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (refreshWhenCreateView()) {
            beginRefresh();
        }

        afterCreateView(savedInstanceState);
        return mBGARefreshLayout;
    }

    /**
     * 开始刷新
     */
    protected void beginRefresh() {
        mBGARefreshLayout.beginRefreshing();
    }

    /**
     * 停止刷新
     */
    protected void endRefreshAndLoadMore() {
        mBGARefreshLayout.endRefreshing();
        mBGARefreshLayout.endLoadingMore();
    }

    /**
     * 是否需要上拉加载
     */
    protected boolean loadingMoreEnable() {
        return false;
    }

    /**
     * 是否需要下拉刷新
     */
    protected boolean pullToRefreshEnable() {
        return false;
    }


    /**
     * 在 onCreateView代码执行之前的回调方法
     */
    protected void beforeCreateView(Bundle bundle) {

    }

    /**
     * 在 onCreateView代码执行之后的回调方法
     */
    protected void afterCreateView(Bundle bundle) {

    }

    /**
     * 获取数据
     */
    protected abstract Observable<T> getData();

    /**
     * 填充数据
     */
    protected abstract void bindData(T t);

    /**
     * 添加一个可以取消的订阅
     */
    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }


    /**
     * 在{@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}的时候是否需要立刻获取数据
     */
    protected boolean refreshWhenCreateView() {
        return true;
    }


    /**
     * @return layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        Logger.d("begin refresh");
        Subscription subscription = getData().subscribe(new Observer<T>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e);
            }

            @Override
            public void onNext(T t) {
                bindData(t);
                endRefreshAndLoadMore();
            }
        });
        addSubscription(subscription);

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
