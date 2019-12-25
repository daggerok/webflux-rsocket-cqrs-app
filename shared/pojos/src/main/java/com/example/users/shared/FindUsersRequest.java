package com.example.users.shared;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class FindUsersRequest {
    private Long size = -1L;
}
