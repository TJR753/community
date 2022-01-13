package com.example.community.service;

import java.util.Date;

/**
 * @author Administrator
 */
public interface DataService {
    void recordUV(String ip);
    Long countUV(Date start,Date end);
    void recordDAU(int userId);
    Long countDAU(Date start,Date end);
}
