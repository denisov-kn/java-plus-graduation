package ru.practicum.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;


public class CompilationMapperCustom {

    public static CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }
}