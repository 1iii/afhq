package com.example.afhq.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afhq.R;
import com.example.afhq.adapter.MaliceAppAdapter;
import com.example.afhq.adapter.ScanListAdapter;
import com.example.afhq.db.dao.AntiVirusDao;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.domain.Malice;
import com.example.afhq.engine.AppInfoParser;
import com.example.afhq.entity.ScanInfo;
import com.example.afhq.staticdo.AppController;
import com.example.afhq.utils.Md5Utils;
import com.example.afhq.widget.SelectPicPopupWindow;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AntiVirusActivity extends Activity {
	protected static final int SCANNING = 1;
	protected static final int SCAN_FINISH = 2;
	protected static final int SCAN_BENGIN = 0;

	/**
	 * ��찴ť
	 */
	private static final int BT_SCAN_START=0;//һ�����
	private static final int BT_SCAN_ING=1;//�������
	private static final int BT_SCAN_RESTART=2;//һ�����
	private static final int BT_SCAN_CLEAN=3;//�������

	@ViewInject(R.id.tv_scan_status)
	private TextView tv_scan_status;
	@ViewInject(R.id.startinng)
	private TextView startinng;
	@ViewInject(R.id.isVirus_list)
	private ListView isVirus_list;//ɨ�蹦���б�
	@ViewInject(R.id.virus_package_list)//�����б�
	private ListView virus_package_list;
	private PackageManager pm;
	private boolean flag; //���
	ScanListAdapter scanListAdapter;//�����б�������
	AppInfoParser  t=new AppInfoParser();
	List<AppInfo>appvirus=new ArrayList<AppInfo>();//ľ�����ļ���
	List<AppInfo> appInfos ;//��װ��Ӧ�ó���ļ���
	@ViewInject(R.id.virus_app_number)//����������ǩ
	private TextView virus_app_number;
	@ViewInject(R.id.oneKeydo)//һ�����ﰴť
	private Button oneKeydo;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCAN_BENGIN:
				if(appvirus.size()>0){
					tv_scan_status.setText("Σ��");
					tv_scan_status.setTextColor(Color.RED);
				}else{
					tv_scan_status.setText("��ȫ");
				}
				oneKeydo.setText("һ�����");
				break;
			case SCANNING:
				oneKeydo.setText("�������");
				ScanInfo info = (ScanInfo) msg.obj;
				startinng.setText("����ɨ��:"+info.process+info.appname);
				if(info.process>=100){
					tv_scan_status.setText(99+"%");
				}else{
					tv_scan_status.setText(info.process+"%");
				}

				List<Malice>list=new ArrayList<Malice>();
				setUI(info.isVirus,info.process,list);
				MaliceAppAdapter malicadapter=new MaliceAppAdapter(getApplicationContext(),list);

				isVirus_list.setAdapter(malicadapter);
				if(info.isVirus){
					for(AppInfo a:appInfos){
						if(info.appname.equals(a.getName())){
							appvirus.add(a);
						}
					};
				}

				if(scanListAdapter!=null){
					virus_package_list.setAdapter(scanListAdapter);
				}else{
					scanListAdapter=new ScanListAdapter(getApplicationContext(), appvirus);	
					virus_package_list.setAdapter(scanListAdapter);
				}
				virus_app_number.setText(appvirus.size()+"��");
				TextView child = new TextView(getApplicationContext());
				child.setText(info.appname+":"+info.desc);
				break;
			case SCAN_FINISH:

				if(appvirus.size()>0){
					tv_scan_status.setText("Σ��");
					tv_scan_status.setTextColor(Color.RED);
					oneKeydo.setText("һ�����ж��");
				}else{
					oneKeydo.setText("�������");
					tv_scan_status.setText("��ȫ");
				}
				startinng.setText("ɨ����ϣ�");
				break;
			}
		}
		private void setUI(boolean isVirus,Integer process,List<Malice>list) {
			Malice network=new Malice();
			network.setName("���绷��");
			Malice sysscan=new Malice();
			sysscan.setName("ϵͳ©��");
			Malice virus=new Malice();
			virus.setName("����ľ��");
			Malice paywork=new Malice();
			paywork.setName("֧������");
			Malice userscan=new Malice();
			userscan.setName("�˺Ű�ȫ");
			if(process<10&&process>=0){
				network.setStart("����ɨ��");
			}
			if(process<30&&process>=10){
				if(isVirus){
					network.setStart("Σ��");
				}else{
					network.setStart("��ȫ"); 
				}
				sysscan.setStart("����ɨ��");
			}
			if(process<60&&process>=30){
				network.setStart("��ȫ");
				network.setStart("��ȫ");
				sysscan.setStart("��ȫ");
				virus.setStart("����ɨ��");
			}
			if(process<90&&process>=60){
				network.setStart("��ȫ");
				sysscan.setStart("��ȫ");
				virus.setStart("��ȫ");
				paywork.setStart("����ɨ��");
			}
			if(process<100&&process>=90){
				network.setStart("��ȫ");
				sysscan.setStart("��ȫ");
				virus.setStart("��ȫ");
				paywork.setStart("��ȫ");
				userscan.setStart("����ɨ��");
			}
			if(process>=100){
				network.setStart("��ȫ");
				sysscan.setStart("��ȫ");
				virus.setStart("��ȫ");
				paywork.setStart("��ȫ");
				userscan.setStart("��ȫ");
			}
			list.add(network);
			list.add(sysscan);
			list.add(virus);
			list.add(paywork);
			list.add(userscan);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		tv_scan_status=(TextView) findViewById(R.id.tv_scan_status);
		appInfos = t.getAppInfos(getApplicationContext());
		pm = getPackageManager();
		ViewUtils.inject(this);  
		scanVirus();
		oneKeydoSomething();
	}
	
	public static void main(String[] args) {
		//b7d7ce745fe049a4d3f33e662234ad90
		AntiVirusDao a=new AntiVirusDao();
		a.add("��Ѷ��iiii�Թܼ�", "b7d7ce745fe049a4d3f33e662234ad90");
		
	}
	
	

	private void scanVirus() {
		flag = true;
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				msg.what = SCAN_BENGIN;
				handler.sendMessage(msg);
				// ����ֻ������ÿһ��Ӧ�ó���
				List<PackageInfo> packInfos = pm.getInstalledPackages(0);
				int max = packInfos.size();
				int process = 0;
				ScanInfo scanInfo = new ScanInfo();
				scanInfo.process=0;
				for (PackageInfo info : packInfos) {
					if(!flag){
						return;
					}
					String apkpath = info.applicationInfo.sourceDir;
					// ����ȡ����ļ��� ������
					String md5info = Md5Utils.getFileMd5(apkpath);
                    System.out.println(info.packageName+"md5info="+md5info);
					String result = AntiVirusDao.checkVirus(md5info);//��ȡ����������
					msg = Message.obtain();
					msg.what = SCANNING;
					if (result == null) {
						scanInfo.desc = "ɨ�谲ȫ";
						scanInfo.isVirus = false;
					} else {
						scanInfo.desc = result;
						scanInfo.isVirus = true;
					}
					System.out.println(info.packageName);
					
					scanInfo.packname = info.packageName;
					scanInfo.appname = info.applicationInfo.loadLabel(pm).toString();
					scanInfo.process++;
					msg.obj = scanInfo;
					handler.sendMessage(msg);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		flag = false;
		super.onDestroy();
	}

	/**
	 * һ���ɵ�ɶ
	 */
	private void oneKeydoSomething(){
		oneKeydo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String startStr=oneKeydo.getText().toString();
				if (startStr.equals("һ�����")) {
				}
				if (startStr.equals("�������")) {

				}
				if (startStr.equals("�������")) {

				}
				if (startStr.equals("һ�����ж��")) {
					Toast.makeText(getApplicationContext(), "������", Toast.LENGTH_LONG).show();
					for(AppInfo info:appvirus){
						boolean isuninsrall= AppController.clientUninstall(info.getPackname());
						if(isuninsrall){
							appvirus.remove(info);
						}
					}
					scanListAdapter.notifyDataSetChanged();
					Toast.makeText(getApplicationContext(), "���", Toast.LENGTH_LONG).show();

				}
			}
		});
	}

	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		//if(keyCode==KeyEvent.KEYCODE_BACK){
	//			SelectPicPopupWindow menuWindow=new SelectPicPopupWindow(this, new OnClickListener() {
	//				@Override
	//				public void onClick(View v) {
	//				}
	//			});
	//			menuWindow.showAsDropDown(AntiVirusActivity.this.findViewById(R.id.miain), 0, 0);
	//		//}
	//		return true;
	//	}
}
