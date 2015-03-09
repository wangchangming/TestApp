package com.example.util;

/**
 * 简历数据库，用于存储个人信息
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public MyDatabaseHelper(Context context) {
		super(context, StaticProperty.DATABASENAME, null,
				StaticProperty.DATABASERVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "
				+ StaticProperty.TABLEATTITUDE // 个人表
				+ "("
				+ "id			INTEGER				PRIMARY KEY ," // 自增
				+ "dsg_name 	VARCHAR(50) 		NOT NULL ,"
				+ "dsg_title 	VARCHAR(50)  		NOT NULL ,"
				+ "dsg_tel 			INTEGER  ,"
				+ "dsg_email 		VARCHAR(50)    ,"
				+ "dsg_sex 		INTEGER    ,"
				+ "dsg_level 		INTEGER    ,"
				+ "dsg_photo 		VARCHAR(1000)    ,"
				+ "group_photo 		VARCHAR(1000)    ,"
				+ "group_id 		INTEGER   		NOT NULL ," 
				+ "group_name 		VARCHAR(30)    ,"
				+ "group_theme 		VARCHAR(1000)    ,"
				+ "group_photo_num 	INTEGER 		" + ")";
		String sql2 = "CREATE TABLE "
				+ StaticProperty.TABLEDETAIL // 个人表
				+ "("
				+ "id			INTEGER				PRIMARY KEY ," // 自增
				+ "photo_url 	VARCHAR(500) 		NOT NULL ,"
				+ "photo_infor 	VARCHAR(1000) 		 ,"
				+ "aid 			INTEGER 		NOT NULL " + ")";
		db.execSQL(sql);
		db.execSQL(sql2);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS "+ StaticProperty.TABLEATTITUDE;
		String sql2 = "DROP TABLE IF EXISTS "+ StaticProperty.TABLEDETAIL;
		db.execSQL(sql);
		db.execSQL(sql2);
		this.onCreate(db);

	}

}
