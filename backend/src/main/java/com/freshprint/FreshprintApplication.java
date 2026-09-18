package com.freshprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/** Starts the local Freshprint API. */
@SpringBootApplication
@EnableAsync
public class FreshprintApplication {

  /** Runs the server. */
  public static void main(String[] args) {
    SpringApplication.run(FreshprintApplication.class, args);
  }
}
