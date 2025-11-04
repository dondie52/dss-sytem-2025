import java.util.*;

public class TopsisCalculator {
    
    private double[][] decisionMatrix;
    private double[] weights;
    private boolean[] isBenefit; // true for benefit, false for cost
    private String[] alternativeNames;
    private String[] criteriaNames;
    
    public TopsisCalculator(double[][] decisionMatrix, double[] weights, boolean[] isBenefit,
                           String[] alternativeNames, String[] criteriaNames) {
        this.decisionMatrix = decisionMatrix;
        this.weights = weights;
        this.isBenefit = isBenefit;
        this.alternativeNames = alternativeNames;
        this.criteriaNames = criteriaNames;
    }
    
    // Step 2: Normalize the decision matrix
    public double[][] normalizeMatrix() {
        int m = decisionMatrix.length;    // number of alternatives
        int n = decisionMatrix[0].length; // number of criteria
        double[][] normalized = new double[m][n];
        
        for (int j = 0; j < n; j++) {
            double sumOfSquares = 0;
            for (int i = 0; i < m; i++) {
                sumOfSquares += Math.pow(decisionMatrix[i][j], 2);
            }
            double denominator = Math.sqrt(sumOfSquares);
            
            for (int i = 0; i < m; i++) {
                normalized[i][j] = decisionMatrix[i][j] / denominator;
            }
        }
        return normalized;
    }
    
    // Step 3: Calculate weighted normalized matrix
    public double[][] calculateWeightedMatrix(double[][] normalized) {
        int m = normalized.length;
        int n = normalized[0].length;
        double[][] weighted = new double[m][n];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                weighted[i][j] = normalized[i][j] * weights[j];
            }
        }
        return weighted;
    }
    
    // Step 4: Determine ideal and negative-ideal solutions
    public double[] getIdealSolution(double[][] weighted) {
        int n = weighted[0].length;
        double[] ideal = new double[n];
        
        for (int j = 0; j < n; j++) {
            double bestValue = weighted[0][j];
            for (int i = 1; i < weighted.length; i++) {
                if (isBenefit[j]) {
                    // For benefit criteria, choose maximum
                    bestValue = Math.max(bestValue, weighted[i][j]);
                } else {
                    // For cost criteria, choose minimum
                    bestValue = Math.min(bestValue, weighted[i][j]);
                }
            }
            ideal[j] = bestValue;
        }
        return ideal;
    }
    
    public double[] getNegativeIdealSolution(double[][] weighted) {
        int n = weighted[0].length;
        double[] negativeIdeal = new double[n];
        
        for (int j = 0; j < n; j++) {
            double worstValue = weighted[0][j];
            for (int i = 1; i < weighted.length; i++) {
                if (isBenefit[j]) {
                    // For benefit criteria, choose minimum
                    worstValue = Math.min(worstValue, weighted[i][j]);
                } else {
                    // For cost criteria, choose maximum
                    worstValue = Math.max(worstValue, weighted[i][j]);
                }
            }
            negativeIdeal[j] = worstValue;
        }
        return negativeIdeal;
    }
    
    // Step 5: Calculate separation measures
    public double[] calculatePositiveSeparation(double[][] weighted, double[] ideal) {
        int m = weighted.length;
        double[] separation = new double[m];
        
        for (int i = 0; i < m; i++) {
            double sum = 0;
            for (int j = 0; j < weighted[0].length; j++) {
                sum += Math.pow(weighted[i][j] - ideal[j], 2);
            }
            separation[i] = Math.sqrt(sum);
        }
        return separation;
    }
    
    public double[] calculateNegativeSeparation(double[][] weighted, double[] negativeIdeal) {
        int m = weighted.length;
        double[] separation = new double[m];
        
        for (int i = 0; i < m; i++) {
            double sum = 0;
            for (int j = 0; j < weighted[0].length; j++) {
                sum += Math.pow(weighted[i][j] - negativeIdeal[j], 2);
            }
            separation[i] = Math.sqrt(sum);
        }
        return separation;
    }
    
    // Step 6: Calculate relative closeness
    public double[] calculateRelativeCloseness(double[] positiveSeparation, double[] negativeSeparation) {
        int m = positiveSeparation.length;
        double[] closeness = new double[m];
        
        for (int i = 0; i < m; i++) {
            closeness[i] = negativeSeparation[i] / (positiveSeparation[i] + negativeSeparation[i]);
        }
        return closeness;
    }
    
    // Step 7: Rank alternatives
    public List<TopsisResult> performTopsis() {
        // Step 2: Normalize
        double[][] normalized = normalizeMatrix();
        
        // Step 3: Weight
        double[][] weighted = calculateWeightedMatrix(normalized);
        
        // Step 4: Find ideal solutions
        double[] ideal = getIdealSolution(weighted);
        double[] negativeIdeal = getNegativeIdealSolution(weighted);
        
        // Step 5: Calculate separations
        double[] positiveSeparation = calculatePositiveSeparation(weighted, ideal);
        double[] negativeSeparation = calculateNegativeSeparation(weighted, negativeIdeal);
        
        // Step 6: Calculate closeness
        double[] closeness = calculateRelativeCloseness(positiveSeparation, negativeSeparation);
        
        // Create results list
        List<TopsisResult> results = new ArrayList<>();
        for (int i = 0; i < alternativeNames.length; i++) {
            TopsisResult result = new TopsisResult(
                alternativeNames[i],
                positiveSeparation[i],
                negativeSeparation[i],
                closeness[i]
            );
            results.add(result);
        }
        
        // Step 7: Sort by closeness (descending)
        results.sort((a, b) -> Double.compare(b.getClosenessCoefficient(), a.getClosenessCoefficient()));
        
        // Assign rankings
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setRanking(i + 1);
        }
        
        return results;
    }
}

