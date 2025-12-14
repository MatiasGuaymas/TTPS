package io.github.grupo01.volve_a_casa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.filters.UserFilter;
import io.github.grupo01.volve_a_casa.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final User dummyUser = mock(User.class);

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        putPrincipalResolver
                )
                .build();
    }

    private final HandlerMethodArgumentResolver putPrincipalResolver = new HandlerMethodArgumentResolver() {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            // Activate this resolver if the controller argument is of type User
            return parameter.getParameterType().isAssignableFrom(User.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            // Always returns our dummy user
            return dummyUser;
        }
    };

    // ========= LIST USERS =========

    @Test
    void listAllUsersOrderByName_whenEmpty_returnsNoContent() throws Exception {
        when(userService.findAllFiltered(any(UserFilter.class), any(Pageable.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllUsersOrderByName_whenUsersExist_returnsOkAndList() throws Exception {
        UserResponseDTO user1 = createResponse(1L, "Juan", "Perez", "juan.perez@test.com");
        UserResponseDTO user2 = createResponse(2L, "Ana", "Gomez", "ana.gomez@test.com");

        when(userService.findAllFiltered(any(UserFilter.class), any(Pageable.class))).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[1].name").value("Ana"));
    }

    // ========= GET USER BY ID =========

    @Test
    void getUserById_whenUserExists_returnsOk() throws Exception {
        long targetUserId = 1L;

        User user = createUser("Marta", "Sanchez", "martasanchez@test.com");

        when(userService.findById(targetUserId)).thenReturn(user);

        mockMvc.perform(get("/api/users/" + targetUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("martasanchez@test.com"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsNotFound() throws Exception {
        long targetUserId = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User no encontrado"))
                .when(userService).findById(targetUserId);

        mockMvc.perform(get("/api/users/" + targetUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User no encontrado"));
    }

    // ========= UPDATE USER =========

    @Test
    void updateUser_returnsOk() throws Exception {
        UserUpdateDTO updateDTO =
                new UserUpdateDTO("Juan Carlos", "Gomez", null, "Buenos Aires", null, 0f, 0f);

        UserResponseDTO updated = createResponse(1L, "Juan Carlos", "Gomez", "juan@test.com");

        when(userService.updateUser(dummyUser, updateDTO)).thenReturn(updated);

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Carlos"))
                .andExpect(jsonPath("$.lastName").value("Gomez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    private UserResponseDTO createResponse(Long id, String name, String lastname, String email) {
        return new UserResponseDTO(
                id,
                name,
                lastname,
                email,
                "221 333-3333",
                "Buenos Aires",
                "Lanús",
                -54.23f,
                -12.32f,
                100
        );
    }

    private User createUser(String name, String lastname, String email) {
        return new User(
                name,
                lastname,
                email,
                "password",
                "221 111-1111",
                "La Plata",
                "La Plata",
                -54.23f,
                -12.32f
        );
    }

    private PetResponseDTO createPetResponse(long id, long creatorId, String name) {
        return new PetResponseDTO(
                id,
                name,
                Pet.Size.MEDIANO,
                "perro mediano",
                "color",
                "race",
                10.0f,
                -54.21f,
                -23.128f,
                LocalDate.now(),
                Pet.State.PERDIDO_PROPIO,
                Pet.Type.PERRO,
                creatorId
        );
    }
}

