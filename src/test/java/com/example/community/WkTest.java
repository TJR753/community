package com.example.community;

import org.junit.Test;

import java.io.IOException;

public class WkTest {
    @Test
    public void image(){
        String command="D:/wkhtmltox/wkhtmltopdf/bin/wkhtmltoimage https://www.nowcoder.com D:/1-TJR/course-java/project/community/image/1.png";
        try {
            Process exec = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
