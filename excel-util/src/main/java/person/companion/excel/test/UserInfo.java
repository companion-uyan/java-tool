package person.companion.excel.test;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Title: 用户信息
 * Author: companion
 * Written by: 2022/8/3 11:13
 */
@Data
@NoArgsConstructor
public class UserInfo {
    private String name;

    private String sex;

    private Short age;

    private String phone;
}
