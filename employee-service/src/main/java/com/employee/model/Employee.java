package com.employee.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "EMPLOYEE_DETAILS")
@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "EMP_ID", unique = true)
	private String employeeId;
	@Column(name = "EMP_NAME")
	private String name;
	@Column(name = "EMP_JOINING_DATE")
	private LocalDate joiningDate;
	@Column(name = "EMP_ORGANISATION")
	private String organisation;
	@Column(name = "EMP_DESIGNATION")
	private String designation;
}
