package me.jchianelli7.Mine4Me.gui;

import java.util.HashMap;
import java.util.Set;

import javax.swing.DefaultListModel;

public class KeyList {

	private DefaultListModel<String> listModel;
	private HashMap<Integer, String> keys;

	public KeyList() {
		listModel = new DefaultListModel<String>();
		keys = new HashMap<Integer, String>();
	}

	public void addKey(int code, String name) {
		if (getKeysMap().containsKey(code)) {
			return;
		}
		getKeysMap().put(code, name);
		getListModel().addElement(name);
	}

	public void removeKey(int row) {
		String name = getListModel().getElementAt(row);
		getListModel().remove(row);

		for (Integer i : getKeys()) {
			if (getKeysMap().get(i).equals(name)) {
				getKeys().remove(i);
				break;
			}
		}
	}

	public HashMap<Integer, String> getKeysMap() {
		return keys;
	}

	public Set<Integer> getKeys() {
		return getKeysMap().keySet();
	}

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

}
