package com.webcube.model.user;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;
import lombok.Data;

/**
 * @author Yang Jiyu
 * Created
 */
@Table(value = "t_user")
@Data
public class User extends ActiveRecord {
    private Integer id;
    private String  userName;
    private String  passWord;
    private Integer age;
    private String  realName;

}
