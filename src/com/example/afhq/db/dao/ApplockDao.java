package com.example.afhq.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.afhq.db.ApplockDBOpenHelper;
import com.example.afhq.enums.AppLockType;

public class ApplockDao {
	private ApplockDBOpenHelper helper;
	private Context context;

	public ApplockDao(Context context) {
		this.context = context;
		helper = new ApplockDBOpenHelper(context);
	}
	/**
	 * ��ѯĳ�������Ƿ���Ҫ������
	 * @param packname
	 * @return
	 */
	public boolean find(String packname){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("info", null, "packname=?", new String[]{packname}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯȫ���������İ���
	 * @return
	 */
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("info", new String[]{"packname"}, null, null, null, null, null);
		List<String> packnames = new ArrayList<String>();
		while(cursor.moveToNext()){
			packnames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return packnames;
	}
	
	public String  getStateBypackageName(String packageName){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("info", new String[]{"state"}, "packname=?", new String[]{packageName}, null, null, null);
		while(cursor.moveToNext()){
			return cursor.getString(0);
		}
		cursor.close();
		db.close();
		return null;
	}
	
	
	/**
	 * ���һ�����������������ݿ�
	 * @param packname
	 */
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		values.put("state", AppLockType.UNLOCK);
		db.insert("info", null, values);
		db.close();
		//֪ͨ���ݹ۲������ݱ仯�ˡ�
		context.getContentResolver().notifyChange(Uri.parse("content://com.itheima.mobileguard.applock"), null);
	}
	
	/**
	 * ɾ��һ���������ӳ��������ݿ�ɾ��
	 * @param packname
	 */
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("info", "packname=?", new String[]{packname});
		db.close();
		//֪ͨ���ݹ۲������ݱ仯�ˡ�
		context.getContentResolver().notifyChange(Uri.parse("content://com.itheima.mobileguard.applock"), null);
	}
	
	/**
	 * �������߽������
	 * @param packageName
	 * @param state 0���ܣ�1����
	 */
	public void lockapp(String packageName,Integer state){
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		System.out.println("����״̬��"+packageName+"------------->"+state);
		values.put("state", state);
		db.update("info", values, "packname=?", new String[]{packageName});
		context.getContentResolver().notifyChange(Uri.parse("content://com.itheima.mobileguard.applock"), null);
	}
	

	
}
