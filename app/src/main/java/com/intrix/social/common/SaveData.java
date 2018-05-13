package com.intrix.social.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveData implements ConstantValue {

	private Context _context;
	private SharedPreferences shared;
	private String SHARED_NAME = "hello_chat_app";
	private Editor edit;

	public SaveData(Context c) {
		_context = c;
		shared = _context.getSharedPreferences(SHARED_NAME,	Context.MODE_PRIVATE);
		edit = shared.edit();
	}

	// ============================================//
	public void save(String key, String value) {
		edit.putString(key, value);
		edit.commit();
	}

	/*
	 * // ============================================// public void save(String
	 * key, int value) { edit.putInt(key, value); edit.commit(); }
	 */

	// ============================================//
	public void saveFloat(String key, float value) {
		edit.putFloat(key, value);
		edit.commit();
	}
	
	// ============================================//
		public void saveInt(String key, int value) {
			edit.putInt(key, value);
			edit.commit();
		}

	// ============================================//
	public void saveBoolean(String key, boolean value) {
		edit.putBoolean(key, value);
		edit.commit();
	}
	// ============================================//
		public void saveLong(String key, Long value) {
			edit.putLong(key, value);
			edit.commit();
		}

	// ============================================//
	public Long getLong(String key) {
		return shared.getLong(key, 0);

	}
	// ============================================//
		public String getString(String key) {
			return shared.getString(key, "");

		}
	// ============================================//
	public int getInt(String key) {
		return shared.getInt(key, 0);

	}

	// ============================================//
	public float getFloat(String key) {
		return shared.getFloat(key, 0);

	}

	// ============================================//
	public boolean getBoolean(String key) {
		return shared.getBoolean(key, false);

	}

	// ============================================//
	public boolean isExist(String key) {
		return shared.contains(key);

	}

	// ============================================//
	public void remove(String key) {

		edit.remove(key);
		edit.commit();

	}

}
