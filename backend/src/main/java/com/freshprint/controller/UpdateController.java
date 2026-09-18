package com.freshprint.controller;

import com.freshprint.dto.UpdateResponse;
import com.freshprint.service.update.UpdateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HTTP endpoints for listing and reviewing template updates. */
@RestController
@RequestMapping("/api/engagements")
public class UpdateController {

  private final UpdateService updates;

  public UpdateController(UpdateService updates) {
    this.updates = updates;
  }

  @GetMapping("/template-updates")
  public UpdateResponse.ListResponse list() {
    return updates.list();
  }

  @GetMapping("/{id}/template-update")
  public UpdateResponse one(@PathVariable("id") String id) {
    return updates.one(id);
  }
}
