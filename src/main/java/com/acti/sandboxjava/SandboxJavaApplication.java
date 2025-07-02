package com.acti.sandboxjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SandboxJavaApplication {
  public static void main(String[] args) {
    System.out.println("➡️ App Main Started");
    SpringApplication.run(SandboxJavaApplication.class, args);
    System.out.println("✅ SpringApplication.run completed");
  }

}
