package com.eaphone.jiankang;

import com.eaphone.jiankang.config.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringNettyApplication implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(SpringNettyApplication.class, args);
    }

    @Override public void run(String... args) throws Exception {
        nettyServer.run(8080);
    }


}
