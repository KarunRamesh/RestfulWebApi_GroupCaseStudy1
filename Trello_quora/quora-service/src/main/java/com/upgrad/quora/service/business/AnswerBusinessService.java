package com.upgrad.quora.service.business;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;
}
