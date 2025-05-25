// PostTagId.java
package com.project.arebbus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTagId implements Serializable {
    private Long postId;
    private Long tagId;
}
