package com.zorgan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

public class SaveAndLoad {
	
	//load game
	public String[] loadGame(File file) throws IOException {
		String str;
		String[] lines;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			str = in.readLine();
			byte[] asBytes = Base64.getDecoder().decode(str);
			str = new String(asBytes, "utf-8");
			lines = str.split("\\r?\\n");
		} catch (FileNotFoundException e) {
			return null;
		}
		for (int i = 0; i < lines.length; i++) {
			System.out.println(lines[i]);
		}
		return lines;
	}

	//save game
	public void saveGame(String content, File file) {
		try {
			String contentEncoded = Base64.getEncoder().encodeToString(content.getBytes("utf-8"));
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(file);
			fileWriter.write(contentEncoded);
			fileWriter.close();
		} catch (Exception e) {
			return;
		}
	}
}
