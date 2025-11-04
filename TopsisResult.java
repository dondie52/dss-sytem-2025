public class TopsisResult {
    private String alternativeName;
    private double separationPositive;
    private double separationNegative;
    private double closenessCoefficient;
    private int ranking;
    
    public TopsisResult(String alternativeName, double separationPositive, 
                       double separationNegative, double closenessCoefficient) {
        this.alternativeName = alternativeName;
        this.separationPositive = separationPositive;
        this.separationNegative = separationNegative;
        this.closenessCoefficient = closenessCoefficient;
    }
    
    // Getters and Setters
    public String getAlternativeName() {
        return alternativeName;
    }
    
    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }
    
    public double getSeparationPositive() {
        return separationPositive;
    }
    
    public void setSeparationPositive(double separationPositive) {
        this.separationPositive = separationPositive;
    }
    
    public double getSeparationNegative() {
        return separationNegative;
    }
    
    public void setSeparationNegative(double separationNegative) {
        this.separationNegative = separationNegative;
    }
    
    public double getClosenessCoefficient() {
        return closenessCoefficient;
    }
    
    public void setClosenessCoefficient(double closenessCoefficient) {
        this.closenessCoefficient = closenessCoefficient;
    }
    
    public int getRanking() {
        return ranking;
    }
    
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
    
    @Override
    public String toString() {
        return String.format("%-20s | S+ = %.4f | S- = %.4f | C* = %.4f | Rank = %d",
            alternativeName, separationPositive, separationNegative, 
            closenessCoefficient, ranking);
    }
}


