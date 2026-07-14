package com.harikrishnan.eurokart.test.service;

import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String simulateTestCondition (Long id){
            if(id == 1) {
                throw new ResourceNotFoundException("Unable to find item with id:" +id);
            }
            else {
                return "Item found. Test acknowledged.";
            }
    }

}
