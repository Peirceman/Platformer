package com.platformer.util;

import com.platformer.main.Block;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter {
 	
	public static void write(String resource, JSONObject obj) throws IOException{
		FileWriter writer = new FileWriter(resource);
 	 	writer.write(obj.toString());
 	 	writer.close();
 	}

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
