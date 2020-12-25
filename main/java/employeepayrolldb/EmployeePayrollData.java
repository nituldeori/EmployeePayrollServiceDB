package employeepayrolldb;

import java.time.LocalDate;
import java.util.Objects;


public class EmployeePayrollData {
    public int id;
    public String name;
    public String department;
    public double basic_pay;
    public double deductions;
    public double taxable_pay;
    public double tax;
    public double net_pay;
    public LocalDate startDate;
    public String gender;

    public EmployeePayrollData(Integer id, String name, Double basic_pay){
        this.id = id;
        this.name = name;
        this.basic_pay = basic_pay;
    }

    public EmployeePayrollData(Integer id, String name, Double basic_pay, String gender, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.basic_pay = basic_pay;
        this.gender = gender;
        this.startDate = startDate;
    }

    public EmployeePayrollData(Integer id, String name, String department, Double basic_pay, double deductions, double taxable_pay, double tax, double net_pay, LocalDate startDate, String gender ) {
        this(id, name, basic_pay, gender,startDate);
        this.department = department;
        this.deductions = deductions;
        this.taxable_pay = taxable_pay;
        this.tax = tax;
        this.net_pay = net_pay;
        this.startDate = startDate;
    }


    public String toString() {
        return "id=" + id + ", name=" + name + ", netpay=" + net_pay;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name, basic_pay, startDate);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id && Double.compare(that.net_pay, net_pay) == 0 && name.equals(that.name);
    }



}




