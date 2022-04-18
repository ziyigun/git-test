package com.ali.service;

import com.ali.pojo.Admin;

public interface AdminService {
    //完成登录判断
    Admin login(String name, String pwd);
}
