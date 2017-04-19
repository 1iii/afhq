package com.example.afhq.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.afhq.R;
import com.example.afhq.db.dao.BlackNumberDao;
import com.example.afhq.domain.BlackNumberInfo;
import com.example.afhq.domain.ContactInfo;
import com.example.afhq.engine.ContactInfoParser;
import com.example.afhq.fragment.MessageinterceptFragment;
import com.example.afhq.service.CallSmsSafeService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MessageInterceptActivity extends FragmentActivity{
	@ViewInject(R.id.bt_inpeople)
	private Button bt_inpeople;//����ϵ�˵���
	@ViewInject(R.id.titleBarRightImage_add)//�������
	private  ImageView  titleBarRightImage_add;
	@ViewInject(R.id.titleBarLeftImage_break)//�������
	private ImageView titleBarLeftImage_break;
	@ViewInject(R.id.message_intercept_list)
	private ListView message_intercept_list;
	@ViewInject(R.id.ll_add_number_tips)
	private LinearLayout ll_add_number_tips;
	@ViewInject(R.id.ll_loading)
	private LinearLayout ll_loading;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> infos=new ArrayList<BlackNumberInfo>(); // ������ǵ�ǰ����ļ��ϡ�

	private CallSmsSafeAdapter adapter;

	/**
	 * ��ʼ��ȡ���ݵ�λ��
	 */
	private int startIndex = 0;

	/**
	 * һ������ȡ��������
	 */
	private int maxCount = 20;
	private int totalCount = 0;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_message_intercept);
		ViewUtils.inject(this);
		titleBarRightImage_add.setVisibility(View.VISIBLE);
		titleBarLeftImage_break.setVisibility(View.VISIBLE);
		dao = new BlackNumberDao(this);
		addIntercept();
		fillData();
		importPerple();
	}
	
	@Override
	protected void onStart() {
		CallSmsSafeService c=new CallSmsSafeService();
		Intent intent=new Intent();
		intent.setClass(this, CallSmsSafeService.class);
		startService(intent);
		super.onStart();
	}
	/**
	 * ���з������ݵ�
	 */
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			Toast.makeText(getApplicationContext(), "����������ɹ�",Toast.LENGTH_SHORT).show();
		String json=data.getStringExtra("import");
		List<ContactInfo>contactInfos=JSON.parseArray(json, ContactInfo.class);
		for(ContactInfo ifo:contactInfos){
			 boolean result=	dao.add(ifo.getPhone(), "3");
				// ˢ�½��档 �����ݼ��뵽infos�������档
				if (result) {
					BlackNumberInfo info = new BlackNumberInfo();
					info.setMode("3");
					info.setNumber(ifo.getPhone());
					infos.add(0, info);// ��������ݼ��Ϸ����˱仯��
					// ֪ͨ����ˢ�¡�
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					} else {
						adapter = new CallSmsSafeAdapter(getApplicationContext(), infos,handler);
						message_intercept_list.setAdapter(adapter);
					}
				}     
		     }
		
			break;
		default:
			break;
		}
	}
	
	private void importPerple() {
		bt_inpeople.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), ImportPerpleActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}
	/**
	 * ��Ӷ�������
	 * 
	 */
	private void addIntercept() {
		titleBarRightImage_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addBlackNumber(v);
			}
		});

	}

	/**
	 * ��Ӻ���������
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);
		final AlertDialog dialog = builder.create();
		final EditText et_black_number = (EditText) dialogView
				.findViewById(R.id.et_black_number);
		final CheckBox cb_phone = (CheckBox) dialogView
				.findViewById(R.id.cb_phone);
		final CheckBox cb_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
		dialogView.findViewById(R.id.bt_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		/**
		 * ����
		 */
		dialogView.findViewById(R.id.bt_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String blackNumber = et_black_number.getText()
								.toString().trim();
						if (TextUtils.isEmpty(blackNumber)) {
							Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 1)
							.show();
							return;
						}
						String mode = "0";
						// 1 ȫ������ 2 �������� 3 �绰����
						if (cb_phone.isChecked() && cb_sms.isChecked()) {
							mode = "1";
						} else if (cb_phone.isChecked()) {
							mode = "3";
						} else if (cb_sms.isChecked()) {
							mode = "2";
						} else {
							Toast.makeText(getApplicationContext(), "��ѡ������ģʽ",
									1).show();
							return;
						}
						// ��������ӵ����ݿ�
						boolean result = dao.add(blackNumber, mode);
						// ˢ�½��档 �����ݼ��뵽infos�������档
						if (result) {
							BlackNumberInfo info = new BlackNumberInfo();
							info.setMode(mode);
							info.setNumber(blackNumber);
							infos.add(0, info);// ��������ݼ��Ϸ����˱仯��
							// ֪ͨ����ˢ�¡�
							if (adapter != null) {
								adapter.notifyDataSetChanged();
							} else {
								adapter = new CallSmsSafeAdapter(getApplicationContext(), infos,handler);
								message_intercept_list.setAdapter(adapter);
							}
						}
						dialog.dismiss();
					}
				});
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}

	private void fillData(){
		dao = new BlackNumberDao(this);
		totalCount = dao.getTotalNumber();
		if(totalCount==0){
			//û�кڵ�����
			Toast.makeText(getApplicationContext(), "���޼�¼", Toast.LENGTH_SHORT).show();
		}else{
			// ���ݿ������Ŀ���� / ÿ��ҳ�������ʾ����������
			// ��ʱ�Ĳ��� �߼�Ӧ�÷������߳�����ִ�С�
			new Thread() {
				public void run() {
					if (infos == null) {
						infos = dao.findPart2(startIndex, maxCount);
					} else {
						// ��������ԭ��������,�µ�����Ӧ�÷��ھɵļ��ϵĺ��档
						infos.addAll(dao.findPart2(startIndex, maxCount));
					}
					Message message=Message.obtain();
					message.what=010;
					message.obj=infos;
					handler.sendMessage(message);
				};
			}.start();
		}
	}
	/**
	 * ��Ϣ������
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==010){
				if (infos.size() == 0){
					// û�����ݣ�����������ݵ�����
				} else {
					//if (adapter == null) {
					adapter = new CallSmsSafeAdapter(getApplicationContext(), (List<BlackNumberInfo>) msg.obj,handler);
					message_intercept_list.setAdapter(adapter);
					//} else {// �������������Ѿ����ڵġ�
					// ��Ϊ������������������� �Ѿ��仯��ˢ�½��档
					adapter.notifyDataSetChanged();
					//}
				}
				if (msg.what==011) {
					adapter.notifyDataSetChanged();
				}
			};
		}
		
		
	};
	
	
	
	
	/**
	 * ������������
	 * @author �Ľ�
	 *
	 */
	class CallSmsSafeAdapter  extends BaseAdapter {
		Context context;
		List<BlackNumberInfo> infos;
		Handler handler;
		 BlackNumberDao dao;
		public CallSmsSafeAdapter(Context context,List<BlackNumberInfo> infos,Handler handler) {
		 this.context=context;
		 this.infos=infos;
		 this.handler=handler;
		  dao = new BlackNumberDao(context);
		}
		@Override
		public int getCount() {
			
			return infos.size();
		}
		// �������Ҫ��ִ�кܶ�Σ� �ж����Ŀ ��Ҫִ�ж��ٴ�
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(context,
						R.layout.item_callsms, null);
				holder = new ViewHolder(); // �����Ӻ��Ӳ�ѯ�Ĵ���
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_item_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_item_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				// �Ѻ���id������ �����holder���棬���ø����� view
				view.setTag(holder);
			} else {
				view = convertView; // ʹ����ʷ����view���� ����view���󱻴����Ĵ���
				holder = (ViewHolder) view.getTag();
			}
			final BlackNumberInfo info = infos.get(position);
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					// �����ݿ�ɾ������������
					boolean result = dao.delete(number);
					if (result) {
						Toast.makeText(context, "ɾ���ɹ�", 0).show();
						// �ӽ���ui����ɾ����Ϣ
						infos.remove(info);
						// ֪ͨ����ˢ��
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(context, "ɾ��ʧ��", 0).show();
					}
				}
			});
			
			holder.tv_phone.setText(info.getNumber());
			// 1 ȫ������ 2 �������� 3 �绰����
			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("ȫ������");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("�������� ");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("�绰���� ");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		class ViewHolder {
			TextView tv_phone;
			TextView tv_mode;
			ImageView iv_delete;
		}

	}

	
}
