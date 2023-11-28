package com.gyp.jx.file.management;

import com.gyp.jx.file.management.service.IVideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GypNasApplicationTests {

    @Autowired
    private IVideoService iVideoService;
    @Value("${app.default-home}")
    private String defaultHome;

    @Test
    void contextLoads() throws Exception {
        iVideoService.refreshFile(defaultHome);
    }

}
