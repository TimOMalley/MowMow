/**
 * 
 */
package com.twelvelouisiana.android.soundboard.mowmow;

import java.io.Serializable;

/**
 * @author Tim O'Malley
 *
 */
public class SoundBoardBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int _rawId;
	private String _name;
	private String _title;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param name
	 * @param title
	 */
	public SoundBoardBean(int id, String name, String title) {
		_rawId = id;
		_name = name;
		_title = title;
	}

	/**
	 * @return the id
	 */
	public int getRawId() {
		return _rawId;
	}

	/**
	 * @param id the id to set
	 */
	public void setRawId(int id) {
		_rawId = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		_title = title;
	}

}
