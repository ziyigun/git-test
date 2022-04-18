package com.ali.service.impl;

import com.ali.mapper.AdminMapper;
import com.ali.pojo.Admin;
import com.ali.pojo.AdminExample;
import com.ali.service.AdminService;
import com.ali.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    //数据访问层的对象
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(String name, String pwd) {
        //根据传入的用户名查找相应的用户对象
        //如果有条件，则一定要创建AdminExample对象，用来封装条件
        AdminExample example = new AdminExample();
        //添加用户名a_name条件
        example.createCriteria().andANameEqualTo(name);
        List<Admin> list = adminMapper.selectByExample(example);
        if(list.size() > 0){
            Admin admin = list.get(0);
            //如果查询到用户，再进行用户密码比对，注意：密码是密文
            String miPwd = MD5Util.getMD5(pwd);
            if(miPwd.equals(admin.getaPass())){
                return admin;
            }
        }
        return null;
    }
}
