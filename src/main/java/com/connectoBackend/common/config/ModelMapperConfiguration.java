package com.connectoBackend.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


 // Configures ModelMapper bean.

@Configuration
public class ModelMapperConfiguration {

    // Register ModelMapper bean
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}