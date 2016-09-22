package org.apache.problem;

import io.vertxbeans.rxjava.VertxBeans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.IOException;

/**
 * Created by manishk on 9/22/16.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.apache.problem"})
@Import(VertxBeans.class)
public class AppStarter {

    public static void main(final String... args) throws IOException {
        System.setProperty("vertx.clustered", Boolean.toString(true));
        System.setProperty("vertx.cluster-host", HostUtil.getNetworkInterfaces().get(0));
        new SpringApplication(AppStarter.class).run(args);
    }
}
