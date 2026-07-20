package com.connectoBackend.common.mapper;

import org.mapstruct.MappingTarget;

/**
 * Generic base mapper interface.
 *
 * <p>
 * Provides common mapping operations between an Entity and a Response DTO.
 * Request DTO mappings (Create/Update) should be declared in the respective
 * module mapper interfaces.
 * </p>
 *
 * @param <R> Response DTO type
 * @param <E> Entity type
 */
public interface BaseMapper<R, E> {

    // -> Convert entity to response DTO.
    R toDto(E entity);

    // -> Update an existing entity from a response DTO if required.
    // -> Most implementations will override or ignore this method.
    void updateEntity(R dto, @MappingTarget E entity);

}