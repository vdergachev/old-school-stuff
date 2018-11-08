/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author LysovIA
 */
public enum Region implements EnumClass<String>{

    altai("01000000"),
    amur("10000000"),
    arkhangelsk("11000000"),
    astrakhan("12000000"),
    baikonur("55000000"),
    belgorod("14000000"),
    bryansk("15000000"),
    vladimir("17000000"),
    volgograd("18000000"),
    vologda("19000000"),
    voronezh("20000000"),
    jewish("99000000"),
    trans_baikal("76000000"),
    ivanovo("24000000"),
    irkutsk("25000000"),
    kaliningrad("27000000"),
    kaluga("29000000"),
    kamchatka("30000000"),
    kemerovo("32000000"),
    kirov("33000000"),
    kostroma("34000000"),
    krasnodar("03000000"),
    krasnoyarsk("04000000"),
    kurgan("37000000"),
    kursk("38000000"),
    leningrad("41000000"),
    lipetsk("42000000"),
    magadan("44000000"),
    moscow("45000000"),
    moscow_oblast("46000000"),
    murmansk("47000000"),
    nenets("11800000"),
    nizhny_novgorod("22000000"),
    novgorod("49000000"),
    novosibirsk("50000000"),
    omsk("52000000"),
    orenburg("53000000"),
    orel("54000000"),
    penza("56000000"),
    perm("57000000"),
    primorye("05000000"),
    pskov("58000000"),
    adygeya("79000000"),
    altai_rep("84000000"),
    bashkortostan("80000000"),
    buryatia("81000000"),
    daghestan("82000000"),
    ingushetia("26000000"),
    kabardino_balkarian("83000000"),
    kalmykia("85000000"),
    karachayevo_circassian("91000000"),
    karelia("86000000"),
    komi("87000000"),
    crimea("35000000"),
    mari_el("88000000"),
    mordovia("89000000"),
    yakutia("98000000"),
    north_ossetia("90000000"),
    tatarstan("92000000"),
    tuva("93000000"),
    udmurtian("94000000"),
    khakassia("95000000"),
    chechen("96000000"),
    chuvash("97000000"),
    rostov("60000000"),
    ryazan("61000000"),
    samara("36000000"),
    st_petersburg("40000000"),
    saratov("63000000"),
    sakhalin("64000000"),
    sverdlovsk("65000000"),
    sevastopol("67000000"),
    smolensk("66000000"),
    stavropol("07000000"),
    tambov("68000000"),
    tver("28000000"),
    tomsk("69000000"),
    tula("70000000"),
    tyumen("71000000"),
    ulyanovsk("73000000"),
    khabarovsk("08000000"),
    yugra("71800000"),
    chelyabinsk("75000000"),
    chukotka("77000000"),
    yamal_nenets("71900000"),
    yaroslavl("78000000");


    private String id;

    Region (String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static Region fromId(String id) {
        for (Region at : Region.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}