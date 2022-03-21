package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alpha1")
public class AlphaDaoHibernatelmpl implements AlphaDao{
    @Override
    public String select() {
        return "yes";
    }
}
