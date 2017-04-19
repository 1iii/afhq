package com.example.afhq.receiver;

import com.example.afhq.db.dao.BlackNumberDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class InnerSmsReceiver extends BroadcastReceiver {
	private BlackNumberDao dao;
	@Override
	public void onReceive(Context context, Intent intent) {
		dao=new BlackNumberDao(context);
		Log.i("InnerSmsReceiver","���ŵ����ˡ�");
		Toast.makeText(context, "���ŵ�����", Toast.LENGTH_SHORT).show();
		//�ж϶��ŵķ������Ƿ��ں������б����棬
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj :objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String sender = smsMessage.getOriginatingAddress();
			
			String mode = dao.findBlockMode(sender);
			if("1".equals(mode)||"2".equals(mode)){
				Log.i("InnerSmsReceiver","���������ű����ء�");
				abortBroadcast();//��ֹ���ŵĹ㲥 �����žͱ����� 
			}
			//�������ء�
			String body = smsMessage.getMessageBody();
			if(body.contains("��Ʊ")){ //���ͷ��Ʊ�����ˡ�
				Log.i("InnerSmsReceiver","���ص�������Ʊ���ţ���ֹ");
				abortBroadcast();//��ֹ���ŵĹ㲥 �����žͱ����� 
			}
		}
	}

}
