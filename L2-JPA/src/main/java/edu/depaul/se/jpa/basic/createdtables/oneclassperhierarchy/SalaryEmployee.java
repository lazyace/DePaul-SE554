/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.depaul.se.jpa.basic.createdtables.oneclassperhierarchy;

import javax.persistence.Entity;

/**
 *
 * @author kyu
 */
@Entity
public class SalaryEmployee extends Employee{
    private int salary;

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

}
