package com.ufind.baselibrary.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.orhanobut.logger.Logger;
import com.ufind.baselibrary.R;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import rx.Observable;
import rx.Observer;
import rx.Subscription;


public abstract class BaseListFragment<T extends Parcelable> extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    public static final String FLAG_IS_MORE = "FLAG_IS_MORE";
    public static final String FLAG_PAGE_INDEX = "FLAG_PAGE_INDEX";
    public static final String FLAG_ENTITY_LIST = "FLAG_ENTITY_LIST";


    RecyclerView mRecyclerView;
    BGARefreshLayout mBgaRefresh;

    private BaseQuickAdapter<T> mTBaseQuickAdapter;
    private boolean isMoreData = true;
    private int currentIndex = 1;


    /**
     * 初始化之前
     */
    @Override
    protected void beforeCreateView(Bundle bundle) {

    }

    /**
     * 初始化之后
     */
    @Override
    @CallSuper
    protected void afterCreateView(Bundle savedInstanceState) {

        initView();

        initViews(savedInstanceState);


        if (savedInstanceState == null) {
            if (refreshWhenCreate()) {
                mBgaRefresh.beginRefreshing();
            }
        } else {
            isMoreData = savedInstanceState.getBoolean(FLAG_IS_MORE, true);
            currentIndex = savedInstanceState.getInt(FLAG_PAGE_INDEX, 1);
            List<T> tList = savedInstanceState.getParcelableArrayList(FLAG_ENTITY_LIST);
            if (tList == null && isMoreData) {
                //保存数据为空并且可以加载更多
                mBgaRefresh.beginRefreshing();
            } else if (tList != null) {
                Logger.d("get data from save instance");
                addData(tList);
            }

        }
    }


    @Override
    protected void initViews(Bundle bundle) {

    }


    /**
     * 是否在 onCreateView的时候就开始加载数据
     */
    protected boolean refreshWhenCreate() {
        return true;
    }

    @Override
    protected View getContentView() {
        return View.inflate(getContext(), getCustomLayoutId() == 0 ? R.layout.fragment_base_list : getCustomLayoutId(), null);
    }

    @Override
    protected View getNetErrorView() {
        View view = View.inflate(getContext(), R.layout.layout_net_error, null);
        View button = view.findViewById(R.id.btn_net_error);
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
        return View.inflate(getContext(), R.layout.layout_empty_data, null);
    }

    @Override
    protected View getCustomErrorView() {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FLAG_IS_MORE, isMoreData);
        outState.putInt(FLAG_PAGE_INDEX, currentIndex);
        outState.putParcelableArrayList(FLAG_ENTITY_LIST, new ArrayList<Parcelable>(mTBaseQuickAdapter.getData()));
        super.onSaveInstanceState(outState);
    }

    /**
     * 如果用的是自定义布局 该方法必须返回自定义布局中的BGARefreshLayout id
     */
    protected
    @IdRes
    int getCustomBgaRefreshLayoutId() {
        return 0;
    }

    /**
     * 如果用的是自定义布局 该方法必须返回自定义布局中的RecyclerView id
     */
    protected
    @IdRes
    int getCustomRecyclerViewId() {
        return 0;
    }

    private void initView() {
        mBgaRefresh = (BGARefreshLayout) findViewById(getCustomLayoutId() == 0 ? R.id.bga_refresh : getCustomBgaRefreshLayoutId());
        mRecyclerView = (RecyclerView) findViewById(getCustomLayoutId() == 0 ? R.id.recycler_view : getCustomRecyclerViewId());


        mBgaRefresh.setDelegate(this);
        mBgaRefresh.setPullDownRefreshEnable(pullToRefreshEnable());

        mBgaRefresh.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity().getApplicationContext(), isLoadingMoreEnable()));


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initAdapter();


        mRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemClick(view, i, getEntity(i));
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemLongClick(view, i, getEntity(i));
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemChildClick(view, i, getEntity(i));
            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemChildLongClick(view, i, getEntity(i));
            }
        });


    }

    protected void initAdapter() {
        initAdapter(new ArrayList<T>(0));
    }

    protected void initAdapter(List<T> list) {
        mTBaseQuickAdapter = new BaseQuickAdapter<T>(getItemLayout(), list == null ? new ArrayList<T>(0) : list) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, T t) {
                convertData(baseViewHolder, t);
            }
        };
        mTBaseQuickAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mRecyclerView.setAdapter(mTBaseQuickAdapter);
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mTBaseQuickAdapter.setNewData(new ArrayList<T>(0));
        currentIndex = 1;
        loadData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (isMoreData) {
            loadData();
        }
        return isMoreData;
    }

    private void loadData() {
        Subscription s = getDataList(currentIndex).subscribe(new Observer<List<T>>() {
            @Override
            public void onCompleted() {
                endRefreshAndLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e);
                endRefreshAndLoadMore();
            }

            @Override
            public void onNext(List<T> ts) {
                if (ts == null || ts.isEmpty()) {
                    isMoreData = false;
                    Logger.d("empty data" + ts);
                    if (mTBaseQuickAdapter.getData().isEmpty()) {
                        //如果没有获取到数据并且数据源的数据是空的，就表示没有数据
                        Logger.d("data source is empty");
                        showEmptyDataView();
                    }
                    return;
                } else {
                    hideEmptyDataView();
                }
                currentIndex++;
                addData(ts);
            }
        });
        addSubscription(s);


    }

    protected void setHasMoreData(boolean moreData) {
        this.isMoreData = moreData;
    }


    protected void addData(List<T> list) {
        beforeAddData(list);
        mTBaseQuickAdapter.addData(list);
        afterAddData(list);
    }

    /**
     * 填充数据之前
     */
    protected void beforeAddData(List<T> list) {

    }

    /**
     * 填充数据之后
     */
    protected void afterAddData(List<T> list) {

    }

    /**
     * 获取自定义布局的layout id，如果使用默认布局，则不用重写此方法
     */
    protected
    @LayoutRes
    int getCustomLayoutId() {
        return 0;
    }


    protected T getEntity(int position) {
        return mTBaseQuickAdapter.getItem(position);
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected BaseQuickAdapter<T> getTBaseQuickAdapter() {
        return mTBaseQuickAdapter;
    }

    /**
     * 开始刷新
     */
    @Override
    public void beginRefresh() {
        mBgaRefresh.beginRefreshing();
    }

    /**
     * 停止刷新和加载
     */
    protected void endRefreshAndLoadMore() {
        mBgaRefresh.endLoadingMore();
        mBgaRefresh.endRefreshing();
    }


    //item 事件

    protected void onItemClick(View view, int position, T entity) {

    }

    protected void onItemLongClick(View view, int position, T entity) {

    }

    protected void onItemChildClick(View view, int position, T entity) {

    }

    protected void onItemChildLongClick(View view, int position, T entity) {

    }


    /**
     * 下拉刷新是否可用
     */
    protected abstract boolean pullToRefreshEnable();

    /**
     * 上拉加载是否可用
     */
    protected abstract boolean isLoadingMoreEnable();

    /**
     * item id
     */
    protected abstract int getItemLayout();

    /**
     * 给item绑定数据
     */
    protected abstract void convertData(BaseViewHolder baseViewHolder, T t);

    /**
     * 获取列表数据
     */
    protected abstract Observable<List<T>> getDataList(int currentIndex);


}
