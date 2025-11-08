import java.io.*;
import java.util.*;

public class Reader {

    // This method takes the file path and returns a list of Procces objects
    public static List<Procces> readProcesses(String filePath) {
        List<Procces> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Expected format: PID:BrustTime:Priority;Size
                String[] parts = line.split("[:;]");
                if (parts.length == 4) {
                    try {
                        Procces p = new Procces();
                        p.setPID(Integer.parseInt(parts[0].trim()));
                        p.setBrustTime(Integer.parseInt(parts[1].trim()));
                        p.setPriority(Integer.parseInt(parts[2].trim()));
                        p.setSize(Integer.parseInt(parts[3].trim()));
                        list.add(p);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number in line: " + line);
                    }
                } else {
                    System.err.println("Invalid format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return list;
    }
}