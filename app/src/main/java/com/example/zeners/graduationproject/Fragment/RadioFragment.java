package com.example.zeners.graduationproject.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zeners.graduationproject.Info.Music;
import com.example.zeners.graduationproject.Info.MusicMeta;
import com.example.zeners.graduationproject.PlayAcitivty;
import com.example.zeners.graduationproject.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by Zeners on 2018/2/4.
 */

public class RadioFragment extends Fragment {
    private static final String TAG = "RadioFragment";
    private List<Map<String, Object> > listData = new ArrayList<>();
    private String dataUrl;
    private Context context;
    private ListView listView;
    private SimpleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String id;
    private boolean isLoaded = false;
    private View view;

//    public static Fragment getInstance(int urlId, String url) {
//        RadioFragment instance = new RadioFragment();
//        Bundle args = new Bundle();
//        args.putInt("id", urlId);
//        args.putString("url", url);
//        instance.setArguments(args);
//
//        return instance;
//    }



    
    public RadioFragment() {
        Log.d(TAG, "RadioFragment: non-parameter constructor executing...");
    }

    // FIXME: 2018/2/4 用Suppress总感觉不对，应该要先实例化这个Fragment才行，待后续查资料
    @SuppressLint("ValidFragment")
    public RadioFragment(Context context, String id) {
        this.id = id;
        this.context = context;
        dataUrl = "http://album.kuwo.cn/album/servlet/commkdtpage?flag=2&rn=50&listid=" + id;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Log.i(TAG, "onInflate: " + id );
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (isLoaded) return view;
        View view = inflater.inflate(R.layout.ftagment_show, container, false);
        listView = view.findViewById(R.id.list_item);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        setSwipeRefreshLayout(swipeRefreshLayout);
        getData();

        adapter = new SimpleAdapter(context, listData, R.layout.listview_item, new String[]{"id", "name", "artist"},
                new int[]{R.id.tx_music_id, R.id.txtMusicName, R.id.txtMusicArtist});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new onItemClickListener() );
        isLoaded = true; //view初始化完成
        this.view = view;
        return view;
    }

    private SwipeRefreshLayout setSwipeRefreshLayout(SwipeRefreshLayout layout) {
        layout.setColorSchemeResources(R.color.swipe_color_1,
                R.color.swipe_color_2,
                R.color.swipe_color_3,
                R.color.swipe_color_4);
        layout.setSize(SwipeRefreshLayout.DEFAULT);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "*** start refreshing ***");
                getData();
            }
        });

        return layout;
    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        new AsyncHttpClient().get(dataUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == HttpStatus.SC_OK) {
                    Log.i(TAG, "*** receive success ***");
                    MusicMeta musicMeta = new Gson().fromJson(new String(responseBody), MusicMeta.class);
                    listData.clear();

                    for (Music music : musicMeta.getMusicList() ) {
                        String name = music.getName();
                        if (name.contains("(") ) { //小写的"("
                            name = name.substring(0, name.indexOf("(") );
                        }else if (name.contains("（") ) { //大写的"（"
                            name = name.substring(0, name.indexOf("（"));
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", music.getId() );
                        map.put("name", name);
                        map.put("artist", music.getArtist() );
                        listData.add(map);
                    }

                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    Log.i(TAG, "*** off the refreshing ***");

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "*** failed to get the data! ***" );
            }
        });
    }


    class onItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String mId = ((TextView) view.findViewById(R.id.tx_music_id) ).getText().toString();
            String mName = ((TextView) view.findViewById(R.id.tx_music_name) ).getText().toString();
            Intent intent = new Intent(context, PlayAcitivty.class);
            intent.putExtra("id", mId);
            intent.putExtra("name", mName);
            startActivity(intent);
        }
    }








}












































