package com.dl7.myapp.module.news;

import com.dl7.myapp.api.NewsUtils;
import com.dl7.myapp.api.RetrofitService;
import com.dl7.myapp.api.bean.NewsBean;
import com.dl7.myapp.entity.NewsMultiItem;
import com.dl7.myapp.module.base.IBasePresenter;
import com.dl7.myapp.view.EmptyLayout;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by long on 2016/8/23.
 * 新闻列表 Presenter
 */
public class NewsListPresenter implements IBasePresenter {

    private static final int MAX_NEWS_LIST_PAGE = 40;
    private static final String NEWS_ITEM_PHOTO_SET = "photoset";


    private INewsListView mView;
    private int mNewsType;

    public NewsListPresenter(INewsListView view, @RetrofitService.NewsType int newsType) {
        this.mView = view;
        this.mNewsType = newsType;
    }

    @Override
    public void loadData() {
        _getData();
    }

    @Override
    public void loadMoreData() {
        _getMoreData();
    }

    /**
     * 获取数据
     */
    private void _getData() {
        mView.showLoading();
        Observable<NewsBean> observable = RetrofitService.getNewsList(mNewsType);
        observable.filter(new Func1<NewsBean, Boolean>() {
            @Override
            public Boolean call(NewsBean newsBean) {
                if (NewsUtils.isAbNews(newsBean)) {
                    mView.loadAdData(newsBean);
                }
                return !NewsUtils.isAbNews(newsBean);
            }
        }).map(new Func1<NewsBean, NewsMultiItem>() {
            @Override
            public NewsMultiItem call(NewsBean newsBean) {
                if (NEWS_ITEM_PHOTO_SET.equals(newsBean.getSkipType())) {
                    return new NewsMultiItem(NewsMultiItem.ITEM_TYPE_PHOTO_SET, newsBean);
                }
                return new NewsMultiItem(NewsMultiItem.ITEM_TYPE_NORMAL, newsBean);
            }
        }).buffer(MAX_NEWS_LIST_PAGE).subscribe(new Subscriber<List<NewsMultiItem>>() {
            @Override
            public void onCompleted() {
                mView.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                mView.showNetError(new EmptyLayout.OnRetryListener() {
                    @Override
                    public void onRetry() {
                        loadData();
                    }
                });
            }

            @Override
            public void onNext(List<NewsMultiItem> newsMultiItems) {
                mView.loadData(newsMultiItems);
            }
        });
    }

    /**
     * 获取更多数据
     */
    private void _getMoreData() {
        RetrofitService.getNewsListNext(mNewsType)
                .map(new Func1<NewsBean, NewsMultiItem>() {
                    @Override
                    public NewsMultiItem call(NewsBean newsBean) {
                        if (NEWS_ITEM_PHOTO_SET.equals(newsBean.getSkipType())) {
                            return new NewsMultiItem(NewsMultiItem.ITEM_TYPE_PHOTO_SET, newsBean);
                        }
                        return new NewsMultiItem(NewsMultiItem.ITEM_TYPE_NORMAL, newsBean);
                    }
                })
                .buffer(MAX_NEWS_LIST_PAGE)
                .subscribe(new Subscriber<List<NewsMultiItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.loadNoData();
                    }

                    @Override
                    public void onNext(List<NewsMultiItem> newsList) {
                        mView.loadMoreData(newsList);
                    }
                });
    }
}
