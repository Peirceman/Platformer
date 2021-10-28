package com.platformer.util;

import com.platformer.main.Block;
import com.platformer.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Arrays;

public class JSONReader {

	public static JSONObject readFile(String resource, boolean isAbsolutePath) throws JSONException, FileNotFoundException {
		JSONTokener t;
		if (!isAbsolutePath) {
			InputStream is = Main.class.getResourceAsStream(resource);

			if (is == null)
				throw new NullPointerException(resource + " not found");

			t = new JSONTokener(is);
		} else {
			t = new JSONTokener(new FileReader(resource));
		}

		return new JSONObject(t);
	}

	public static Block[] JSONArrayToBlockArray(JSONArray array, int length) throws JSONException {
		Block[] level = new Block[length];
		Arrays.fill(level, null);

		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			Block current = new Block(obj.getInt("id"), obj.getInt("x"), obj.getInt("y"));
			level[current.getArrayIndex()] = current;
		}

		return level;
	}
}
