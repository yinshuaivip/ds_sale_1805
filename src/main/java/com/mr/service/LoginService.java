package com.mr.service;

import com.mr.model.TMallUserAccount;

/**
 * Created by YinShuai on 2018/11/5.
 */
public interface LoginService {
    TMallUserAccount login(String yhMch, String yhMm);
}
