package beta;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class convertJPG {

	public static void main(String[] args) throws IOException, InterruptedException {
		List<String> cmdList = new ArrayList<>();
		cmdList.add("E:\\SourceCode\\Python\\MyStudyProject\\buildPy\\test.png");
		convertJPG(cmdList);
//		集合转换为数组
//		String[] cmdArray = new String[cmdList.size()];
//		cmdArray = cmdList.toArray(cmdArray);s
	}

	public static void convertJPG(List<String> fileList) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("ExProc\\~tem~run.bat"));
			bw.write("ExProc\\ConvertIMGtoJPG.exe ");
			for (String c : fileList) {
				bw.write(c + " ");
			}
			bw.close();
			Desktop.getDesktop().open(new File("ExProc\\~tem~run.bat"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
