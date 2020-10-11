package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {
    @Autowired
    private AdminDao adminDao;
}
