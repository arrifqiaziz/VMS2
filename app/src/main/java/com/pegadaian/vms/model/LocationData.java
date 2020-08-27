package com.pegadaian.vms.model;

// MOHAMAD ALFIQKO MAULANA
// ARRIFQI AZIZ ARDHIANSYAH

public class LocationData {

    private String itemNama, itemAlamat, itemTelp;

    public LocationData() {
    }

    public LocationData(String itemNama, String itemAlamat, String itemTelp) {

        this.itemNama = itemNama;
        this.itemAlamat = itemAlamat;
        this.itemTelp = itemAlamat;
    }

    public String getItemNama() {
        return itemNama;
    }

    public String getItemAlamat() {
        return itemAlamat;
    }

    public String getItemTelp() {
        return itemTelp;
    }
}
