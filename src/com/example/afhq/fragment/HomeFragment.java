package com.example.afhq.fragment;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.afhq.R;
import com.example.afhq.activity.AntiVirusActivity;
import com.example.afhq.activity.MessageInterceptActivity;
import com.example.afhq.activity.RublishcleanActivity;
import com.example.afhq.activity.TrafficManagerActivity;
import com.example.afhq.base.BaseFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeFragment extends BaseFragment {
	@ViewInject(R.id.fragment_main_trojan)
	private LinearLayout fragment_main_trojan;//ľ���ɱ
	@ViewInject(R.id.messagee_linearlayout)//��������
	private LinearLayout messagee_linearlayout;
	@ViewInject(R.id.traffice_manager)
	private LinearLayout traffice_manager;//�������
	@ViewInject(R.id.ll_rubbish)
	private LinearLayout ll_rubbish;
	
	@Override
	public void initData() {
		fragment_main_trojan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent();
				intent.setClass(getActivity(), AntiVirusActivity.class);
				startActivity(intent);
			}
		});
		messagee_linearlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent();
				intent.setClass(getActivity(), MessageInterceptActivity.class);
				startActivity(intent);
			}
		});
		//�������
		traffice_manager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				TrafficManagerActivity 
				Intent intent=new Intent();
				intent.setClass(context, TrafficManagerActivity.class);
				startActivity(intent);
				
			}
		});
		ll_rubbish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, RublishcleanActivity.class);
				startActivity(intent);				
			}
		});
		
	}
	@Override
	public View initView() {
		view= View.inflate(getActivity(), R.layout.fragment_main, null);
		ViewUtils.inject(this,view);
		return view;
	}

}
