package com.app.demoAPI.controller;

import com.app.demoAPI.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Restful Customer Controller
 * - Contains CRUD endpoints
 */
@RestController
@RequestMapping("customers")
public class CustomerController {
    private Map<String, Customer> customers;

    //DB Accessor Methods
    private boolean isCustomersInitialized() {
        return customers != null && !customers.isEmpty();
    }

    private boolean isCustomerInitialized(String uid) {
        return customers.containsKey(uid);
    }

    private ArrayList<Customer>  getCustomersFromDB() {
        return new ArrayList<>(customers.values());
    }

    private Customer getCustomerByIDFromDB(String uid) {
        return customers.get(uid);
    }

    private void addCustomerToDB(String uid, Customer customer) {
        customers.put(uid, customer);
    }

    private void removeCustomerFromDB(String uid) {
        customers.remove(uid);
    }

    // GET Requests
    /**
     *
     * Read Customers
     */
    // Query Parameters
    @GetMapping
    public ResponseEntity<ArrayList<Customer>> getCustomers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "limit", defaultValue = "50") int limit,//use default value to assign a value and make the parameter optional. Or use required = BOOLEAN
                                                            @RequestParam(value = "sort", required = false) String sort) { //required = BOOLEAN does not work well with primitive types, can cause errors. Since primitive types cannot be converted to null. Recommend usage is with strings
        ResponseEntity<ArrayList<Customer>> response;
        if (isCustomersInitialized()) {
            response = new ResponseEntity<>(getCustomersFromDB(), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Read Customer
     */
    // Route Parameters
    // Response Entity Usage
    @GetMapping(path = "/{userID}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String userID) {
        ResponseEntity<Customer> response;
        if (isCustomersInitialized() && isCustomerInitialized(userID)) {
            response = new ResponseEntity<>(getCustomerByIDFromDB(userID), HttpStatus.OK);//Add generic to specify response body
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    // POST Request
    /**
     * Create Customer
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer request) {
        Customer response = new Customer();
        response.setFname(request.getFname());
        response.setLname(request.getLname());

        //UID Generation
        String UID = UUID.randomUUID().toString();
        response.setUserID(UID);
        System.out.println(UID);

        //Check if customer is created
        if (!isCustomersInitialized()) {
            customers = new HashMap<>();
        }

        //Add Customer
        this.addCustomerToDB(UID, response);

        //Return Response
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT Request
    /**
     * Update Customer
     */
    @PutMapping(path = "/{userID}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String userID,
                                                   @RequestBody Customer request) {
        ResponseEntity<Customer> response;
        if (isCustomerInitialized(userID)) {

            Customer customer = getCustomerByIDFromDB(userID);
            customer.setFname(request.getFname());
            customer.setLname(request.getLname());
            addCustomerToDB(userID, customer);

            response = new ResponseEntity<>(customer, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    // DELETE
    /**
     * Delete Customer
     */
    @DeleteMapping(path = "/{userID}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String userID) {
        ResponseEntity<Void> response;
        if (isCustomerInitialized(userID)){
            removeCustomerFromDB(userID);
            response = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
