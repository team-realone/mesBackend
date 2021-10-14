package com.mes.mesBackend.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "SUB_NAVS")
@Data
public class SubNavigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LEVEL")
    private int level;  // 유저 레벨

    @Column(name = "USE_YN")
    private boolean useYn;  // 사용여부

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MAIN_NAVS_ID")
    private MainNavigation mainNavigation;

    @Column(name = "ORDERS")
    private int orders;     // 출력순번

    public void put(SubNavigation newSubNavigation) {
        setName(newSubNavigation.name);
        setLevel(newSubNavigation.level);
        setOrders(newSubNavigation.orders);
    }
}
