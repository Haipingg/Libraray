package com.example.onlinelibrary.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.BinderThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlinelibrary.R;
import com.example.onlinelibrary.logic.swper.DataBean;
import com.example.onlinelibrary.logic.swper.ImageAdapter;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;


public class SwiperActivity extends AppCompatActivity {

    Banner banner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swaper);
        useBanner();
    }

    public void useBanner() {
        banner = findViewById(R.id.banner);

        //—————————————————————————是图片轮播————————————————————————
        banner.setAdapter(new BannerImageAdapter<DataBean>(DataBean.getTestData3()) {
            @Override
            public void onBindView(BannerImageHolder holder, DataBean data, int position, int size) {
                //图片加载自己实现
                Glide.with(holder.itemView)
                        .load(data.imageUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }
        })
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(this));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁
        banner.destroy();
    }

}
