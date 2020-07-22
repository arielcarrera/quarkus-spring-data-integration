package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public interface ItemQueryRepository extends Repository<ItemEntity,Integer> {

    // methods with @Query
    @Query(value = "from ItemEntity i where i.uniqueValue = ?1")
    List<ItemEntity> queryUniqueValue(Integer uniqueValue); 
    
    @Query(value = "from ItemEntity i where i.uniqueValue = ?1",
    		countQuery = "select count(i) from ItemEntity i where i.uniqueValue = ?1")
    Page<ItemEntity> queryUniqueValue(Integer uniqueValue, Pageable peable); 
   
    @Query(name = "ItemEntity.findByUniqueValue")
    List<ItemEntity> namedQueryUniqueValue(Integer uniqueValue); 
    
    @Query(name = "ItemEntity.findByUniqueValue",
    		countName = "ItemEntity.findByUniqueValue.count")
    Page<ItemEntity> namedQueryUniqueValue(Integer uniqueValue, Pageable peable); 
    
    @Query(value = "select * from ItemEntity i where i.uniqueValue = ?1", nativeQuery = true)
    List<ItemEntity> nativeQueryUniqueValue(Integer uniqueValue); 
    
    @Query(value = "select * from ItemEntity i where i.uniqueValue = ?1",
    		countQuery = "select count(*) from ItemEntity i where i.uniqueValue = ?1", nativeQuery = true)
    Page<ItemEntity> nativeQueryUniqueValue(Integer uniqueValue, Pageable peable); 
    
    @Query(value = "from ItemEntity i where i.uniqueValue = :uniqueVal")
    List<ItemEntity> queryUniqueValueNamedParameter(@Param("uniqueVal") Integer uniqueValue); 
    
    @Query(value = "from #{#entityName} i where i.uniqueValue = ?1")
    List<ItemEntity> queryUniqueValueSpelExpressions(Integer uniqueValue); 
   
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ItemEntity i set i.value = ?1 where i.id = ?2")
    int updateValue(Integer value, Integer uniqueValue);
    
    @Modifying
    @Query("delete from ItemEntity i where i.value = ?1")
    void deleteInBulkByValue(Integer value);
}
