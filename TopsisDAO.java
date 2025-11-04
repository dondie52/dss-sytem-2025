import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TopsisDAO {
    
    // Save a new problem
    public int saveProblem(String problemName, String description) throws SQLException {
        String sql = "INSERT INTO problems (problem_name, description) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, problemName);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    // Save criteria for a problem
    public void saveCriteria(int problemId, String criterionName, double weight, String type) throws SQLException {
        String sql = "INSERT INTO criteria (problem_id, criterion_name, weight, criterion_type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, problemId);
            pstmt.setString(2, criterionName);
            pstmt.setDouble(3, weight);
            pstmt.setString(4, type);
            pstmt.executeUpdate();
        }
    }
    
    // Save alternative
    public int saveAlternative(int problemId, String alternativeName) throws SQLException {
        String sql = "INSERT INTO alternatives (problem_id, alternative_name) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, problemId);
            pstmt.setString(2, alternativeName);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    // Save performance score
    public void savePerformanceScore(int problemId, int alternativeId, int criterionId, double score) throws SQLException {
        String sql = "INSERT INTO performance_scores (problem_id, alternative_id, criterion_id, score) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, problemId);
            pstmt.setInt(2, alternativeId);
            pstmt.setInt(3, criterionId);
            pstmt.setDouble(4, score);
            pstmt.executeUpdate();
        }
    }
    
    // Save TOPSIS results
    public void saveTopsisResult(int problemId, int alternativeId, TopsisResult result) throws SQLException {
        String sql = "INSERT INTO topsis_results (problem_id, alternative_id, separation_positive, " +
                    "separation_negative, closeness_coefficient, ranking) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, problemId);
            pstmt.setInt(2, alternativeId);
            pstmt.setDouble(3, result.getSeparationPositive());
            pstmt.setDouble(4, result.getSeparationNegative());
            pstmt.setDouble(5, result.getClosenessCoefficient());
            pstmt.setInt(6, result.getRanking());
            pstmt.executeUpdate();
        }
    }
    
    // Get all problems
    public List<String[]> getAllProblems() throws SQLException {
        List<String[]> problems = new ArrayList<>();
        String sql = "SELECT id, problem_name, description, created_at FROM problems ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] problem = new String[4];
                problem[0] = String.valueOf(rs.getInt("id"));
                problem[1] = rs.getString("problem_name");
                problem[2] = rs.getString("description");
                problem[3] = rs.getTimestamp("created_at").toString();
                problems.add(problem);
            }
        }
        return problems;
    }
    
    // Get problem details with all related data
    public ResultSet getProblemDetails(int problemId) throws SQLException {
        String sql = "SELECT p.problem_name, c.criterion_name, c.weight, c.criterion_type, " +
                    "a.alternative_name, ps.score " +
                    "FROM problems p " +
                    "JOIN criteria c ON p.id = c.problem_id " +
                    "JOIN alternatives a ON p.id = a.problem_id " +
                    "JOIN performance_scores ps ON ps.problem_id = p.id " +
                    "AND ps.criterion_id = c.id AND ps.alternative_id = a.id " +
                    "WHERE p.id = ? " +
                    "ORDER BY a.id, c.id";
        
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, problemId);
        return pstmt.executeQuery();
    }
    
    // Get TOPSIS results for a problem
    public List<String[]> getTopsisResults(int problemId) throws SQLException {
        List<String[]> results = new ArrayList<>();
        String sql = "SELECT a.alternative_name, tr.separation_positive, tr.separation_negative, " +
                    "tr.closeness_coefficient, tr.ranking " +
                    "FROM topsis_results tr " +
                    "JOIN alternatives a ON tr.alternative_id = a.id " +
                    "WHERE tr.problem_id = ? " +
                    "ORDER BY tr.ranking";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, problemId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] result = new String[5];
                result[0] = rs.getString("alternative_name");
                result[1] = String.format("%.4f", rs.getDouble("separation_positive"));
                result[2] = String.format("%.4f", rs.getDouble("separation_negative"));
                result[3] = String.format("%.4f", rs.getDouble("closeness_coefficient"));
                result[4] = String.valueOf(rs.getInt("ranking"));
                results.add(result);
            }
        }
        return results;
    }
    
    // Delete a problem (cascades to all related data)
    public void deleteProblem(int problemId) throws SQLException {
        String sql = "DELETE FROM problems WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, problemId);
            pstmt.executeUpdate();
        }
    }
}

