package com.mr.serviceImpl;

import com.mr.mapper.LoginMapper;
import com.mr.model.TMallUserAccount;
import com.mr.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by YinShuai on 2018/11/5.
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    public TMallUserAccount login(String yhMch, String yhMm) {
        return loginMapper.login(yhMch,yhMm);
    }
}
