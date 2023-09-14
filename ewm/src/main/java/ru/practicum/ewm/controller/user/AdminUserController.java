package ru.practicum.ewm.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {
    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid NewUserRequest userRequest) {
        return mapper.toDto(service.add(mapper.toEntity(userRequest)));
    }

    @GetMapping
    public Collection<UserDto> getByIds(@RequestParam(required = false) Set<Long> ids,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getByIds(ids, from, size).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId) {
        service.delete(userId);
    }
}
