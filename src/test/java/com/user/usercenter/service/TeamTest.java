package com.user.usercenter.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.user.usercenter.common.TeamStats;
import com.user.usercenter.mapper.TeamMapper;
import com.user.usercenter.model.Team;
import com.user.usercenter.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeamTest {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private TeamMapper teamMapper;



    @Test
    void test() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("tags");
        List<User> list = userService.list();
        System.out.println(list);
    }

    @Test
    void TestTeam() {
//        String[] arr = {"JSON", "C++", "Web"};
//        String[] arr1 = {"JSON", "C++", "Web", "Pathon"};
//        String[] arr2 = {"JSON", "C++"};
//        System.out.println(Arrays.toString(arr1));
//        System.out.println(minDistance(arr,arr1));
        List<String> arr = Arrays.asList("JSON", "C++", "Web");
        List<String> arr1 = Arrays.asList("JSON", "C++", "Web","Pathon");
                System.out.println(minDistance(arr,arr1));


    }

    public int minDistance(List<String> word1, List<String>  word2) {
        int n = word1.size();
        int m = word2.size();

        if (n * m == 0)
            return n + m;

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(word1.get(i - 1), word2.get(j - 1))) {
                    left_down += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
