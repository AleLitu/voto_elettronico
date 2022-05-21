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
		File file = new File(path);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		try {
			
			//fw = new FileWriter(file);
			//bw = new BufferedWriter(fw);
			//bw.append(dateHandle(LocalDateTime.now()));
			//pw = new PrintWriter(bw);

            pw.println(dateHandle(LocalDateTime.now()) + "---" + mes);
            //pw.println("Root");
            //pw.println("Ben");
			pw.flush();
			
			//bw.close();
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