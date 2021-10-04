package com.example.onlinelibrary.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.swper.DataBean
import com.example.onlinelibrary.logic.swper.ImageAdapter
import com.youth.banner.Banner
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.fragment_inbox.*


class InboxFragment : Fragment() {
    var mContext : Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }
    /**这里是轮播图插件 */
    var newbanner: Banner<*, *>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**这里是轮播图插件 */
        super.onViewCreated(view, savedInstanceState)
        newbanner?.setAdapter(ImageAdapter(DataBean.getTestData3()) as Nothing?)
        newbanner?.indicator ?:   RectangleIndicator(activity)
        newbanner?.setIndicatorSpace(BannerUtils.dp2px(4f))
        newbanner?.setIndicatorRadius(0)

    }



}