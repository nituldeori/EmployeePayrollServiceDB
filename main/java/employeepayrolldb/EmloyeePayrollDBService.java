package employeepayrolldb;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmloyeePayrollDBService {
    private int connectionCounter = 0;
    private PreparedStatement employeePayrollDataStatement;
    private static EmloyeePayrollDBService emloyeePayrollDBService;
    private EmloyeePayrollDBService(){

    }

    public static EmloyeePayrollDBService getInstance(){
        if(emloyeePayrollDBService == null)
            emloyeePayrollDBService = new EmloyeePayrollDBService();
        return emloyeePayrollDBService;
    }

    private synchronized Connection getConnection() throws SQLException {
        connectionCounter++;
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "Myheart@18/6";
        Connection con;
        System.out.println("Processing Thread: "+Thread.currentThread().getName()+
                            " Connecting to database with id:"+connectionCounter);
        con = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Processing Thread: "+Thread.currentThread().getName()+
                           " Id: "+connectionCounter+ " Connection is successful!!!"+con);
        return con;

    }

    public List<EmployeePayrollData> readData(){
        String sql = "SELECT * FROM employee_payroll";
        List<EmployeePayrollData> employeePayrollList =new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()){
                int id = result.getInt("id");
                String name = result.getString("name");
                double net_pay = result.getDouble("net_pay");
                LocalDate startDate = result.getDate("start").toLocalDate();
                String department = result.getString("department");
                double basic_pay = result.getDouble("basic_pay");
                double deductions = result.getDouble("deductions");
                double taxable_pay = result.getDouble("taxable_pay");
                double tax = result.getDouble("tax");
                String gender = result.getString("gender");
                employeePayrollList.add(new EmployeePayrollData(id,name,department,basic_pay,deductions,taxable_pay,tax,net_pay,startDate,gender));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double net_pay = resultSet.getDouble("net_pay");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                String department = resultSet.getString("department");
                double basic_pay = resultSet.getDouble("basic_pay");
                double deductions = resultSet.getDouble("deductions");
                double taxable_pay = resultSet.getDouble("taxable_pay");
                double tax = resultSet.getDouble("tax");
                String gender = resultSet.getString("gender");
                employeePayrollList.add(new EmployeePayrollData(id,name,department,basic_pay,deductions,taxable_pay,tax,net_pay,startDate,gender));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData(){
        try{
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM employee_payroll WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(String name, double salary){
        return this.updateEmployeeDataUsingStatement(name, salary);
    }

    private int updateEmployeeDataUsingStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set basic_pay = %.2f where name = '%s';",salary,name );
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }





    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';",
                                    Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection=this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public EmployeePayrollData addEmployeePayrollUC7(String name, String department, double salary, double deductions, double taxable_pay, double tax, double net_pay, LocalDate start, String gender) {
        int employeeId = -1;
        Connection connection =null;
        EmployeePayrollData employeePayrollData = null;
        try{
            connection = this.getConnection();
        } catch (SQLException e){
            e.printStackTrace();
        }
        try(Statement statement = connection.createStatement()){
            String sql = String.format("INSERT INTO employee_payroll (name, department, basic_pay, deductions, taxable_pay, tax, net_pay, start, gender)"+
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", name, department, salary, deductions, taxable_pay, tax, net_pay, Date.valueOf(start), gender);
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId,name, salary, gender,start);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, String department, double salary, double deductions, double taxable_pay, double tax, double net_pay, LocalDate start, String gender){
        int employeeId = -1;
        Connection connection =null;
        EmployeePayrollData employeePayrollData = null;
        try{
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e){
            e.printStackTrace();
        }
        try(Statement statement = connection.createStatement()){
            deductions = salary*0.2;
            taxable_pay = salary - deductions;
            tax = taxable_pay*0.1;
            net_pay = salary - tax;
            String sql = String.format("INSERT INTO employee_payroll (name, department, basic_pay, deductions, taxable_pay, tax, net_pay, start, gender)"+
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", name, department, salary, deductions, taxable_pay, tax, net_pay, Date.valueOf(start), gender);
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId,name, salary, gender,start);
        } catch (SQLException e){
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try(Statement statement = connection.createStatement()){
            String sql = String.format("INSERT INTO payroll_details (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES "+
                                       "('%s','%s','%s','%s','%s','%s')", employeeId,salary,deductions,taxable_pay,tax,net_pay);
            int rowAffected = statement.executeUpdate(sql);
            if(rowAffected == 1){
                employeePayrollData = new EmployeePayrollData(employeeId,name,department,salary,deductions,taxable_pay,tax,net_pay,start,gender);

            }

        } catch (SQLException e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return employeePayrollData;

    }


    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVG(basic_pay) as avg_salary FROM employee_payroll GROUP BY gender;";
        Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }
}


