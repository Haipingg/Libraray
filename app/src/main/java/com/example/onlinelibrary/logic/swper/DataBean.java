package com.example.onlinelibrary.logic.swper;



import com.example.onlinelibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataBean {
    public Integer imageRes;
    public String imageUrl;
    public String title;
    public int viewType;

    public DataBean(Integer imageRes, String title, int viewType) {
        this.imageRes = imageRes;
        this.title = title;
        this.viewType = viewType;
    }

    public DataBean(String imageUrl, String title, int viewType) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.viewType = viewType;
    }

    public static List<DataBean> getTestData3() {
        List<DataBean> list = new ArrayList<>();
        list.add(new DataBean("https://img11.360buyimg.com/cms/jfs/t4474/138/2882395190/116784/8ce927f4/58f47cbcN567b1a99.jpg", "做好自己", 1));
        list.add(new DataBean("https://img.zcool.cn/community/01d20757fc8b36a84a0d304f64e9f4.jpg@1280w_1l_2o_100sh.jpg", null, 1));
        list.add(new DataBean("https://img11.360buyimg.com/cms/jfs/t4957/75/1771931416/128223/776f6a2c/58f47cbcNe1be93e6.jpg", null, 1));
        list.add(new DataBean("https://img.zcool.cn/community/01818f5a69535aa80120a123df3e2f.jpg@1280w_1l_2o_100sh.jpg", null, 1));
        list.add(new DataBean("https://img.zcool.cn/community/01569d5a695358a8012134663e92d5.jpg@1280w_1l_2o_100sh.jpg", null, 1));
        return list;
    }


    public static List<String> getColors(int size) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            list.add(getRandColor());
        }
        return list;
    }

    /**
     * 获取十六进制的颜色代码.例如  "#5A6677"
     * 分别取R、G、B的随机值，然后加起来即可
     *
     * @return String
     */
    public static String getRandColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        return "#" + R + G + B;
    }
}
