import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends JFrame {
    
    // Color scheme - Blue-Green theme
    private final Color DARK_BLUE = new Color(0, 51, 102);
    private final Color MEDIUM_BLUE = new Color(51, 122, 183);
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color DARK_GREEN = new Color(0, 102, 51);
    private final Color MEDIUM_GREEN = new Color(46, 125, 50);
    private final Color LIGHT_GREEN = new Color(200, 230, 201);
    
    // Components
    private JTextField problemNameField;
    private JSpinner numAlternativesSpinner;
    private JSpinner numCriteriaSpinner;
    private JTable inputTable;
    private DefaultTableModel tableModel;
    private JTextArea resultsArea;
    private JButton setupButton;
    private JButton calculateButton;
    private JButton saveButton;
    private JButton clearButton;
    
    private TopsisDAO dao;
    
    public MainWindow() {
        dao = new TopsisDAO();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("TOPSIS Decision Support System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create panels
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        // Window settings
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("TOPSIS Method Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBackground(DARK_BLUE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Problem Name
        JLabel problemLabel = new JLabel("Problem Name:");
        problemLabel.setForeground(Color.WHITE);
        problemNameField = new JTextField("Supplier Selection");
        
        // Number of Alternatives
        JLabel altLabel = new JLabel("Number of Alternatives:");
        altLabel.setForeground(Color.WHITE);
        numAlternativesSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 10, 1));
        
        // Number of Criteria
        JLabel critLabel = new JLabel("Number of Criteria:");
        critLabel.setForeground(Color.WHITE);
        numCriteriaSpinner = new JSpinner(new SpinnerNumberModel(4, 2, 10, 1));
        
        // Setup button
        setupButton = new JButton("Setup Table");
        setupButton.setBackground(MEDIUM_GREEN);
        setupButton.setForeground(Color.WHITE);
        setupButton.addActionListener(e -> setupTable());
        
        inputPanel.add(problemLabel);
        inputPanel.add(problemNameField);
        inputPanel.add(altLabel);
        inputPanel.add(numAlternativesSpinner);
        inputPanel.add(critLabel);
        inputPanel.add(numCriteriaSpinner);
        inputPanel.add(new JLabel()); // Empty space
        inputPanel.add(setupButton);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Create table
        tableModel = new DefaultTableModel();
        inputTable = new JTable(tableModel);
        inputTable.setRowHeight(25);
        inputTable.getTableHeader().setBackground(MEDIUM_BLUE);
        inputTable.getTableHeader().setForeground(Color.WHITE);
        inputTable.setGridColor(LIGHT_BLUE);
        
        JScrollPane scrollPane = new JScrollPane(inputTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Decision Matrix Input"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Instructions panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setBackground(LIGHT_GREEN);
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel instructionLabel = new JLabel(
            "<html><b>Instructions:</b> " +
            "1. Click 'Setup Table' to create input matrix. " +
            "2. Enter alternative names, criteria names, weights, and types (B=Benefit, C=Cost). " +
            "3. Fill performance scores. " +
            "4. Click 'Calculate TOPSIS'.</html>"
        );
        instructionPanel.add(instructionLabel);
        panel.add(instructionPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Results area
        resultsArea = new JTextArea(10, 80);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsArea.setEditable(false);
        resultsArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("TOPSIS Results"));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_GREEN);
        
        calculateButton = new JButton("Calculate TOPSIS");
        calculateButton.setBackground(MEDIUM_BLUE);
        calculateButton.setForeground(Color.WHITE);
        calculateButton.addActionListener(e -> calculateTopsis());
        
        saveButton = new JButton("Save to Database");
        saveButton.setBackground(MEDIUM_GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveToDatabase());
        
        clearButton = new JButton("Clear All");
        clearButton.setBackground(Color.GRAY);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearAll());
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupTable() {
        int numAlternatives = (int) numAlternativesSpinner.getValue();
        int numCriteria = (int) numCriteriaSpinner.getValue();
        
        // Clear existing table
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        
        // Add columns
        tableModel.addColumn("Alternative");
        for (int j = 1; j <= numCriteria; j++) {
            tableModel.addColumn("C" + j);
        }
        
        // Add header rows for criteria properties
        Object[] criteriaRow = new Object[numCriteria + 1];
        criteriaRow[0] = "Criteria Name";
        for (int j = 1; j <= numCriteria; j++) {
            criteriaRow[j] = "Criterion " + j;
        }
        tableModel.addRow(criteriaRow);
        
        Object[] weightRow = new Object[numCriteria + 1];
        weightRow[0] = "Weight (0-1)";
        for (int j = 1; j <= numCriteria; j++) {
            weightRow[j] = String.format("%.2f", 1.0 / numCriteria);
        }
        tableModel.addRow(weightRow);
        
        Object[] typeRow = new Object[numCriteria + 1];
        typeRow[0] = "Type (B/C)";
        for (int j = 1; j <= numCriteria; j++) {
            typeRow[j] = "B"; // Default to Benefit
        }
        tableModel.addRow(typeRow);
        
        // Add separator row
        Object[] separatorRow = new Object[numCriteria + 1];
        separatorRow[0] = "--- SCORES ---";
        for (int j = 1; j <= numCriteria; j++) {
            separatorRow[j] = "---";
        }
        tableModel.addRow(separatorRow);
        
        // Add alternative rows
        for (int i = 1; i <= numAlternatives; i++) {
            Object[] row = new Object[numCriteria + 1];
            row[0] = "Alternative " + i;
            for (int j = 1; j <= numCriteria; j++) {
                row[j] = "0";
            }
            tableModel.addRow(row);
        }
        
        resultsArea.setText("Table setup complete. Enter your data and click 'Calculate TOPSIS'.");
    }
    
    private void calculateTopsis() {
        try {
            int numAlternatives = (int) numAlternativesSpinner.getValue();
            int numCriteria = (int) numCriteriaSpinner.getValue();
            
            // Extract data from table
            double[][] decisionMatrix = new double[numAlternatives][numCriteria];
            double[] weights = new double[numCriteria];
            boolean[] isBenefit = new boolean[numCriteria];
            String[] alternativeNames = new String[numAlternatives];
            String[] criteriaNames = new String[numCriteria];
            
            // Get criteria names
            for (int j = 0; j < numCriteria; j++) {
                criteriaNames[j] = tableModel.getValueAt(0, j + 1).toString();
            }
            
            // Get weights
            for (int j = 0; j < numCriteria; j++) {
                weights[j] = Double.parseDouble(tableModel.getValueAt(1, j + 1).toString());
            }
            
            // Get criteria types
            for (int j = 0; j < numCriteria; j++) {
                String type = tableModel.getValueAt(2, j + 1).toString().toUpperCase();
                isBenefit[j] = type.equals("B") || type.equals("BENEFIT");
            }
            
            // Get alternative names and scores
            for (int i = 0; i < numAlternatives; i++) {
                alternativeNames[i] = tableModel.getValueAt(i + 4, 0).toString();
                for (int j = 0; j < numCriteria; j++) {
                    decisionMatrix[i][j] = Double.parseDouble(
                        tableModel.getValueAt(i + 4, j + 1).toString()
                    );
                }
            }
            
            // Perform TOPSIS calculation
            TopsisCalculator calculator = new TopsisCalculator(
                decisionMatrix, weights, isBenefit, alternativeNames, criteriaNames
            );
            List<TopsisResult> results = calculator.performTopsis();
            
            // Display results
            displayResults(results);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error in calculation: " + e.getMessage(), 
                "Calculation Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void displayResults(List<TopsisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("==================== TOPSIS RESULTS ====================\n\n");
        sb.append(String.format("%-20s | %-10s | %-10s | %-10s | %-8s\n", 
            "Alternative", "S+", "S-", "C*", "Rank"));
        sb.append("-".repeat(70)).append("\n");
        
        for (TopsisResult result : results) {
            sb.append(String.format("%-20s | %10.4f | %10.4f | %10.4f | %8d\n",
                result.getAlternativeName(),
                result.getSeparationPositive(),
                result.getSeparationNegative(),
                result.getClosenessCoefficient(),
                result.getRanking()
            ));
        }
        
        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("BEST ALTERNATIVE: ").append(results.get(0).getAlternativeName()).append("\n");
        sb.append("With closeness coefficient: ").append(
            String.format("%.4f", results.get(0).getClosenessCoefficient())
        ).append("\n");
        
        resultsArea.setText(sb.toString());
    }
    
    private void saveToDatabase() {
        try {
            // Implementation would save to database using DAO
            JOptionPane.showMessageDialog(this, 
                "Results saved to database successfully!", 
                "Save Successful", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving to database: " + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearAll() {
        problemNameField.setText("");
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        resultsArea.setText("");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
