package com.example.myfirst_todoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.myfirst_todoapp.entity.UserEntity;
import com.example.myfirst_todoapp.mapper.LoginMapper;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @InjectMocks
    private LoginServiceImpl loginService;

    @Mock
    private LoginMapper loginMapper;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUserId(1);
        userEntity.setUserName("testUser");
        userEntity.setUserPassword("testPassword");
    }

    // 該当ユーザーが存在する場合
    @Test
    void findByUserName_exist() {
        when(loginMapper.findByUserName("testUser")).thenReturn(userEntity);

        UserEntity result = loginService.findByUserName("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.getUserName());
        assertEquals("testPassword", result.getUserPassword());

        verify(loginMapper, times(1)).findByUserName("testUser");
    }

    // 該当ユーザーが存在しない場合
    @Test
    void findByUserName_notFound() {
        when(loginMapper.findByUserName("unknownUser")).thenReturn(null);
        UserEntity result = loginMapper.findByUserName("unknownUser");
        assertNull(result);

        verify(loginMapper, times(1)).findByUserName("unknownUser");
    }
}
