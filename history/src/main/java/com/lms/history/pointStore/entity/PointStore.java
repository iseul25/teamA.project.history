package com.lms.history.pointStore.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointStore {
    private Integer itemId;
    private String category;
    private String brand;
    private String imgUrl;
    private String itemName;
    private Integer cost;
}
