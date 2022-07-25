package com.employee.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dao.EmployeeDao;
import com.employee.model.Employee;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("employees")
public class EmployeeController {

	@Autowired
	private EmployeeDao dao;

	@GetMapping
	public ResponseEntity<List<Employee>> getEmployees() {
		log.info("Getting all employees from DB");
		List<Employee> employeesList = dao.findAll();
		log.info("employee list of size {}", employeesList.size());
		return ResponseEntity.ok(employeesList);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable long id) {
		Optional<Employee> employeeOptional = dao.findById(id);
		if (employeeOptional.isPresent()) {
			log.info("Employee found with id [{}] employee [{}]", id, employeeOptional.get());
			return ResponseEntity.ok(employeeOptional.get());
		}
		throw new RuntimeException("No employee found for id " + id);
	}
	
	@PostMapping
	public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee) {
		try {
			Employee employee2 = dao.save(employee);
			return ResponseEntity.ok(employee2);
		} catch (Exception e) {
			log.error("Error in saving employee to DB");
		}
		throw new RuntimeException();
	}
	
	@PutMapping("{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
		boolean isFound = dao.existsById(id);
		if (!isFound) {
			throw new IllegalArgumentException("No employee is found with id " + id);
		}
		Employee employee2 = null;
		try {
			employee.setId(id);
			employee2 = dao.save(employee);
		} catch (Exception e) {
			log.error("Error in updating employee with id {}, error: [{}]", id, e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		}
		return ResponseEntity.ok(employee2);
	}
	
	@DeleteMapping("{id}")
	public void deleteEmployee(@PathVariable long id) {
		boolean isFound = dao.existsById(id);
		if (!isFound) {
			throw new IllegalArgumentException("No employee is found with id " + id);
		}
		dao.deleteById(id);
		log.info("Employee with id {} deleted successfully", id);
	}
}
