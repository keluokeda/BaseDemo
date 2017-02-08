package com.ufind.baselibrary.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
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

public abstract class BaseDataFragment<T extends Parcelable> extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    public static final String INSTANCE_ENTITY = "INSTANCE_ENTITY";

    private BGARefreshLayout mBGARefreshLayout;

    private T mData;


    @Override
    protected View getContentView() {
        return View.inflate(getContext(),R.layout.fragment_base_data,null);
    }

    @Override
    protected View getNetErrorView() {
        View view= View.inflate(getContext(), R.layout.layout_net_error, null);
        View button=view.findViewById(R.id.btn_net_error);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNetErrorView();
                beginRefresh();
            }
        });

        return view;
    }

    @Override
    protected View getEmptyDataView() {
        return View.inflate(getContext(),R.layout.layout_empty_data,null);
    }


    @Override
    protected View getCustomErrorView() {
        return null;
    }

    /**
     * 开始刷新
     */
    @Override
    public void beginRefresh() {
        Logger.d("begin refresh");
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
     * 是否需要保存现场数据
     */
    protected boolean needSaveInstance() {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (needSaveInstance() && mData != null) {
            outState.putParcelable(INSTANCE_ENTITY, mData);
        }
        super.onSaveInstanceState(outState);
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
    @CallSuper
    protected void afterCreateView(Bundle bundle) {
        mBGARefreshLayout = (BGARefreshLayout) findViewById(R.id.bga_refresh);
        mBGARefreshLayout.setDelegate(this);
        mBGARefreshLayout.setPullDownRefreshEnable(pullToRefreshEnable());
        mBGARefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity().getApplicationContext(), loadingMoreEnable()));

        FrameLayout frameLayout= (FrameLayout) mBGARefreshLayout.findViewById(R.id.frame_layout);
        frameLayout.addView(View.inflate(getContext(),getLayoutId(),null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initViews(bundle);

        if (bundle != null && (mData = bundle.getParcelable(INSTANCE_ENTITY)) != null) {
            Logger.d("get data from bundle");
            setData(mData);
        } else {
            if (refreshWhenCreateView()) {
                beginRefresh();
            }
        }
    }

    //在创建视图完成之后 在设置数据之前 调用
    @Override
    protected void initViews(Bundle bundle){

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
     * 设置数据并把数据保存下来
     */
    protected final void setData(T t) {
        this.mData = t;
        bindData(t);
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
                setData(t);
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
