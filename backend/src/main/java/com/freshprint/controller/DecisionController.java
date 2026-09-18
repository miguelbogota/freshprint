package com.freshprint.controller;

import com.freshprint.dto.DecisionReceipt;
import com.freshprint.dto.DecisionRequest;
import com.freshprint.dto.OperationResponse;
import com.freshprint.service.decision.DecisionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HTTP endpoints for versioned decisions and their eventual outcomes. */
@RestController
@RequestMapping("/api")
public class DecisionController {

  private final DecisionService decisions;

  public DecisionController(DecisionService decisions) {
    this.decisions = decisions;
  }

  @PostMapping("/engagements/{id}/template-update-decisions")
  public DecisionReceipt decide(@PathVariable("id") String id,
      @RequestBody DecisionRequest request) {
    return decisions.decide(id, request);
  }

  @GetMapping("/template-update-operations/{id}")
  public OperationResponse operation(@PathVariable("id") String id) {
    return decisions.operation(id);
  }
}
