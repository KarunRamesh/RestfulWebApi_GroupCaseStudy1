package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {
    @Autowired
    private CommonDao commonDao;
}
