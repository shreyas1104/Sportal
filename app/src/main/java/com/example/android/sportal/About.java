package com.example.android.sportal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Zamaan on 06-04-2018.
 */



public class About extends Fragment implements View.OnClickListener {

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;
    private ImageView iv5;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        iv1 = view.findViewById(R.id.iv1);
        iv2 = view.findViewById(R.id.iv2);
        iv3 = view.findViewById(R.id.iv3);
        iv4 = view.findViewById(R.id.iv4);
        iv5 = view.findViewById(R.id.iv5);

        String url1 = "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/19598998_10213186384557806_1093497167197426585_n.jpg?_nc_cat=0&oh=7f1979e2aaa9aafdb781560d3a082609&oe=5B2D404B";
        String url2 = "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/20638322_10212701527589532_1049241319387559838_n.jpg?_nc_cat=0&oh=71b5cd591a230b271da222fc36e060c8&oe=5B36D7A7";
        String url3 = "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/12027533_916659958415063_1258127485300485858_n.jpg?_nc_cat=0&oh=245584eefb48f6949cdfbe77c918c63e&oe=5B2B2B1A";
        String url4 = "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/26730913_1498495073603296_3476978393706183448_n.jpg?_nc_cat=0&oh=0caca1a084ee76ed7bee061191a527a4&oe=5B60CAC8";
        String url5 = "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/24174607_733529826842742_7522364588857605929_n.jpg?_nc_cat=0&oh=0833f246c292cbb6fb70aa18e88bf065&oe=5B2FE84C";

        Glide.with(this).load(url1).into(iv1);
        Glide.with(this).load(url2).into(iv2);
        Glide.with(this).load(url3).into(iv3);
        Glide.with(this).load(url4).into(iv4);
        Glide.with(this).load(url5).into(iv5);

        return view;
    }


    @Override
    public void onClick(View view) {

    }
}
