import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class ProductWriter extends JFrame implements ActionListener {
    JPanel mainPnl, searchPnl, displayPnlL, displayPnlR, controlPnl;
    JTextArea textAreaL, textAreaR;
    JScrollPane scrollPaneL, scrollPaneR;

    JButton addBtn, searchBtn, quitBtn;
    JTextField searchField = new JTextField(10);
    String search = searchField.getText();

    JFileChooser chooser = new JFileChooser();
    File selectedFile;
    String rec = "";
    Path path = Paths.get(System.getProperty("user.dir"));
    ArrayList<String> searchList = new ArrayList<>();

    int totalRecs = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            String ID = JOptionPane.showInputDialog("Enter ID");
            String fName = JOptionPane.showInputDialog("Enter product name");
            String lName = JOptionPane.showInputDialog("Enter product description");
            double yob = Double.parseDouble(JOptionPane.showInputDialog("Enter product price"));
            String record = ID + ", " + fName + ", " + lName + ", " + yob;
            searchList.add(record);
            totalRecs++;
            JOptionPane.showMessageDialog(mainPnl, "Number of records entered: " + totalRecs);
        }

        if (e.getSource() == searchBtn) {
            try (Stream<String> lines = Files.lines(path)) {
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    int line = 0;
                    while (reader.ready()) {
                        rec = reader.readLine();
                        searchList.add(rec);
                        textAreaL.append(rec + "\n");
                        line++;
                        System.out.printf("\nLine %4d %-60s ", line, rec);
                    }

                    reader.close();
                    System.out.println("\n\nData file read!");
                    System.out.println(searchField.getText());
                } else {
                    System.out.println("No file selected!!! ... exiting.\nRun the program again and select a file.");
                }
            } catch (FileNotFoundException ae) {
                System.out.println("File not found!!!");
                JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ae) {
                JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath()))) {
                lines
                        .filter(line -> line.contains(searchField.getText()))
                        .map(x -> x + "\n")
                        .forEach(textAreaR::append);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

        if (e.getSource() == quitBtn) {
            int closer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (closer == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public ProductWriter() {
        setTitle("File Streams");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        add(mainPnl);
        createSearchPnl();
        createDisplayPnlL();
        createDisplayPnlR();
        createControlPnl();
        setVisible(true);

    }

    public void createSearchPnl() {
        searchPnl = new JPanel();
        searchPnl.setLayout(new BorderLayout());
        searchBtn = new JButton("Search");
        searchBtn.addActionListener(this);
        searchPnl.add(searchField, BorderLayout.CENTER);
        searchPnl.add(searchBtn, BorderLayout.EAST);
        mainPnl.add(searchPnl, BorderLayout.NORTH);
    }

    public void createDisplayPnlL() {
        displayPnlL = new JPanel();
        textAreaL = new JTextArea(50, 30);
        scrollPaneL = new JScrollPane(textAreaL);
        displayPnlL.add(scrollPaneL);
        mainPnl.add(displayPnlL, BorderLayout.WEST);
    }

    public void createDisplayPnlR() {
        displayPnlR = new JPanel();
        textAreaR = new JTextArea(50, 30);
        scrollPaneR = new JScrollPane(textAreaR);
        displayPnlR.add(scrollPaneR);
        mainPnl.add(displayPnlR, BorderLayout.EAST);
    }

    public void createControlPnl() {
        controlPnl = new JPanel();
        addBtn = new JButton("Add");
        addBtn.addActionListener(this);
        controlPnl.add(addBtn);
        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(this);
        controlPnl.add(quitBtn);
        mainPnl.add(controlPnl, BorderLayout.SOUTH);
    }
}
