package com.mr.service;

import com.mr.service.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemService {
    public Item getItems(Item item){
        Integer id= new Random().nextInt();
        item.setId(id);
        return item;
    }
}
