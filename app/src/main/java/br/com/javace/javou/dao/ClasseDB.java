package br.com.javace.javou.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.javace.javou.util.Constant;

public class ClasseDB extends SQLiteOpenHelper {

	private Context mContext;
	private static ClasseDB mInstance = null;

	public ClasseDB(Context context) {
		super(context, Constant.DATABASE, null, Constant.DATABASE_VERSION);
        mContext = context;
	}
	
	public static synchronized ClasseDB getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ClasseDB(context.getApplicationContext());
		}
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {

		String TAG = "ClasseDB";
		try {

    		String[] tables = Constant.CREATE_TABLE();
			for (String table : tables) {
				database.execSQL(table);
				Log.d(TAG, "Criando tabela: " + table);
			}
    		
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "O banco parou");
		}
	}
		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
