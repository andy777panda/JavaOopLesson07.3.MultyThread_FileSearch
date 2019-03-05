package net.ukr.andy777;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class SearchFile implements Callable<String> {
	private String fileSearch;
	private File folder;

	public SearchFile(String fileSearch, File folder) {
		super();
		this.fileSearch = fileSearch;
		this.folder = folder;
	}

	public SearchFile() {
		super();
	}

	// метод потоку
	@Override
	public String call() throws Exception {
		return searchFile(this.fileSearch, this.folder);
	}

	public static String searchFile(String fileSearch, File folder) {
		if (folder.isFile()) {
			if (fileSearch.equals(folder.getName()))
				return folder.getAbsolutePath() + System.getProperty("line.separator").toString();
			else
				return "";
		} else {
			String res = "";
			File[] fileArray = folder.listFiles();
			for (File file : fileArray) {
				res += searchFile(fileSearch, file);
			}
			return res;
		}
	}

}