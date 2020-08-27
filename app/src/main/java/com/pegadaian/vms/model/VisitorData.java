package com.pegadaian.vms.model;

public class VisitorData {

    private String itemNama, itemTelp, itemInstansi, itemEmail, itemHost, itemLokasi, itemTujuan,
            itemCheckin, itemCheckout, itemFoto, itemQr, itemJamDibuat, itemTglDibuat, itemStatus, key;

    public VisitorData() {
    }

    public VisitorData(String itemNama, String itemTelp, String itemInstansi, String itemEmail,
                       String itemHost, String itemLokasi, String itemTujuan, String itemCheckin,
                       String itemCheckout, String itemFoto, String itemQr, String itemJamDibuat,
                       String itemTglDibuat, String itemStatus) {

        this.itemNama = itemNama;
        this.itemTelp = itemTelp;
        this.itemInstansi = itemInstansi;
        this.itemEmail = itemEmail;
        this.itemHost = itemHost;
        this.itemLokasi = itemLokasi;
        this.itemTujuan = itemTujuan;
        this.itemCheckin = itemCheckin;
        this.itemCheckout = itemCheckout;
        this.itemFoto = itemFoto;
        this.itemQr = itemQr;
        this.itemJamDibuat = itemJamDibuat;
        this.itemTglDibuat = itemTglDibuat;
        this.itemStatus = itemStatus;
    }

    public String getItemNama() {
        return itemNama;
    }

    public String getItemTelp() {
        return itemTelp;
    }

    public String getItemInstansi() {
        return itemInstansi;
    }

    public String getItemEmail() {
        return itemEmail;
    }

    public String getItemHost() {
        return itemHost;
    }

    public String getItemLokasi() {
        return itemLokasi;
    }

    public String getItemTujuan() {
        return itemTujuan;
    }

    public String getItemCheckin() {
        return itemCheckin;
    }

    public String getItemCheckout() {
        return itemCheckout;
    }

    public String getItemFoto() {
        return itemFoto;
    }

    public String getItemQr() {
        return itemQr;
    }

    public String getItemJamDibuat() {
        return itemJamDibuat;
    }

    public String getItemTglDibuat() {
        return itemTglDibuat;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
