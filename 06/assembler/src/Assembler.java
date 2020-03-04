import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Assembler {
	public static void main(String[] args) {
		if (args[0] == null || !args[0].endsWith(".asm")) {
			System.err.println("You need to have a .asm file as an argument");
		} else {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				ArrayList<String> lines = new ArrayList<String>();
				try {
					while (reader.ready()) {
						lines.add(reader.readLine());
					}
				} catch (IOException e) {
				}
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).trim().startsWith("//") || lines.get(i).trim().equals(""))
					{
						lines.remove(i);
						i--;
					}
				}
				for (String line : lines) {
					System.out.println(line);
				}
			} catch (FileNotFoundException e) {
				System.err.println(args[0] + " is an invalid filename-");
			}
		}
	}
}