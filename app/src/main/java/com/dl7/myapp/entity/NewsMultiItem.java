package com.dl7.myapp.entity;

import android.support.annotation.IntDef;

import com.dl7.helperlibrary.entity.MultiItemEntity;
import com.dl7.myapp.api.bean.NewsBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by long on 2016/8/24.
 * 新闻复用列表项
 */
public class NewsMultiItem extends MultiItemEntity {

    public static final int ITEM_TYPE_NORMAL = 1;
    public static final int ITEM_TYPE_PHOTO_SET = 2;

    private NewsBean mNewsBean;

    public NewsMultiItem(@NewsItemType int itemType, NewsBean newsBean) {
        super(itemType);
        mNewsBean = newsBean;
    }

    public NewsBean getNewsBean() {
        return mNewsBean;
    }

    public void setNewsBean(NewsBean newsBean) {
        mNewsBean = newsBean;
    }

    @Override
    public void setItemType(@NewsItemType int itemType) {
        super.setItemType(itemType);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ITEM_TYPE_NORMAL, ITEM_TYPE_PHOTO_SET})
    public @interface NewsItemType {}
}
