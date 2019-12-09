package com.dotoyo.archivedb.ctl;

import com.dotoyo.archivedb.entity.Users;
import com.dotoyo.archivedb.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {

    @Autowired
    private IUserService userService;

    @PostMapping("/test")
    public String test(String name, String phone) {
        Users users = new Users();
        users.setName(name);
        users.setPhone(phone);
        userService.insert(users, Users.class);
        return "OK";
    }
}
