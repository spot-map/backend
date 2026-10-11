package com.ridemap.backend.type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name="types")
public class SpotType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private  String name;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    protected  SpotType(){

    }

   public Integer getId() {
    return id;
   }

   public String getName(){
    return name;
   }

   public String getCode(){
    return code; 
   }
}
