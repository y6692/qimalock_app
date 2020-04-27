package cn.jock.pickerview.view.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qimalocl.manage.model.RegionInfo;

import java.util.ArrayList;
import java.util.List;


public class RegionDAO {

	public static List<RegionInfo> getProvencesOrCityOnId(int id) {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();// String.valueOf(type)
		Cursor cursor = db.rawQuery("select * from REGIONS where _id=" + id, null);

		while (cursor.moveToNext()) {
			RegionInfo regionInfo = new RegionInfo();
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int parent = cursor.getInt(cursor.getColumnIndex("parent"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int type1 = cursor.getInt(cursor.getColumnIndex("type"));
			regionInfo.setId(_id);
			regionInfo.setParent(parent);
			regionInfo.setName(name);
			regionInfo.setType(type1);
			regionInfos.add(regionInfo);
		}
		cursor.close();
		db.close();
		return regionInfos;
	}

	public static List<RegionInfo> getProvencesOrCity(int type) {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();// String.valueOf(type)
		Cursor cursor = db.rawQuery("select * from REGIONS where type=" + type, null);

		while (cursor.moveToNext()) {
			RegionInfo regionInfo = new RegionInfo();
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int parent = cursor.getInt(cursor.getColumnIndex("parent"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int type1 = cursor.getInt(cursor.getColumnIndex("type"));
			regionInfo.setId(_id);
			regionInfo.setParent(parent);
			regionInfo.setName(name);
			regionInfo.setType(type1);
			regionInfos.add(regionInfo);
		}
		cursor.close();
		db.close();
		return regionInfos;
	}

	public static List<RegionInfo> getProvencesOrCityOnParent(int parent) {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();// String.valueOf(type)
		Cursor cursor = db.rawQuery("select * from REGIONS where parent=" + parent, null);

		while (cursor.moveToNext()) {
			RegionInfo regionInfo = new RegionInfo();
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int parent1 = cursor.getInt(cursor.getColumnIndex("parent"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			regionInfo.setId(_id);
			regionInfo.setParent(parent1);
			regionInfo.setName(name);
			regionInfo.setType(type);
			regionInfos.add(regionInfo);
		}
		cursor.close();
		db.close();
		return regionInfos;
	}

	// 插入 ，不用
	public static void insertRegion(SQLiteDatabase db, RegionInfo ri) {
		ContentValues values = new ContentValues();
		values.put("parent", ri.getParent());
		values.put("name", ri.getName());
		values.put("type", ri.getType());
		db.insert("REGIONS", null, values);
	}

	// 返回所有的省市信息
	public static List<RegionInfo> queryAllInfo() {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();
		Cursor cursor = db.rawQuery("select * from REGIONS", null);

		while (cursor.moveToNext()) {
			RegionInfo regionInfo = new RegionInfo();
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int parent = cursor.getInt(cursor.getColumnIndex("parent"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			regionInfo.setId(_id);
			regionInfo.setParent(parent);
			regionInfo.setName(name);
			regionInfo.setType(type);

			regionInfos.add(regionInfo);
		}
		cursor.close();
		db.close();
		return regionInfos;
	}

	public static RegionInfo querySingleRemind(SQLiteDatabase db, int _id) {
		String sql = "select * from remindtable where _id =" + _id;
		Cursor cursor = db.rawQuery(sql, null);
		RegionInfo remind = null;
		if (cursor.moveToNext()) {

		}
		cursor.close();
		return remind;
	}

	public static void deleteRegion(int _id, SQLiteDatabase db) {
		db.execSQL("delete from remindtable where _id = ?", new Object[] { _id });
	}

}
