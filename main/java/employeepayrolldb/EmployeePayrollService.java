package employeepayrolldb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {


    public Map<String, Double> readAverageSalaryByGender() {
        return null;
    }



    public enum IOService{CONSOLE_IO,FILE_IO,REST_IO, DB_IO}
    private List<EmployeePayrollData> employeePayrollList;
    private EmloyeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService(){
        employeePayrollDBService = EmloyeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList){
        this();
        this.employeePayrollList=employeePayrollList;

    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList=new ArrayList<>();
        EmployeePayrollService employeePayrollService=new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader=new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
    }

    public void readEmployeePayrollData(Scanner consoleInputReader){
        System.out.println("Enter Employee ID: ");
        int id= consoleInputReader.nextInt();
        System.out.println("Enter Employee Name: ");
        String name=consoleInputReader.next();
        double salary=consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id,name,salary));
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService){
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);

    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollList.stream()
                    .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                    .findFirst()
                    .orElse(null);
    }

    public void addEmployeePayrollData(String name, String department, double salary, double deductions, double taxable_pay, double tax, double net_pay, LocalDate start, String gender) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, department, salary, deductions, taxable_pay, tax, net_pay, start, gender));
    }



    public  void writeEmployeePayrollData(IOService ioService){
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to Console\n"+employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
    }

    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            //System.out.println("Employee Being Added: "+employeePayrollData.name);
            this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.department,  employeePayrollData.basic_pay, employeePayrollData.deductions, employeePayrollData.taxable_pay, employeePayrollData.tax, employeePayrollData.net_pay, employeePayrollData.startDate, employeePayrollData.gender);
            //System.out.println("Employee Added: "+employeePayrollData.name);
        });
        //System.out.println(this.employeePayrollList);
    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList){
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = ()->{
                employeeAdditionStatus.put(employeePayrollData.hashCode(),false);
                System.out.println("Employee Being Added: "+Thread.currentThread().getName());
                this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.department, employeePayrollData.basic_pay, employeePayrollData.deductions, employeePayrollData.taxable_pay, employeePayrollData.tax, employeePayrollData.net_pay, employeePayrollData.startDate, employeePayrollData.gender);
                employeeAdditionStatus.put(employeePayrollData.hashCode(),true);
                System.out.println("Employee Added: "+Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();

        });
        while (employeeAdditionStatus.containsValue(false)){
            try {
                Thread.sleep(10);
            }catch (InterruptedException e){}
        }
        System.out.println(employeePayrollDataList);
    }

    public  void addEmployeeToPayroll(String name, String department, double salary, double deductions, double taxable_pay, double tax, double net_pay, LocalDate startDate, String gender){
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,department, salary, deductions, taxable_pay, tax, net_pay, startDate, gender));
    }


    public boolean checkEmployeePayrollInSymcWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }



    public void printData(IOService ioService){
        if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
        else System.out.println(employeePayrollList);
    }

    public long countEntries(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return employeePayrollList.size();
    }

    public List<EmployeePayrollData> readEmployeePayrollforDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
        return null;
    }

    public Map<String, Double> readAverageSalaryByGender(IOService ioService){
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }





}
