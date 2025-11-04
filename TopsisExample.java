import java.util.List;

public class TopsisExample {
    
    public static void main(String[] args) {
        System.out.println("=== TOPSIS Method Example ===\n");
        
        // Example: Supplier Selection
        // 3 alternatives (suppliers), 4 criteria
        
        // Decision matrix (alternatives x criteria)
        double[][] decisionMatrix = {
            {3, 7, 8, 6},  // Supplier A1: Cost=3, Reliability=7, Warranty=8, Lead Time=6
            {4, 9, 6, 7},  // Supplier A2: Cost=4, Reliability=9, Warranty=6, Lead Time=7
            {2, 6, 9, 5}   // Supplier A3: Cost=2, Reliability=6, Warranty=9, Lead Time=5
        };
        
        // Criteria weights (should sum to 1)
        double[] weights = {0.3, 0.3, 0.2, 0.2}; // Cost=30%, Reliability=30%, Warranty=20%, Lead Time=20%
        
        // Criteria types: true = benefit (higher is better), false = cost (lower is better)
        boolean[] isBenefit = {
            false,  // Cost - lower is better
            true,   // Reliability - higher is better
            true,   // Warranty - higher is better
            false   // Lead Time - lower is better
        };
        
        // Alternative names
        String[] alternativeNames = {"Supplier A1", "Supplier A2", "Supplier A3"};
        
        // Criteria names
        String[] criteriaNames = {"Cost", "Reliability", "Warranty", "Lead Time"};
        
        // Create calculator and perform TOPSIS
        TopsisCalculator calculator = new TopsisCalculator(
            decisionMatrix, weights, isBenefit, alternativeNames, criteriaNames
        );
        
        // Print input data
        System.out.println("INPUT DATA:");
        System.out.println("-----------");
        System.out.println("\nDecision Matrix:");
        System.out.printf("%-15s", "Alternative");
        for (String criterion : criteriaNames) {
            System.out.printf("%-12s", criterion);
        }
        System.out.println();
        
        for (int i = 0; i < alternativeNames.length; i++) {
            System.out.printf("%-15s", alternativeNames[i]);
            for (int j = 0; j < criteriaNames.length; j++) {
                System.out.printf("%-12.1f", decisionMatrix[i][j]);
            }
            System.out.println();
        }
        
        System.out.println("\nCriteria Weights:");
        for (int i = 0; i < criteriaNames.length; i++) {
            System.out.printf("%s: %.1f%% (%s)\n", 
                criteriaNames[i], 
                weights[i] * 100,
                isBenefit[i] ? "Benefit" : "Cost"
            );
        }
        
        // Perform TOPSIS calculation
        List<TopsisResult> results = calculator.performTopsis();
        
        // Print step-by-step results
        System.out.println("\n\nTOPSIS CALCULATION STEPS:");
        System.out.println("-------------------------");
        
        // Step 2: Normalized matrix
        System.out.println("\nStep 2: Normalized Decision Matrix:");
        double[][] normalized = calculator.normalizeMatrix();
        printMatrix(normalized, alternativeNames, criteriaNames);
        
        // Step 3: Weighted normalized matrix
        System.out.println("\nStep 3: Weighted Normalized Matrix:");
        double[][] weighted = calculator.calculateWeightedMatrix(normalized);
        printMatrix(weighted, alternativeNames, criteriaNames);
        
        // Step 4: Ideal solutions
        System.out.println("\nStep 4: Ideal and Negative-Ideal Solutions:");
        double[] ideal = calculator.getIdealSolution(weighted);
        double[] negativeIdeal = calculator.getNegativeIdealSolution(weighted);
        
        System.out.printf("%-15s", "A+ (Ideal):");
        for (double val : ideal) {
            System.out.printf("%-12.4f", val);
        }
        System.out.println();
        
        System.out.printf("%-15s", "A- (Neg-Ideal):");
        for (double val : negativeIdeal) {
            System.out.printf("%-12.4f", val);
        }
        System.out.println();
        
        // Final results
        System.out.println("\n\nFINAL RESULTS:");
        System.out.println("--------------");
        System.out.println("\n" + String.format("%-20s | %-10s | %-10s | %-10s | %-8s", 
            "Alternative", "S+", "S-", "C*", "Rank"));
        System.out.println("-".repeat(70));
        
        for (TopsisResult result : results) {
            System.out.println(String.format("%-20s | %10.4f | %10.4f | %10.4f | %8d",
                result.getAlternativeName(),
                result.getSeparationPositive(),
                result.getSeparationNegative(),
                result.getClosenessCoefficient(),
                result.getRanking()
            ));
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("BEST ALTERNATIVE: " + results.get(0).getAlternativeName());
        System.out.println("Closeness Coefficient: " + 
            String.format("%.4f", results.get(0).getClosenessCoefficient()));
    }
    
    private static void printMatrix(double[][] matrix, String[] rowNames, String[] colNames) {
        System.out.printf("%-15s", "");
        for (String col : colNames) {
            System.out.printf("%-12s", col);
        }
        System.out.println();
        
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("%-15s", rowNames[i]);
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%-12.4f", matrix[i][j]);
            }
            System.out.println();
        }
    }
}


