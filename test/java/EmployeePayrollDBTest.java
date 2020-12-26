import employeepayrolldb.EmloyeePayrollDBService;
import employeepayrolldb.EmployeePayroll;
import employeepayrolldb.EmployeePayrollData;
import employeepayrolldb.EmployeePayrollService;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static employeepayrolldb.EmployeePayrollService.IOService.DB_IO;

public class EmployeePayrollDBTest {
    @Test
    public void givenEmployeePayrolllInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
        Assert.assertEquals(6, employeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.updateEmployeeSalary("Jeff Bezos", 9000000.0);
        boolean result = employeePayrollService.checkEmployeePayrollInSymcWithDB("Jeff Bezos");
        Assert.assertTrue(result);
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        LocalDate startDate = LocalDate.of(2018, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollforDateRange(DB_IO, startDate, endDate);
        Assert.assertEquals(5, employeePayrollData.size());
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00) && averageSalaryByGender.get("F").equals(3000000.00));
    }

    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.addEmployeePayrollData("Mark", "Tech", 3000000.00,0, 0, 0 ,0 , LocalDate.now(),"M" );
        boolean result = employeePayrollService.checkEmployeePayrollInSymcWithDB("Mark");
        Assert.assertTrue(result);
    }

    @Test
    public void given6Empoyess_WhenAddedToDB_ShouldMatchEmployeeEntries(){
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1,"Jeff Bezos","Amazon",1000000.0,0.0,0.0,0.0,0.0,LocalDate.now(),"M"),
                new EmployeePayrollData(2,"Bill Gates", "Microsoft",2000000.0,0.0,0.0,0.0,0.0,LocalDate.now(),"M"),
                new EmployeePayrollData(3,"MarkZuckerberg", "Facebook",3000000.0,0.0,0.0,0.0,0.0,LocalDate.now(),"M"),
                new EmployeePayrollData(4,"Sundar", "Google",4000000.0,0.0,0.0,0.0,0.0,LocalDate.now(),"M"),
                new EmployeePayrollData(5,"Mukesh", "Reliance",5000000.0,0.0,0.0,0.0,0.0,LocalDate.now(),"M"),
                new EmployeePayrollData(6,"Anil", "Idea",6000000.0,0.0,0.0,0.0,0.0,LocalDate.now(),"M")
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without Thread: "+ Duration.between(start,end));
        Instant threadStart = Instant.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
        Assert.assertEquals(6, employeePayrollData.size());
    }

}
