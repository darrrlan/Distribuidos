/**
 * Lab0: Leitura de Base de Dados Não-Distribuida
 * 
 * Autor: Darlan Oliveira Santos e Sandro Pinheiro Christe
 * Ultima atualizacao: 26/03/2024
 * 
 * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * 
 */

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Principal_v0 {

	public final static Path path = Paths			
			.get("src\\fortune-br.txt");
	private int NUM_FORTUNES = 0;

	public class FileReader {

		public int countFortunes() throws FileNotFoundException {

			int lineCount = 0;

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();

				}// fim while

				System.out.println(lineCount);
			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
			return lineCount;
		}

		public void parser(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				int lineCount = 0;

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();
					StringBuffer fortune = new StringBuffer();
					while (!(line == null) && !line.equals("%")) {
						fortune.append(line + "\n");
						line = br.readLine();
						// System.out.print(lineCount + ".");
					}

					hm.put(lineCount, fortune.toString());
					System.out.println(fortune.toString());

					System.out.println(lineCount);
				}// fim while

			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
		}

		public void read(HashMap<Integer, String> hm) throws FileNotFoundException {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            	 String line;
                 while ((line = br.readLine()) != null) {
                     System.out.println(line);
                 }
            } catch (IOException e) {
            	System.out.println("SHOW: Excecao na leitura do arquivo.");
            }
        }

		public void write(HashMap<Integer, String> hm) throws FileNotFoundException {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile()))) {
                for (String value : hm.values()) {
                    bw.write(value);
                    bw.newLine();
                    bw.write("%");
                }
            } catch (IOException e) {
                System.out.println("SHOW: Excecao na escrita do arquivo.");
            }
        }
	}

	public void iniciar() {

		FileReader fr = new FileReader();
		try {
			NUM_FORTUNES = fr.countFortunes();
			HashMap hm = new HashMap<Integer, String>();
			fr.parser(hm);
			fr.read(hm);
			fr.write(hm);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Principal_v0().iniciar();
	}

}