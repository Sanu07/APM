package com.employee.dashboard.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Employee {

	private Long id;
	private String employeeId;
	private String name;
	private LocalDate joiningDate;
	private String organisation;
	private String designation;
}
