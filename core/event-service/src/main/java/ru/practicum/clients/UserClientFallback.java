package ru.practicum.clients;

import ru.practicum.dto.user.UserShortDto;

import java.util.List;

public class UserClientFallback implements UserClient {
    @Override
    public UserShortDto getUserShortById(Long id) {
        return UserShortDto.builder()
                .id(0L)
                .name("test")
                .build();

    }

    @Override
    public List<UserShortDto> getUsersShort(List<Long> ids, Integer from, Integer size) {
        return List.of(
                UserShortDto.builder()
                        .id(0L)
                        .name("test")
                        .build()
        );
    }
}
