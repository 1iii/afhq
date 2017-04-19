package com.example.afhq.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.afhq.domain.ContactInfo;
/**
 * ��ȡȫ����ϵ��
 * @author �Ľ�
 *
 */
public class ContactInfoParser {

	/**
	 * ��ȡϵͳȫ����ϵ�˵�API����
	 * 
	 * @param context
	 * @return
	 */
	public static List<ContactInfo> findAll(Context context) {
		ContentResolver resolver = context.getContentResolver();
		// 1. ��ѯraw_contacts������ϵ�˵�idȡ����
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			if (id != null) {
				ContactInfo info = new ContactInfo();
				info.setId(id);
				// 2. ������ϵ�˵�id����ѯdata�������id������ȡ����
				// ϵͳapi ��ѯdata���ʱ�� ���������Ĳ�ѯdata�� ���ǲ�ѯ��data�����ͼ
				Cursor dataCursor = resolver.query(datauri, new String[] {
						"data1", "mimetype" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(0);
					String mimetype = dataCursor.getString(1);
					if ("vnd.android.cursor.item/name".equals(mimetype)) {
						info.setName(data1);
					} else if ("vnd.android.cursor.item/email_v2"
							.equals(mimetype)) {
						info.setEmail(data1);
					} else if ("vnd.android.cursor.item/phone_v2"
							.equals(mimetype)) {
						info.setPhone(data1);
					} else if ("vnd.android.cursor.item/im".equals(mimetype)) {
						info.setQq(data1);
					}
				}
				infos.add(info);
				dataCursor.close();
			}
		}
		cursor.close();
		return infos;
	}
}
