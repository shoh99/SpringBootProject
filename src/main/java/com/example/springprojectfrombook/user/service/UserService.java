package com.example.springprojectfrombook.user.service;

import com.example.springprojectfrombook.exception.NotFoundException;
import com.example.springprojectfrombook.user.data.UserDto;
import com.example.springprojectfrombook.user.entity.UserEntity;
import com.example.springprojectfrombook.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private  ModelMapper modelMapper;
    private UserRepository userRepository;


    public UserEntity searchByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserDto> findAllUsers() {
        var userEntityList = new ArrayList<>(userRepository.findAll());

        return userEntityList
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(final UUID id) {
        var user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                "User by id " + id + " was not found"
                        )
                );
        return convertToDto(user);
    }

    public UserDto createUser(UserDto userDto, String password)
        throws NoSuchAlgorithmException {
        var user = convertToEntity(userDto);

        if(password.isBlank()) throw new IllegalArgumentException(
                "Password is required"
        );

        var existEmail = userRepository.selectExistsEmail(user.getEmail());
        if (existEmail) throw new BadCredentialsException(
                "Email " + user.getEmail() + " taken"
        );

        byte[] salt = createSalt();
        byte[] hashedPassword = createPasswordHash(password, salt);

        user.setStoredSalt(salt);
        user.setStoredHash(hashedPassword);

        userRepository.save(user);

        return convertToDto(user);
    }

    public void updateUser(UUID id, UserDto userDto, String password)
    throws NoSuchAlgorithmException{
        var user = findOrThrow(id);
        var userParam = convertToEntity(userDto);

        user.setEmail(userParam.getEmail());
        user.setMobileNumber(userParam.getMobileNumber());

        if (!password.isBlank()) {
            byte[] salt = createSalt();
            byte[] hashedPassword = createPasswordHash(password, salt);

            user.setStoredSalt(salt);
            user.setStoredHash(hashedPassword);
        }
        userRepository.save(user);
    }

    public void removeUserById(UUID id) {
        findOrThrow(id);
        userRepository.deleteById(id);
    }

    private UserEntity findOrThrow(final UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                "User by id "+ id + " was not found")
                );
    }

    private byte[] createSalt() {
        var random = new SecureRandom();
        var salt = new byte[128];
        random.nextBytes(salt);

        return salt;
    }

    private byte[] createPasswordHash(String password, byte[] salt)
            throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        return md.digest(
                password.getBytes(StandardCharsets.UTF_8)
        );
    }

    private UserDto convertToDto(UserEntity entity) {
        return modelMapper.map(entity, UserDto.class);
    }

    private UserEntity convertToEntity(UserDto dto) {
        return modelMapper.map(dto, UserEntity.class);
    }
}
