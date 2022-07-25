package com.employee.dashboard.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
import org.springframework.web.client.RestTemplate;

import com.employee.dashboard.model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("employees")
@Slf4j
public class EmployeeDashboardController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@GetMapping
	public ResponseEntity<List<Employee>> getEmployeeData() {
		log.info("[EmployeeDashboardController][getEmployeeData] getting all employees from DB");
		ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:9000/employees", String.class);
		int randomNum = new Random().nextInt(100);
		if (randomNum % 3 == 0) {
			try {
				log.debug("Caught Inside Thread sleep. It will take time");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<Employee> employees = null;
		try {
			employees = mapper.readValue(entity.getBody().getBytes(), new TypeReference<List<Employee>>() {
			});
		} catch (IOException e) {
			log.error("[EmployeeDashboardController][getEmployeeData] Error in getting employees [{}]", e.getMessage(),
					e);
			e.printStackTrace();
		}
		if (!employees.isEmpty()) {
			log.info("[EmployeeDashboardController][getEmployeeData] received employee list of size {}",
					employees.size());
			return ResponseEntity.ok(employees);
		}
		log.warn("[EmployeeDashboardController][getEmployeeData] No employee record found. Size is {}",
				employees.size());
		return ResponseEntity.noContent().build();
	}

	@Trace(dispatcher = true)
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable long id) {
		ResponseEntity<Employee> employee = null;
		try {
			employee = restTemplate.getForEntity("http://localhost:9000/employees/{id}", Employee.class,
					Collections.singletonMap("id", id));
		} catch (Exception e) {
			log.error("Error in retrieving employee with id {}", id, e);
		}
		if (Objects.nonNull(employee) && Objects.nonNull(employee.getBody())) {
			log.info("Employee object received with id {} and name {}", id, employee.getBody().getName());
			NewRelic.addCustomParameter("userID::NewRelic", id);
			NewRelic.noticeError("Custom error logged using newRelic");
			NewRelic.setTransactionName("category-1", "my-transaction");
			return ResponseEntity.ok(employee.getBody());
		}
		log.warn("No employee found with Id {}", id);
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee) {
		log.info("Save employee request received for employee name {}", employee.getName());
		ResponseEntity<Employee> employeeEntity = restTemplate.postForEntity("http://localhost:9000/employees", employee, Employee.class);
		Employee employee2 = employeeEntity.getBody();
		if (Objects.nonNull(employee2)) {
			log.info("Employee saved in DB {}", employee2);
			return ResponseEntity.ok(employee2);
		}
		log.error("Error in saving employee to DB {}", employee);
		throw new RuntimeException();
	}

	@PutMapping("{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
		log.info("Update employee request received for employee Id {}", employee.getId());
		try {
			restTemplate.put("http://localhost:9000/employees/{id}", employee, Collections.singletonMap("id", id));
			log.info("Employee with id {} is updated successfully", id);
			return ResponseEntity.ok(employee);
		} catch (Exception e) {
			log.error("Error in getting employee with id {}", id, e);
		}
		throw new RuntimeException();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable long id) {
		log.info("Delete employee request received for employee with id {}", id);
		restTemplate.delete("http://localhost:9000/employees/{id}", Collections.singletonMap("id", id));
		log.info("Employee with id {} deleted successfully", id);
		return ResponseEntity.ok().build();
	}
}
