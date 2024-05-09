package com.mostafatamer.trysomethingcrazy.mappers;

public interface Mapper<A, B> {

    B entityToDto(A a);
    A dtoToEntity(B b);
}
