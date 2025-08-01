package com.project.arebbus.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ToggleUpvoteResponse {
  private boolean upvoteStatus;
  private Date toggledAt;
}
