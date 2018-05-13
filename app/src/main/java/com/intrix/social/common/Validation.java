package com.intrix.social.common;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.intrix.social.R;


public class Validation {
	private Context _context;

	public Validation(Context _context) {
		this._context = _context;
	}

	/* return true if edit box is not empty otherwise return false */

	public boolean checkEmpty(EditText edit, String editName) {
		String str = edit.getText().toString().trim();
		if (str.length() == 0) {
			toastMsg(editName
					+ " "
					+ _context.getString(R.string.should_not_be_empty)
							.toString());
			return true;
		}
		return false;
	}

	/* return true if edit box is contain spaces otherwise return false */

	public boolean checkSpaces(EditText edit, String editName) {
		String str = edit.getText().toString().trim();
		if (str.contains(" ")) {
			toastMsg(editName + " should not contain space");
			return true;
		}
		return false;
	}

	/* return true if email is valid otherwise return false */

	public boolean checkForEmail(EditText edit, String editName) {
		String str = edit.getText().toString();
		if (android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
			return true;
		}
		toastMsg(editName + " "
				+ _context.getString(R.string.is_not_valid).toString());
		return false;
	}

	/* return true if website is valid otherwise return false */

	public boolean checkForWebsite(EditText edit, String editName) {
		String str = edit.getText().toString();
		if (android.util.Patterns.DOMAIN_NAME.matcher(str).matches()) {

			return true;
		}
		toastMsg(editName + " "
				+ _context.getString(R.string.is_not_valid).toString());
		return false;
	}
	
	 

	public boolean checkForPhone(EditText edit, String editName) {
		String str = edit.getText().toString();
		if (android.util.Patterns.PHONE.matcher(str).matches()) {
			return true;
		}
		toastMsg(editName + " "
				+ _context.getString(R.string.is_not_valid).toString());
		return false;
	}

	/* return true if space exist otherwise return false */
	public boolean checkForSpace(EditText edit, String editName) {
		String str = edit.getText().toString().trim();
		if (str.contains(" ")) {
			toastMsg(editName
					+ " "
					+ _context.getString(R.string.should_not_be_space)
							.toString());
			return true;
		}
		return false;
	}

	/* return true if length is invalid otherwise return false */
	public boolean checkForLength(EditText edit, String editName) {
		String str = edit.getText().toString().trim();
		if (str.length() < 10 || str.length() > 10) {
			toastMsg(editName + " "
					+ _context.getString(R.string.is_not_valid).toString());
			return true;
		}
		return false;
	}

	/* return true if length is invalid otherwise return false */

	public boolean checkForLength(EditText edit, int max, String editName) {
		String str = edit.getText().toString().trim();
		if (str.length() != 10 && str.length() != max) {
			toastMsg(editName + " "
					+ _context.getString(R.string.is_not_valid).toString());
			return true;
		}
		return false;
	}

	/* return true if integer is present otherwise return false */
	public boolean checkForInteger(EditText edit, String editName) {
		String str = edit.getText().toString().trim();
		if (str.matches(".*\\d.*")) {
			toastMsg(editName + " "
					+ _context.getString(R.string.is_not_valid).toString());
			return true;
		}
		return false;
	}

	// ======================================================================//

	public boolean checkSpecialChar(EditText edit, String editName) {
		String str = edit.getText().toString().trim();
		if (!str.matches("[a-zA-Z.? ]*")) {
			toastMsg(editName + " "
					+ _context.getString(R.string.is_not_valid).toString());
			return true;
		}
		return false;
	}

	// ======================================//

	public void toastMsg(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
	}

}
