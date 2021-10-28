package com.platformer.util;

import com.platformer.main.Block;
import com.platformer.main.Game;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class JSONWriter {
 	
	public static void write(JSONObject obj) throws IOException{
		File file = new File(Game.testJsonPath);
		FileWriter writer = new FileWriter(file);
 	 	writer.write(obj.toString());
 	 	writer.close();
 	}

 	/*static public String formatJson(String Json) {
 	 	int amountOfTabs = 0;
 	 	StringBuilder str = new StringBuilder(Json);

 	 	for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);
			switch (current) {
				case '{', '[' -> {
					amountOfTabs++;
					str.replace(i, i + 1, current + "\n" + "\t".repeat(amountOfTabs));
					i += amountOfTabs + 1;
				}
				case '}', ']' -> {
					amountOfTabs--;
					str.replace(i, i + 1, "\n" + "\t".repeat(amountOfTabs) + current);
					i += amountOfTabs + 1;
				}
				case ',' -> {
					str.replace(i, i + 1, ",\n" + "\t".repeat(amountOfTabs));
					i += amountOfTabs + +1;
				}
				case ':' -> {
					str.replace(i, i + 1, " : ");
					i += 2;
				}
			}
		}

 	 	return str.toString();
 	}*/

 	public static JSONArray blockArrayToJSONArray(Block[] array) throws JSONException{
 	 	JSONArray jArray = new JSONArray();


 	 	for (Block block : array) {
 	 	 	if (block == null) continue;
 	 	 	JSONObject object = new JSONObject();

 	 	 	try {
 	 	 	 	object.put("id", block.getId());
 	 	 	 	object.put("x", block.getX());
 	 	 	 	object.put("y", block.getY());
 	 	 	} catch (JSONException e) {
 	 	 	 	e.printStackTrace();
 	 	 	}

 	 	 	jArray.put(object);
 	 	}

 	 	return jArray;
 	}
}
