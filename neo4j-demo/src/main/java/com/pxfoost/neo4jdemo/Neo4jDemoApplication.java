package com.pxfoost.neo4jdemo;

import com.pxfoost.neo4jdemo.service.Service;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.graphdb.*;
import org.neo4j.io.fs.DefaultFileSystemAbstraction;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootApplication
@Slf4j
public class Neo4jDemoApplication {


    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(Neo4jDemoApplication.class, args);
        final Service service = new Service();
        service.test();
    }


}
