import java.sql.*;
import java.util.*;

class UniversityDatabaseManager {
    
    // Define constants for the database connection
    private static final String DB_URL = "jdbc:mysql://localhost/university";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "krose";
    
    // Define constants for the menu options
    private static final int DISPLAY_DATA = 1;
    private static final int INSERT_STUDENT = 2;
    private static final int MODIFY_ADVISOR = 3;
    private static final int DELETE_STUDENT = 4;
    private static final int ADD_ADVISOR = 5;
    private static final int QUIT = 6;
    
    // Define a global variable for the database connection
    private static Connection conn = null;
    
    public static void main(String[] args) {
        try {
            // Establish a connection to the database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Display the main menu and handle user input
            int choice = -1;
            while (choice != QUIT) {
                displayMainMenu();
                choice = getUserChoice();
                switch (choice) {
                    case DISPLAY_DATA:
                        displayData();
                        break;
                    case INSERT_STUDENT:
                        insertStudent();
                        break;
                    case MODIFY_ADVISOR:
                        modifyAdvisor();
                        break;
                    case DELETE_STUDENT:
                        deleteStudent();
                        break;
                    case ADD_ADVISOR:
                    	addAdvisor();
                    	break;
                    case QUIT:
                        System.out.println("Exiting program...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
            
            // Close the database connection
            conn.close();
        } catch (SQLException e) {
            System.out.println("***CONNECTION IS NOT SUCESSFULL***");
        }
    }
    
    private static void displayMainMenu() {
        System.out.println("===== University Database Manager =====");
        System.out.println("1. Display Data");
        System.out.println("2. Insert Student");
        System.out.println("3. Modify Advisor");
        System.out.println("4. Delete Student");
        System.out.println("5. Add Advisor");
        System.out.println("6. Quit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
    
    
    private static void displayData() {
        System.out.print("Enter the name of the table to display: ");
        Scanner scanner = new Scanner(System.in);
        String tableName = scanner.nextLine();
        
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int numColumns = meta.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();


            while (rs.next()) {
            	for (int i = 1; i <= numColumns; i++) {
            		System.out.print(rs.getString(i) + "\t");
            	}
            	System.out.println();
            }
        } catch (SQLException e) {
        	System.out.println("***Table does not exist, please check***");
        }	
    }
    
    


private static void insertStudent() {
  
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter the student's name: ");
    String name = scanner.nextLine();
    System.out.print("Enter the student's ID: ");
    int id = scanner.nextInt();
    scanner.nextLine(); 
    System.out.print("Enter the student's major: ");
    String major = scanner.nextLine();
    System.out.print("Enter the student's total credit : ");
    int cred = scanner.nextInt();
    
     
    String query = "INSERT INTO student VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, id);
        stmt.setString(2, name);
        stmt.setString(3, major);
        stmt.setInt(4, cred);
        stmt.executeUpdate();
        System.out.println("Student added successfully.");
    } catch (SQLException e) {
        System.out.println("***check foreign keys in student table***");
    }
    
    try {
    	String query2 = "INSERT INTO advisor VALUES (?, NULL)";
    	PreparedStatement stmt2 = conn.prepareStatement(query2);
    	
    	stmt2.setInt(1, id);
    	
    	stmt2.executeUpdate();
    } catch (SQLException e) {
    	System.out.println("***check foreign keys in advisor table***");
    }
    
}





private static void addAdvisor() {
    Scanner scanner = new Scanner(System.in);
    
    System.out.print("Enter the s_ID : ");
    String sid = scanner.next();
    System.out.print("Enter the i_ID : ");
    String iid = scanner.next();

    String query = "INSERT INTO advisor (s_ID, i_ID) VALUES (?, ?);";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
          stmt.setString(1, sid);
          stmt.setString(2, iid);
          stmt.executeUpdate();
          System.out.println("Advisor added sucessfully");

    } catch (SQLException e) {
    	System.out.println("***check foreign keys in advisor table***");
    }
}

private static void forAdvisorDisplay(String table) {
	String query = "SELECT * FROM " + table;
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        ResultSetMetaData meta = rs.getMetaData();
        int numColumns = meta.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
            System.out.print(meta.getColumnName(i) + "\t");
        }
        System.out.println();
        while (rs.next()) {
        	for (int i = 1; i <= numColumns; i++) {
        		System.out.print(rs.getString(i) + "\t");
        	}
        	System.out.println();
        }
    } catch (SQLException e) {
    	System.out.println("***Table does not exist***");
    }	
}

private static void modifyAdvisor() {
	System.out.println("=====Student table======");
	forAdvisorDisplay("student");
	System.out.println("=====instructor table=====");
	forAdvisorDisplay("instructor");
	System.out.println("=====advisot table=====");
	forAdvisorDisplay("advisor");

    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter the student's ID: ");
    int studentId = scanner.nextInt();
    System.out.print("Enter the new advisor ID: ");
    int newAdvisorId = scanner.nextInt();
    
    String query = "UPDATE advisor SET i_ID = ? WHERE s_ID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, newAdvisorId);
        stmt.setInt(2, studentId);
        int numRowsAffected = stmt.executeUpdate();
        if (numRowsAffected == 0) {
            System.out.println("No student with ID " + studentId + " found.");
        } else {
            System.out.println("Advisor updated successfully.");
        }
    } catch (SQLException e) {
        System.out.println("***update not sucessfull please check entries***");
    }
}






private static void deleteStudent() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter the student's ID: ");
    int studentId = scanner.nextInt();

    String query = "SELECT * FROM student WHERE ID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String name = rs.getString("name");
            int id = rs.getInt("id");
            String major = rs.getString("dept_name");
            int advisorId = rs.getInt("tot_cred");
            System.out.println("Student information:");
            System.out.println("ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Major: " + major);
            System.out.println("total credit: " + advisorId);

            System.out.print("Are you sure you want to delete this student? (y/n): ");
            String answer = scanner.next().toLowerCase();
            if (answer.equals("y")) {
                String deleteQuery = "DELETE FROM student WHERE ID = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    int numRowsAffected = deleteStmt.executeUpdate();
                    if (numRowsAffected == 0) {
                        System.out.println("No student with ID " + studentId + " found.");
                    } 
                    
                    else {
                        String deleteAdvisorQuery = "UPDATE advisor SET s_ID = NULL WHERE s_ID = ?";
                        try (PreparedStatement deleteAdvisorStmt = conn.prepareStatement(deleteAdvisorQuery)) {
                            deleteAdvisorStmt.setInt(1, advisorId);
                            deleteAdvisorStmt.executeUpdate();
                        }
                    }
                    
                } catch (SQLException e) {
                    System.out.println("*** update not sucessful please check entries ***");
                }
                System.out.println("Student deleted successfully.");
            }
        } else {
            System.out.println("No student with ID " + studentId + " found.");
        }
    } catch (SQLException e) {
        System.out.println("** please check entries ***");
    }
}



}