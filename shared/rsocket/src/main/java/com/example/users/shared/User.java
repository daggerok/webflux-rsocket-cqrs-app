package com.example.users.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class User {
    private UUID id;
    private @NonNull String name, username;
}
