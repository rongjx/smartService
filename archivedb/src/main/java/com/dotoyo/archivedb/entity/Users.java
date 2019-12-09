package com.dotoyo.archivedb.entity;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("users")
public class Users extends Model<Users> {

    /**
     * ID
     */
    @TableId("id")
    private int id;

    @TableField("name")
    private String name;

    @TableField("phone")
    private String phone;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
