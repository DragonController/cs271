import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Assembler {
	public static void main(String[] args) {
		String args0 = "C:\\Users\\Dragon Controller\\Documents\\ComputerArchitecture\\nand2tetris\\projects\\06\\rect\\Rect.asm";
		if (args0 == null || !args0.endsWith(".asm")) {
			System.err.println("You need to have a .asm file as an argument");
		} else {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args0));
				ArrayList<String> lines = new ArrayList<String>();
				try {
					while (reader.ready()) {
						lines.add(reader.readLine().replace(" ", "").toUpperCase());
					}
					reader.close();
				} catch (IOException e) {
				}
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).contains("//")) {
						lines.set(i, lines.get(i).substring(0, lines.get(i).indexOf("//")));
					}
					if (lines.get(i).equals("")) {
						lines.remove(i);
						i--;
					}
				}
				HashMap<String, Integer> pointers = new HashMap<String, Integer>();
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).startsWith("(") && (lines.get(i).endsWith(")"))) {
						pointers.put(lines.get(i).substring(1, lines.get(i).length() - 1), i);
						lines.remove(i);
						i--;
					}
				}
				pointers.put("SCREEN", 16384);
				pointers.put("KBD", 24576);
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).startsWith("@")) {
						lines.set(i, lines.get(i).substring(1));
						try {
							if (lines.get(i).startsWith("R")) {
								lines.set(i, ("000000000000000" + Integer.toBinaryString(Integer.parseInt(lines.get(i).substring(1)))).substring(Integer.toBinaryString(Integer.parseInt(lines.get(i).substring(1))).length() - 1));
							} else {
								lines.set(i, ("000000000000000" + Integer.toBinaryString(Integer.parseInt(lines.get(i)))).substring(Integer.toBinaryString(Integer.parseInt(lines.get(i))).length() - 1));
							}
						} catch (NumberFormatException e) {
							for (int j = 16; !pointers.containsKey(lines.get(i)); j++) {
								if (!pointers.containsValue(j)) {
									pointers.put(lines.get(i), j);
								}
							}
							lines.set(i,("000000000000000" +  Integer.toBinaryString(pointers.get(lines.get(i)))).substring(Integer.toBinaryString(pointers.get(lines.get(i))).length() - 1));
						}
					} else {
						String dest = "000";
						if (lines.get(i).startsWith("M=")) {
							dest = "001";
							lines.set(i, lines.get(i).substring(2));
						} else if (lines.get(i).startsWith("D=")) {
							dest = "010";
							lines.set(i, lines.get(i).substring(2));
						} else if (lines.get(i).startsWith("MD=")) {
							dest = "011";
							lines.set(i, lines.get(i).substring(3));
						} else if (lines.get(i).startsWith("A=")) {
							dest = "100";
							lines.set(i, lines.get(i).substring(2));
						} else if (lines.get(i).startsWith("AM=")) {
							dest = "101";
							lines.set(i, lines.get(i).substring(3));
						} else if (lines.get(i).startsWith("AD=")) {
							dest = "110";
							lines.set(i, lines.get(i).substring(3));
						} else if (lines.get(i).startsWith("AMD=")) {
							dest = "111";
							lines.set(i, lines.get(i).substring(4));
						}
						String jump = "000";
						if (lines.get(i).endsWith(";JGT")) {
							jump = "001";
						} else if (lines.get(i).endsWith(";JEQ")) {
							jump = "010";
						} else if (lines.get(i).endsWith(";JLT")) {
							jump = "011";
						} else if (lines.get(i).endsWith(";JNE")) {
							jump = "101";
						} else if (lines.get(i).endsWith(";JLE")) {
							jump = "110";
						} else if (lines.get(i).endsWith(";JMP")) {
							jump = "111";
							lines.set(i, lines.get(i).substring(0, lines.get(i).length() - 4));
						}
						String a = "0";
						if (lines.get(i).contains("M")) {
							a = "1";
							lines.set(i, lines.get(1).replace("M", "A"));
						}
						String comp = "101010";
						if (lines.get(i).startsWith("1")) {
							comp = "111111";
						} else if (lines.get(i).startsWith("-1")) {
							comp = "111010";
						} else if (lines.get(i).startsWith("!D")) {
							comp = "001101";
						} else if (lines.get(i).startsWith("!A")) {
							comp = "110001";
						} else if (lines.get(i).startsWith("-D")) {
							comp = "001111";
						} else if (lines.get(i).startsWith("-A")) {
							comp = "110011";
						} else if (lines.get(i).startsWith("D+1")) {
							comp = "011111";
						} else if (lines.get(i).startsWith("A+1")) {
							comp = "110111";
						} else if (lines.get(i).startsWith("D-1")) {
							comp = "001110";
						} else if (lines.get(i).startsWith("A-1")) {
							comp = "110010";
						} else if (lines.get(i).startsWith("D+A")) {
							comp = "000010";
						} else if (lines.get(i).startsWith("D-A")) {
							comp = "010011";
						} else if (lines.get(i).startsWith("A-D")) {
							comp = "000111";
						} else if (lines.get(i).startsWith("D&A")) {
							comp = "000000";
						} else if (lines.get(i).startsWith("D|A")) {
							comp = "010101";
						} else if (lines.get(i).startsWith("D")) {
							comp = "001100";
						} else if (lines.get(i).startsWith("A")) {
							comp = "110000";
						}
						lines.set(i, "111" + a + comp + dest + jump);
					}
				}
				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(args0.substring(0, args0.length() - 3) + "hack"));
					for (int i = 0; i < lines.size(); i++) {
						writer.write(lines.get(i));
						if (i < lines.size() - 1) {
							writer.write("\n");
						}
					}
					writer.close();
				} catch (IOException e) {
				}
			} catch (FileNotFoundException e) {
				System.err.println(args0 + " is an invalid filename-");
			}
		}
	}
}