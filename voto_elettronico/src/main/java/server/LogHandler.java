package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogHandler {
	private static String path = "log/log.txt";
	
	public static void createLog() {
		try {
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeLog(String mes) throws IOException {
		FileWriter fw = new FileWriter(path, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		try {
            pw.println(dateHandle(LocalDateTime.now()) + " --- " + mes);
			pw.flush();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
				pw.close();
                bw.close();
                fw.close();
		}
	}
	
	private static String dateHandle(LocalDateTime date){
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = date.format(formatter);
        return formattedDateTime;
	}
}